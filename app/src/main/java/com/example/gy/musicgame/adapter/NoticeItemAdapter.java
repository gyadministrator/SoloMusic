package com.example.gy.musicgame.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gy.musicgame.R;
import com.example.gy.musicgame.activity.NoticeDetailActivity;
import com.example.gy.musicgame.activity.WebActivity;
import com.example.gy.musicgame.model.NoticeVo;

import java.util.List;

/**
 * Date:2019/12/11
 * TIME:14:57
 * author:fldserver
 * email:1984629668@qq.com
 **/
public class NoticeItemAdapter extends RecyclerView.Adapter<NoticeItemAdapter.ViewHolder> {
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


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notice_item_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NoticeVo noticeVo = list.get(position);
        if (noticeVo != null) {
            String title = noticeVo.getTitle();
            if (title != null && title.length() > 20) {
                title = title.substring(0, 17) + "...";
            }
            holder.tvTitle.setText(title);
            String content = noticeVo.getContent();
            if (content != null && content.length() > 40) {
                content = content.substring(0, 37) + "...";
            }
            holder.tvContent.setText(content);
            holder.llContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(noticeVo.getUrl())) {
                        NoticeDetailActivity.startActivity((Activity) context, noticeVo.getId());
                    } else {
                        WebActivity.startActivity((Activity) context, noticeVo.getUrl());
                    }
                }
            });
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvContent;
        LinearLayout llContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);
            llContent = itemView.findViewById(R.id.ll_content);
        }
    }
}
