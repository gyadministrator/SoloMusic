package com.android.customer.music.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.android.customer.music.R;
import com.android.customer.music.api.Api;
import com.android.customer.music.constant.Constants;
import com.android.customer.music.helper.LoadingDialogHelper;
import com.android.customer.music.helper.RetrofitHelper;
import com.android.customer.music.listener.OnItemClickListener;
import com.android.customer.music.model.BottomBarVo;
import com.android.customer.music.model.PlayMusicModel;
import com.android.customer.music.model.SearchMusicModel;

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
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private Context mContext;
    private View mItemView;
    private List<SearchMusicModel.SongBean> list;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public SearchAdapter(Context mContext, List<SearchMusicModel.SongBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mItemView = LayoutInflater.from(mContext).inflate(R.layout.search_adapter, parent, false);
        return new ViewHolder(mItemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final SearchMusicModel.SongBean bean = list.get(position);
        holder.tvAuthor.setText(bean.getArtistname());
        final String title = bean.getSongname();
        if (title.length() > 15) {
            holder.tvName.setText(title.substring(0, 15) + "...");
        } else {
            holder.tvName.setText(title);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSongPath(bean);
            }
        });
    }

    private void getSongPath(final SearchMusicModel.SongBean bean) {
        LoadingDialogHelper.show((Activity) mContext, "获取播放源中...");
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.BASE_URL);
        Map<String, Object> params = retrofitHelper.getmParams();
        params.put("method", Constants.METHOD_PLAY);
        params.put("songid", bean.getSongid());
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
                                bottomBarVo.setAuthor(bean.getArtistname());
                                bottomBarVo.setImage(Constants.DEFAULT_ALBUM_URL);
                                bottomBarVo.setSongId(bean.getSongid());
                                bottomBarVo.setName(bean.getSongname());
                                bottomBarVo.setPath(file_link);
                                bottomBarVo.setTingUid(null);
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
        TextView tvName;
        TextView tvAuthor;
        ImageView ivPlay;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvName = itemView.findViewById(R.id.tv_name);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            ivPlay = itemView.findViewById(R.id.iv_play);
        }
    }
}
