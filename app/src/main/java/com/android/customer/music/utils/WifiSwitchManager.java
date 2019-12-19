package com.android.customer.music.utils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

/**
 * **Create By RLY
 * **Time 14:53
 * **Description Provision
 **/
public class WifiSwitchManager {
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private static Receiver receiver;
    private static WifiSwitchListener mWifiSwitchListener;

    public interface WifiSwitchListener {
        int WIFI_STATE_ENABLING = 0;
        int WIFI_STATE_ENABLED = 1;
        int WIFI_STATE_DISABLING = 2;
        int WIFI_STATE_DISABLED = 3;
        int WIFI_STATE_UNKNOWN = 4;

        void wifiSwitchState(int state);
    }


    public WifiSwitchManager(Context context, WifiSwitchListener wifiSwitchListener) {
        mContext = context;
        mWifiSwitchListener = wifiSwitchListener;
        observeWifiSwitch();
    }

    private void observeWifiSwitch() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        receiver = new Receiver();
        mContext.registerReceiver(receiver, filter);
    }

    /**
     * 释放资源
     */
    public static void onDestroy() {
        if (receiver != null) {
            mContext.unregisterReceiver(receiver);
        }
        if (mContext != null) {
            mContext = null;
        }
    }

    private static class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLED:
                    if (mWifiSwitchListener != null) {
                        mWifiSwitchListener.wifiSwitchState(WifiSwitchListener.WIFI_STATE_DISABLED);
                    }
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    if (mWifiSwitchListener != null) {
                        mWifiSwitchListener.wifiSwitchState(WifiSwitchListener.WIFI_STATE_DISABLING);
                    }
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    if (mWifiSwitchListener != null) {
                        mWifiSwitchListener.wifiSwitchState(WifiSwitchListener.WIFI_STATE_ENABLED);
                    }
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    if (mWifiSwitchListener != null) {
                        mWifiSwitchListener.wifiSwitchState(WifiSwitchListener.WIFI_STATE_ENABLING);
                    }
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    if (mWifiSwitchListener != null) {
                        mWifiSwitchListener.wifiSwitchState(WifiSwitchListener.WIFI_STATE_UNKNOWN);
                    }
                    break;
            }
        }
    }
}
