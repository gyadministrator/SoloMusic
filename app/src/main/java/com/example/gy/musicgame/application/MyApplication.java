package com.example.gy.musicgame.application;

import android.app.Application;

import com.blankj.utilcode.util.Utils;
import com.example.gy.musicgame.constant.Constants;
import com.tencent.bugly.Bugly;

/**
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/10/31 8:52
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        Bugly.init(getApplicationContext(), Constants.BUGLY_APPID, false);
    }
}
