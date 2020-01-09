package com.android.customer.music.fragment.im;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.android.customer.music.R;
import com.android.customer.music.activity.SearchFriendActivity;
import com.android.customer.music.activity.TxChatActivity;
import com.android.customer.music.event.DeleteEvent;
import com.android.customer.music.model.UserInfoVo;
import com.android.customer.music.utils.GenerateUserSig;
import com.android.customer.music.utils.UserManager;
import com.android.customer.music.view.TitleView;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.qcloud.tim.uikit.component.TitleBarLayout;
import com.tencent.qcloud.tim.uikit.modules.contact.ContactItemBean;
import com.tencent.qcloud.tim.uikit.modules.contact.ContactLayout;
import com.tencent.qcloud.tim.uikit.modules.contact.ContactListView;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

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
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Object o) {
        if (o instanceof DeleteEvent) {
            contactLayout.initDefault();
        }
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
                //设置头像
                setIMIcon();
            }
        });
    }

    private void setIMIcon() {
        UserInfoVo userInfoVo = UserManager.getUserInfoVo(mActivity);
        if (userInfoVo == null) {
            ToastUtil.toastShortMessage("获取用户信息失败！");
            return;
        }
        HashMap<String, Object> hashMap = new HashMap<>();
        if (!TextUtils.isEmpty(userInfoVo.getAvatarUrl())) {
            hashMap.put(TIMUserProfile.TIM_PROFILE_TYPE_KEY_FACEURL, userInfoVo.getAvatarUrl());
        }
        TIMFriendshipManager.getInstance().modifySelfProfile(hashMap, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                ToastUtil.toastShortMessage("获取用户头像失败：" + i + " " + s);
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
        if (position > 2) {
            TxChatActivity.startActivity(mActivity, contact);
        }
    }
}
