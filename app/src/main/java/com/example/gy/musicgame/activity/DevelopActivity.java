package com.example.gy.musicgame.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.gy.musicgame.R;
import com.example.gy.musicgame.utils.ImgUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class DevelopActivity extends BaseActivity implements View.OnLongClickListener {
    private ImageView ivWeiXin;
    private ImageView ivAli;
    private Bitmap bitmap;
    private final String[] PERMISSIONS = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private final int REQUEST_CODE = 1001;
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
                showBottom();
                break;
        }
        return true;
    }

    private void showBottom() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.activity_save_code, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
        TextView tv_save = view.findViewById(R.id.tv_save);
        TextView tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                if (ActivityCompat.checkSelfPermission(DevelopActivity.this, PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(DevelopActivity.this, PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DevelopActivity.this, PERMISSIONS, REQUEST_CODE);
                } else {
                    ImgUtils.saveImageToGallery(DevelopActivity.this, bitmap, name);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            ImgUtils.saveImageToGallery(DevelopActivity.this, bitmap, name);
        }
    }
}
