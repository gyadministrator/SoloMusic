package com.example.gy.musicgame.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.gy.musicgame.R;
import com.example.gy.musicgame.model.NoticeVo;

import java.util.List;

/**
 * Date:2019/12/11
 * TIME:14:57
 * author:fldserver
 * email:1984629668@qq.com
 **/
public class NoticeItemAdapter extends BaseAdapter {
    private List<NoticeVo> list;
    private Context context;

    public NoticeItemAdapter(List<NoticeVo> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void addData(List<NoticeVo> moreData) {
        list.addAll(moreData);
        this.notifyDataSetChanged();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.notice_item_adapter, null);
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = convertView.findViewById(R.id.tv_title);
            viewHolder.tvContent = convertView.findViewById(R.id.tv_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        NoticeVo noticeVo = list.get(position);
        if (noticeVo != null) {
            String title = noticeVo.getTitle();
            if (title != null && title.length() > 20) {
                title = title.substring(0, 17) + "...";
            }
            viewHolder.tvTitle.setText(title);
            String content = noticeVo.getContent();
            if (content != null && content.length() > 40) {
                content = content.substring(0, 37) + "...";
            }
            viewHolder.tvContent.setText(content);
        }
        return convertView;
    }

    class ViewHolder {
        TextView tvTitle;
        TextView tvContent;
    }
}
