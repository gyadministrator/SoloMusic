package com.example.gy.musicgame.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Build;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.api.Api;
import com.example.gy.musicgame.constant.Constants;
import com.example.gy.musicgame.helper.LoadingDialogHelper;
import com.example.gy.musicgame.helper.MediaPlayerHelper;
import com.example.gy.musicgame.helper.RetrofitHelper;
import com.example.gy.musicgame.model.LrcModel;
import com.example.gy.musicgame.view.ILrcBuilder;
import com.example.gy.musicgame.view.ILrcViewListener;
import com.example.gy.musicgame.view.TitleView;
import com.example.gy.musicgame.view.impl.DefaultLrcBuilder;
import com.example.gy.musicgame.view.impl.LrcRow;
import com.example.gy.musicgame.view.impl.LrcView;
import com.gyf.barlibrary.ImmersionBar;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class LrcActivity extends BaseActivity {
    private String songId;
    private LrcView mLrcView;
    private String title;
    private String pic;
    private ImageView ivBg;
    private ImmersionBar immersionBar;
    private TitleView titleView;
    private MediaPlayerHelper mediaPlayerHelper;
    //更新歌词的定时器
    private Timer mTimer;
    //更新歌词的定时任务
    private TimerTask mTask;

    @Override
    protected void initView() {
        mLrcView = fd(R.id.lrcView);
        ivBg = fd(R.id.iv_bg);
        titleView = fd(R.id.titleView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (immersionBar != null) {
            immersionBar.destroy();
        }
    }

    /**
     * 展示歌曲的定时任务
     */
    class LrcTask extends TimerTask {
        @Override
        public void run() {
            //获取歌曲播放的位置
            if (mediaPlayerHelper != null) {
                final long timePassed = mediaPlayerHelper.getmMediaPlayer().getCurrentPosition();
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

        //歌曲播放完毕监听
        mediaPlayerHelper.getmMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                stopLrcPlay();
            }
        });

        //设置自定义的LrcView上下拖动歌词时监听
        mLrcView.setListener(new ILrcViewListener() {
            //当歌词被用户上下拖动的时候回调该方法,从高亮的那一句歌词开始播放
            public void onLrcSeeked(int newPosition, LrcRow row) {
                mediaPlayerHelper.getmMediaPlayer().seekTo((int) row.time);
            }
        });
    }

    @Override
    protected void initData() {
        mediaPlayerHelper = MediaPlayerHelper.getInstance(mActivity);
        immersionBar = ImmersionBar.with(mActivity);
        immersionBar.statusBarDarkFont(true, 0.2f)
                .statusBarColor(R.color.transparent)
                .navigationBarColor(R.color.splash_top_color)
                .init();
        Intent intent = getIntent();
        songId = intent.getStringExtra("songId");
        title = intent.getStringExtra("title");
        pic = intent.getStringExtra("pic");
        titleView.setTitle(title);

        Glide.with(this).load(pic)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 10)))
                .into(ivBg);
    }

    public static void startActivity(Activity activity, String title, String songId, String pic) {
        Intent intent = new Intent(activity, LrcActivity.class);
        intent.putExtra("songId", songId);
        intent.putExtra("title", title);
        intent.putExtra("pic", pic);
        activity.startActivity(intent);
    }

    @Override
    protected void initAction() {
        getLrc(songId);
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
