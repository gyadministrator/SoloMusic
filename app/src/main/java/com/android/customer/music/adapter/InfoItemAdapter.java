package com.android.customer.music.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.android.customer.music.R;
import com.android.customer.music.model.MsgVo;

import java.util.List;

/**
 * Date:2019/12/11
 * TIME:14:57
 * author:fldserver
 * email:1984629668@qq.com
 **/
public class InfoItemAdapter extends RecyclerView.Adapter<InfoItemAdapter.ViewHolder> {
    private List<MsgVo> list;
    private Context context;
    private OnInfoItemListener itemListener;

    public void setItemListener(OnInfoItemListener itemListener) {
        this.itemListener = itemListener;
    }

    public interface OnInfoItemListener {
        void onItemClick(int position);
    }

    public InfoItemAdapter(List<MsgVo> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.info_item_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MsgVo msgVo = list.get(position);
        if (msgVo != null) {
            if (!TextUtils.isEmpty(msgVo.getImage())) {
                Glide.with(context).load(msgVo.getImage()).into(holder.image);
            }
            holder.tvName.setText(msgVo.getName());
            String content = msgVo.getContent();
            if (content != null && content.length() > 20) {
                content = content.substring(0, 17) + "...";
            }
            holder.tvContent.setText(content);
            holder.tvTime.setText(msgVo.getTime());
            holder.llContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemListener != null) {
                        itemListener.onItemClick(position);
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

    public void update(int position, MsgVo msgVo) {
        list.remove(position);
        list.add(position, msgVo);
        this.notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView tvName;
        TextView tvContent;
        TextView tvTime;
        LinearLayout llContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.iv_image);
            tvName = itemView.findViewById(R.id.tv_name);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvTime = itemView.findViewById(R.id.tv_time);
            llContent = itemView.findViewById(R.id.ll_content);
        }
    }
}
