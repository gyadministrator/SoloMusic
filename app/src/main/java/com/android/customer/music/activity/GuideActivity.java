package com.android.customer.music.activity;

import android.view.View;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.customer.music.R;
import com.android.customer.music.adapter.ViewPagerAdapter;
import com.android.customer.music.fragment.guide.OneFragment;
import com.android.customer.music.fragment.guide.ThreeFragment;
import com.android.customer.music.fragment.guide.TwoFragment;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends BaseActivity {
    private List<View> listImg;

    @Override
    protected void initView() {
        //将屏幕设置为全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ViewPager viewPager = fd(R.id.viewpager);
        listImg = new ArrayList<>();
        listImg.add(fd(R.id.y1));
        listImg.add(fd(R.id.y2));
        listImg.add(fd(R.id.y3));
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), showView()));
        viewPager.setOnPageChangeListener(showPageChange);
    }

    ViewPager.OnPageChangeListener showPageChange = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < listImg.size(); i++) {
                if (i == arg0) {
                    listImg.get(arg0).setBackgroundResource(R.drawable.y_focused);
                } else {
                    listImg.get(i).setBackgroundResource(R.drawable.y_normal);
                }
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }
    };

    private List<Fragment> showView() {
        List<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i < listImg.size(); i++) {
            Fragment fragment;
            switch (i) {
                case 0:
                    fragment = OneFragment.newInstance();
                    break;
                case 1:
                    fragment = TwoFragment.newInstance();
                    break;
                case 2:
                    fragment = ThreeFragment.newInstance();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + i);
            }
            fragments.add(fragment);
        }
        return fragments;
    }

    @Override
    protected void initData() {
        setSwipeBackEnable(false);
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_guide;
    }
}
