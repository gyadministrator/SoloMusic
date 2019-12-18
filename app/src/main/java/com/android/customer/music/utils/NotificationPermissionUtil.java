package com.android.customer.music.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

import com.android.customer.music.R;
import com.hb.dialog.dialog.ConfirmDialog;

/**
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/11/3 14:16
 */
public class NotificationPermissionUtil {

    public static boolean isNotificationEnabled(Context context) {
        boolean isOpened;
        try {
            isOpened = NotificationManagerCompat.from(context).areNotificationsEnabled();
        } catch (Exception e) {
            e.printStackTrace();
            isOpened = false;
        }
        return isOpened;

    }


    public static void checkNotificationEnable(final Context context) {
        if (!isNotificationEnabled(context)) {
            openPermission(context);
        }

    }

    public static void openPermission(final Context context) {
        ConfirmDialog confirmDialog = new ConfirmDialog(context);
        confirmDialog.setLogoImg(R.mipmap.logo).setMsg("通知权限没有开启," +
                "开启后才能在通知栏显示当前音乐播放的信息,你要开启吗?");
        confirmDialog.setClickListener(new ConfirmDialog.OnBtnClickListener() {
            @Override
            public void ok() {
                toSetting(context);
            }

            @Override
            public void cancel() {

            }
        });
        confirmDialog.setCancelable(false);
        confirmDialog.setCanceledOnTouchOutside(false);
        confirmDialog.show();
    }

    private static void toSetting(Context context) {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 26) {
            // android 8.0引导
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
        } else if (Build.VERSION.SDK_INT >= 21) {
            // android 5.0-7.0
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else {
            // 其他
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}