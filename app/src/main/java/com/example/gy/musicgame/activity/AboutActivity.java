package com.example.gy.musicgame.activity;

import android.annotation.SuppressLint;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.example.gy.musicgame.R;

public class AboutActivity extends BaseActivity {
    private TextView version;

    @Override
    protected void initView() {
        version = fd(R.id.version);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        version.setText("当前版本：V" + AppUtils.getAppVersionName());
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_about;
    }
}
