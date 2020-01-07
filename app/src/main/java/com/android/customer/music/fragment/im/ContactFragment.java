package com.android.customer.music.fragment.im;

import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.android.customer.music.utils.GenerateUserSig;
import com.android.customer.music.view.TitleView;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMManager;
import com.tencent.qcloud.tim.uikit.base.ITitleBarLayout;
import com.tencent.qcloud.tim.uikit.component.TitleBarLayout;
import com.tencent.qcloud.tim.uikit.modules.contact.ContactItemBean;
import com.tencent.qcloud.tim.uikit.modules.contact.ContactLayout;
import com.tencent.qcloud.tim.uikit.modules.contact.ContactListView;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;

import static android.content.Context.MODE_PRIVATE;

public class ContactFragment extends Fragment implements ContactListView.OnItemClickListener {

    private ContactViewModel mViewModel;
    private Activity mActivity;
    private ContactLayout contactLayout;
    private TitleView titleView;

    public static ContactFragment newInstance() {
        return new ContactFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_fragment, container, false);
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
        loginIM();
    }

    private void loginIM() {
        SharedPreferences sharedPreferences = mActivity.getSharedPreferences("login", MODE_PRIVATE);
        //TODO 设置用户数据
        final String username = sharedPreferences.getString("username", null);
        String userSig = GenerateUserSig.genTestUserSig(username);
        TIMManager.getInstance().login(username, userSig, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                loginIM();
            }

            @Override
            public void onSuccess() {

            }
        });
    }

    private void initData() {

    }

    private void initView(View view) {
        contactLayout = view.findViewById(R.id.contact_layout);
        titleView = view.findViewById(R.id.titleView);
        // 通讯录面板的默认 UI 和交互初始化
        contactLayout.initDefault();
        TitleBarLayout titleBar = contactLayout.getTitleBar();
        titleBar.setVisibility(View.GONE);
        ContactListView contactListView = contactLayout.getContactListView();
        initListView(contactListView);
        titleView.setRightClickListener(new TitleView.OnRightClickListener() {
            @Override
            public void clickRight(View view) {
                mActivity.startActivity(new Intent(mActivity, SearchFriendActivity.class));
            }

            @Override
            public void clickLeft(View view) {

            }
        });
    }

    private void initListView(ContactListView contactListView) {
        contactListView.setOnItemClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ContactViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onItemClick(int position, ContactItemBean contact) {
        if (position>2) {
            TxChatActivity.startActivity(mActivity, contact);
        }
    }
}