package com.example.gy.musicgame.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.gy.musicgame.model.MusicVo;
import com.example.gy.musicgame.model.PlayMusicModel;
import com.example.gy.musicgame.model.RecommendMusicModel;
import com.example.gy.musicgame.view.WidthEqualHeightImageView;

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
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private Context mContext;
    private List<RecommendMusicModel.ResultBean.ListBean> list;
    private View mItemView;

    public RecyclerAdapter(Context mContext, List<RecommendMusicModel.ResultBean.ListBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.flow_adapter, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final RecommendMusicModel.ResultBean.ListBean bean = list.get(position);
        final String title = bean.getTitle();
        if (title.length() > 5) {
            holder.tvName.setText(title.substring(0, 5) + "...");
        } else {
            holder.tvName.setText(title);
        }
        String hot = bean.getHot();
        int num = Integer.parseInt(hot);
        if (num < 10000) {
            holder.tvNum.setText(hot);
        } else {
            holder.tvNum.setText((double) num / 10000 + "万");
        }
        Glide.with(mContext).load(bean.getPic_small()).into(holder.iv_album);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSongPath(bean);
            }
        });
    }

    public interface OnItemClickListener {
        void play(MusicVo musicVo);
    }

    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void getSongPath(final RecommendMusicModel.ResultBean.ListBean bean) {
        LoadingDialogHelper.show((Activity) mContext, "获取播放源中...");
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.BASE_URL);
        Map<String, Object> params = retrofitHelper.getmParams();
        params.put("method", Constants.METHOD_PLAY);
        params.put("songid", bean.getSong_id());
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
                                MusicVo musicVo = new MusicVo();
                                musicVo.setAuthor(bean.getAuthor());
                                if (!TextUtils.isEmpty(bean.getPic_small())) {
                                    musicVo.setImageUrl(bean.getPic_small());
                                } else {
                                    musicVo.setImageUrl(bean.getPic_big());
                                }
                                musicVo.setSongId(bean.getSong_id());
                                musicVo.setTitle(bean.getTitle());
                                musicVo.setPath(file_link);
                                musicVo.setTingUid(bean.getTing_uid());

                                if (onItemClickListener != null) {
                                    onItemClickListener.play(musicVo);
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
        WidthEqualHeightImageView iv_album;
        TextView tvName;
        TextView tvNum;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemView = itemView;
            iv_album = itemView.findViewById(R.id.iv_album);
            tvName = itemView.findViewById(R.id.tv_name);
            tvNum = itemView.findViewById(R.id.tv_num);
        }
    }
}
