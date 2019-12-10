package com.example.gy.musicgame.fragment;

import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gy.musicgame.R;
import com.example.gy.musicgame.constant.Constants;
import com.example.gy.musicgame.model.BottomBarVo;
import com.example.gy.musicgame.utils.SharedPreferenceUtil;
import com.example.gy.musicgame.view.BottomBarView;
import com.example.gy.musicgame.view.TitleView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class InfoFragment extends Fragment {

    private InfoViewModel mViewModel;
    private Activity mActivity;
    private TitleView titleView;
    private BottomBarView bottomBarView;

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

    }

    private void initView(View view) {
        titleView= view.findViewById(R.id.titleView);
        bottomBarView= view.findViewById(R.id.bottom_bar_view);
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
