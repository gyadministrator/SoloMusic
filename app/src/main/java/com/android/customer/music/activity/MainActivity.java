package com.android.customer.music.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.customer.music.R;
import com.android.customer.music.constant.Constants;
import com.android.customer.music.event.CustomEvent;
import com.android.customer.music.fragment.FriendFragment;
import com.android.customer.music.fragment.InfoFragment;
import com.android.customer.music.fragment.ListenFragment;
import com.android.customer.music.fragment.MeFragment;
import com.android.customer.music.fragment.RecipeFragment;
import com.android.customer.music.model.BottomBarVo;
import com.android.customer.music.model.NewFriendVo;
import com.android.customer.music.topmessage.utils.FloatWindowManager;
import com.android.customer.music.utils.NotificationPermissionUtil;
import com.android.customer.music.utils.SharedPreferenceUtil;
import com.android.customer.music.view.BottomBarView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.next.easynavigation.view.EasyNavigationBar;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import zhy.com.highlight.HighLight;
import zhy.com.highlight.interfaces.HighLightInterface;
import zhy.com.highlight.position.OnBottomPosCallback;
import zhy.com.highlight.shape.CircleLightShape;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class MainActivity extends BaseActivity {
    private static final int REQUEST_CODE = 1002;
    private String[] tabText = {"听歌", "歌友", "消息", "我的"};
    //未选中icon
    private int[] normalIcon = {R.mipmap.listen, R.mipmap.friend, R.mipmap.info, R.mipmap.me};
    //选中时icon
    private int[] selectIcon = {R.mipmap.listen_pressed, R.mipmap.friend_pressed, R.mipmap.info_pressed, R.mipmap.me_pressed};

    private List<Fragment> fragments = new ArrayList<>();
    private EasyNavigationBar navigationBar;
    private MyMusicReceiver musicReceiver;
    private BottomBarView bottomBarView;
    private FloatingActionButton floatBtn;
    private final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.CAMERA};
    private Animation showAnimation, hideAnimation;
    private SharedPreferences preferences;
    private HighLight mHighLight;

    @Override
    protected void initView() {
        navigationBar = fd(R.id.navigationBar);
        bottomBarView = fd(R.id.bottom_bar_view);
        floatBtn = fd(R.id.float_btn);
        showAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.show);
        hideAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.hide);
        floatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomBarView.getVisibility() == View.VISIBLE) {
                    bottomBarView.setVisibility(View.GONE);
                    bottomBarView.startAnimation(hideAnimation);
                    floatBtn.setImageResource(R.mipmap.show);
                } else if (bottomBarView.getVisibility() == View.GONE) {
                    bottomBarView.setVisibility(View.VISIBLE);
                    bottomBarView.startAnimation(showAnimation);
                    floatBtn.setImageResource(R.mipmap.hide);
                }
            }
        });
    }

    public BottomBarView getBottomBarView() {
        return bottomBarView;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void initData() {
        setSwipeBackEnable(false);
        fragments.add(ListenFragment.newInstance());
        fragments.add(FriendFragment.newInstance());
        fragments.add(InfoFragment.newInstance());
        fragments.add(MeFragment.newInstance());

        navigationBar.titleItems(tabText)
                .normalIconItems(normalIcon)
                .selectIconItems(selectIcon)
                .tabTextSize(14)
                .normalTextColor(Color.parseColor("#707070"))
                .selectTextColor(Color.parseColor("#d81e06"))
                .navigationBackground(Color.rgb(245, 245, 245))   //导航栏背景色
                .fragmentList(fragments)
                .fragmentManager(getSupportFragmentManager())
                .build();
        musicReceiver = new MyMusicReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("mainMusic");
        registerReceiver(musicReceiver, filter);
        getUnreadMsg();
        showTip();
    }

    @Override
    public void onEvent(Object object) {
        super.onEvent(object);
        if (object instanceof CustomEvent) {
            setBottomBarData();
        }
    }

    /**
     * 显示提示
     */
    private void showTip() {
        preferences = getPreferences(Context.MODE_PRIVATE);
        boolean isShowTip = preferences.getBoolean("isShowTip", true);
        if (isShowTip) {
            showNextTipViewOnCreated();
            SharedPreferences.Editor edit = preferences.edit();
            edit.putBoolean("isShowTip", false);
            edit.apply();
        }
    }

    /**
     * 当界面布局完成显示next模式提示布局
     * 显示方法必须在onLayout中调用
     * 适用于Activity及Fragment中使用
     * 可以直接在onCreated方法中调用
     */
    public void showNextTipViewOnCreated() {
        mHighLight = new HighLight(mActivity)
                .autoRemove(false)
                .enableNext()
                .setOnLayoutCallback(new HighLightInterface.OnLayoutCallback() {
                    @Override
                    public void onLayouted() {
                        //mAnchor界面布局完成添加tipView
                        mHighLight.addHighLight(R.id.float_btn, R.layout.btn_tip, new OnBottomPosCallback() {
                            @Override
                            public void getPosition(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo) {
                                marginInfo.rightMargin = rectF.width() - 2 * rectF.right / 7;
                                marginInfo.bottomMargin = bottomMargin + rectF.height();
                            }
                        }, new CircleLightShape(0, 0, 20));
                        //然后显示高亮布局
                        mHighLight.show();
                    }
                })
                .setClickCallback(new HighLight.OnClickCallback() {
                    @Override
                    public void onClick() {
                        mHighLight.next();
                    }
                });
    }

    private void getUnreadMsg() {
        int unreadMessageCount = EMClient.getInstance().chatManager().getUnreadMessageCount();
        if (unreadMessageCount > 0) {
            setMsgPoint(2, unreadMessageCount);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void checkPermission() {
        NotificationPermissionUtil.checkNotificationEnable(mActivity);
        FloatWindowManager.getInstance().applyOrShowFloatWindow(this);
    }

    public void play(BottomBarVo bottomBarVo) {
        if (bottomBarView != null) {
            bottomBarView.play(bottomBarVo);
        }
    }

    private void requestPermission() {
        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(mActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity, PERMISSIONS, REQUEST_CODE);
            }
        }
    }

    @Override
    protected void musicStop() {
        super.musicStop();
        bottomBarView.close();
    }

    private void setBottomBarData() {
        SharedPreferenceUtil preferenceUtil = new SharedPreferenceUtil();
        String json = preferenceUtil.getObject(mActivity, Constants.CURRENT_BOTTOM_VO);
        Type type = new TypeToken<BottomBarVo>() {
        }.getType();
        BottomBarVo bottomBarVo = new Gson().fromJson(json, type);
        bottomBarView.setBottomBarVo(bottomBarVo);
    }

    /**
     * 设置消息
     *
     * @param position tab位置
     * @param number   数量
     */
    public void setMsgPoint(int position, int number) {
        if (navigationBar != null) {
            navigationBar.setMsgPointCount(position, number);
        }
    }

    public void clearMsgPoint(int position) {
        if (navigationBar != null) {
            navigationBar.clearMsgPoint(position);
        }
    }

    public void clearHintPoint(int position) {
        if (navigationBar != null) {
            navigationBar.clearHintPoint(position);
        }
    }

    /**
     * 设置消息圆点
     *
     * @param position tab位置
     */
    public void setHintPoint(int position) {
        if (navigationBar != null) {
            navigationBar.setHintPoint(position, true);
        }
    }

    @Override
    protected void acceptApply() {
        super.acceptApply();
        List<NewFriendVo> newFriendVoList = getNewFriendVoList();
        if (newFriendVoList != null && newFriendVoList.size() > 0) {
            setMsgPoint(1, newFriendVoList.size());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (musicReceiver != null) {
            unregisterReceiver(musicReceiver);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 获取到Activity下的Fragment
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        // 查找在Fragment中onRequestPermissionsResult方法并调用
        for (Fragment fragment : fragments) {
            if (fragment != null) {
                // 这里就会调用我们Fragment中的onRequestPermissionsResult方法
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void initAction() {
        requestPermission();
        //检测通知栏权限
        checkPermission();
        setBottomBarData();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    private class MyMusicReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                if ("mainMusic".equals(action)) {
                    setBottomBarData();
                }
            }
        }
    }
}
