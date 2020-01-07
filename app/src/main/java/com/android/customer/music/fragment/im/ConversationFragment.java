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
import com.android.customer.music.activity.MainActivity;
import com.android.customer.music.activity.SearchFriendActivity;
import com.android.customer.music.activity.TxChatActivity;
import com.android.customer.music.utils.LogUtils;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.tencent.qcloud.tim.uikit.base.ITitleBarLayout;
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack;
import com.tencent.qcloud.tim.uikit.component.TitleBarLayout;
import com.tencent.qcloud.tim.uikit.modules.contact.ContactItemBean;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationLayout;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationListAdapter;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationListLayout;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationManagerKit;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationProvider;
import com.tencent.qcloud.tim.uikit.modules.conversation.base.ConversationInfo;
import com.tencent.qcloud.tim.uikit.modules.conversation.interfaces.IConversationProvider;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;

import java.util.List;

public class ConversationFragment extends Fragment implements ConversationListLayout.OnItemClickListener {

    private ConversationViewModel mViewModel;
    private Activity mActivity;
    private ConversationLayout conversationLayout;
    private int count;
    private List<ConversationInfo> conversationInfos;

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

    private void initUnread() {
        if (conversationInfos != null && conversationInfos.size() > 0) {
            for (ConversationInfo conversationInfo : conversationInfos) {
                TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, conversationInfo.getConversationId());
                long unreadMessageNum = conversation.getUnreadMessageNum();
                count += unreadMessageNum;
            }
        }
        ((MainActivity) mActivity).setMsgPoint(2, count);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    private void initAction() {
    }

    private void initData() {
        String loginUser = TIMManager.getInstance().getLoginUser();
        TIMManager.getInstance().initStorage(loginUser, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                ToastUtil.toastShortMessage("获取会话失败：" + i + " " + s);
            }

            @Override
            public void onSuccess() {

            }
        });
    }

    private void initView(View view) {
        conversationLayout = view.findViewById(R.id.conversation_layout);
        // 初始化聊天面板
        conversationLayout.initDefault();
        TitleBarLayout titleBar = conversationLayout.getTitleBar();
        titleBar.setVisibility(View.GONE);

        ConversationListLayout conversationListLayout = conversationLayout.getConversationList();
        conversationListLayout.setOnItemClickListener(this);
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
