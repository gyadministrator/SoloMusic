package com.example.gy.musicgame.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
public class AlbumRecyclerItemAdapter extends RecyclerView.Adapter<AlbumRecyclerItemAdapter.ViewHolder> {
    private List<AlbumVo> list;
    private Context context;
    private OnAlbumItemListener onAlbumItemListener;

    public void setOnAlbumItemListener(OnAlbumItemListener onAlbumItemListener) {
        this.onAlbumItemListener = onAlbumItemListener;
    }

    public interface OnAlbumItemListener {
        void onItemClick(int position);

        void onItemLongClick(int position);
    }


    public AlbumRecyclerItemAdapter(List<AlbumVo> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void addLoad(List<AlbumVo> loadMore) {
        if (loadMore != null && loadMore.size() > 0) {
            list.addAll(loadMore);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.album_item_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AlbumVo albumVo = list.get(position);
        if (albumVo != null) {
            if (!TextUtils.isEmpty(albumVo.getImage())) {
                Glide.with(context).load(albumVo.getImage()).into(holder.ivAlbum);
            }
            holder.tvAlbum.setText(albumVo.getAlbum());

            holder.llContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onAlbumItemListener != null) {
                        onAlbumItemListener.onItemClick(position);
                    }
                }
            });
            holder.llContent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onAlbumItemListener != null) {
                        onAlbumItemListener.onItemLongClick(position);
                    }
                    return true;
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
        CircleImageView ivAlbum;
        TextView tvAlbum;
        LinearLayout llContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAlbum = itemView.findViewById(R.id.iv_album);
            tvAlbum = itemView.findViewById(R.id.tv_album);
            llContent = itemView.findViewById(R.id.ll_content);
        }
    }
}
