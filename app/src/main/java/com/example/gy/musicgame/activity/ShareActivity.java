package com.example.gy.musicgame.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import com.example.gy.musicgame.R;
import com.example.gy.musicgame.utils.ShareUtils;
import com.example.gy.musicgame.view.TitleView;
import com.yzq.zxinglibrary.encode.CodeCreator;

public class ShareActivity extends BaseActivity {
    private TitleView titleView;
    private ImageView ivCode;

    @Override
    protected void initView() {
        titleView = fd(R.id.titleView);
        ivCode = fd(R.id.iv_code);
    }

    @Override
    protected void initData() {
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.mipmap.logo);
        Bitmap bitmap = CodeCreator.createQRCode(getString(R.string.app_name), 400, 400, logo);
        ivCode.setImageBitmap(bitmap);
    }

    @Override
    protected void initAction() {
        titleView.setRightClickListener(new TitleView.OnRightClickListener() {
            @Override
            public void clickRight(View view) {
                ShareUtils.showShare(mActivity, getString(R.string.app_name), getString(R.string.download_tip), "");
            }

            @Override
            public void clickLeft(View view) {

            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_share;
    }
}
