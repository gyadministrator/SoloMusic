package com.example.gy.musicgame.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.gy.musicgame.R;
import com.example.gy.musicgame.helper.DialogHelper;
import com.example.gy.musicgame.listener.SheetDialogListener;
import com.example.gy.musicgame.utils.ImgUtils;

import java.util.ArrayList;
import java.util.List;

public class DevelopActivity extends BaseActivity implements View.OnLongClickListener {
    private ImageView ivWeiXin;
    private ImageView ivAli;
    private Bitmap bitmap;
    private final String[] PERMISSIONS = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private final int REQUEST_CODE = 1006;
    private String name;

    @Override
    protected void initView() {
        ivWeiXin = fd(R.id.iv_weixin);
        ivAli = fd(R.id.iv_ali);

        ivWeiXin.setOnLongClickListener(this);
        ivAli.setOnLongClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initAction() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_develop;
    }

    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.iv_ali:
                bitmap = ((BitmapDrawable) ivAli.getDrawable()).getBitmap();
                name = "weixin";
                showBottom();
                break;
            case R.id.iv_weixin:
                bitmap = ((BitmapDrawable) ivWeiXin.getDrawable()).getBitmap();
                name = "ali";
                //长按保存图片
                showBottom();
                break;
        }
        return true;
    }

    private void showBottom() {
        List<String> items = new ArrayList<>();
        items.add("保存到相册");
        DialogHelper.getInstance().showBottomDialog(mActivity, items, new SheetDialogListener() {
            @Override
            public void selectPosition(int position) {
                if (position == 0) {
                    if (ActivityCompat.checkSelfPermission(mActivity, PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(mActivity, PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(mActivity, PERMISSIONS, REQUEST_CODE);
                    } else {
                        ImgUtils.saveImageToGallery(mActivity, bitmap, name);
                    }
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            ImgUtils.saveImageToGallery(mActivity, bitmap, name);
        }
    }
}
