package com.android.customer.music.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.customer.music.R;
import com.android.customer.music.adapter.WifiAdapter;
import com.android.customer.music.utils.WifiManagerUtils;

import java.util.List;

public class WifiActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    public static List<ScanResult> scanResults = null;
    private ListView listView;
    private WifiAdapter adapter;
    private WifiBroadcastReceiver wifiBroadcastReceiver;
    private WifiManager wifiManager;
    private boolean isShow = false;

    private void requestPermission() {
        //动态获取定位权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);
        } else {
            //申请的权限成功
            getScanResult();
            setAdapter();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            scanResults = WifiManagerUtils.getWifiInfo(mActivity, wifiManager);
            setAdapter();
        } else if (requestCode == 1315) {
            requestPermission();
        }
    }


    @Override
    protected void initView() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        listView = fd(R.id.listView);
        //监听wifi强度变化
        wifiBroadcastReceiver = new WifiBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiBroadcastReceiver, filter);
        requestPermission();
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_wifi;
    }

    private void getScanResult() {
        scanResults = WifiManagerUtils.getWifiInfo(mActivity, wifiManager);
        setAdapter();
    }

    /**
     * 设置listView视图
     */
    private void setAdapter() {
        adapter = new WifiAdapter(scanResults, mActivity);
        listView.setAdapter(adapter);
        @SuppressLint("InflateParams") View footer = LayoutInflater.from(mActivity).inflate(R.layout.activity_net_footer, null);
        LinearLayout lin = footer.findViewById(R.id.footer_lin);
        lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, JoinNetworkActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        int footerViewsCount = listView.getFooterViewsCount();
        if (footerViewsCount == 0) {
            listView.addFooterView(footer);
        }
        //listView设置点击事件
        listView.setOnItemClickListener(this);
    }

    /**
     * 释放资源
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wifiBroadcastReceiver != null) {
            unregisterReceiver(wifiBroadcastReceiver);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position >= scanResults.size()) return;
        final ScanResult scanResult = scanResults.get(position);
        String ssid = scanResult.SSID;
        String capabilities = scanResult.capabilities;
        if (capabilities.contains("WPA") || capabilities.contains("wpa") || capabilities.contains("WEP") || capabilities.contains("wep")) {
            //有密码
            Intent intent = new Intent(mActivity, ConnectWifiActivity.class);
            intent.putExtra("ssid", ssid);
            mActivity.startActivityForResult(intent, 0);
        }
    }


    private void resetScan() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestPermission();
            }
        }, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isShow) {
            resetScan();
            isShow = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isShow = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isShow = true;
    }

    private class WifiBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (WifiManager.RSSI_CHANGED_ACTION.equals(action)
                    || WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)
                    || WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                // 可以使用 获取wifi状态代码 相关代码
                refreshWifiIconState();
            }
        }
    }

    private void refreshWifiIconState() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
