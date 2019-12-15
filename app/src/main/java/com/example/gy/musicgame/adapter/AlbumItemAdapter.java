package com.example.gy.musicgame.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.model.AlbumVo;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Date:2019/12/11
 * TIME:14:57
 * author:fldserver
 * email:1984629668@qq.com
 **/
public class AlbumItemAdapter extends BaseAdapter {
    private List<AlbumVo> list;
    private Context context;

    public AlbumItemAdapter(List<AlbumVo> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void addLoad(List<AlbumVo> loadMore) {
        if (loadMore != null && loadMore.size() > 0) {
            list.addAll(loadMore);
            notifyDataSetChanged();
        }
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
            convertView = LayoutInflater.from(context).inflate(R.layout.album_item_adapter, null);
            viewHolder = new ViewHolder();
            viewHolder.ivAlbum = convertView.findViewById(R.id.iv_album);
            viewHolder.tvAlbum = convertView.findViewById(R.id.tv_album);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        AlbumVo albumVo = list.get(position);
        if (albumVo != null) {
            if (!TextUtils.isEmpty(albumVo.getImage())) {
                Glide.with(context).load(albumVo.getImage()).into(viewHolder.ivAlbum);
            }
            viewHolder.tvAlbum.setText(albumVo.getAlbum());
        }
        return convertView;
    }

    class ViewHolder {
        CircleImageView ivAlbum;
        TextView tvAlbum;
    }
}
