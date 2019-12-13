package com.example.gy.musicgame.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/11/3 21:38
 */
public class SharedPreferenceUtil {
    /**
     * 存对象
     *
     * @param context context
     * @param name    存储名
     */
    public void saveObject(String json, Context context, String name) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(name, json);
        edit.apply();
    }

    /**
     * 取对象
     *
     * @param context context
     * @param name    存储名
     * @return
     */
    public String getObject(Context context, String name) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getString(name, "");
    }
}
