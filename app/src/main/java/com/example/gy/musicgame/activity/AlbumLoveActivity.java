package com.example.gy.musicgame.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.ToastUtils;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.adapter.AlbumLoveLinearAdapter;
import com.example.gy.musicgame.api.Api;
import com.example.gy.musicgame.constant.Constants;
import com.example.gy.musicgame.helper.LoadingDialogHelper;
import com.example.gy.musicgame.helper.RetrofitHelper;
import com.example.gy.musicgame.listener.OnItemClickListener;
import com.example.gy.musicgame.model.BaseAlbumLoveVo;
import com.example.gy.musicgame.model.BottomBarVo;
import com.example.gy.musicgame.utils.HandlerUtils;
import com.example.gy.musicgame.utils.MusicUtils;
import com.example.gy.musicgame.view.TitleView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AlbumLoveActivity extends BaseActivity implements OnItemClickListener, XRecyclerView.LoadingListener {
    private TitleView titleView;
    private XRecyclerView recyclerView;
    private AlbumLoveLinearAdapter loveLinearAdapter;
    private boolean isLoad = false;
    private String token;
    private Integer currentPage = 1;
    private Integer pageSize = 20;
    private List<BaseAlbumLoveVo> list;
    private int loveSize = 0;
    private LinearLayout llNoData;
    private boolean isRefresh = false;

    @Override
    protected void initView() {
        titleView = fd(R.id.titleView);
        recyclerView = fd(R.id.rv_linear);
        llNoData = fd(R.id.ll_no_data);

        recyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        recyclerView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
    }

    public static void startActivity(Activity activity, String token) {
        Intent intent = new Intent(activity, AlbumLoveActivity.class);
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
                            Gson gson = new Gson();
                            String json = gson.toJson(map.get("data"));
                            Type type = new TypeToken<List<BaseAlbumLoveVo>>() {
                            }.getType();
                            list = gson.fromJson(json, type);
                            if (list != null && list.size() > 0) {
                                if (isLoad) {
                                    loveSize += list.size();
                                    loveLinearAdapter.addData(list);
                                    recyclerView.loadMoreComplete();
                                } else {
                                    loveSize = list.size();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (isRefresh) {
                                                recyclerView.refreshComplete();
                                            }
                                            setData(list);
                                        }
                                    });
                                }
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isLoad) {
                                            llNoData.setVisibility(View.VISIBLE);
                                            recyclerView.setVisibility(View.GONE);
                                        } else {
                                            recyclerView.setNoMore(true);
                                        }
                                    }
                                });
                            }
                        }
                        SharedPreferences preferences = mActivity.getSharedPreferences("myFragment", MODE_PRIVATE);
                        SharedPreferences.Editor edit = preferences.edit();
                        edit.putInt("loveAlbum", loveSize);
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

    private void setData(List<BaseAlbumLoveVo> list) {
        loveLinearAdapter = new AlbumLoveLinearAdapter(mActivity, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setAdapter(loveLinearAdapter);
        loveLinearAdapter.setOnItemClickListener(this);
        recyclerView.setLoadingListener(this);
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
    public void play(BottomBarVo bottomBarVo) {
        if (bottomBarVo != null) {
            MusicUtils.play(bottomBarVo.getPath(), mActivity, new MusicUtils.IMusicListener() {
                @Override
                public void success() {

                }

                @Override
                public void error(String msg) {
                    ToastUtils.showShort(msg);
                }
            });
        }
    }

    @Override
    public void onRefresh() {
        isLoad = false;
        isRefresh = true;
        currentPage = 1;
        getAlbumLove(false);
    }

    @Override
    public void onLoadMore() {
        isLoad = true;
        currentPage += 1;
        getAlbumLove(false);
    }
}
