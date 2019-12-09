package com.example.gy.musicgame.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.model.StepModel;

import java.util.List;

/**
 * author:Administrator
 * time:2018/11/16
 * decription:
 **/
public class ListRecipeDetailAdapter extends BaseAdapter {
    private Context context;
    private List<StepModel> list;

    public ListRecipeDetailAdapter(Context context, List<StepModel> list) {
        this.context = context;
        this.list = list;
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
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_step, null);
            viewHolder.name = convertView.findViewById(R.id.tv_step);
            viewHolder.imageView = convertView.findViewById(R.id.iv_step);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        StepModel step = list.get(position);
        viewHolder.name.setText(step.getStep());
        Glide.with(context).load(step.getImg()).into(viewHolder.imageView);
        return convertView;
    }

    static class ViewHolder {
        TextView name;
        ImageView imageView;
    }
}
