package com.android.customer.music.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.android.customer.music.R;
import com.android.customer.music.model.NoticeVo;

public class NoticeDetailActivity extends BaseActivity {
    private NoticeVo noticeVo;
    private TextView tvTitle;
    private TextView tvTime;
    private TextView tvContent;
    private ImageView ivIcon;

    @Override
    protected void initView() {
        tvTitle = fd(R.id.tv_notice_title);
        tvTime = fd(R.id.tv_time);
        tvContent = fd(R.id.tv_content);
        ivIcon = fd(R.id.iv_icon);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("notice");
        if (bundle != null) {
            noticeVo = (NoticeVo) bundle.getSerializable("noticeVo");
        }
    }

    public static void startActivity(Activity activity, NoticeVo noticeVo) {
        Intent intent = new Intent(activity, NoticeDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("noticeVo", noticeVo);
        intent.putExtra("notice", bundle);
        activity.startActivity(intent);
    }

    @Override
    protected void initAction() {
        getNoticeDetail();
    }

    private void getNoticeDetail() {
        if (noticeVo == null) {
            ToastUtils.showShort("获取详情失败");
            return;
        }
        //请求数据
        tvTitle.setText(noticeVo.getTitle());
        tvTime.setText(noticeVo.getAddTime());
        tvContent.setText(noticeVo.getContent());
        if (!TextUtils.isEmpty(noticeVo.getImage())) {
            Glide.with(mActivity).load(noticeVo.getImage()).into(ivIcon);
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_notice_detail;
    }
}
