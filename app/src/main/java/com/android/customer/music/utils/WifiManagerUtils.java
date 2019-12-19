package com.android.customer.music.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.provider.Settings;

import com.android.customer.music.R;
import com.blankj.utilcode.util.ToastUtils;
import com.hb.dialog.dialog.ConfirmDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>文件描述：<p>
 * <p>作者：RLY<p>
 * <p>创建时间：2018/9/20<p>
 * <p>更改时间：2018/9/20<p>
 * <p>版本号：1<p>
 */
public class WifiManagerUtils {
    /**
     * 获取附近WiFi的信息
     *
     * @return
     */
    public static List<ScanResult> getWifiInfo(Activity activity, WifiManager wifiManager) {
        if (wifiManager == null) {
            wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        }
        startScan(activity, wifiManager);//开始扫描
        try {
            if (wifiManager != null) {
                return wifiManager.getScanResults();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private static void startScan(Activity activity, WifiManager wifiManager) {
        LocationManager locManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        assert locManager != null;
        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // 未打开位置开关，可能导致定位失败或定位不准，提示用户或做相应处理
            ConfirmDialog confirmDialog = new ConfirmDialog(activity);
            confirmDialog.setLogoImg(R.mipmap.logo).setMsg("扫描WiFi需要获取定位的权限，请授权打开");
            confirmDialog.setClickListener(new ConfirmDialog.OnBtnClickListener() {
                @Override
                public void ok() {
                    confirmDialog.dismiss();
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    activity.startActivityForResult(intent, 1315);
                }

                @Override
                public void cancel() {

                }
            });
            confirmDialog.setCancelable(false);
            confirmDialog.setCanceledOnTouchOutside(false);
            confirmDialog.show();
        } else {
            //开始扫描
            try {
                wifiManager.startScan();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
