package com.example.gy.musicgame.fragment;

import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.ToastUtils;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.utils.LogUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import static android.content.Context.MODE_PRIVATE;

public class FriendFragment extends Fragment {

    private FriendViewModel mViewModel;
    private Activity mActivity;

    public static FriendFragment newInstance() {
        return new FriendFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friend_fragment, container, false);
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

    private void initData() {

    }

    private void initView(View view) {

    }

    private void loginIM() {
        SharedPreferences sharedPreferences = mActivity.getSharedPreferences("login", MODE_PRIVATE);
        //TODO 设置用户数据
        final String username = sharedPreferences.getString("username", null);
        final String password = sharedPreferences.getString("password", null);
        if (username != null) {
            if (password != null) {
                EMClient.getInstance().login(username, password, new EMCallBack() {//回调
                    @Override
                    public void onSuccess() {
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();
                        LogUtils.d("success", "连接IM成功");
                        ToastUtils.showShort("连接IM成功");
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }

                    @Override
                    public void onError(int code, String message) {
                        if (code != EMError.USER_NOT_FOUND) {
                            ToastUtils.showShort("连接IM失败code：" + code + "错误信息：" + message);
                        }
                        LogUtils.d("failure", "连接IM失败code：" + code + "错误信息：" + message);
                        if (code == EMError.USER_NOT_FOUND) {//用户不存在，此时注册
                            //注册失败会抛出HyphenateException
                            try {
                                EMClient.getInstance().createAccount(username, password);//同步方法
                                loginIM();
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                                //ToastUtils.showToast("注册IM账户失败");
                                LogUtils.d("error", "onError: 注册IM账户失败");
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FriendViewModel.class);
        // TODO: Use the ViewModel
    }

}
