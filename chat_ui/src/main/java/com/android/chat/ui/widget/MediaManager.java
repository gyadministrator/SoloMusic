package com.android.chat.ui.widget;


import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MediaManager {
    private static MediaPlayer mPlayer;
    private static boolean isPause;
    private static boolean isStart = false;
    private static String filepathstrings ;
    public static void playSound(Context context ,String filePathString,
                                 OnCompletionListener onCompletionListener) {//
        if (mPlayer == null) {
            mPlayer = getMediaPlayer(context);
            //保险起见，设置报错监听
            mPlayer.setOnErrorListener(new OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mPlayer.reset();
                    return false;
                }
            });
        } else {
            mPlayer.reset();//就恢复
        }
        try {
            filepathstrings = filePathString ;
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setOnCompletionListener(onCompletionListener);
            mPlayer.setDataSource(filePathString);
            mPlayer.setVolume(90,90);
            mPlayer.setLooping(false);
            mPlayer.prepare();
            mPlayer.start();
            isStart = true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //停止函数
    public static void pause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            isPause = true;
        }
    }


    //停止函数
    public static void reset() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.reset();

        }
    }

    //停止函数
    public static boolean isStart() {
        return mPlayer != null && mPlayer.isPlaying();
    }

    //继续
    public static void resume() {
        if (mPlayer != null && isPause) {
            mPlayer.start();
            isPause = false;
        }
    }


    public static void release() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
    private static MediaPlayer getMediaPlayer(Context context) {
        MediaPlayer mediaplayer = new MediaPlayer();
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
            return mediaplayer;
        }
        try {
            @SuppressLint("PrivateApi") Class<?> cMediaTimeProvider = Class.forName("android.media.MediaTimeProvider");
            @SuppressLint("PrivateApi") Class<?> cSubtitleController = Class.forName("android.media.SubtitleController");
            @SuppressLint("PrivateApi") Class<?> iSubtitleControllerAnchor = Class.forName("android.media.SubtitleController$Anchor");
            @SuppressLint("PrivateApi") Class<?> iSubtitleControllerListener = Class.forName("android.media.SubtitleController$Listener");
            Constructor constructor = cSubtitleController.getConstructor(
                    Context.class, cMediaTimeProvider, iSubtitleControllerListener);
            Object subtitleInstance = constructor.newInstance(context, null, null);
            Field f = cSubtitleController.getDeclaredField("mHandler");
            f.setAccessible(true);
            try {
                f.set(subtitleInstance, new Handler());
            } catch (IllegalAccessException e) {
                return mediaplayer;
            } finally {
                f.setAccessible(false);
            }
            Method setsubtitleanchor = mediaplayer.getClass().getMethod("setSubtitleAnchor",
                    cSubtitleController, iSubtitleControllerAnchor);
            setsubtitleanchor.invoke(mediaplayer, subtitleInstance, null);
        } catch (Exception e) {
            Log.d("mediaManager","getMediaPlayer crash ,exception = "+e);
        }
        return mediaplayer;
    }
}
