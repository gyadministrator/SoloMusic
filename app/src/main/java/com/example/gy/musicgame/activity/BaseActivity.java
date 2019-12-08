package com.example.gy.musicgame.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.utils.NetWorkUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
}
