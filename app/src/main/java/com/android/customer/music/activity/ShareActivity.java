package com.android.customer.music.activity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.blankj.utilcode.util.ToastUtils;
import com.android.customer.music.R;
import com.android.customer.music.helper.DialogHelper;
import com.android.customer.music.listener.SheetDialogListener;
import com.android.customer.music.utils.ImgUtils;
import com.android.customer.music.utils.ShareUtils;
import com.android.customer.music.view.TitleView;
import com.yzq.zxinglibrary.encode.CodeCreator;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class ShareActivity extends BaseActivity implements View.OnLongClickListener {
    private TitleView titleView;
    private ImageView ivCode;
    private Bitmap bitmap;
    private String name = "solo_download";
    private final String[] PERMISSIONS = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void initView() {
        titleView = fd(R.id.titleView);
        ivCode = fd(R.id.iv_code);
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
                        ActivityCompat.requestPermissions(mActivity, PERMISSIONS, 10010);
                    } else {
                        ImgUtils.saveImageToGallery(mActivity, bitmap, name);
                    }
                }
            }
        });
    }


    @Override
    protected void initData() {
        SharedPreferences preferences = mActivity.getSharedPreferences("apk", Context.MODE_PRIVATE);
        String downloadUrl = preferences.getString("downloadUrl", "");
        if (TextUtils.isEmpty(downloadUrl)) {
            ToastUtils.showShort("获取APP下载链接失败！");
        }
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.mipmap.logo);
        Bitmap bitmap = CodeCreator.createQRCode(downloadUrl, 400, 400, logo);
        ivCode.setImageBitmap(bitmap);

        ivCode.setOnLongClickListener(this);
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

    @Override
    public boolean onLongClick(View view) {
        bitmap = ((BitmapDrawable) ivCode.getDrawable()).getBitmap();
        showBottom();
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10010) {
            ImgUtils.saveImageToGallery(mActivity, bitmap, name);
        }
    }
}
