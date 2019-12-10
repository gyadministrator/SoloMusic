package com.example.gy.musicgame.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/11/3 21:38
 */
public class SharedPreferenceUtil<T> {
    /**
     * 存对象
     *
     * @param context context
     * @param name    存储名
     */
    public void saveObject(T t, Context context, String name) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(t);
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
    public T getObject(Context context, String name) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp.getString(name, "");
        Type type = new TypeToken<T>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public String getObjectJson(Context context, String name) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getString(name, "");
    }
}
