package com.android.customer.music.fragment.info;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.ToastUtils;
import com.android.customer.music.R;
import com.android.customer.music.activity.LoginActivity;
import com.android.customer.music.adapter.NoticeItemAdapter;
import com.android.customer.music.api.Api;
import com.android.customer.music.constant.Constants;
import com.android.customer.music.helper.RetrofitHelper;
import com.android.customer.music.model.NoticeVo;
import com.android.customer.music.utils.HandlerUtils;
import com.android.customer.music.utils.SharedPreferenceUtil;
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

public class NoticeFragment extends Fragment implements XRecyclerView.LoadingListener {

    private NoticeViewModel mViewModel;
    private Activity mActivity;
    private TextView tvNotice;
    private NoticeItemAdapter itemAdapter;
    private List<NoticeVo> list = new ArrayList<>();
    private int currentPage = 1;
    private int pageSize = 20;
    private boolean isLoadMore;
    private XRecyclerView recyclerView;

    public static NoticeFragment newInstance() {
        return new NoticeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notice_fragment, container, false);
        initView(view);
        initData();
        initAction();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    private void initAction() {
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

    private void initData() {

    }

    private void initView(View view) {
        tvNotice = view.findViewById(R.id.tv_notice);
        recyclerView = view.findViewById(R.id.rv_linear);
        recyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        recyclerView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setLoadingListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(NoticeViewModel.class);
        // TODO: Use the ViewModel
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
}
