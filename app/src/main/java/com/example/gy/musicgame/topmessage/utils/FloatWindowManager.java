package com.example.gy.musicgame.topmessage.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.blankj.utilcode.util.ToastUtils;
import com.example.gy.musicgame.R;
import com.hb.dialog.dialog.ConfirmDialog;

/**
 * Description:
 *
 * @author gy
 */

public class FloatWindowManager {
    private static final String TAG = "FloatWindowManager";

    private static volatile FloatWindowManager instance;

    public static FloatWindowManager getInstance() {
        if (instance == null) {
            synchronized (FloatWindowManager.class) {
                if (instance == null) {
                    instance = new FloatWindowManager();
                }
            }
        }
        return instance;
    }

    public void applyOrShowFloatWindow(Context context) {
        if (!checkPermission(context)) {
            showConfirmDialog(context);
        }
    }

    private boolean checkPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //6.0 版本之后由于 google 增加了对悬浮窗权限的管理，所以方式就统一了
            return Settings.canDrawOverlays(context);
        } else if (RomUtils.checkIsMiuiRom()) {
            return MiuiUtils.checkFloatWindowPermission(context);
        } else if (RomUtils.checkIsMeizuRom()) {
            return MeizuUtils.checkFloatWindowPermission(context);
        } else if (RomUtils.checkIsHuaweiRom()) {
            return HuaweiUtils.checkFloatWindowPermission(context);
        } else if (RomUtils.checkIs360Rom()) {
            return QikuUtils.checkFloatWindowPermission(context);
        } else {
            return true;
        }
    }

    private void showConfirmDialog(final Context context) {
        ConfirmDialog confirmDialog = new ConfirmDialog(context);
        confirmDialog.setLogoImg(R.mipmap.logo).setMsg("您的手机没有授予悬浮窗权限，无法显示消息提醒，请授权");
        confirmDialog.setClickListener(new ConfirmDialog.OnBtnClickListener() {
            @Override
            public void ok() {
                confirmResult(context, true);
            }

            @Override
            public void cancel() {
                confirmResult(context, false);
            }
        });
        confirmDialog.setCancelable(false);
        confirmDialog.setCanceledOnTouchOutside(false);
        confirmDialog.show();
    }

    private void confirmResult(Context context, boolean bool) {
        if (!bool) {
            ToastUtils.showShort("你拒绝了悬浮窗弹出权限，消息将不能以弹出框形式给你提醒");
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        } else if (RomUtils.checkIsMiuiRom()) {
            MiuiUtils.applyMiuiPermission(context);
        } else if (RomUtils.checkIsMeizuRom()) {
            MeizuUtils.applyPermission(context);
        } else if (RomUtils.checkIsHuaweiRom()) {
            HuaweiUtils.applyPermission(context);
        } else if (RomUtils.checkIs360Rom()) {
            QikuUtils.applyPermission(context);
        }
    }

}
