package com.example.gy.musicgame.utils;

import com.blankj.utilcode.util.ToastUtils;

import java.util.Map;

/**
 * Description: SoloMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/12/6 23:55
 */
public class HandlerUtils {
    public static boolean isHandler(Map map) {
        if (map != null) {
            if (!map.containsKey("data")) {
                if (map.containsKey("errmsg")) {
                    String errmsg = (String) map.get("errmsg");
                    if (errmsg != null) {
                        ToastUtils.showShort(errmsg);
                    } else {
                        ToastUtils.showShort("数据异常");
                    }
                }
            } else {
                return false;
            }
        } else {
            ToastUtils.showShort("数据异常");
        }
        return true;
    }
}
