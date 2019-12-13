package com.example.gy.musicgame.activity;

import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.adapter.LocalMusicLinearAdapter;
import com.example.gy.musicgame.listener.OnItemClickListener;
import com.example.gy.musicgame.model.BottomBarVo;
import com.example.gy.musicgame.model.LocalMusicModel;
import com.example.gy.musicgame.utils.LocalMusicUtils;
import com.example.gy.musicgame.utils.MusicUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

public class LocalMusicActivity extends BaseActivity implements OnRefreshListener {
    private RecyclerView recyclerView;
    private LocalMusicLinearAdapter linerAdapter;
    private List<LocalMusicModel> music;
    private SmartRefreshLayout refreshLayout;

    @Override
    protected void initView() {
        recyclerView = fd(R.id.recyclerView);
        refreshLayout = fd(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initAction() {
        getLocalMusic();
    }

    private void getLocalMusic() {
        music = LocalMusicUtils.getMusic(mActivity);
        linerAdapter = new LocalMusicLinearAdapter(mActivity, music);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(linerAdapter);
        refreshLayout.finishRefresh(1500);
        if (music != null) {
            SharedPreferences preferences = getSharedPreferences("myFragment", MODE_PRIVATE);
            SharedPreferences.Editor edit = preferences.edit();
            edit.putInt("localMusicSize", music.size());
            edit.apply();
        }

        linerAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void play(BottomBarVo bottomBarVo) {
                MusicUtils.play(bottomBarVo.getPath(), mActivity, new MusicUtils.IMusicListener() {
                    @Override
                    public void success() {

                    }

                    @Override
                    public void error(String msg) {
                        ToastUtils.showShort(msg);
                    }
                });
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        Intent intent = new Intent();
        intent.setAction("mainMusic");
        sendBroadcast(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_local_music;
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getLocalMusic();
    }
}