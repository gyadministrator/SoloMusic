package com.android.customer.music.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.customer.music.R;
import com.android.customer.music.helper.DialogHelper;
import com.android.customer.music.listener.SheetDialogListener;
import com.blankj.utilcode.util.ToastUtils;

import java.util.Arrays;
import java.util.List;

import static com.android.customer.music.activity.ConnectWifiActivity.createWifiConfig;
import static com.android.customer.music.activity.ConnectWifiActivity.getCipherType;
import static com.android.customer.music.activity.ConnectWifiActivity.openWifi;

public class JoinNetworkActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivClose;
    private TextView tvTitle;
    private TextView tvConnect;
    private EditText net_name;
    private EditText input_pwd;
    private TextView save_tv;
    private List<String> list;
    private static WifiManager wifiManager;
    //是否显示密码框
    private boolean flag = false;


    @Override
    protected void initAction() {
        save_tv.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        save_tv.setOnClickListener(this);
        tvConnect.setOnClickListener(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_join_network;
    }


    @Override
    protected void initView() {
        ivClose = fd(R.id.iv_close);
        tvTitle = fd(R.id.tv_title);
        tvConnect = fd(R.id.tv_connect);
        net_name = fd(R.id.net_name);
        input_pwd = fd(R.id.input_pwd);
        save_tv = fd(R.id.safe_tv);
    }

    @Override
    protected void initData() {
        tvTitle.setText("添加网络");
        list = Arrays.asList(getResources().getStringArray(R.array.safe));
        save_tv.setKeyListener(null);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.safe_tv:
                showBottom();
                break;
            case R.id.tv_connect:
                String name = net_name.getText().toString();
                String pwd = input_pwd.getText().toString();
                if ("".equals(name)) {
                    net_name.setHint("请输入网络名称...");
                    net_name.setHintTextColor(Color.RED);
                    return;
                } else if (flag) {
                    if ("".equals(pwd)) {
                        input_pwd.setHint("请输入密码...");
                        input_pwd.setHintTextColor(Color.RED);
                        return;
                    } else if (pwd.length() < 8) {
                        input_pwd.setText("");
                        input_pwd.setHint("请至少输入8位数...");
                        input_pwd.setHintTextColor(Color.RED);
                        return;
                    }
                } else {
                    pwd = "";
                }
                //进行连接操作
                openWifi(wifiManager);
                // 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
                assert wifiManager != null;
                while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
                    try {
                        // 为了避免程序一直while循环，让它睡个100毫秒检测……
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                    }
                }
                //createWifiConfig主要用于构建一个WifiConfiguration，代码中的例子主要用于连接不需要密码的Wifi
                //WifiManager的addNetwork接口，传入WifiConfiguration后，得到对应的NetworkId
                int cipherType = getCipherType(name);
                int netId = wifiManager.addNetwork(createWifiConfig(name, pwd, cipherType, wifiManager));
                if (cipherType == -1) {
                    ToastUtils.showShort("没有找到该网络");
                } else {
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
            case R.id.iv_close:
                finish();
                break;
            default:
                break;
        }
    }

    private void showBottom() {
        DialogHelper.getInstance().showBottomDialog(mActivity, list, new SheetDialogListener() {
            @Override
            public void selectPosition(int position) {
                String s = list.get(position);
                save_tv.setText(s);
                if (!"开放".equals(s)) {
                    input_pwd.setVisibility(View.VISIBLE);
                    flag = true;
                } else {
                    input_pwd.setVisibility(View.GONE);
                    flag = false;
                }
            }
        });
    }
}
