package com.android.customer.music.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.android.customer.music.R;
import com.android.customer.music.helper.DialogHelper;
import com.android.customer.music.listener.DialogListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendDetailActivity extends BaseActivity implements View.OnClickListener {
    private List<String> list = new ArrayList<>();
    private String userId;
    private TextView tvNickName;
    private TextView tvSex;
    private TextView tvBirthday;
    private TextView tvAddress;
    private TextView tvDelete;
    private CircleImageView ivUser;

    @Override
    protected void initView() {
        tvNickName = fd(R.id.tv_nick_name);
        tvSex = fd(R.id.tv_sex);
        tvBirthday = fd(R.id.tv_birthday);
        tvAddress = fd(R.id.tv_address);
        tvDelete = fd(R.id.tv_delete);
        ivUser = fd(R.id.iv_user);
        tvDelete.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        list.add(userId);
    }

    public static void startActivity(Activity activity, String userId) {
        Intent intent = new Intent(activity, FriendDetailActivity.class);
        intent.putExtra("userId", userId);
        activity.startActivity(intent);
    }

    @Override
    protected void initAction() {

    }


    @SuppressLint("SimpleDateFormat")
    private String millsToDateString(long l) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(l);
        return simpleDateFormat.format(date);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_friend_detail;
    }

    @Override
    public void onClick(View v) {
        DialogHelper.getInstance().showSureDialog(mActivity, "温馨提示", "你确定要删除该歌友吗？", new DialogListener() {
            @Override
            public void clickSure() {
            }

            @Override
            public void clickCancel() {

            }
        });
    }
}
