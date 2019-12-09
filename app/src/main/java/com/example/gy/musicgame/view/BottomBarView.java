package com.example.gy.musicgame.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.helper.MediaPlayerHelper;
import com.example.gy.musicgame.model.BottomBarVo;
import com.example.gy.musicgame.model.MusicVo;
import com.example.gy.musicgame.service.MusicService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Date:2019/12/9
 * TIME:10:48
 * author:fldserver
 * email:1984629668@qq.com
 **/
public class BottomBarView extends LinearLayout {
    private CircleImageView ivIcon;
    private TextView tvName;
    private TextView tvAuthor;
    private ImageView ivPlay;
    private ImageView ivNext;
    private BottomBarVo bottomBarVo;
    private Context mContext;
    private Animation animation;
    private LinearLayout llBottomBar;
    private MediaPlayerHelper mediaPlayerHelper;
    private String path;
    private List<MusicVo> list = new ArrayList<>();
    private int currentPosition = -1;
    private Intent mServiceIntent;
    private MusicService.MusicBind mMusicBind;
    private boolean isBindService;
    private MusicVo mMusic;

    public void setBottomBarVo(BottomBarVo bottomBarVo) {
        this.bottomBarVo = bottomBarVo;
        initData(bottomBarVo);
    }

    public void setList(List<MusicVo> list) {
        this.list = list;
    }

    public BottomBarView(Context context) {
        this(context, null);
    }

    public BottomBarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        View mView = LayoutInflater.from(context).inflate(R.layout.bottom_bar, this);
        ivIcon = mView.findViewById(R.id.iv_icon);
        ivPlay = mView.findViewById(R.id.iv_play);
        ivNext = mView.findViewById(R.id.iv_next);
        tvName = mView.findViewById(R.id.tv_name);
        tvAuthor = mView.findViewById(R.id.tv_author);
        llBottomBar = mView.findViewById(R.id.ll_bottom_bar);

        mediaPlayerHelper = MediaPlayerHelper.getInstance(context);
        animation = AnimationUtils.loadAnimation(context, R.anim.play_music_anim);
        initEvent();
        EventBus.getDefault().register(this);
    }

    //处理eventBus事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Object object) {
        if (object instanceof MusicVo) {
            final MusicVo musicVo = (MusicVo) object;
            this.bottomBarVo = new BottomBarVo();
            bottomBarVo.setAuthor(musicVo.getAuthor());
            bottomBarVo.setImage(musicVo.getImageUrl());
            bottomBarVo.setName(musicVo.getTitle());
            bottomBarVo.setPath(musicVo.getPath());
            mediaPlayerHelper.setPath(bottomBarVo.getPath());

            mediaPlayerHelper.setOnMediaPlayerHelperListener(new MediaPlayerHelper.OnMediaPlayerHelperListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    ivIcon.startAnimation(animation);
                    ivPlay.setImageResource(R.mipmap.stop);
                    initData(bottomBarVo);
                    mMusic = musicVo;
                    startMusicService();
                }

                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {

                }
            });
        }
    }

    /**
     * 启动音乐服务
     */
    private void startMusicService() {
        if (mServiceIntent == null) {
            mServiceIntent = new Intent(mContext, MusicService.class);
            mContext.startService(mServiceIntent);
        } else {
            mMusicBind.playMusic();
        }
        //绑定service,当前service未绑定，绑定服务
        if (!isBindService) {
            isBindService = true;
            mContext.bindService(mServiceIntent, conn, Context.BIND_AUTO_CREATE);
        }
    }

    ServiceConnection conn = new ServiceConnection() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mMusicBind = (MusicService.MusicBind) iBinder;
            mMusicBind.setMusic(mMusic);
            mMusicBind.playMusic();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    private void initEvent() {
        llBottomBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ivPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPosition += 1;
                if (list.size() > 1 && currentPosition == list.size()) currentPosition = 0;
                if (TextUtils.isEmpty(path)) return;
                if (mediaPlayerHelper.isPlaying()) {
                    mediaPlayerHelper.pause();
                    ivPlay.setImageResource(R.mipmap.play);
                    close();
                } else {
                    mediaPlayerHelper.setPath(path);
                    ivPlay.setImageResource(R.mipmap.stop);
                    ivIcon.startAnimation(animation);
                }
            }
        });

        ivNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list == null || list.size() == 0) {
                    ToastUtils.showShort("当前是最后一曲了！");
                    return;
                }
                next();
            }
        });
    }

    private void next() {
        if (currentPosition == -1) currentPosition = 0;
        MusicVo musicVo = list.get(currentPosition);
        if (musicVo != null) {
            Glide.with(mContext).load(musicVo.getImageUrl()).into(ivIcon);
            ivIcon.startAnimation(animation);

            tvName.setText(musicVo.getTitle());
            tvAuthor.setText(musicVo.getAuthor());
            mediaPlayerHelper.setPath(musicVo.getPath());
        }
    }

    private void initData(BottomBarVo bottomBarVo) {
        if (bottomBarVo != null) {
            path = bottomBarVo.getPath();
            if (!TextUtils.isEmpty(bottomBarVo.getImage())) {
                Glide.with(mContext).load(bottomBarVo.getImage()).into(ivIcon);
                if (mediaPlayerHelper.isPlaying()) {
                    ivIcon.startAnimation(animation);
                }
            }
            if (!TextUtils.isEmpty(bottomBarVo.getName())) {
                tvName.setText(bottomBarVo.getName());
            }
            if (!TextUtils.isEmpty(bottomBarVo.getAuthor())) {
                tvAuthor.setText(bottomBarVo.getAuthor());
            }
        }
    }

    /**
     * 清除动画
     */
    public void close() {
        if (animation != null) {
            ivIcon.clearAnimation();
        }
    }

    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (isBindService) {
            isBindService = false;
            mContext.unbindService(conn);
        }
    }
}
