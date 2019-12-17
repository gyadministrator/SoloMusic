package com.example.gy.musicgame.activity;

import android.content.SharedPreferences;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.gy.musicgame.R;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

public class MusicDownloadActivity extends BaseActivity implements XRecyclerView.LoadingListener {
    private XRecyclerView recyclerView;
    private LinearLayout llNoData;
    private SharedPreferences preferences;

    @Override
    protected void initView() {
        recyclerView = fd(R.id.rv_linear);
        llNoData = fd(R.id.ll_no_data);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        recyclerView.setLoadingMoreEnabled(false);
        recyclerView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        recyclerView.setLoadingListener(this);
    }

    @Override
    protected void initData() {
        preferences = mActivity.getSharedPreferences("myFragment", MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putInt("myDownload", 0);
        edit.apply();
    }

    @Override
    protected void initAction() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getDownload();
            }
        });
    }

    private void getDownload() {
        recyclerView.refreshComplete();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_music_download;
    }

    @Override
    public void onRefresh() {
        initAction();
    }

    @Override
    public void onLoadMore() {

    }
}
