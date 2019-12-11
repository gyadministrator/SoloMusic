package com.example.gy.musicgame.fragment.info;

import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gy.musicgame.R;
import com.example.gy.musicgame.adapter.InfoItemAdapter;
import com.example.gy.musicgame.model.MsgVo;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {

    private MessageViewModel mViewModel;
    private Activity mActivity;
    private TextView tvNoInfo;
    private ListView listView;
    private InfoItemAdapter itemAdapter;

    public static MessageFragment newInstance() {
        return new MessageFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_fragment, container, false);
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
        getMessageList();
    }

    private void getMessageList() {
        List<MsgVo> list = new ArrayList<>();
        list.add(new MsgVo("", "jack", "你好", "11:25"));
        list.add(new MsgVo("", "王菲", "你好！！！", "13:25"));
        itemAdapter = new InfoItemAdapter(list, mActivity);
        listView.setAdapter(itemAdapter);
    }

    private void initData() {

    }

    private void initView(View view) {
        tvNoInfo = view.findViewById(R.id.tv_no_info);
        listView = view.findViewById(R.id.listView);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MessageViewModel.class);
        // TODO: Use the ViewModel
    }

}
