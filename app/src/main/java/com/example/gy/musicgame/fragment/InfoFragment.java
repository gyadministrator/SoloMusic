package com.example.gy.musicgame.fragment;

import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gy.musicgame.R;
import com.example.gy.musicgame.adapter.MyPagerAdapter;
import com.example.gy.musicgame.constant.Constants;
import com.example.gy.musicgame.fragment.info.MessageFragment;
import com.example.gy.musicgame.fragment.info.NoticeFragment;
import com.example.gy.musicgame.model.BottomBarVo;
import com.example.gy.musicgame.utils.SharedPreferenceUtil;
import com.example.gy.musicgame.view.BottomBarView;
import com.example.gy.musicgame.view.TitleView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class InfoFragment extends Fragment {

    private InfoViewModel mViewModel;
    private Activity mActivity;
    private TitleView titleView;
    private BottomBarView bottomBarView;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    public static InfoFragment newInstance() {
        return new InfoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_fragment, container, false);
        initView(view);
        initData();
        initAction();
        return view;
    }

    private void initAction() {

    }

    private void initData() {
        List<Fragment> fragmentList = new ArrayList<>();
        List<String> list_Title = new ArrayList<>();
        fragmentList.add(NoticeFragment.newInstance());
        fragmentList.add(MessageFragment.newInstance());
        list_Title.add("通知");
        list_Title.add("消息");
        viewPager.setAdapter(new MyPagerAdapter(getFragmentManager(), mActivity, fragmentList, list_Title));
        tabLayout.setupWithViewPager(viewPager);//此方法就是让tabLayout和ViewPager联动
    }

    private void initView(View view) {
        titleView = view.findViewById(R.id.titleView);
        bottomBarView = view.findViewById(R.id.bottom_bar_view);
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(InfoViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setBottomBarData();
                }
            }, 1200);
        }
    }

    private void setBottomBarData() {
        SharedPreferenceUtil<BottomBarVo> preferenceUtil = new SharedPreferenceUtil<>();
        String json = preferenceUtil.getObjectJson(mActivity, Constants.CURRENT_BOTTOM_VO);
        Type type = new TypeToken<BottomBarVo>() {
        }.getType();
        BottomBarVo bottomBarVo = new Gson().fromJson(json, type);
        bottomBarView.setBottomBarVo(bottomBarVo);
    }
}
