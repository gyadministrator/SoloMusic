package com.example.gy.musicgame.friend;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.model.UserModel;

import java.util.List;

public class SortAdapter extends BaseAdapter {

    private List<UserModel> list;
    private Context mContext;

    public SortAdapter(Context mContext, List<UserModel> list) {
        this.mContext = mContext;
        this.list = list;
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder;
        UserModel user = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.friend_item, null);
            viewHolder.image = view.findViewById(R.id.iv_image);
            viewHolder.name = view.findViewById(R.id.name);
            viewHolder.catalog = view.findViewById(R.id.catalog);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        //根据position获取首字母作为目录catalog
        String catalog = user.getFirstLetter();

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(catalog)) {
            viewHolder.catalog.setVisibility(View.VISIBLE);
            viewHolder.catalog.setText(user.getFirstLetter().toUpperCase());
        } else {
            viewHolder.catalog.setVisibility(View.GONE);
        }

        viewHolder.name.setText(user.getName());
        if (!TextUtils.isEmpty(user.getImage())) {
            Glide.with(mContext).load(user.getImage()).into(viewHolder.image);
        } else {
            if (user.isBoot()) {
                Glide.with(mContext).load(R.mipmap.reboot).into(viewHolder.image);
            } else {
                Glide.with(mContext).load(R.mipmap.default_user).into(viewHolder.image);
            }
        }
        return view;

    }

    final static class ViewHolder {
        TextView catalog;
        TextView name;
        ImageView image;
    }

    /**
     * 获取catalog首次出现位置
     */
    private int getPositionForSection(String catalog) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getFirstLetter();
            if (catalog.equalsIgnoreCase(sortStr)) {
                return i;
            }
        }
        return -1;
    }

}