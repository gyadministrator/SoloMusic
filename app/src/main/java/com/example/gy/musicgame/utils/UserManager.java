package com.example.gy.musicgame.utils;

import android.content.Context;

import com.example.gy.musicgame.constant.Constants;
import com.example.gy.musicgame.model.UserInfoVo;

/**
 * Description: SoloMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/12/7 18:52
 */
public class UserManager {
    public static void setUserInfoVo(UserInfoVo userInfoVo, Context context) {
        SharedPreferenceUtil<UserInfoVo> preferenceUtil = new SharedPreferenceUtil<>();
        preferenceUtil.saveObject(userInfoVo, context, Constants.CURRENT_USER_INFO);
    }

    public static UserInfoVo getUserInfoVo(Context context) {
        SharedPreferenceUtil<UserInfoVo> preferenceUtil = new SharedPreferenceUtil<>();
        return preferenceUtil.getObject(context, Constants.CURRENT_USER_INFO);
    }
}
