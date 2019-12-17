package com.example.gy.musicgame.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.adapter.AlbumItemAdapter;
import com.example.gy.musicgame.api.Api;
import com.example.gy.musicgame.constant.Constants;
import com.example.gy.musicgame.helper.DialogHelper;
import com.example.gy.musicgame.helper.LoadingDialogHelper;
import com.example.gy.musicgame.helper.RetrofitHelper;
import com.example.gy.musicgame.listener.SheetDialogListener;
import com.example.gy.musicgame.model.AlbumVo;
import com.example.gy.musicgame.model.LoveAlbumVo;
import com.example.gy.musicgame.model.LrcModel;
import com.example.gy.musicgame.model.SingerInfoModel;
import com.example.gy.musicgame.model.UserAlbumVo;
import com.example.gy.musicgame.utils.HandlerUtils;
import com.example.gy.musicgame.utils.MusicUtils;
import com.example.gy.musicgame.utils.NotificationPermissionUtil;
import com.example.gy.musicgame.utils.NotificationUtils;
import com.example.gy.musicgame.utils.SharedPreferenceUtil;
import com.example.gy.musicgame.utils.Utility;
import com.example.gy.musicgame.view.ILrcBuilder;
import com.example.gy.musicgame.view.ILrcViewListener;
import com.example.gy.musicgame.view.TitleView;
import com.example.gy.musicgame.view.impl.DefaultLrcBuilder;
import com.example.gy.musicgame.view.impl.LrcRow;
import com.example.gy.musicgame.view.impl.LrcView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.barlibrary.ImmersionBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class LrcActivity extends BaseActivity implements AdapterView.OnItemClickListener, OnLoadMoreListener {
    private String songId;
    private LrcView mLrcView;
    private String title;
    private String pic;
    private ImageView ivBg;
    private ImmersionBar immersionBar;
    private TitleView titleView;
    private String tingUid;
    private String url;
    //更新歌词的定时器
    private Timer mTimer;
    //更新歌词的定时任务
    private TimerTask mTask;
    private String author;
    private String path;
    private String token;
    private List<AlbumVo> albumVoList;
    private Integer currentPage = 1;
    private Integer PageSize = 20;
    private ListView listView;
    private AlbumItemAdapter itemAdapter;
    private BottomSheetDialog bottomSheetDialog;
    private boolean isLoad = false;
    private SmartRefreshLayout refreshLayout;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    @SuppressLint("SdCardPath")
    private static final String savePath = "/sdcard/music_game_download/";
    private int progress;
    private static final String saveFileName = savePath + UUID.randomUUID().toString().replaceAll("-", "");
    private boolean interceptFlag = false;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_OVER:
                    ToastUtils.showShort("下载成功！");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void initView() {
        mLrcView = fd(R.id.lrcView);
        ivBg = fd(R.id.iv_bg);
        titleView = fd(R.id.titleView);

        titleView.setRightClickListener(new TitleView.OnRightClickListener() {
            @Override
            public void clickRight(View view) {
                List<String> items = new ArrayList<>();
                items.add("查看歌手");
                items.add("收藏音乐");
                items.add("加入我喜欢");
                items.add("下载歌曲");
                DialogHelper.getInstance().showBottomDialog(mActivity, items, new SheetDialogListener() {
                    @Override
                    public void selectPosition(int position) {
                        if (position == 0) {
                            //查看歌手
                            findSingerInFo();
                        } else if (position == 1) {
                            //收藏音乐
                            collectMusic();
                        } else if (position == 2) {
                            //加入我喜欢
                            addAlbumLove();
                        } else if (position == 3) {
                            //下载歌曲
                            new Thread(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void run() {
                                    downloadMusic();
                                }
                            }).start();
                        }
                    }
                });
            }

            @Override
            public void clickLeft(View view) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void downloadMusic() {
        if (TextUtils.isEmpty(path)) {
            ToastUtils.showShort("下载链接不存在！");
            return;
        }
        try {
            URL url = new URL(path);

            HttpURLConnection conn = (HttpURLConnection) url
                    .openConnection();
            conn.connect();
            int length = conn.getContentLength();
            InputStream is = conn.getInputStream();
            File file = new File(savePath);
            if (!file.exists()) {
                file.mkdir();
            }
            File f = new File(saveFileName);
            FileOutputStream out = new FileOutputStream(f);

            int count = 0;
            byte buf[] = new byte[1024];
            do {
                int read = is.read(buf);
                count += read;
                progress = (int) (((float) count / length) * 100);
                // 更新进度
                if (NotificationPermissionUtil.isNotificationEnabled(mActivity)) {
                    NotificationUtils.sendMusicDownloadProgressCustomNotification(mActivity, progress);
                } else {
                    NotificationPermissionUtil.checkNotificationEnable(mActivity);
                }
                if (progress == 99) {
                    NotificationUtils.closeNotification(10);
                }
                if (read <= 0) {
                    // 下载完成通知
                    mHandler.sendEmptyMessage(DOWN_OVER);
                    break;
                }
                out.write(buf, 0, read);
            } while (!interceptFlag);// 点击取消就停止下载.

            out.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addAlbumLove() {
        LoveAlbumVo loveAlbumVo = new LoveAlbumVo();
        loveAlbumVo.setAuthor(author);
        loveAlbumVo.setImage(pic);
        loveAlbumVo.setPath(path);
        loveAlbumVo.setSongId(songId);
        loveAlbumVo.setTitle(title);
        loveAlbumVo.setTingUid(tingUid);
        LoadingDialogHelper.show(mActivity, "添加到我喜欢中...");
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.SERVER_URL);
        Observable<Map> observable = api.albumLoveAdd(token, loveAlbumVo);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Map>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Map map) {
                        HandlerUtils.isHandler(map, mActivity);
                        if (map.containsKey("errno")) {
                            double code = (double) map.get("errno");
                            if (code == 0) {
                                ToastUtils.showShort("添加成功");
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoadingDialogHelper.dismiss();
                        ToastUtils.showShort(Objects.requireNonNull(e.getMessage()));
                    }

                    @Override
                    public void onComplete() {
                        LoadingDialogHelper.dismiss();
                    }
                });
    }

    private void collectMusic() {
        bottomSheetDialog = new BottomSheetDialog(mActivity);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(mActivity).inflate(R.layout.bottom_album_list, null);
        ImageView ivClose = view.findViewById(R.id.iv_close);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        listView = view.findViewById(R.id.listView);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.show();
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
        refreshLayout.setOnLoadMoreListener(this);
        getAlbumList(token);
    }

    private void getAlbumList(String token) {
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.SERVER_URL);
        Observable<Map> observable = api.albumList(token, currentPage, PageSize);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Map>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onNext(Map map) {
                        refreshLayout.finishLoadMore(1500);
                        boolean handler = HandlerUtils.isHandler(map, mActivity);
                        if (!handler) {
                            Gson gson = new Gson();
                            String json = gson.toJson(map.get("data"));
                            Type type = new TypeToken<List<AlbumVo>>() {
                            }.getType();
                            albumVoList = gson.fromJson(json, type);
                            if (albumVoList != null && albumVoList.size() > 0) {
                                if (isLoad) {
                                    itemAdapter.addLoad(albumVoList);
                                } else {
                                    setAlbumData(albumVoList);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        refreshLayout.finishLoadMore(1500);
                        ToastUtils.showShort(Objects.requireNonNull(e.getMessage()));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void setAlbumData(List<AlbumVo> albumVoList) {
        itemAdapter = new AlbumItemAdapter(albumVoList, mActivity);
        listView.setAdapter(itemAdapter);
        //重新计算ListView的高度
        Utility.setListViewHeightBasedOnChildren(listView);
        listView.setOnItemClickListener(this);
    }


    private void findSingerInFo() {
        if (TextUtils.isEmpty(tingUid)) {
            ToastUtils.showShort("该歌手信息暂未找到");
        } else {
            WebActivity.startActivity(mActivity, url);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (immersionBar != null) {
            immersionBar.destroy();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        bottomSheetDialog.dismiss();
        AlbumVo albumVo = albumVoList.get(i);
        UserAlbumVo userAlbumVo = new UserAlbumVo();
        userAlbumVo.setAuthor(author);
        userAlbumVo.setImage(pic);
        userAlbumVo.setPath(path);
        userAlbumVo.setTingUid(tingUid);
        userAlbumVo.setSongId(songId);
        userAlbumVo.setTitle(title);
        userAlbumVo.setAlbumId(albumVo.getId());

        addUserAlbum(userAlbumVo);
    }

    private void addUserAlbum(UserAlbumVo userAlbumVo) {
        LoadingDialogHelper.show(mActivity, "添加到歌单中...");
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.SERVER_URL);
        Observable<Map> observable = api.albumUserAdd(token, userAlbumVo);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Map>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Map map) {
                        HandlerUtils.isHandler(map, mActivity);
                        if (map.containsKey("errno")) {
                            double code = (double) map.get("errno");
                            if (code == 0) {
                                ToastUtils.showShort("添加成功");
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoadingDialogHelper.dismiss();
                        ToastUtils.showShort(Objects.requireNonNull(e.getMessage()));
                    }

                    @Override
                    public void onComplete() {
                        LoadingDialogHelper.dismiss();
                    }
                });
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        isLoad = true;
        currentPage += 1;
        getAlbumList(token);
    }

    /**
     * 展示歌曲的定时任务
     */
    class LrcTask extends TimerTask {
        @Override
        public void run() {
            //获取歌曲播放的位置
            if (MusicUtils.getMediaPlayer() != null) {
                final long timePassed = MusicUtils.getMediaPlayer().getCurrentPosition();
                LrcActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        //滚动歌词
                        mLrcView.seekLrcToTime(timePassed);
                    }
                });
            }
        }
    }

    /**
     * 停止展示歌曲
     */
    public void stopLrcPlay() {
        if (mTimer != null) {
            mTimer.cancel();
            mTask.cancel();
            mTask = null;
            mTimer = null;
        }
    }


    private void initLrc() {
        if (mTimer == null) {
            mTimer = new Timer();
            mTask = new LrcTask();
            //更新歌词的频率，每秒更新一次
            int mP1ayTimerDuration = 1000;
            mTimer.scheduleAtFixedRate(mTask, 0, mP1ayTimerDuration);
        }

        if (MusicUtils.getMediaPlayer() == null) return;

        //歌曲播放完毕监听
        MusicUtils.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                stopLrcPlay();
            }
        });

        //设置自定义的LrcView上下拖动歌词时监听
        mLrcView.setListener(new ILrcViewListener() {
            //当歌词被用户上下拖动的时候回调该方法,从高亮的那一句歌词开始播放
            public void onLrcSeeked(int newPosition, LrcRow row) {
                MusicUtils.getMediaPlayer().seekTo((int) row.time);
            }
        });
    }

    @Override
    protected void initData() {
        immersionBar = ImmersionBar.with(mActivity);
        immersionBar.statusBarDarkFont(true, 0.2f)
                .statusBarColor(R.color.transparent)
                .navigationBarColor(R.color.splash_top_color)
                .init();
        Intent intent = getIntent();
        songId = intent.getStringExtra("songId");
        title = intent.getStringExtra("title");
        pic = intent.getStringExtra("pic");
        tingUid = intent.getStringExtra("tingUid");
        path = intent.getStringExtra("path");
        author = intent.getStringExtra("author");
        titleView.setTitle(title);

        SharedPreferenceUtil preferenceUtil = new SharedPreferenceUtil();
        token = preferenceUtil.getObject(mActivity, Constants.CURRENT_TOKEN);

        Glide.with(this).load(pic)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 10)))
                .into(ivBg);
    }

    public static void startActivity(Activity activity, String title, String songId, String pic, String tingUid, String author, String path) {
        Intent intent = new Intent(activity, LrcActivity.class);
        intent.putExtra("songId", songId);
        intent.putExtra("title", title);
        intent.putExtra("pic", pic);
        intent.putExtra("tingUid", tingUid);
        intent.putExtra("path", path);
        intent.putExtra("author", author);
        activity.startActivity(intent);
    }

    @Override
    protected void initAction() {
        getLrc(songId);
        if (!TextUtils.isEmpty(tingUid)) {
            getSingerInfo();
        }
    }

    private void getSingerInfo() {
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.BASE_URL);
        Map<String, Object> params = retrofitHelper.getmParams();
        params.put("method", Constants.METHOD_SINGER);
        params.put("tinguid", tingUid);
        Observable<SingerInfoModel> observable = api.getInfo(params);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SingerInfoModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(SingerInfoModel singerInfoModel) {
                        if (singerInfoModel != null) {
                            url = singerInfoModel.getUrl();
                        }
                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showShort(Objects.requireNonNull(e.getMessage()));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getLrc(String songId) {
        LoadingDialogHelper.show(mActivity, "获取歌词中...");
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.BASE_URL);
        Map<String, Object> params = retrofitHelper.getmParams();
        params.put("method", Constants.METHOD_LRC);
        params.put("songid", songId);
        Observable<LrcModel> observable = api.lrc(params);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LrcModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LrcModel lrcModel) {
                        setLrc(lrcModel);
                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onError(Throwable e) {
                        LoadingDialogHelper.dismiss();
                        ToastUtils.showShort(Objects.requireNonNull(e.getMessage()));
                    }

                    @Override
                    public void onComplete() {
                        LoadingDialogHelper.dismiss();
                    }
                });
    }

    private void setLrc(LrcModel lrcModel) {
        if (lrcModel != null && !TextUtils.isEmpty(lrcModel.getLrcContent())) {
            //解析歌词构造器
            ILrcBuilder builder = new DefaultLrcBuilder();
            //解析歌词返回LrcRow集合
            List<LrcRow> rows = builder.getLrcRows(lrcModel.getLrcContent());
            //将得到的歌词集合传给mLrcView用来展示
            mLrcView.setLrc(rows);

            initLrc();
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_lrc;
    }
}
