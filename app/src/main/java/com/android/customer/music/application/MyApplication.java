package com.android.customer.music.application;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;

import androidx.multidex.MultiDex;

import com.android.customer.music.activity.LoginActivity;
import com.android.customer.music.utils.GenerateUserSig;
import com.blankj.utilcode.util.Utils;
import com.android.customer.music.constant.Constants;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.tencent.bugly.Bugly;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMGroupEventListener;
import com.tencent.imsdk.TIMGroupTipsElem;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMRefreshListener;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserStatusListener;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.config.CustomFaceConfig;
import com.tencent.qcloud.tim.uikit.config.GeneralConfig;
import com.tencent.qcloud.tim.uikit.config.TUIKitConfigs;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;

import java.util.List;

/**
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/10/31 8:52
 */
public class MyApplication extends Application {
    /**
     * 屏幕宽度
     */
    public static int screenWidth;
    /**
     * 屏幕高度
     */
    public static int screenHeight;
    /**
     * 屏幕密度
     */
    public static float screenDensity;

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化MultiDex
        MultiDex.install(this);
        Utils.init(this);
        Bugly.init(getApplicationContext(), Constants.BUG_APP_ID, Constants.isDebug);
        initIM();
        initScreenSize();
        if (Constants.isDebug) {
            initCrash();
        }

        initTXIM();
    }

    private void initTXIM() {
        // 配置 Config，请按需配置
        TUIKitConfigs configs = TUIKit.getConfigs();
        configs.setSdkConfig(new TIMSdkConfig(Constants.TX_IM_APP_ID));
        configs.setCustomFaceConfig(new CustomFaceConfig());
        configs.setGeneralConfig(new GeneralConfig());

        TUIKit.init(this, Constants.TX_IM_APP_ID, configs);

        //基本用户配置
        TIMUserConfig userConfig = new TIMUserConfig()
                //设置用户状态变更事件监听器
                .setUserStatusListener(new TIMUserStatusListener() {
                    @Override
                    public void onForceOffline() {
                        //被其他终端踢下线
                        ToastUtil.toastShortMessage("你的账户在其它设备上登录，请重新登录");
                        LoginActivity.startActivity((Activity) getApplicationContext());
                    }

                    @Override
                    public void onUserSigExpired() {
                        //用户签名过期了，需要刷新 userSig 重新登录 IM SDK
                        loginIM();
                    }
                })
                //设置连接状态事件监听器
                .setConnectionListener(new TIMConnListener() {
                    @Override
                    public void onConnected() {
                        ToastUtil.toastShortMessage("IM已连接");
                    }

                    @Override
                    public void onDisconnected(int code, String desc) {
                        ToastUtil.toastShortMessage("IM已经断开：" + code + " " + desc);
                    }

                    @Override
                    public void onWifiNeedAuth(String name) {
                        ToastUtil.toastShortMessage("onWifiNeedAuth：" + name);
                    }
                });

        //禁用本地所有存储
        //userConfig.disableStorage();
        //开启消息已读回执
        userConfig.enableReadReceipt(true);

        //将用户配置与通讯管理器进行绑定
        TIMManager.getInstance().setUserConfig(userConfig);
    }

    private void loginIM() {
        SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        //TODO 设置用户数据
        final String username = sharedPreferences.getString("username", null);
        String userSig = GenerateUserSig.genTestUserSig(username);
        TIMManager.getInstance().login(username, userSig, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                loginIM();
            }

            @Override
            public void onSuccess() {

            }
        });
    }

    /**
     * 初始化当前设备屏幕宽高
     */
    private void initScreenSize() {
        DisplayMetrics curMetrics = getApplicationContext().getResources().getDisplayMetrics();
        screenWidth = curMetrics.widthPixels;
        screenHeight = curMetrics.heightPixels;
        screenDensity = curMetrics.density;
    }


    private void initCrash() {
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }

    private void initIM() {
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        //关闭自动登录
        options.setAutoLogin(false);
        //初始化
        EMClient.getInstance().init(this, options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);
    }
}
