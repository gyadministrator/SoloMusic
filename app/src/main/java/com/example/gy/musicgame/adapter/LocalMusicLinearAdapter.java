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
import com.example.gy.musicgame.constant.Constants;
import com.example.gy.musicgame.listener.OnItemClickListener;
import com.example.gy.musicgame.model.BottomBarVo;
import com.example.gy.musicgame.model.LocalMusicModel;
import com.example.gy.musicgame.utils.LocalMusicUtils;

import java.util.List;

/**
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/10/31 9:13
 */
public class LocalMusicLinearAdapter extends RecyclerView.Adapter<LocalMusicLinearAdapter.ViewHolder> {
    private Context mContext;
    private List<LocalMusicModel> list;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public LocalMusicLinearAdapter(Context mContext, List<LocalMusicModel> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = LayoutInflater.from(mContext).inflate(R.layout.linear_adapter, parent, false);
        return new ViewHolder(mItemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final LocalMusicModel musicModel = list.get(position);
        holder.tvAuthor.setText(musicModel.getSinger());
        final String title = musicModel.getName();
        if (title.length() > 15) {
            holder.tvName.setText(title.substring(0, 15) + "...");
        } else {
            holder.tvName.setText(title);
        }
        String albumArt = LocalMusicUtils.getAlbumArt(mContext, musicModel.getAlbumId());
        if (!TextUtils.isEmpty(albumArt)) {
            Glide.with(mContext).load(albumArt).into(holder.ivIcon);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSongPath(musicModel);
            }
        });
    }

    private void getSongPath(LocalMusicModel musicModel) {
        String file_link = musicModel.getPath();
        BottomBarVo bottomBarVo = new BottomBarVo();
        bottomBarVo.setAuthor(musicModel.getSinger());
        String albumArt = LocalMusicUtils.getAlbumArt(mContext, musicModel.getAlbumId());
        if (!TextUtils.isEmpty(albumArt)) {
            bottomBarVo.setImage(albumArt);
        } else {
            bottomBarVo.setImage(Constants.DEFAULT_ALBUM_URL);
        }
        bottomBarVo.setName(musicModel.getName());
        bottomBarVo.setPath(file_link);
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
