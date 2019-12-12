package com.example.gy.musicgame.fragment.info;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.activity.LoginActivity;
import com.example.gy.musicgame.activity.NoticeDetailActivity;
import com.example.gy.musicgame.activity.WebActivity;
import com.example.gy.musicgame.adapter.NoticeItemAdapter;
import com.example.gy.musicgame.api.Api;
import com.example.gy.musicgame.constant.Constants;
import com.example.gy.musicgame.helper.RetrofitHelper;
import com.example.gy.musicgame.model.NoticeVo;
import com.example.gy.musicgame.utils.HandlerUtils;
import com.example.gy.musicgame.utils.SharedPreferenceUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

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

public class NoticeFragment extends Fragment implements AdapterView.OnItemClickListener, OnRefreshListener, OnRefreshLoadMoreListener {

    private NoticeViewModel mViewModel;
    private Activity mActivity;
    private ListView listView;
    private TextView tvNotice;
    private NoticeItemAdapter itemAdapter;
    private List<NoticeVo> list = new ArrayList<>();
    private int currentPage = 1;
    private int pageSize = 20;
    private SmartRefreshLayout refreshLayout;
    private boolean isLoadMore;

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
        SharedPreferenceUtil<String> preferenceUtil = new SharedPreferenceUtil<>();
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
                            list = gson.fromJson(json, type);

                            if (isLoadMore) {
                                itemAdapter.addData(list);
                            } else {
                                setData(list);
                            }
                        }
                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showShort(Objects.requireNonNull(e.getMessage()));
                        refreshLayout.finishRefresh(false);
                        refreshLayout.finishLoadMore(false);
                    }

                    @Override
                    public void onComplete() {
                        refreshLayout.finishRefresh(1500);
                        refreshLayout.finishLoadMore(1500);
                    }
                });
    }

    private void setData(List<NoticeVo> list) {
        if (list == null || list.size() == 0) tvNotice.setVisibility(View.VISIBLE);
        itemAdapter = new NoticeItemAdapter(list, mActivity);
        listView.setAdapter(itemAdapter);
        listView.setOnItemClickListener(this);
    }

    private void initData() {

    }

    private void initView(View view) {
        tvNotice = view.findViewById(R.id.tv_notice);
        listView = view.findViewById(R.id.listView);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnRefreshLoadMoreListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(NoticeViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NoticeVo noticeVo = list.get(position);
        if (TextUtils.isEmpty(noticeVo.getUrl())) {
            NoticeDetailActivity.startActivity(mActivity, noticeVo.getId());
        } else {
            WebActivity.startActivity(mActivity, noticeVo.getUrl());
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        currentPage = 1;
        getNoticeList();
        isLoadMore = false;
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        currentPage += 1;
        isLoadMore = true;
        getNoticeList();
    }
}
