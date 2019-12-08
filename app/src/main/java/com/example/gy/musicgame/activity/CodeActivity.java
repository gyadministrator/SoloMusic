package com.example.gy.musicgame.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.gy.musicgame.R;
import com.example.gy.musicgame.utils.ImgUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.yzq.zxinglibrary.encode.CodeCreator;

public class CodeActivity extends BaseActivity implements View.OnLongClickListener {
    private ImageView ivCode;
    private Bitmap bitmap;
    private final String[] PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private final int REQUEST_CODE = 1001;
    private String name = "code";

    @Override
    protected void initView() {
        ivCode = fd(R.id.iv_code);
    }

    @Override
    protected void initData() {
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.mipmap.logo);
        bitmap = CodeCreator.createQRCode(getString(R.string.app_name), 400, 400, logo);
        ivCode.setImageBitmap(bitmap);
        ivCode.setOnLongClickListener(this);
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_code;
    }

    @Override
    public boolean onLongClick(View view) {
        //长按保存图片
        showBottom();
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
                if (ActivityCompat.checkSelfPermission(CodeActivity.this, PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(CodeActivity.this, PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CodeActivity.this, PERMISSIONS, REQUEST_CODE);
                } else {
                    ImgUtils.saveImageToGallery(CodeActivity.this, bitmap, name);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            ImgUtils.saveImageToGallery(CodeActivity.this, bitmap, name);
        }
    }
}
