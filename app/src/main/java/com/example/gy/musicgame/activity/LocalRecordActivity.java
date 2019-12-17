package com.example.gy.musicgame.activity;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.ToastUtils;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.adapter.LocalRecordLinearAdapter;
import com.example.gy.musicgame.dao.BottomBarDao;
import com.example.gy.musicgame.listener.OnItemClickListener;
import com.example.gy.musicgame.model.BottomBarVo;
import com.example.gy.musicgame.utils.MusicUtils;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.List;

public class LocalRecordActivity extends BaseActivity implements XRecyclerView.LoadingListener, OnItemClickListener {
    private SharedPreferences preferences;
    private LocalRecordLinearAdapter linearAdapter;
    private XRecyclerView recyclerView;
    private LinearLayout llNoData;

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
    }

    @Override
    protected void initAction() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getLocalRecord();
            }
        });
    }

    private void getLocalRecord() {
        BottomBarDao bottomBarDao = new BottomBarDao(mActivity);
        List<BottomBarVo> list = bottomBarDao.queryForAll();
        if (list == null || list.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            llNoData.setVisibility(View.VISIBLE);
        } else {
            linearAdapter = new LocalRecordLinearAdapter(mActivity, list);
            recyclerView.setAdapter(linearAdapter);
            linearAdapter.setOnItemClickListener(this);

            recyclerView.refreshComplete();
            SharedPreferences.Editor edit = preferences.edit();
            edit.putInt("localRecord", list.size());
            edit.apply();
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_local_record;
    }

    @Override
    public void onRefresh() {
        initAction();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void play(BottomBarVo bottomBarVo) {
        if (!TextUtils.isEmpty(bottomBarVo.getPath())) {
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
    }
}
