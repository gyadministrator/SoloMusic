package com.example.gy.musicgame.fragment.info;

import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
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

import com.example.gy.musicgame.R;
import com.example.gy.musicgame.activity.NoticeDetailActivity;
import com.example.gy.musicgame.activity.WebActivity;
import com.example.gy.musicgame.adapter.NoticeItemAdapter;
import com.example.gy.musicgame.model.NoticeVo;

import java.util.ArrayList;
import java.util.List;

public class NoticeFragment extends Fragment implements AdapterView.OnItemClickListener {

    private NoticeViewModel mViewModel;
    private Activity mActivity;
    private ListView listView;
    private TextView tvNotice;
    private NoticeItemAdapter itemAdapter;
    private List<NoticeVo> list = new ArrayList<>();
    ;

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
        list.add(new NoticeVo("版本升级", "更新内容", "12:15"));
        list.add(new NoticeVo("版本升级2", "更新内容", "12:15"));
        itemAdapter = new NoticeItemAdapter(list, mActivity);
        listView.setAdapter(itemAdapter);
        listView.setOnItemClickListener(this);
    }

    private void initData() {

    }

    private void initView(View view) {
        tvNotice = view.findViewById(R.id.tv_notice);
        listView = view.findViewById(R.id.listView);
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
            NoticeDetailActivity.startActivity(mActivity, noticeVo.getNoticeId());
        } else {
            WebActivity.startActivity(mActivity, noticeVo.getUrl());
        }
    }
}
