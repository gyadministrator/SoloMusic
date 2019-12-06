package com.example.gy.musicgame.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gy.musicgame.R;;
import com.example.gy.musicgame.model.MusicModel;

import java.util.List;

/**
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/10/31 9:13
 */
public class LinearAdapter extends RecyclerView.Adapter<LinearAdapter.ViewHolder> {
    private Context mContext;
    private View mItemView;
    private RecyclerView mRecyclerView;
    private boolean isCalculationRvHeight;
    private List<MusicModel.SongListBean> list;
    private boolean isFlag;

    public void setList(List<MusicModel.SongListBean> list, boolean isFlag) {
        this.list = list;
        this.isFlag = isFlag;
    }

    public LinearAdapter(Context mContext, RecyclerView mRecyclerView) {
        this.mContext = mContext;
        this.mRecyclerView = mRecyclerView;
    }

    public void setData(List<MusicModel.SongListBean> more) {
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
        if (isFlag) {
            setRecyclerViewHeight();
        }
        final MusicModel.SongListBean bean = list.get(position);
        holder.tvAuthor.setText(bean.getAuthor());
        final String title = bean.getTitle();
        if (title.length() > 15) {
            holder.tvName.setText(title.substring(0, 15) + "...");
        } else {
            holder.tvName.setText(title);
        }
        Glide.with(mContext).load(bean.getPic_small()).into(holder.ivIcon);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //PlayMusicActivity.startActivity(mContext, bean.getPic_big(), title, bean.getAuthor(), bean.getSong_id());
            }
        });
    }

    private void setRecyclerViewHeight() {
        if (isCalculationRvHeight || mRecyclerView == null) return;
        isCalculationRvHeight = true;
        RecyclerView.LayoutParams itemViewLp = (RecyclerView.LayoutParams) mItemView.getLayoutParams();
        int itemCount = getItemCount();
        int recyclerViewHeight = itemViewLp.height * itemCount;
        LinearLayout.LayoutParams rvLp = (LinearLayout.LayoutParams) mRecyclerView.getLayoutParams();
        rvLp.height = recyclerViewHeight;
        mRecyclerView.setLayoutParams(rvLp);
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
