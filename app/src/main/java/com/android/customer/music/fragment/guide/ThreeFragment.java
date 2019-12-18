package com.android.customer.music.fragment.guide;

import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.customer.music.R;
import com.android.customer.music.activity.SplashActivity;

public class ThreeFragment extends Fragment implements View.OnClickListener {

    private ThreeViewModel mViewModel;
    private ImageView ivIcon;
    private TextView tvSkip;
    private Activity mActivity;
    private SharedPreferences preferences;

    public static ThreeFragment newInstance() {
        return new ThreeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.three_fragment, container, false);
        initView(view);
        initData();
        initAction();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    private void initAction() {

    }

    private void initData() {
        preferences = mActivity.getSharedPreferences("guide", Context.MODE_PRIVATE);
    }

    private void initView(View view) {
        ivIcon = view.findViewById(R.id.iv_icon);
        tvSkip = view.findViewById(R.id.tv_skip);
        tvSkip.setOnClickListener(this);
    }

    public void setImage(int drawable) {
        if (ivIcon != null) {
            ivIcon.setImageResource(drawable);
        }
    }

    public void showSkip() {
        if (tvSkip != null) {
            tvSkip.setVisibility(View.VISIBLE);
        }
    }

    public void hideSkip() {
        if (tvSkip != null) {
            tvSkip.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ThreeViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onClick(View v) {
        SharedPreferences preferences = mActivity.getSharedPreferences("guide", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean("guide", false);
        edit.apply();
        startActivity(new Intent(mActivity, SplashActivity.class));
        mActivity.finish();
    }
}
