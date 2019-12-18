package com.android.customer.music.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.android.customer.music.R;
import com.android.customer.music.adapter.MyPagerAdapter;
import com.android.customer.music.fragment.info.MessageFragment;
import com.android.customer.music.fragment.info.NoticeFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class InfoFragment extends Fragment {

    private InfoViewModel mViewModel;
    private Activity mActivity;
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
}
