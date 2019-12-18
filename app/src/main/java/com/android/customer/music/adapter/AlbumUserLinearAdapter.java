package com.android.customer.music.adapter;

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
import com.android.customer.music.R;
import com.android.customer.music.api.Api;
import com.android.customer.music.constant.Constants;
import com.android.customer.music.helper.LoadingDialogHelper;
import com.android.customer.music.helper.RetrofitHelper;
import com.android.customer.music.listener.OnItemClickListener;
import com.android.customer.music.model.BaseAlbumUserVo;
import com.android.customer.music.model.BottomBarVo;
import com.android.customer.music.model.PlayMusicModel;
import com.android.customer.music.model.UserAlbumVo;

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
public class AlbumUserLinearAdapter extends RecyclerView.Adapter<AlbumUserLinearAdapter.ViewHolder> {
    private Context mContext;
    private View mItemView;
    private List<BaseAlbumUserVo> list;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public AlbumUserLinearAdapter(Context mContext, List<BaseAlbumUserVo> list) {
        this.mContext = mContext;
        this.list = list;
    }

    public void addData(List<BaseAlbumUserVo> more) {
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
        UserAlbumVo userAlbumVo = list.get(position);
        holder.tvAuthor.setText(userAlbumVo.getAuthor());
        final String title = userAlbumVo.getTitle();
        if (title.length() > 15) {
            holder.tvName.setText(title.substring(0, 15) + "...");
        } else {
            holder.tvName.setText(title);
        }
        Glide.with(mContext).load(userAlbumVo.getImage()).into(holder.ivIcon);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSongPath(userAlbumVo);
            }
        });
    }

    private void getSongPath(UserAlbumVo userAlbumVo) {
        LoadingDialogHelper.show((Activity) mContext, "获取播放源中...");
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.BASE_URL);
        Map<String, Object> params = retrofitHelper.getmParams();
        params.put("method", Constants.METHOD_PLAY);
        params.put("songid", userAlbumVo.getSongId());
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
                                bottomBarVo.setAuthor(userAlbumVo.getAuthor());
                                if (!TextUtils.isEmpty(userAlbumVo.getImage())) {
                                    bottomBarVo.setImage(userAlbumVo.getImage());
                                }
                                bottomBarVo.setSongId(userAlbumVo.getSongId());
                                bottomBarVo.setName(userAlbumVo.getTitle());
                                bottomBarVo.setPath(file_link);
                                bottomBarVo.setTingUid(userAlbumVo.getTingUid());
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
