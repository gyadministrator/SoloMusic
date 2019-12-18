package com.android.customer.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.customer.music.R;
import com.android.customer.music.activity.AlbumActivity;
import com.android.customer.music.constant.Constants;
import com.android.customer.music.helper.RetrofitHelper;
import com.android.customer.music.listener.OnItemClickListener;
import com.android.customer.music.model.BottomBarVo;
import com.android.customer.music.model.MusicModel;
import com.android.customer.music.view.BottomBarView;

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
 * Created Time on 2019/11/2 17:30
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private Context mContext;
    private List<String> mTitle;
    private List<Integer> mType;
    private LinearAdapter linerAdapter;
    private RecyclerView mRecyclerView;
    private boolean isCalculationRvHeight;
    private View mItemView;
    private BottomBarView bottomBarView;

    public interface OnMainAdapterListener {
        void success();
    }

    private OnMainAdapterListener onMainAdapterListener;

    public void setOnMainAdapterListener(OnMainAdapterListener onMainAdapterListener) {
        this.onMainAdapterListener = onMainAdapterListener;
    }

    public MainAdapter(Context context, RecyclerView recyclerView, List<String> title, List<Integer> type, BottomBarView bottomBarView) {
        mContext = context;
        mTitle = title;
        mType = type;
        mRecyclerView = recyclerView;
        this.bottomBarView = bottomBarView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mItemView = LayoutInflater.from(mContext).inflate(R.layout.main_adapter, parent, false);
        return new ViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        setRecyclerViewHeight();
        //设置标题
        holder.tvTitle.setText(mTitle.get(position));
        //设置更多点击事件
        holder.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //去专辑页面
                AlbumActivity.startActivity(mContext, mTitle.get(position), mType.get(position));
            }
        });
        //设置数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                setData(holder, position);
            }
        }).start();
    }

    private void setData(final ViewHolder holder, int position) {
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Map<String, Object> params = retrofitHelper.getmParams();
        params.put("method", Constants.METHOD_LIST);
        params.put("type", mType.get(position));
        params.put("size", 6);
        params.put("offset", 0);
        Observable<MusicModel> observable = retrofitHelper.initRetrofit(Constants.BASE_URL).list(params);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MusicModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MusicModel musicModel) {
                        holder.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                        holder.recyclerView.addItemDecoration(new DividerItemDecoration(mContext, RecyclerView.VERTICAL));
                        holder.recyclerView.setNestedScrollingEnabled(false);
                        linerAdapter = new LinearAdapter(mContext, holder.recyclerView);
                        linerAdapter.setList(musicModel.getSong_list(), true);
                        holder.recyclerView.setAdapter(linerAdapter);

                        linerAdapter.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void play(BottomBarVo bottomBarVo) {
                                if (bottomBarView != null) {
                                    bottomBarView.play(bottomBarVo);
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        onMainAdapterListener.success();
                    }

                    @Override
                    public void onComplete() {
                        onMainAdapterListener.success();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mType.size();
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

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView ivMore;
        RecyclerView recyclerView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            ivMore = itemView.findViewById(R.id.iv_more);
            recyclerView = itemView.findViewById(R.id.recyclerView);
        }
    }
}
