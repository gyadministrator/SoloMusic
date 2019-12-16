package com.example.gy.musicgame.fragment.info;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
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

import com.example.gy.musicgame.R;
import com.example.gy.musicgame.activity.MainActivity;
import com.example.gy.musicgame.adapter.InfoItemAdapter;
import com.example.gy.musicgame.model.MsgVo;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MessageFragment extends Fragment implements XRecyclerView.LoadingListener, InfoItemAdapter.OnInfoItemListener {

    private MessageViewModel mViewModel;
    private Activity mActivity;
    private TextView tvNoInfo;
    private InfoItemAdapter itemAdapter;
    private XRecyclerView recyclerView;
    private boolean isLoad = false;

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
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getMessageList();
            }
        });
    }

    private void getMessageList() {
        List<MsgVo> list = new ArrayList<>();
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        for (Map.Entry<String, EMConversation> m : conversations.entrySet()) {
            String key = m.getKey();
            EMConversation conversation = m.getValue();
            List<EMMessage> allMessages = conversation.getAllMessages();
            for (EMMessage message : allMessages) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = sdf.format(new Date(message.getMsgTime()));
                MsgVo msgVo = new MsgVo("", message.getUserName(), ((EMTextMessageBody) message.getBody()).getMessage(), time);
                list.add(msgVo);
            }
        }
        recyclerView.refreshComplete();
        if (list.size() == 0) {
            tvNoInfo.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoInfo.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            itemAdapter = new InfoItemAdapter(list, mActivity);
            recyclerView.setAdapter(itemAdapter);

            itemAdapter.setItemListener(this);
        }
    }

    private void initData() {

    }

    private void initView(View view) {
        tvNoInfo = view.findViewById(R.id.tv_no_info);
        recyclerView = view.findViewById(R.id.rv_linear);
        recyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        recyclerView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setLoadingMoreEnabled(false);
        recyclerView.setLoadingListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MessageViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onRefresh() {
        isLoad = false;
        initAction();
    }

    @Override
    public void onLoadMore() {
        isLoad = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onItemClick(int position) {
        ((MainActivity) mActivity).clearMsgPoint(3);
    }
}
