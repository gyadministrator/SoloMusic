package com.example.gy.musicgame.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;

import com.blankj.utilcode.util.ToastUtils;

import java.io.IOException;


/**
 * Created by Administrator on 2017/9/14.
 */

public class MusicUtils {
    private static MediaPlayer mediaPlayer = null;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private static MyCountDownTimer myCountDownTimer;

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public interface IMusicListener {
        void success();

        void error(String msg);
    }

    /**
     * 播放音乐
     *
     * @param url 音乐地址
     */
    public static void play(final String url, final Context context, final IMusicListener musicListener) {
        if (url == null || url.equals("")) {
            ToastUtils.showShort("该歌曲暂时无法获取播放源");
            return;
        }
        if (musicListener == null) return;
        mContext = context;
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    musicListener.success();
                    mediaPlayer.start();
                    int duration = mediaPlayer.getDuration();
                    myCountDownTimer = new MyCountDownTimer(duration, 1000);
                    myCountDownTimer.start();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.stop();
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    musicListener.error("获取音乐失败");
                    return true;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 继续播放
     */
    public static void playContinue() {
        mediaPlayer.start();
        myCountDownTimer = new MyCountDownTimer(mediaPlayer.getCurrentPosition(), 1000);
        myCountDownTimer.start();
    }

    /**
     * 暂停
     */
    public static void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            myCountDownTimer.cancel();
        }
    }

    /**
     * 停止
     */
    public static void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            myCountDownTimer.cancel();
            myCountDownTimer = null;
        }
    }

    /**
     * 销毁音乐媒体
     */
    public static void destoryMedia() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            myCountDownTimer.cancel();
            myCountDownTimer = null;
        }
    }

    /**
     * 获取播放状态
     *
     * @return
     */
    public static boolean isPlaying() {
        if (mediaPlayer == null) return false;
        return mediaPlayer.isPlaying();
    }

    private static class MyCountDownTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            if (mContext != null) {
                Intent intent = new Intent();
                intent.setAction("music_stop");
                mContext.sendBroadcast(intent);
            }
        }
    }
}
