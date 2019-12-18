package com.android.customer.music.utils;

import android.content.Context;

import com.android.customer.music.constant.Constants;
import com.android.customer.music.model.UserInfoVo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Description: SoloMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/12/7 18:52
 */
public class UserManager {
    public static void setUserInfoVo(String json, Context context) {
        SharedPreferenceUtil preferenceUtil = new SharedPreferenceUtil();
        preferenceUtil.saveObject(json, context, Constants.CURRENT_USER_INFO);
    }

    public static UserInfoVo getUserInfoVo(Context context) {
        SharedPreferenceUtil preferenceUtil = new SharedPreferenceUtil();
        String json = preferenceUtil.getObject(context, Constants.CURRENT_USER_INFO);
        Type type = new TypeToken<UserInfoVo>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }
}
