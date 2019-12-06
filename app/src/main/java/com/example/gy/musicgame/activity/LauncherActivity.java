package com.example.gy.musicgame.activity;

import android.content.Intent;
import android.os.Handler;
import com.example.gy.musicgame.R;

public class LauncherActivity extends BaseActivity {

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initAction() {
        jumpActivity();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_launcher;
    }

    private void jumpActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(mActivity, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
