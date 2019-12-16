package com.example.gy.musicgame.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.listener.OnItemClickListener;
import com.example.gy.musicgame.model.BaseAlbumLoveVo;
import com.example.gy.musicgame.model.BottomBarVo;
import com.example.gy.musicgame.model.LoveAlbumVo;

import java.util.List;

/**
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/10/31 9:13
 */
public class AlbumLoveLinearAdapter extends RecyclerView.Adapter<AlbumLoveLinearAdapter.ViewHolder> {
    private Context mContext;
    private View mItemView;
    private List<BaseAlbumLoveVo> list;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public AlbumLoveLinearAdapter(Context mContext, List<BaseAlbumLoveVo> list) {
        this.mContext = mContext;
        this.list = list;
    }

    public void addData(List<BaseAlbumLoveVo> more) {
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
        LoveAlbumVo loveAlbumVo = list.get(position);
        holder.tvAuthor.setText(loveAlbumVo.getAuthor());
        final String title = loveAlbumVo.getTitle();
        if (title.length() > 15) {
            holder.tvName.setText(title.substring(0, 15) + "...");
        } else {
            holder.tvName.setText(title);
        }
        Glide.with(mContext).load(loveAlbumVo.getImage()).into(holder.ivIcon);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSongPath(loveAlbumVo);
            }
        });
    }

    private void getSongPath(LoveAlbumVo loveAlbumVo) {
        String file_link = loveAlbumVo.getPath();
        BottomBarVo bottomBarVo = new BottomBarVo();
        bottomBarVo.setAuthor(loveAlbumVo.getAuthor());
        if (!TextUtils.isEmpty(loveAlbumVo.getImage())) {
            bottomBarVo.setImage(loveAlbumVo.getImage());
        }
        bottomBarVo.setSongId(loveAlbumVo.getSongId());
        bottomBarVo.setName(loveAlbumVo.getTitle());
        bottomBarVo.setPath(file_link);
        bottomBarVo.setTingUid(loveAlbumVo.getTingUid());
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
