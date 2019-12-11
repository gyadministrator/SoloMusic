package com.example.gy.musicgame.activity;

import android.app.Activity;
import android.content.Intent;

import com.blankj.utilcode.util.ToastUtils;
import com.example.gy.musicgame.R;

public class NoticeDetailActivity extends BaseActivity {
    private int noticeId;

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        noticeId = intent.getIntExtra("noticeId", 0);
    }

    public static void startActivity(Activity activity, int noticeId) {
        Intent intent = new Intent(activity, NoticeDetailActivity.class);
        intent.putExtra("noticeId", noticeId);
        activity.startActivity(intent);
    }

    @Override
    protected void initAction() {
        getNoticeDetail(noticeId);
    }

    private void getNoticeDetail(int noticeId) {
        if (noticeId == 0) {
            ToastUtils.showShort("获取详情失败");
            return;
        }
        //请求数据
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_notice_detail;
    }
}
