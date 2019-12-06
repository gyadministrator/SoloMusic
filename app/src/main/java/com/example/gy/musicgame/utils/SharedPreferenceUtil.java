package com.example.gy.musicgame.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/11/3 21:38
 */
public class SharedPreferenceUtil {
    /**
     * 序列化对象
     *
     * @param object
     * @return
     * @throws IOException
     */
    private static String serialize(Object object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
        String serStr = byteArrayOutputStream.toString("ISO-8859-1");
        serStr = java.net.URLEncoder.encode(serStr, "UTF-8");
        objectOutputStream.close();
        byteArrayOutputStream.close();
        return serStr;
    }

    /**
     * 反序列化对象
     *
     * @param str
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static Object deSerialization(String str) throws IOException, ClassNotFoundException {
        String redStr = java.net.URLDecoder.decode(str, "UTF-8");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(redStr.getBytes("ISO-8859-1"));
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Object person = objectInputStream.readObject();
        objectInputStream.close();
        byteArrayInputStream.close();
        return person;
    }

    /**
     * 存对象
     *
     * @param object  对象
     * @param context context
     * @param name    存储名
     */
    public static void saveObject(Object object, Context context, String name) throws IOException {
        String strObject = serialize(object);
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(name, strObject);
        edit.apply();
    }

    /**
     * 取对象
     *
     * @param context context
     * @param name    存储名
     * @return
     */
    public static Object getObject(Context context, String name) throws IOException, ClassNotFoundException {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return deSerialization(sp.getString(name, ""));
    }
}
