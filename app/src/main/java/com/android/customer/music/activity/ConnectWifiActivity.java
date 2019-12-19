package com.android.customer.music.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.customer.music.R;
import com.blankj.utilcode.util.ToastUtils;

import java.util.List;

public class ConnectWifiActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivClose;
    private TextView tvTitle;
    private TextView tvConnect;
    private TextView wifi_name_tv;
    private EditText wifi_pwd;
    private String ssid = "";
    private static WifiManager wifiManager;
    private static final int WIFICIPHER_NOPASS = 0;
    private static final int WIFICIPHER_WEP = 1;
    private static final int WIFICIPHER_WPA = 2;

    @SuppressLint("SetTextI18n")
    private void setTopTitle() {
        tvTitle.setText("输入密码");
        Intent intent = getIntent();
        ssid = intent.getStringExtra("ssid");
        wifi_name_tv.setText("请输入“" + ssid + "”的密码");
    }

    @Override
    protected void initView() {
        ivClose = fd(R.id.iv_close);
        tvTitle = fd(R.id.tv_title);
        tvConnect = fd(R.id.tv_connect);
        wifi_name_tv = fd(R.id.wifi_name);
        wifi_pwd = fd(R.id.pwd_txt);
    }

    @Override
    protected void initData() {
        setTopTitle();
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    protected void initAction() {
        ivClose.setOnClickListener(this);
        tvConnect.setOnClickListener(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_connect_wifi;
    }

    public static WifiConfiguration createWifiConfig(String ssid, String password, int type, WifiManager wifiManager) {
        //初始化WifiConfiguration
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();

        //指定对应的SSID
        config.SSID = "\"" + ssid + "\"";

        //如果之前有类似的配置
        WifiConfiguration tempConfig = isExist(ssid, wifiManager);
        if (tempConfig != null) {
            //则清除旧有配置
            wifiManager.removeNetwork(tempConfig.networkId);
        }

        //不需要密码的场景
        if (type == WIFICIPHER_NOPASS) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            //以WEP加密的场景
        } else if (type == WIFICIPHER_WEP) {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
            //以WPA加密的场景，自己测试时，发现热点以WPA2建立时，同样可以用这种配置连接
        } else if (type == WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    public static WifiConfiguration isExist(String ssid, WifiManager wifiManager) {
        List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : configs) {
            if (config.SSID.equals("\"" + ssid + "\"")) {
                return config;
            }
        }
        return null;
    }

    // 打开wifi功能
    public static void openWifi(WifiManager wifiManager) {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 获取ssid的加密方式
     */
    public static int getCipherType(String ssid) {
        for (ScanResult scResult : WifiActivity.scanResults) {
            if (!TextUtils.isEmpty(scResult.SSID) && scResult.SSID.equals(ssid)) {
                String capabilities = scResult.capabilities;
                if (!TextUtils.isEmpty(capabilities)) {
                    if (capabilities.contains("WPA")
                            || capabilities.contains("wpa")) {
                        return WIFICIPHER_WPA;
                    } else if (capabilities.contains("WEP")
                            || capabilities.contains("wep")) {
                        return WIFICIPHER_WEP;
                    } else {
                        return WIFICIPHER_NOPASS;
                    }
                }
            }
        }
        return -1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                finish();
                break;
            case R.id.tv_connect:
                String pwd = wifi_pwd.getText().toString();
                if ("".equals(pwd)) {
                    wifi_pwd.setHint("请输入密码...");
                    wifi_pwd.setHintTextColor(Color.RED);
                    return;
                } else if (pwd.length() < 8) {
                    wifi_pwd.setText("");
                    wifi_pwd.setHint("请至少输入8位数...");
                    wifi_pwd.setHintTextColor(Color.RED);
                    return;
                } else {
                    //进行连接操作
                    openWifi(wifiManager);
                    // 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
                    assert wifiManager != null;
                    while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
                        try {
                            // 为了避免程序一直while循环，让它睡个100毫秒检测……
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //createWifiConfig主要用于构建一个WifiConfiguration，代码中的例子主要用于连接不需要密码的Wifi
                    //WifiManager的addNetwork接口，传入WifiConfiguration后，得到对应的NetworkId
                    int netId = wifiManager.addNetwork(createWifiConfig(ssid, pwd, getCipherType(ssid), wifiManager));

                    //WifiManager的enableNetwork接口，就可以连接到netId对应的wifi了
                    //其中boolean参数，主要用于指定是否需要断开其它Wifi网络
                    boolean enable = wifiManager.enableNetwork(netId, true);
                    if (enable) {
                        ToastUtils.showShort("连接成功");
                        SharedPreferences preferences = mActivity.getSharedPreferences("myFragment", MODE_PRIVATE);
                        SharedPreferences.Editor edit = preferences.edit();
                        edit.putBoolean("myWifi", true);
                        edit.apply();
                        setResult(1);
                        finish();
                    } else {
                        ToastUtils.showShort("连接失败");
                    }
                }
                break;
            default:
                break;
        }
    }
}
