package com.android.customer.music.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.android.customer.music.R;
import com.android.customer.music.view.TitleView;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.qcloud.tim.uikit.component.TitleBarLayout;
import com.tencent.qcloud.tim.uikit.modules.chat.ChatLayout;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.modules.contact.ContactItemBean;

public class TxChatActivity extends BaseActivity {
    private ChatLayout chatLayout;
    private ChatInfo chatInfo;
    private ContactItemBean contactItemBean;
    private TitleView titleView;

    @Override
    protected void initView() {
        chatLayout = fd(R.id.chat_layout);
        titleView = fd(R.id.titleView);
    }

    public static void startActivity(Activity activity, ContactItemBean contactItemBean) {
        Intent intent = new Intent(activity, TxChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("itemBean", contactItemBean);
        intent.putExtra("chatItem", bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    protected void initData() {
        // 单聊面板的默认 UI 和交互初始化
        chatLayout.initDefault();
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("chatItem");
        if (bundle != null) {
            contactItemBean = (ContactItemBean) bundle.getSerializable("itemBean");
            chatInfo = new ChatInfo();
            if (contactItemBean != null) {
                if (TextUtils.isEmpty(contactItemBean.getRemark())) {
                    chatInfo.setChatName(contactItemBean.getNickname());
                } else {
                    chatInfo.setChatName(contactItemBean.getRemark());
                }
                chatInfo.setId(contactItemBean.getId());
                chatInfo.setType(TIMConversationType.C2C);
                chatLayout.setChatInfo(chatInfo);
                titleView.setTitle(chatInfo.getChatName());
            }
        }
        TitleBarLayout titleBar = chatLayout.getTitleBar();
        titleBar.setVisibility(View.GONE);

        titleView.setRightClickListener(new TitleView.OnRightClickListener() {
            @Override
            public void clickRight(View view) {
                FriendDetailActivity.startActivity(mActivity, contactItemBean.getId());
            }

            @Override
            public void clickLeft(View view) {

            }
        });
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_tx_chat;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatLayout.exitChat();
    }
}
