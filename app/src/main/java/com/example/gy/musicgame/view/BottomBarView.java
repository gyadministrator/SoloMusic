package com.example.gy.musicgame.view;

import android.app.Activity;
import android.content.Context;
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

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.activity.LrcActivity;
import com.example.gy.musicgame.constant.Constants;
import com.example.gy.musicgame.model.BottomBarVo;
import com.example.gy.musicgame.utils.MusicUtils;
import com.example.gy.musicgame.utils.SharedPreferenceUtil;

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
    private String path;
    private int currentPosition = -1;
    private List<BottomBarVo> list;

    public void setBottomBarVo(BottomBarVo bottomBarVo) {
        this.bottomBarVo = bottomBarVo;
        initData(bottomBarVo);
    }

    public void setList(List<BottomBarVo> list) {
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

        animation = AnimationUtils.loadAnimation(context, R.anim.play_music_anim);
        initEvent();
    }

    public void play(final BottomBarVo bottomBarVo) {
        SharedPreferenceUtil<BottomBarVo> preferenceUtil = new SharedPreferenceUtil<>();
        preferenceUtil.saveObject(bottomBarVo, mContext, Constants.CURRENT_BOTTOM_VO);
        MusicUtils.play(bottomBarVo.getPath(), mContext, new MusicUtils.IMusicListener() {
            @Override
            public void success() {
                ivIcon.startAnimation(animation);
                ivPlay.setImageResource(R.mipmap.stop);
                initData(bottomBarVo);
            }

            @Override
            public void error(String msg) {

            }
        });
    }

    private void initEvent() {
        llBottomBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MusicUtils.getMediaPlayer() == null) return;
                LrcActivity.startActivity((Activity) mContext, bottomBarVo.getName(), bottomBarVo.getSongId(), bottomBarVo.getImage(), bottomBarVo.getTingUid());
            }
        });

        ivPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomBarVo != null && !MusicUtils.isPlaying()) {
                    play(bottomBarVo);
                    return;
                }
                if (list == null) return;
                if (list.size() > 1 && currentPosition == list.size()) currentPosition = 0;
                if (TextUtils.isEmpty(path) || MusicUtils.getMediaPlayer() == null) return;
                if (MusicUtils.isPlaying()) {
                    MusicUtils.pause();
                    ivPlay.setImageResource(R.mipmap.play);
                    close();
                } else {
                    MusicUtils.playContinue();
                    ivPlay.setImageResource(R.mipmap.stop);
                    ivIcon.startAnimation(animation);
                }
            }
        });

        ivNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomBarVo == null) return;
                if (list == null || list.size() == 0) {
                    ToastUtils.showShort("当前是最后一曲了！");
                    return;
                }
                next();
            }
        });
    }

    private void next() {
        currentPosition += 1;
        BottomBarVo bottomBarVo = list.get(currentPosition);
        if (bottomBarVo != null) {
            Glide.with(mContext).load(bottomBarVo.getImage()).into(ivIcon);
            ivIcon.startAnimation(animation);

            tvName.setText(bottomBarVo.getName());
            tvAuthor.setText(bottomBarVo.getAuthor());

            play(bottomBarVo);
        }
    }

    private void initData(BottomBarVo bottomBarVo) {
        this.bottomBarVo = bottomBarVo;
        if (bottomBarVo != null) {
            path = bottomBarVo.getPath();
            if (!TextUtils.isEmpty(bottomBarVo.getImage())) {
                Glide.with(mContext).load(bottomBarVo.getImage()).into(ivIcon);
                if (MusicUtils.isPlaying()) {
                    ivIcon.startAnimation(animation);
                    ivPlay.setImageResource(R.mipmap.stop);
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
}
