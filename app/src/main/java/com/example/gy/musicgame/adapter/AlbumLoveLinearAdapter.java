package com.example.gy.musicgame.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.api.Api;
import com.example.gy.musicgame.constant.Constants;
import com.example.gy.musicgame.helper.LoadingDialogHelper;
import com.example.gy.musicgame.helper.RetrofitHelper;
import com.example.gy.musicgame.listener.OnItemClickListener;
import com.example.gy.musicgame.model.BaseAlbumLoveVo;
import com.example.gy.musicgame.model.BottomBarVo;
import com.example.gy.musicgame.model.LoveAlbumVo;
import com.example.gy.musicgame.model.PlayMusicModel;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
        LoadingDialogHelper.show((Activity) mContext, "获取播放源中...");
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.BASE_URL);
        Map<String, Object> params = retrofitHelper.getmParams();
        params.put("method", Constants.METHOD_PLAY);
        params.put("songid", loveAlbumVo.getSongId());
        Observable<PlayMusicModel> observable = api.play(params);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PlayMusicModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PlayMusicModel playMusicModel) {
                        if (playMusicModel != null) {
                            PlayMusicModel.BitrateBean bitrate = playMusicModel.getBitrate();
                            if (bitrate != null) {
                                String file_link = bitrate.getFile_link();
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
                        }
                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onError(Throwable e) {
                        LoadingDialogHelper.dismiss();
                        ToastUtils.showShort("未获取到播放源");
                    }

                    @Override
                    public void onComplete() {
                        LoadingDialogHelper.dismiss();
                    }
                });
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
