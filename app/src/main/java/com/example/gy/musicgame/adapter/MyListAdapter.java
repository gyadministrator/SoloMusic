package com.example.gy.musicgame.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gy.musicgame.R;
import com.example.gy.musicgame.model.ItemModel;

import java.util.List;

/**
 * **Create By gy
 * **Time 9:23
 * **Description MusicGame
 **/
public class MyListAdapter extends BaseAdapter {
    private List<ItemModel> list;
    private Context context;

    public MyListAdapter(List<ItemModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void setData(int position, String data) {
        ItemModel item = list.get(position);
        item.setData(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.my_list_adapter, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ItemModel item = list.get(position);
        viewHolder.iv_icon.setImageResource(item.getIcon());
       /* ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);//饱和度 0灰色 100过度彩色，50正常
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        viewHolder.iv_icon.setColorFilter(filter);*/
        viewHolder.tv_item_name.setText(item.getName());
        viewHolder.tv_data.setText(item.getData());
        return convertView;
    }

    class ViewHolder {
        ImageView iv_icon;
        TextView tv_item_name;
        ImageView iv_right;
        TextView tv_data;

        ViewHolder(View view) {
            iv_icon = view.findViewById(R.id.iv_icon);
            tv_item_name = view.findViewById(R.id.tv_item_name);
            iv_right = view.findViewById(R.id.iv_right);
            tv_data = view.findViewById(R.id.tv_data);
        }
    }
}
