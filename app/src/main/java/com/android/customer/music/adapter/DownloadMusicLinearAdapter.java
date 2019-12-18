package com.android.customer.music.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.android.customer.music.R;
import com.android.customer.music.constant.Constants;
import com.android.customer.music.listener.OnItemClickListener;
import com.android.customer.music.model.BottomBarVo;
import com.android.customer.music.model.LocalMusicModel;
import com.android.customer.music.utils.LocalMusicUtils;

import java.util.Iterator;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/10/31 9:13
 */
public class DownloadMusicLinearAdapter extends RecyclerView.Adapter<DownloadMusicLinearAdapter.ViewHolder> {
    private Context mContext;
    private List<LocalMusicModel> list;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public DownloadMusicLinearAdapter(Context mContext, List<LocalMusicModel> list) {
        this.mContext = mContext;
        Iterator<LocalMusicModel> iterator = list.iterator();
        while (iterator.hasNext()) {
            LocalMusicModel musicModel = iterator.next();
            if (!musicModel.getSinger().contains("SoloMusic")) {
                iterator.remove();
            }
        }
        this.list = list;

        SharedPreferences preferences = mContext.getSharedPreferences("myFragment", MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putInt("myDownload", list.size());
        edit.apply();
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
        if (musicModel.getSinger() != null) {
            if (!musicModel.getSinger().contains("SoloMusic")) return;
        }
        holder.tvAuthor.setText(musicModel.getSinger());
        final String title = musicModel.getName();
        if (title.length() > 15) {
            holder.tvName.setText(title.substring(0, 15) + "...");
        } else {
            holder.tvName.setText(title);
        }
        String albumArt = getAlbumArt((int) musicModel.getAlbumId());
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

    private String getAlbumArt(int album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[]{"album_art"};
        Cursor cur = mContext.getContentResolver().query(Uri.parse(mUriAlbums + "/" + album_id), projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        cur = null;
        return album_art;
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
