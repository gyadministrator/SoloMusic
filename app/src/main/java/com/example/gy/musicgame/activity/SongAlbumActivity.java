package com.example.gy.musicgame.activity;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.adapter.AlbumUserLinearAdapter;
import com.example.gy.musicgame.api.Api;
import com.example.gy.musicgame.constant.Constants;
import com.example.gy.musicgame.helper.LoadingDialogHelper;
import com.example.gy.musicgame.helper.RetrofitHelper;
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

public class SongAlbumActivity extends BaseActivity implements OnRefreshListener, OnLoadMoreListener {
    private TitleView titleView;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private AlbumUserLinearAdapter userLinearAdapter;
    private boolean isLoad = false;
    private String token;
    private Integer albumId;
    private Integer currentPage = 1;
    private Integer pageSize = 20;
    private String album;

    @Override
    protected void initView() {
        titleView = fd(R.id.titleView);
        refreshLayout = fd(R.id.refreshLayout);
        recyclerView = fd(R.id.rv_linear);

        refreshLayout.setOnLoadMoreListener(this);
        refreshLayout.setOnRefreshListener(this);
    }

    public static void startActivity(Activity activity, String token, Integer albumId, String album) {
        Intent intent = new Intent(activity, SongAlbumActivity.class);
        intent.putExtra("token", token);
        intent.putExtra("albumId", albumId);
        intent.putExtra("album", album);
        activity.startActivity(intent);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        album = intent.getStringExtra("album");
        albumId = intent.getIntExtra("albumId", 0);

        titleView.setTitle(album);
    }

    @Override
    protected void initAction() {
        getAlbumUser(true);
    }

    private void getAlbumUser(boolean b) {
        if (b) {
            LoadingDialogHelper.show(mActivity, "加载中...");
        }
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.SERVER_URL);
        Observable<Map> observable = api.albumUserList(token, albumId, currentPage, pageSize);
        observable.subscribeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Map>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Map map) {

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
    protected int getContentView() {
        return R.layout.activity_song_album;
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        isLoad = false;
        currentPage = 1;
        getAlbumUser(false);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        isLoad = true;
        currentPage += 1;
        getAlbumUser(false);
    }
}
