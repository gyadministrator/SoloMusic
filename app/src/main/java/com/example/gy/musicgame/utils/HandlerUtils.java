package com.example.gy.musicgame.utils;

import android.app.Activity;

import com.blankj.utilcode.util.ToastUtils;
import com.example.gy.musicgame.activity.LoginActivity;

import java.util.Map;

/**
 * Description: SoloMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/12/6 23:55
 */
public class HandlerUtils {
    public static boolean isHandler(Map map, Activity activity) {
        if (map != null) {
            if (map.containsKey("errno")) {
                double code = (double) map.get("errno");
                if (code == 501) {
                    //登录失效
                    goLogin(activity);
                    return true;
                }
            }
            if (!map.containsKey("data")) {
                if (map.containsKey("errmsg") && map.containsKey("errno")) {
                    String errmsg = (String) map.get("errmsg");
                    double code = (double) map.get("errno");
                    if (errmsg != null && code != 0) {
                        ToastUtils.showShort(errmsg);
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            ToastUtils.showShort("数据异常");
            return true;
        }
    }

    private static void goLogin(Activity activity) {
        LoginActivity.startActivity(activity);
    }
}
