package com.android.customer.music.utils;

import android.util.Log;

import com.android.customer.music.constant.Constants;

/**
 * Description: SoloMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/12/6 23:36
 */
public class LogUtils {
    public static void d(String tag, String msg) {
        if (Constants.isDebug) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (Constants.isDebug) {
            Log.i(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (Constants.isDebug) {
            Log.e(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (Constants.isDebug) {
            Log.w(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (Constants.isDebug) {
            Log.v(tag, msg);
        }
    }
}
