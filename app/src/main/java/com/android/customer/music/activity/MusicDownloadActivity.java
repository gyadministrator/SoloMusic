package com.android.customer.music.activity;

import android.content.Intent;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.ToastUtils;
import com.android.customer.music.R;
import com.android.customer.music.adapter.DownloadMusicLinearAdapter;
import com.android.customer.music.constant.Constants;
import com.android.customer.music.dao.BottomBarDao;
import com.android.customer.music.listener.OnItemClickListener;
import com.android.customer.music.model.BottomBarVo;
import com.android.customer.music.model.LocalMusicModel;
import com.android.customer.music.utils.LocalMusicUtils;
import com.android.customer.music.utils.MusicUtils;
import com.android.customer.music.utils.SharedPreferenceUtil;
import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.List;

public class MusicDownloadActivity extends BaseActivity implements XRecyclerView.LoadingListener {
    private XRecyclerView recyclerView;
    private LinearLayout llNoData;
    private DownloadMusicLinearAdapter linerAdapter;
    private List<LocalMusicModel> music;

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
        music = LocalMusicUtils.getMusic(mActivity);
        linerAdapter = new DownloadMusicLinearAdapter(mActivity, music);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setAdapter(linerAdapter);
        recyclerView.refreshComplete();

        linerAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void play(BottomBarVo bottomBarVo) {
                MusicUtils.play(bottomBarVo.getPath(), mActivity, new MusicUtils.IMusicListener() {
                    @Override
                    public void success() {
                        SharedPreferenceUtil preferenceUtil = new SharedPreferenceUtil();
                        String json = new Gson().toJson(bottomBarVo);
                        preferenceUtil.saveObject(json, mActivity, Constants.CURRENT_BOTTOM_VO);
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

    @Override
    public void finish() {
        super.finish();
        Intent intent = new Intent();
        intent.setAction("mainMusic");
        sendBroadcast(intent);
    }
}
