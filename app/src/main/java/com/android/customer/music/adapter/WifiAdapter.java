package com.android.customer.music.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.customer.music.R;

import java.util.List;

/**
 * <p>文件描述：<p>
 * <p>作者：RLY<p>
 * <p>创建时间：2018/9/20<p>
 * <p>更改时间：2018/9/20<p>
 * <p>版本号：1<p>
 */
public class WifiAdapter extends BaseAdapter {
    private List<ScanResult> allValues;
    private Context mContext;

    public WifiAdapter(List<ScanResult> allValues, Context mContext) {
        this.allValues = allValues;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return allValues.size();
    }

    @Override
    public Object getItem(int position) {
        return allValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ScanResult scanResult = allValues.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_net_item, null);
            viewHolder.normal = convertView.findViewById(R.id.normal_line);
            viewHolder.special = convertView.findViewById(R.id.special_line);
            viewHolder.name = convertView.findViewById(R.id.wifi_name);
            viewHolder.lock = convertView.findViewById(R.id.lock);
            viewHolder.level = convertView.findViewById(R.id.level);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (position == allValues.size() - 1) {
            viewHolder.normal.setVisibility(View.GONE);
            viewHolder.special.setVisibility(View.VISIBLE);
        }
        String name = scanResult.SSID;
        if (TextUtils.isEmpty(name)) {
            name = "Unknown wifi";
        }
        if (name.length() > 10) {
            name = name.substring(0, 10) + "...";
        }
        if (!"".equals(name)) {
            viewHolder.name.setText(name);
            String capabilities = scanResult.capabilities;
            if (capabilities.contains("WPA") || capabilities.contains("wpa") || capabilities.contains("WEP") || capabilities.contains("wep")) {
                //有密码
                viewHolder.lock.setBackgroundResource(R.drawable.lock);
            } else {
                //无密码
                viewHolder.lock.setVisibility(View.GONE);
            }
            int level = scanResult.level;
            /**
             * 0 —— (-55)dbm 满格(4格)信号
             (-55) —— (-70)dbm 3格信号
             (-70) —— (-85)dbm　2格信号
             (-85) —— (-100)dbm 1格信号
             */
            if (level > -55 && level < 0) {
                viewHolder.level.setBackgroundResource(R.drawable.wifi3);
            } else if (level > -70 && level < -55) {
                viewHolder.level.setBackgroundResource(R.drawable.wifi3);
            } else if (level > -85 && level < -70) {
                viewHolder.level.setBackgroundResource(R.drawable.wifi2);
            } else if (level > -100 && level < -85) {
                viewHolder.level.setBackgroundResource(R.drawable.wifi1);
            }
        }
        return convertView;
    }

    static class ViewHolder {
        TextView name;
        TextView lock;
        TextView level;
        View normal;
        View special;
    }
}
