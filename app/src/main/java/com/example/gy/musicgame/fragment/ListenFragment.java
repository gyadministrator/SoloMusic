package com.example.gy.musicgame.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.activity.LoginActivity;
import com.example.gy.musicgame.activity.MainActivity;
import com.example.gy.musicgame.activity.SearchActivity;
import com.example.gy.musicgame.adapter.MainAdapter;
import com.example.gy.musicgame.adapter.RecyclerAdapter;
import com.example.gy.musicgame.api.Api;
import com.example.gy.musicgame.constant.Constants;
import com.example.gy.musicgame.helper.LoadingDialogHelper;
import com.example.gy.musicgame.helper.RetrofitHelper;
import com.example.gy.musicgame.listener.OnItemClickListener;
import com.example.gy.musicgame.model.ApkModel;
import com.example.gy.musicgame.model.BottomBarVo;
import com.example.gy.musicgame.model.RecommendMusicModel;
import com.example.gy.musicgame.model.UserInfoVo;
import com.example.gy.musicgame.presenter.MainPresenter;
import com.example.gy.musicgame.utils.HandlerUtils;
import com.example.gy.musicgame.utils.SharedPreferenceUtil;
import com.example.gy.musicgame.utils.UpdateManager;
import com.example.gy.musicgame.utils.UserManager;
import com.example.gy.musicgame.view.MainView;
import com.example.gy.musicgame.view.RecyclerDecoration;
import com.example.gy.musicgame.view.TitleView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.bugly.beta.Beta;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ListenFragment extends Fragment implements OnRefreshListener, TitleView.OnRightClickListener, MainView, MainAdapter.OnMainAdapterListener {

    private ListenViewModel mViewModel;
    private RecyclerView rv_recommend;
    private RecyclerAdapter recyclerAdapter;
    private RecyclerView recyclerView;
    private MainAdapter mainAdapter;
    private MainPresenter<MainView> mainPresenter;
    private SmartRefreshLayout refreshLayout;
    private ShimmerRecyclerView mShimmerRecyclerView;
    private ShimmerRecyclerView gridShimmerRecyclerView;
    private TitleView titleView;
    private Activity mActivity;
    private String token;
    private ApkModel apkModel;
    private boolean isRefresh = false;

    public static ListenFragment newInstance() {
        return new ListenFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listen_fragment, container, false);
        initView(view);
        initData();
        initAction();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initAction() {
        mainPresenter = new MainPresenter<>(this);
        mainPresenter.getRecommendList();
        mShimmerRecyclerView.showShimmerAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setNestedScrollingEnabled(false);
        mainAdapter = new MainAdapter(mActivity, recyclerView, mainPresenter.getTitles(), mainPresenter.getTypes(), ((MainActivity) mActivity).getBottomBarView());
        mainAdapter.setOnMainAdapterListener(this);
        recyclerView.setAdapter(mainAdapter);

        SharedPreferenceUtil preferenceUtil = new SharedPreferenceUtil();
        token = preferenceUtil.getObject(mActivity, Constants.CURRENT_TOKEN);
        if (!isRefresh) {
            checkUpdate();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            UpdateManager mUpdateManager = new UpdateManager(mActivity);
            mUpdateManager.checkUpdateInfo(apkModel);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initData() {
        gridShimmerRecyclerView.showShimmerAdapter();
    }


    private void initView(View view) {
        rv_recommend = view.findViewById(R.id.rv_recommend);
        recyclerView = view.findViewById(R.id.recyclerView);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(this);
        mShimmerRecyclerView = view.findViewById(R.id.shimmer_recycler_view);
        gridShimmerRecyclerView = view.findViewById(R.id.grid_shimmer_recycler_view);
        titleView = view.findViewById(R.id.navigation);
        titleView.setRightClickListener(this);
    }

    private void checkUpdate() {
        String packageName = AppUtils.getAppPackageName();
        Integer versionCode = AppUtils.getAppVersionCode();
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        final Api api = retrofitHelper.initRetrofit(Constants.SERVER_URL);
        Observable<Map> observable = api.apkUpdate(token, packageName, versionCode);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Map>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onNext(Map map) {
                        boolean handler = HandlerUtils.isHandler(map, mActivity);
                        if (!handler) {
                            Type type = new TypeToken<ApkModel>() {
                            }.getType();
                            Gson gson = new Gson();
                            apkModel = gson.fromJson(Objects.requireNonNull(gson.toJson(map.get("data"))), type);
                            if (apkModel != null) {
                                int appVersionCode = AppUtils.getAppVersionCode();
                                if (apkModel.getApkCode() > appVersionCode) {
                                    showNotice(apkModel);
                                }
                                SharedPreferences preferences = mActivity.getSharedPreferences("apk", Context.MODE_PRIVATE);
                                SharedPreferences.Editor edit = preferences.edit();
                                edit.putString("downloadUrl", apkModel.getDownloadUrl());
                                edit.apply();
                            }
                        }
                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onError(Throwable e) {
                        Beta.checkUpgrade();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showNotice(ApkModel apkModel) {
        // 这里来检测版本是否需要更新
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
        } else {
            UpdateManager mUpdateManager = new UpdateManager(mActivity);
            mUpdateManager.checkUpdateInfo(apkModel);
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ListenViewModel.class);
        // TODO: Use the ViewModel
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        isRefresh = true;
        initAction();
        gridShimmerRecyclerView.showShimmerAdapter();
    }

    @Override
    public void clickRight(View view) {
        Intent intent = new Intent(mActivity, SearchActivity.class);
        startActivity(intent);
    }

    @Override
    public void clickLeft(View view) {

    }

    @Override
    public void success(RecommendMusicModel result) {
        gridShimmerRecyclerView.hideShimmerAdapter();
        setGridData(result);
        refreshLayout.finishRefresh(2000);
    }

    private void setGridData(RecommendMusicModel result) {
        rv_recommend.setLayoutManager(new GridLayoutManager(mActivity, 3));
        rv_recommend.addItemDecoration(new RecyclerDecoration(getResources().getDimensionPixelSize(R.dimen.itemWidth)));
        rv_recommend.setNestedScrollingEnabled(false);
        recyclerAdapter = new RecyclerAdapter(mActivity, result.getResult().getList());
        rv_recommend.setAdapter(recyclerAdapter);
        recyclerAdapter.setOnItemClickListener(new OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void play(BottomBarVo bottomBarVo) {
                ((MainActivity) mActivity).play(bottomBarVo);
            }
        });
    }

    @Override
    public void fail(String msg) {
        LoadingDialogHelper.dismiss();
        gridShimmerRecyclerView.hideShimmerAdapter();
        ToastUtils.showShort(msg);
        refreshLayout.finishRefresh(false);//传入false表示刷新失败
    }

    @Override
    public void showLoading(String msg) {
    }

    @Override
    public void dismissLoading() {
    }

    @Override
    public void success() {
        mShimmerRecyclerView.hideShimmerAdapter();
    }
}
