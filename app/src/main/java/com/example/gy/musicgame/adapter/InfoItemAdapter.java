package com.example.gy.musicgame.adapter;

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
import com.example.gy.musicgame.model.MsgVo;

import java.util.List;

/**
 * Date:2019/12/11
 * TIME:14:57
 * author:fldserver
 * email:1984629668@qq.com
 **/
public class InfoItemAdapter extends BaseAdapter {
    private List<MsgVo> list;
    private Context context;

    public InfoItemAdapter(List<MsgVo> list, Context context) {
        this.list = list;
        this.context = context;
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

    public void update(int position, MsgVo msgVo) {
        list.remove(position);
        list.add(position, msgVo);
        this.notifyDataSetChanged();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.info_item_adapter, null);
            viewHolder = new ViewHolder();
            viewHolder.image = convertView.findViewById(R.id.iv_image);
            viewHolder.tvName = convertView.findViewById(R.id.tv_name);
            viewHolder.tvContent = convertView.findViewById(R.id.tv_content);
            viewHolder.tvTime = convertView.findViewById(R.id.tv_time);
            viewHolder.line = convertView.findViewById(R.id.line);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MsgVo msgVo = list.get(position);
        if (list.size() == 1 || position == list.size() - 1) {
            viewHolder.line.setVisibility(View.GONE);
        }
        if (msgVo != null) {
            if (!TextUtils.isEmpty(msgVo.getImage())) {
                Glide.with(context).load(msgVo.getImage()).into(viewHolder.image);
            }
            viewHolder.tvName.setText(msgVo.getName());
            String content = msgVo.getContent();
            if (content != null && content.length() > 20) {
                content = content.substring(0, 17) + "...";
            }
            viewHolder.tvContent.setText(content);
            viewHolder.tvTime.setText(msgVo.getTime());
        }
        return convertView;
    }

    class ViewHolder {
        ImageView image;
        TextView tvName;
        TextView tvContent;
        TextView tvTime;
        View line;
    }
}
