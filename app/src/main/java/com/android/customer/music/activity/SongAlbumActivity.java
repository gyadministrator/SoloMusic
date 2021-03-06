package com.android.customer.music.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.ToastUtils;
import com.android.customer.music.R;
import com.android.customer.music.adapter.AlbumUserLinearAdapter;
import com.android.customer.music.api.Api;
import com.android.customer.music.constant.Constants;
import com.android.customer.music.dao.BottomBarDao;
import com.android.customer.music.event.CustomEvent;
import com.android.customer.music.helper.LoadingDialogHelper;
import com.android.customer.music.helper.RetrofitHelper;
import com.android.customer.music.listener.OnItemClickListener;
import com.android.customer.music.model.BaseAlbumUserVo;
import com.android.customer.music.model.BottomBarVo;
import com.android.customer.music.utils.HandlerUtils;
import com.android.customer.music.utils.MusicUtils;
import com.android.customer.music.utils.SharedPreferenceUtil;
import com.android.customer.music.view.TitleView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SongAlbumActivity extends BaseActivity implements XRecyclerView.LoadingListener, OnItemClickListener {
    private TitleView titleView;
    private XRecyclerView recyclerView;
    private AlbumUserLinearAdapter userLinearAdapter;
    private boolean isLoad = false;
    private String token;
    private Integer albumId;
    private Integer currentPage = 1;
    private Integer pageSize = 20;
    private String album;
    private boolean isRefresh = false;
    private List<BaseAlbumUserVo> list;
    private LinearLayout llNoData;

    @Override
    protected void initView() {
        titleView = fd(R.id.titleView);
        recyclerView = fd(R.id.rv_linear);
        llNoData = fd(R.id.ll_no_data);
        recyclerView.setLoadingListener(this);
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
                        boolean handler = HandlerUtils.isHandler(map, mActivity);
                        if (!handler) {
                            Gson gson = new Gson();
                            String json = gson.toJson(map.get("data"));
                            Type type = new TypeToken<List<BaseAlbumUserVo>>() {
                            }.getType();
                            list = gson.fromJson(json, type);
                            if (list != null && list.size() > 0) {
                                if (isLoad) {
                                    userLinearAdapter.addData(list);
                                    recyclerView.loadMoreComplete();
                                } else {
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

    private void setData(List<BaseAlbumUserVo> list) {
        userLinearAdapter = new AlbumUserLinearAdapter(mActivity, list);
        recyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        recyclerView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setAdapter(userLinearAdapter);
        userLinearAdapter.setOnItemClickListener(this);
        recyclerView.setLoadingListener(this);
    }


    @Override
    protected int getContentView() {
        return R.layout.activity_song_album;
    }


    @Override
    public void onRefresh() {
        isLoad = false;
        isRefresh = true;
        currentPage = 1;
        getAlbumUser(false);
    }

    @Override
    public void onLoadMore() {
        isLoad = true;
        currentPage += 1;
        getAlbumUser(false);
    }

    @Override
    public void play(BottomBarVo bottomBarVo) {
        if (bottomBarVo != null) {
            MusicUtils.play(bottomBarVo.getPath(), mActivity, new MusicUtils.IMusicListener() {
                @Override
                public void success() {
                    SharedPreferenceUtil preferenceUtil = new SharedPreferenceUtil();
                    String json = new Gson().toJson(bottomBarVo);
                    preferenceUtil.saveObject(json, mActivity, Constants.CURRENT_BOTTOM_VO);
                    BottomBarDao bottomBarDao = new BottomBarDao(mActivity);
                    List<BottomBarVo> list = bottomBarDao.queryForSongId(bottomBarVo.getSongId());
                    if (list == null || list.size() == 0) {
                        bottomBarDao.add(bottomBarVo);
                    }
                    EventBus.getDefault().post(new CustomEvent());
                }

                @Override
                public void error(String msg) {
                    ToastUtils.showShort(msg);
                }
            });
        }
    }
}
