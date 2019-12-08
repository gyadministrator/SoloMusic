package com.example.gy.musicgame.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.activity.LoginActivity;
import com.example.gy.musicgame.activity.SearchActivity;
import com.example.gy.musicgame.adapter.MainAdapter;
import com.example.gy.musicgame.adapter.RecyclerAdapter;
import com.example.gy.musicgame.api.Api;
import com.example.gy.musicgame.constant.Constants;
import com.example.gy.musicgame.helper.LoadingDialogHelper;
import com.example.gy.musicgame.helper.RetrofitHelper;
import com.example.gy.musicgame.model.RecommendMusicModel;
import com.example.gy.musicgame.model.UserInfoVo;
import com.example.gy.musicgame.presenter.MainPresenter;
import com.example.gy.musicgame.utils.HandlerUtils;
import com.example.gy.musicgame.utils.NotificationPermissionUtil;
import com.example.gy.musicgame.utils.SharedPreferenceUtil;
import com.example.gy.musicgame.utils.UserManager;
import com.example.gy.musicgame.view.MainView;
import com.example.gy.musicgame.view.RecyclerDecoration;
import com.example.gy.musicgame.view.TitleView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.bugly.beta.Beta;

import java.util.Map;

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
    private boolean isShow = true;

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

    private void initAction() {
        mainPresenter = new MainPresenter<MainView>(this);
        mainPresenter.getRecommendList(isShow);
        mShimmerRecyclerView.showShimmerAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setNestedScrollingEnabled(false);
        mainAdapter = new MainAdapter(mActivity, recyclerView, mainPresenter.getTitles(), mainPresenter.getTypes());
        mainAdapter.setOnMainAdapterListener(this);
        recyclerView.setAdapter(mainAdapter);

        SharedPreferenceUtil<String> preferenceUtil = new SharedPreferenceUtil<>();
        String token = preferenceUtil.getObject(mActivity, Constants.CURRENT_TOKEN);
        if (TextUtils.isEmpty(token)) {
            goLogin();
        }else {
            getUserInfo(token);
        }
    }

    private void getUserInfo(String token) {
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.SERVER_URL);
        Observable<Map> observable = api.userInfo(token);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Map>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Map map) {
                        boolean handler = HandlerUtils.isHandler(map, mActivity);
                        if (!handler) {
                            Map<String, Object> data = (Map<String, Object>) map.get("data");
                            UserInfoVo userInfoVo = new UserInfoVo();
                            if (data != null) {
                                if (data.containsKey("username")) {
                                    String username = (String) data.get("username");
                                    userInfoVo.setUserName(username);
                                }
                                if (data.containsKey("avatar")) {
                                    String avatar = (String) data.get("avatar");
                                    userInfoVo.setAvatarUrl(avatar);
                                }
                                if (data.containsKey("mobile")) {
                                    String mobile = (String) data.get("mobile");
                                    userInfoVo.setMobile(mobile);
                                }
                            }
                            UserManager.setUserInfoVo(userInfoVo, mActivity);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        goLogin();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 去登录
     */
    private void goLogin() {
        LoginActivity.startActivity(mActivity);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initData() {
        //检测通知栏权限
        checkPermission();
        //检测版本更新
        checkUpdate();
        gridShimmerRecyclerView.showShimmerAdapter();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void checkPermission() {
        NotificationPermissionUtil.checkNotificationEnable(mActivity);
    }

    private void checkUpdate() {
        Beta.checkUpgrade();
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ListenViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        isShow = false;
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
        LoadingDialogHelper.show(mActivity, msg);
    }

    @Override
    public void dismissLoading() {
        LoadingDialogHelper.dismiss();
    }

    @Override
    public void success() {
        mShimmerRecyclerView.hideShimmerAdapter();
    }
}
