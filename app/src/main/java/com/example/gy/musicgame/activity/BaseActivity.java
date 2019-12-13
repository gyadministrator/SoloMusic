package com.example.gy.musicgame.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.blankj.utilcode.util.CleanUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.model.NewFriendVo;
import com.example.gy.musicgame.utils.LogUtils;
import com.example.gy.musicgame.utils.NetWorkUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/10/30 14:21
 */
public abstract class BaseActivity extends SwipeBackActivity {
    protected Activity mActivity;
    private NetworkChangedReceiver networkChangedReceiver;
    private long start;
    private boolean isNet = true;
    private MyMusicReceiver musicReceiver;
    private String acceptUserName;
    private String acceptReason;
    private FriendReceiver friendReceiver;
    private List<NewFriendVo> newFriendVoList = new ArrayList<>();

    /**
     * 初始化布局
     */
    protected abstract void initView();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 初始化操作
     */
    protected abstract void initAction();

    /**
     * 获取布局
     *
     * @return
     */
    protected abstract int getContentView();

    /**
     * 音乐暂停
     */
    protected void musicStop() {
    }

    /**
     * 获取控件的值
     *
     * @param id  id
     * @param <T> 控件
     * @return
     */
    protected <T extends View> T fd(@IdRes int id) {
        return (T) findViewById(id);
    }

    //处理eventBus事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Object object) {

    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                NewFriendVo newFriendVo = new NewFriendVo();
                newFriendVo.setTitle(acceptUserName + "请求添加你为好友");
                newFriendVo.setReason(acceptReason);
                newFriendVo.setUsername(acceptUserName);
                newFriendVoList.add(newFriendVo);

                acceptApply();
            }
        }
    };

    public List<NewFriendVo> getNewFriendVoList() {
        return newFriendVoList;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        if (!this.getClass().getSimpleName().contains("LauncherActivity")) {
            setTheme(R.style.AppTheme);
        }
        if (getContentView() != 0) {
            setContentView(getContentView());
        }
        //初始化控件
        initView();
        //初始化数据
        initData();
        //初始化操作
        initAction();

        //注册网络广播
        networkChangedReceiver = new NetworkChangedReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangedReceiver, intentFilter);
        //注册eventBus
        EventBus.getDefault().register(this);

        musicReceiver = new MyMusicReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("music_stop");
        registerReceiver(musicReceiver, filter);

        //注册一个监听连接状态的listener
        EMClient.getInstance().addConnectionListener(new MyConnectionListener());
        registerReceiver();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, 1001);
    }

    private void registerReceiver() {
        friendReceiver = new FriendReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("invited");
        filter.addAction("accepted");
        filter.addAction("declined");
        filter.addAction("deleted");
        filter.addAction("added");
        registerReceiver(friendReceiver, filter);
    }

    /**
     * 有网络
     */
    protected void hasNet() {
        isNet = false;
    }

    /**
     * 无网络
     */
    protected void noNet() {
        isNet = true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!this.getClass().getSimpleName().contains("MainActivity")) {
                onBackPressed();
                return true;
            }
            if (System.currentTimeMillis() - start > 2000) {
                ToastUtils.showShort("再次点击返回到桌面");
                start = System.currentTimeMillis();
            } else {
                //回到桌面
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkChangedReceiver != null) {
            unregisterReceiver(networkChangedReceiver);
        }
        if (musicReceiver != null) {
            unregisterReceiver(musicReceiver);
        }
        //解绑eventBus
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {
        super.startActivity(intent, options);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private class NetworkChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int netWorkStates = NetWorkUtils.getNetWorkStates(context);

            switch (netWorkStates) {
                case NetWorkUtils.TYPE_NONE:
                    //断网了
                    ToastUtils.showShort("当前没有网络连接");
                    noNet();
                    break;
                case NetWorkUtils.TYPE_MOBILE:
                case NetWorkUtils.TYPE_WIFI:
                    //打开了WIFI
                    //打开了移动网络
                    if (!isNet) {
                        hasNet();
                    }
                    break;
            }
        }
    }


    private class MyMusicReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                if ("music_stop".equals(action)) {
                    musicStop();
                }
            }
        }
    }

    protected void acceptApply() {

    }

    private class FriendReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtils.d("onReceive", "onReceive: " + action);
            String msg = intent.getStringExtra("msg");
            String username = intent.getStringExtra("username");
            String reason = intent.getStringExtra("reason");
            if (action != null) {
                switch (action) {
                    case "invited":
                        if (!TextUtils.isEmpty(username)) {
                            acceptUserName = username;
                        }
                        if (!TextUtils.isEmpty(reason)) {
                            acceptReason = reason;
                        }
                        mHandler.sendEmptyMessage(0);
                        break;
                    case "accepted":
                    case "declined":
                    case "deleted":
                    case "added":
                        if (!TextUtils.isEmpty(msg)) {
                            showStatusMsg(msg);
                        }
                        break;
                }
            }
        }
    }

    //实现ConnectionListener接口
    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
        }

        @Override
        public void onDisconnected(final int error) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        // 显示帐号已经被移除
                        showStatusMsg("帐号已经被移除");
                        logout();
                    } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                        // 显示帐号在其他设备登录
                        showStatusMsg("帐号在其他设备登录");
                        logout();
                    }
                }
            });
        }
    }

    private void logout() {
        EMClient.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                //登出成功
                CleanUtils.cleanInternalSp();
                CleanUtils.cleanInternalFiles();
                CleanUtils.cleanInternalDbs();
                CleanUtils.cleanInternalCache();
                CleanUtils.cleanExternalCache();
                LoginActivity.startActivity(mActivity);
            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub
                ToastUtils.showShort("退出失败：code" + code + "错误信息：" + message);
            }
        });
    }

    private void showStatusMsg(String msg) {
        ToastUtils.showShort(msg);
    }
}
