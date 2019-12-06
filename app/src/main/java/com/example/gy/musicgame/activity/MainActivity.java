package com.example.gy.musicgame.activity;

import android.graphics.Color;

import androidx.fragment.app.Fragment;

import com.example.gy.musicgame.R;
import com.example.gy.musicgame.fragment.FriendFragment;
import com.example.gy.musicgame.fragment.ListenFragment;
import com.example.gy.musicgame.fragment.MeFragment;
import com.example.gy.musicgame.fragment.RecordFragment;
import com.next.easynavigation.view.EasyNavigationBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private String[] tabText = {"听歌", "最近", "歌友", "我的"};
    //未选中icon
    private int[] normalIcon = {R.mipmap.listen, R.mipmap.record, R.mipmap.friend, R.mipmap.me};
    //选中时icon
    private int[] selectIcon = {R.mipmap.listen_pressed, R.mipmap.record_pressed, R.mipmap.friend_pressed, R.mipmap.me_pressed};

    private List<Fragment> fragments = new ArrayList<>();
    private EasyNavigationBar navigationBar;

    @Override
    protected void initView() {
        navigationBar = fd(R.id.navigationBar);
    }

    @Override
    protected void initData() {
        setSwipeBackEnable(false);
        fragments.add(ListenFragment.newInstance());
        fragments.add(RecordFragment.newInstance());
        fragments.add(FriendFragment.newInstance());
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

       /* navigationBar.setMsgPointCount(0, 12);
        navigationBar.setHintPoint(3, true);*/
    }

    @Override
    protected void initAction() {
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }
}
