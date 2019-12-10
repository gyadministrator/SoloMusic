package com.example.gy.musicgame.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.adapter.LinearAdapter;
import com.example.gy.musicgame.constant.Constants;
import com.example.gy.musicgame.helper.LoadingDialogHelper;
import com.example.gy.musicgame.helper.RetrofitHelper;
import com.example.gy.musicgame.model.BottomBarVo;
import com.example.gy.musicgame.model.MusicModel;
import com.example.gy.musicgame.utils.SharedPreferenceUtil;
import com.example.gy.musicgame.view.BottomBarView;
import com.example.gy.musicgame.view.TitleView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AlbumActivity extends BaseActivity implements OnRefreshListener, OnLoadMoreListener {
    private TitleView titleView;
    private TextView tvTopTitle;
    private RecyclerView rv_linear;
    private LinearAdapter linerAdapter;
    private int mType;
    private SmartRefreshLayout refreshLayout;
    private int offset = 0;
    private boolean isLoad;
    private BottomBarView bottomBarView;

    @Override
    protected void initView() {
        titleView = fd(R.id.navigation);
        tvTopTitle = fd(R.id.tv_top_title);
        rv_linear = fd(R.id.rv_linear);
        refreshLayout = fd(R.id.refreshLayout);
        bottomBarView = fd(R.id.bottom_bar_view);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        String mTitle = intent.getStringExtra("title");
        mType = intent.getIntExtra("type", 1);
        tvTopTitle.setText(mTitle);
        titleView.setTitle(mTitle);

        setBottomBarData();
    }

    @Override
    protected void hasNet() {
        super.hasNet();
        initAction();
    }

    @Override
    protected void initAction() {
        setData(true);
    }

    private void setData(final boolean isShow) {
        if (isShow) {
            LoadingDialogHelper.show(mActivity, "加载中...");
        }
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Map<String, Object> params = retrofitHelper.getmParams();
        params.put("method", Constants.METHOD_LIST);
        params.put("type", mType);
        params.put("size", 20);
        params.put("offset", offset);
        Observable<MusicModel> observable = retrofitHelper.initRetrofit(Constants.BASE_URL).list(params);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MusicModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MusicModel musicModel) {
                        if (isLoad) {
                            //加载更多
                            if (linerAdapter != null) {
                                linerAdapter.setData(musicModel.getSong_list());
                                refreshLayout.finishLoadMore(2000);
                            }
                        } else {
                            refreshLayout.finishRefresh(2000);//传入false表示刷新失败
                            rv_linear.setLayoutManager(new LinearLayoutManager(mActivity));
                            rv_linear.addItemDecoration(new DividerItemDecoration(mActivity, RecyclerView.VERTICAL));
                            linerAdapter = new LinearAdapter(mActivity, rv_linear);
                            linerAdapter.setList(musicModel.getSong_list(), false);
                            rv_linear.setAdapter(linerAdapter);
                        }
                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onError(Throwable e) {
                        if (isShow) {
                            LoadingDialogHelper.dismiss();
                        }
                        ToastUtils.showShort(Objects.requireNonNull(e.getMessage()));
                        if (isLoad) {
                            refreshLayout.finishLoadMore(false);//传入false表示加载失败
                        } else {
                            refreshLayout.finishRefresh(false);//传入false表示刷新失败
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (isShow) {
                            LoadingDialogHelper.dismiss();
                        }
                    }
                });
    }

    private void setBottomBarData() {
        SharedPreferenceUtil<BottomBarVo> preferenceUtil = new SharedPreferenceUtil<>();
        String json = preferenceUtil.getObjectJson(mActivity, Constants.CURRENT_BOTTOM_VO);
        Type type = new TypeToken<BottomBarVo>() {
        }.getType();
        BottomBarVo bottomBarVo = new Gson().fromJson(json, type);
        bottomBarView.setBottomBarVo(bottomBarVo);
    }

    /**
     * 启动活动
     *
     * @param context context
     * @param title   标题
     * @param type    类型
     */
    public static void startActivity(Context context, String title, int type) {
        Intent intent = new Intent(context, AlbumActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_album;
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        setData(false);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        isLoad = true;
        ++offset;
        setData(false);
    }
}
