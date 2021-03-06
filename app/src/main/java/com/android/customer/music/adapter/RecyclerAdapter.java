package com.android.customer.music.adapter;

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
import com.android.customer.music.R;
import com.android.customer.music.api.Api;
import com.android.customer.music.constant.Constants;
import com.android.customer.music.helper.LoadingDialogHelper;
import com.android.customer.music.helper.RetrofitHelper;
import com.android.customer.music.listener.OnItemClickListener;
import com.android.customer.music.model.BottomBarVo;
import com.android.customer.music.model.PlayMusicModel;
import com.android.customer.music.model.RecommendMusicModel;
import com.android.customer.music.view.WidthEqualHeightImageView;

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
    private OnItemClickListener onItemClickListener;

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
                                BottomBarVo bottomBarVo = new BottomBarVo();
                                bottomBarVo.setAuthor(bean.getAuthor());
                                if (!TextUtils.isEmpty(bean.getPic_small())) {
                                    bottomBarVo.setImage(bean.getPic_small());
                                } else {
                                    bottomBarVo.setImage(bean.getPic_big());
                                }
                                bottomBarVo.setSongId(bean.getSong_id());
                                bottomBarVo.setName(bean.getTitle());
                                bottomBarVo.setPath(file_link);
                                bottomBarVo.setTingUid(bean.getTing_uid());

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
