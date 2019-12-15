package com.example.gy.musicgame.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.adapter.AlbumLoveLinearAdapter;
import com.example.gy.musicgame.api.Api;
import com.example.gy.musicgame.constant.Constants;
import com.example.gy.musicgame.helper.LoadingDialogHelper;
import com.example.gy.musicgame.helper.RetrofitHelper;
import com.example.gy.musicgame.utils.HandlerUtils;
import com.example.gy.musicgame.view.TitleView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AlbumLoveActivity extends BaseActivity implements OnLoadMoreListener, OnRefreshListener {
    private TitleView titleView;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private AlbumLoveLinearAdapter loveLinearAdapter;
    private boolean isLoad = false;
    private String token;
    private Integer currentPage = 1;
    private Integer pageSize = 20;

    @Override
    protected void initView() {
        titleView = fd(R.id.titleView);
        refreshLayout = fd(R.id.refreshLayout);
        recyclerView = fd(R.id.rv_linear);

        refreshLayout.setOnLoadMoreListener(this);
        refreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        titleView.setTitle(getResources().getString(R.string.my_love));
    }

    public static void startActivity(Activity activity, String token) {
        Intent intent = new Intent(activity, SongAlbumActivity.class);
        intent.putExtra("token", token);
        activity.startActivity(intent);
    }

    private void getAlbumLove(boolean b) {
        if (b) {
            LoadingDialogHelper.show(mActivity, "加载中...");
        }
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.SERVER_URL);
        Observable<Map> observable = api.albumLoveList(token, currentPage, pageSize);
        observable.subscribeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Map>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Map map) {
                        boolean handler = HandlerUtils.isHandler(map, mActivity);
                        if (!handler) {

                        }
                        SharedPreferences preferences = mActivity.getSharedPreferences("myFragment", MODE_PRIVATE);
                        SharedPreferences.Editor edit = preferences.edit();
                        edit.putInt("loveAlbum", 0);
                        edit.apply();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (b) {
                            LoadingDialogHelper.dismiss();
                        }
                        ToastUtils.showShort(Objects.requireNonNull(e.getMessage()));
                    }

                    @Override
                    public void onComplete() {
                        if (b) {
                            LoadingDialogHelper.dismiss();
                        }
                    }
                });
    }

    @Override
    protected void initAction() {
        getAlbumLove(true);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_album_love;
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        isLoad = true;
        currentPage += 1;
        getAlbumLove(false);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        isLoad = false;
        currentPage = 1;
        getAlbumLove(false);
    }
}
