package com.android.customer.music.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
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
import com.android.customer.music.R;
import com.android.customer.music.activity.LrcActivity;
import com.android.customer.music.constant.Constants;
import com.android.customer.music.dao.BottomBarDao;
import com.android.customer.music.model.BottomBarVo;
import com.android.customer.music.utils.MusicUtils;
import com.android.customer.music.utils.NotificationPermissionUtil;
import com.android.customer.music.utils.NotificationUtils;
import com.android.customer.music.utils.SharedPreferenceUtil;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

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
    private BottomBarVo bottomBarVo;
    private Context mContext;
    private Animation playAnimation;
    private LinearLayout llBottomBar;
    private Bitmap bitmap;
    private LinearLayout llContent;


    public void setBottomBarVo(BottomBarVo bottomBarVo) {
        this.bottomBarVo = bottomBarVo;
        initData(bottomBarVo);
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
        tvName = mView.findViewById(R.id.tv_name);
        tvAuthor = mView.findViewById(R.id.tv_author);
        llBottomBar = mView.findViewById(R.id.ll_bottom_bar);
        llContent = mView.findViewById(R.id.ll_content);

        playAnimation = AnimationUtils.loadAnimation(context, R.anim.play_music_anim);
        initEvent();
    }

    public void play(final BottomBarVo bottomBarVo) {
        this.bottomBarVo=bottomBarVo;
        SharedPreferenceUtil preferenceUtil = new SharedPreferenceUtil();
        String json = new Gson().toJson(bottomBarVo);
        preferenceUtil.saveObject(json, mContext, Constants.CURRENT_BOTTOM_VO);
        MusicUtils.play(bottomBarVo.getPath(), mContext, new MusicUtils.IMusicListener() {
            @Override
            public void success() {
                SharedPreferenceUtil preferenceUtil = new SharedPreferenceUtil();
                String json = new Gson().toJson(bottomBarVo);
                preferenceUtil.saveObject(json, mContext, Constants.CURRENT_BOTTOM_VO);
                BottomBarDao bottomBarDao = new BottomBarDao(mContext);
                List<BottomBarVo> list = bottomBarDao.queryForSongId(bottomBarVo.getSongId());
                if (list == null || list.size() == 0) {
                    bottomBarDao.add(bottomBarVo);
                }
                ivIcon.startAnimation(playAnimation);
                ivPlay.setImageResource(R.mipmap.stop);
                if (!TextUtils.isEmpty(bottomBarVo.getName())) {
                    tvName.setText(bottomBarVo.getName());
                }
                if (!TextUtils.isEmpty(bottomBarVo.getAuthor())) {
                    tvAuthor.setText(bottomBarVo.getAuthor());
                }
                tvName.setTextColor(mContext.getResources().getColor(R.color.pressed));
                tvAuthor.setTextColor(mContext.getResources().getColor(R.color.pressed));
                //开启通知栏
                openNotice(bottomBarVo);
            }

            @Override
            public void error(String msg) {
                ToastUtils.showShort(msg);
            }
        });
    }

    private void openNotice(final BottomBarVo bottomBarVo) {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                if (bottomBarVo.getImage() != null) {
                    bitmap = returnBitmap(bottomBarVo.getImage());
                }
                if (NotificationPermissionUtil.isNotificationEnabled(mContext)) {
                    if (bitmap != null) {
                        NotificationUtils.sendCustomNotification(mContext, bottomBarVo, bitmap, R.mipmap.stop);
                    }
                } else {
                    NotificationPermissionUtil.openPermission(mContext);
                }
            }
        }).start();
    }

    /**
     * 根据图片的url路径获得Bitmap对象
     *
     * @param url url
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static Bitmap returnBitmap(String url) {
        URL fileUrl = null;
        Bitmap bitmap = null;

        try {
            fileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            HttpURLConnection conn = (HttpURLConnection) Objects.requireNonNull(fileUrl)
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;

    }

    private void initEvent() {
        llBottomBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MusicUtils.getMediaPlayer() == null) return;
                if (MusicUtils.isPlaying()) {
                    LrcActivity.startActivity((Activity) mContext, bottomBarVo.getName(), bottomBarVo.getSongId(), bottomBarVo.getImage(), bottomBarVo.getTingUid()
                            , bottomBarVo.getAuthor(), bottomBarVo.getPath());
                }
            }
        });

        ivPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomBarVo == null) return;
                if (MusicUtils.getMediaPlayer() == null) {
                    MusicUtils.play(bottomBarVo.getPath(), mContext, new MusicUtils.IMusicListener() {
                        @Override
                        public void success() {
                            SharedPreferenceUtil preferenceUtil = new SharedPreferenceUtil();
                            String json = new Gson().toJson(bottomBarVo);
                            preferenceUtil.saveObject(json, mContext, Constants.CURRENT_BOTTOM_VO);
                            BottomBarDao bottomBarDao = new BottomBarDao(mContext);
                            List<BottomBarVo> list = bottomBarDao.queryForSongId(bottomBarVo.getSongId());
                            if (list == null || list.size() == 0) {
                                bottomBarDao.add(bottomBarVo);
                            }
                            openNotice(bottomBarVo);
                        }

                        @Override
                        public void error(String msg) {
                            ToastUtils.showShort(msg);
                        }
                    });
                    ivIcon.startAnimation(playAnimation);
                    ivPlay.setImageResource(R.mipmap.stop);
                    tvName.setTextColor(mContext.getResources().getColor(R.color.pressed));
                    tvAuthor.setTextColor(mContext.getResources().getColor(R.color.pressed));
                } else {
                    if (MusicUtils.isPlaying()) {
                        MusicUtils.pause();
                        close();
                    } else {
                        MusicUtils.playContinue();
                        ivPlay.setImageResource(R.mipmap.stop);
                        ivIcon.startAnimation(playAnimation);
                        tvName.setTextColor(mContext.getResources().getColor(R.color.pressed));
                        tvAuthor.setTextColor(mContext.getResources().getColor(R.color.pressed));
                        openNotice(bottomBarVo);
                    }
                }
            }
        });
    }

    private void initData(BottomBarVo bottomBarVo) {
        if (bottomBarVo != null) {
            if (!TextUtils.isEmpty(bottomBarVo.getImage())) {
                Glide.with(mContext).load(bottomBarVo.getImage()).into(ivIcon);
                if (MusicUtils.isPlaying()) {
                    ivIcon.startAnimation(playAnimation);
                    ivPlay.setImageResource(R.mipmap.stop);
                    tvName.setTextColor(mContext.getResources().getColor(R.color.pressed));
                    tvAuthor.setTextColor(mContext.getResources().getColor(R.color.pressed));
                } else {
                    close();
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
        ivPlay.setImageResource(R.mipmap.play);
        tvName.setTextColor(mContext.getResources().getColor(R.color.normal));
        tvAuthor.setTextColor(mContext.getResources().getColor(R.color.normal));
        if (playAnimation != null) {
            ivIcon.clearAnimation();
        }
    }
}
