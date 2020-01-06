package com.android.customer.music.fragment.im;

import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.customer.music.R;
import com.android.customer.music.activity.SearchFriendActivity;
import com.android.customer.music.activity.TxChatActivity;
import com.tencent.qcloud.tim.uikit.base.ITitleBarLayout;
import com.tencent.qcloud.tim.uikit.component.TitleBarLayout;
import com.tencent.qcloud.tim.uikit.modules.contact.ContactItemBean;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationLayout;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationListLayout;
import com.tencent.qcloud.tim.uikit.modules.conversation.base.ConversationInfo;

public class ConversationFragment extends Fragment implements ConversationListLayout.OnItemClickListener {

    private ConversationViewModel mViewModel;
    private Activity mActivity;
    private ConversationLayout conversationLayout;

    public static ConversationFragment newInstance() {
        return new ConversationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.conversation_fragment, container, false);
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

    }

    private void initData() {
    }

    private void initView(View view) {
        conversationLayout = view.findViewById(R.id.conversation_layout);
        // 初始化聊天面板
        conversationLayout.initDefault();
        TitleBarLayout titleBar = conversationLayout.getTitleBar();
        titleBar.setVisibility(View.GONE);

        ConversationListLayout conversationList = conversationLayout.getConversationList();
        conversationList.setOnItemClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ConversationViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onItemClick(View view, int position, ConversationInfo messageInfo) {
        ContactItemBean contactItemBean = new ContactItemBean();
        contactItemBean.setId(messageInfo.getId());
        contactItemBean.setGroup(messageInfo.isGroup());
        contactItemBean.setNickname(messageInfo.getTitle());
        TxChatActivity.startActivity(mActivity, contactItemBean);
    }
}
