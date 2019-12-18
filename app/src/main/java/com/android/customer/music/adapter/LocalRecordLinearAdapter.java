package com.android.customer.music.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.android.customer.music.R;
import com.android.customer.music.listener.OnItemClickListener;
import com.android.customer.music.model.BottomBarVo;

import java.util.List;

/**
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/10/31 9:13
 */
public class LocalRecordLinearAdapter extends RecyclerView.Adapter<LocalRecordLinearAdapter.ViewHolder> {
    private Context mContext;
    private View mItemView;
    private List<BottomBarVo> list;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public LocalRecordLinearAdapter(Context mContext, List<BottomBarVo> list) {
        this.mContext = mContext;
        this.list = list;
    }

    public void addData(List<BottomBarVo> more) {
        list.addAll(more);
        notifyDataSetChanged();
        notifyItemChanged(list.size() - more.size());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mItemView = LayoutInflater.from(mContext).inflate(R.layout.linear_adapter, parent, false);
        return new ViewHolder(mItemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BottomBarVo bottomBarVo = list.get(position);
        holder.tvAuthor.setText(bottomBarVo.getAuthor());
        final String title = bottomBarVo.getName();
        if (title.length() > 15) {
            holder.tvName.setText(title.substring(0, 15) + "...");
        } else {
            holder.tvName.setText(title);
        }
        Glide.with(mContext).load(bottomBarVo.getImage()).into(holder.ivIcon);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSongPath(bottomBarVo);
            }
        });
    }

    private void getSongPath(BottomBarVo bottomBarVo) {
        if (onItemClickListener != null) {
            onItemClickListener.play(bottomBarVo);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView ivIcon;
        TextView tvName;
        TextView tvAuthor;
        ImageView ivPlay;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            ivPlay = itemView.findViewById(R.id.iv_play);
        }
    }
}
