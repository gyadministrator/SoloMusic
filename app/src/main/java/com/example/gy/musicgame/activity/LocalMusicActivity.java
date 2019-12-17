package com.example.gy.musicgame.activity;

import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.ToastUtils;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.adapter.LocalMusicLinearAdapter;
import com.example.gy.musicgame.dao.BottomBarDao;
import com.example.gy.musicgame.listener.OnItemClickListener;
import com.example.gy.musicgame.model.BottomBarVo;
import com.example.gy.musicgame.model.LocalMusicModel;
import com.example.gy.musicgame.utils.LocalMusicUtils;
import com.example.gy.musicgame.utils.MusicUtils;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

public class LocalMusicActivity extends BaseActivity implements OnRefreshListener, XRecyclerView.LoadingListener {
    private XRecyclerView recyclerView;
    private LocalMusicLinearAdapter linerAdapter;
    private List<LocalMusicModel> music;

    @Override
    protected void initView() {
        recyclerView = fd(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        recyclerView.setLoadingMoreEnabled(false);
        recyclerView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        recyclerView.setLoadingListener(this);
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
        recyclerView.setAdapter(linerAdapter);
        recyclerView.refreshComplete();
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
                        BottomBarDao bottomBarDao = new BottomBarDao(mActivity);
                        List<BottomBarVo> list = bottomBarDao.queryForSongId(bottomBarVo.getSongId());
                        if (list == null || list.size() == 0) {
                            bottomBarDao.add(bottomBarVo);
                        }
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

    @Override
    public void onRefresh() {
        getLocalMusic();
    }

    @Override
    public void onLoadMore() {

    }
}
