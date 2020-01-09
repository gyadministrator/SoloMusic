package com.android.customer.music.activity;

import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.customer.music.R;
import com.android.customer.music.adapter.NoticeItemAdapter;
import com.android.customer.music.api.Api;
import com.android.customer.music.constant.Constants;
import com.android.customer.music.helper.RetrofitHelper;
import com.android.customer.music.model.NoticeVo;
import com.android.customer.music.utils.HandlerUtils;
import com.android.customer.music.utils.SharedPreferenceUtil;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NoticeActivity extends BaseActivity implements XRecyclerView.LoadingListener {
    private TextView tvNotice;
    private NoticeItemAdapter itemAdapter;
    private List<NoticeVo> list = new ArrayList<>();
    private int currentPage = 1;
    private int pageSize = 20;
    private boolean isLoadMore;
    private XRecyclerView recyclerView;

    @Override
    protected void initView() {
        tvNotice = fd(R.id.tv_notice);
        recyclerView = fd(R.id.rv_linear);
        recyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        recyclerView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setLoadingListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initAction() {
        getNoticeList();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_notice;
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        getNoticeList();
        isLoadMore = false;
    }

    @Override
    public void onLoadMore() {
        currentPage += 1;
        isLoadMore = true;
        getNoticeList();
    }

    private void getNoticeList() {
        SharedPreferenceUtil preferenceUtil = new SharedPreferenceUtil();
        String token = preferenceUtil.getObject(mActivity, Constants.CURRENT_TOKEN);
        if (TextUtils.isEmpty(token)) {
            LoginActivity.startActivity(mActivity);
        } else {
            getNotice(token);
        }
    }

    private void getNotice(String token) {
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.SERVER_URL);
        Observable<Map> observable = api.noticeList(token, currentPage, pageSize);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Map>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Map map) {
                        boolean handler = HandlerUtils.isHandler(map, mActivity);
                        if (!handler) {
                            Gson gson = new Gson();
                            String json = gson.toJson(map.get("data"));
                            Type type = new TypeToken<List<NoticeVo>>() {
                            }.getType();
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isLoadMore) {
                                        list = gson.fromJson(json, type);
                                        recyclerView.refreshComplete();
                                    }
                                    if (isLoadMore) {
                                        recyclerView.loadMoreComplete();
                                        List<NoticeVo> list = gson.fromJson(json, type);
                                        itemAdapter.addData(list);
                                        if (list == null || list.size() == 0) {
                                            recyclerView.setNoMore(true);
                                        }
                                    } else {
                                        setData(list);
                                    }
                                }
                            });
                        }
                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showShort(Objects.requireNonNull(e.getMessage()));
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void setData(List<NoticeVo> list) {
        if (list == null || list.size() == 0) tvNotice.setVisibility(View.VISIBLE);
        itemAdapter = new NoticeItemAdapter(list, mActivity);
        recyclerView.setAdapter(itemAdapter);
    }
}
