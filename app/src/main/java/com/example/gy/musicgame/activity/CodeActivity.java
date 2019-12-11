package com.example.gy.musicgame.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.gy.musicgame.R;
import com.example.gy.musicgame.helper.DialogHelper;
import com.example.gy.musicgame.listener.SheetDialogListener;
import com.example.gy.musicgame.utils.ImgUtils;
import com.yzq.zxinglibrary.encode.CodeCreator;

import java.util.ArrayList;
import java.util.List;

public class CodeActivity extends BaseActivity implements View.OnLongClickListener {
    private ImageView ivCode;
    private Bitmap bitmap;
    @SuppressLint("InlinedApi")
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
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            ImgUtils.saveImageToGallery(CodeActivity.this, bitmap, name);
        }
    }
}
