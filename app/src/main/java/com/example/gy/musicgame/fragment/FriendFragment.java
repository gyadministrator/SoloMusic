package com.example.gy.musicgame.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.blankj.utilcode.util.ToastUtils;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.friend.SideBar;
import com.example.gy.musicgame.friend.SortAdapter;
import com.example.gy.musicgame.model.UserModel;
import com.example.gy.musicgame.utils.LogUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.Context.MODE_PRIVATE;

public class FriendFragment extends Fragment {

    private FriendViewModel mViewModel;
    private Activity mActivity;
    private ListView listView;
    private SideBar sideBar;
    private ArrayList<UserModel> list;

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
        initFriendData();
        sideBar.setOnStrSelectCallBack(new SideBar.ISideBarSelectCallBack() {
            @Override
            public void onSelectStr(int index, String selectStr) {
                for (int i = 0; i < list.size(); i++) {
                    if (selectStr.equalsIgnoreCase(list.get(i).getFirstLetter())) {
                        listView.setSelection(i); // 选择到首字母出现的位置
                        return;
                    }
                }
            }
        });
    }

    private void initFriendData() {
        list = new ArrayList<>();
        list.add(new UserModel("亳州")); // 亳[bó]属于不常见的二级汉字
        list.add(new UserModel("大娃"));
        list.add(new UserModel("二娃"));
        list.add(new UserModel("三娃"));
        list.add(new UserModel("四娃"));
        list.add(new UserModel("五娃"));
        list.add(new UserModel("六娃"));
        list.add(new UserModel("七娃"));
        list.add(new UserModel("喜羊羊"));
        list.add(new UserModel("美羊羊"));
        list.add(new UserModel("懒羊羊"));
        list.add(new UserModel("沸羊羊"));
        list.add(new UserModel("暖羊羊"));
        list.add(new UserModel("慢羊羊"));
        list.add(new UserModel("灰太狼"));
        list.add(new UserModel("红太狼"));
        list.add(new UserModel("孙悟空"));
        list.add(new UserModel("黑猫警长"));
        list.add(new UserModel("舒克"));
        list.add(new UserModel("贝塔"));
        list.add(new UserModel("海尔"));
        list.add(new UserModel("阿凡提"));
        list.add(new UserModel("邋遢大王"));
        list.add(new UserModel("哪吒"));
        list.add(new UserModel("没头脑"));
        list.add(new UserModel("不高兴"));
        list.add(new UserModel("蓝皮鼠"));
        list.add(new UserModel("大脸猫"));
        list.add(new UserModel("大头儿子"));
        list.add(new UserModel("小头爸爸"));
        list.add(new UserModel("蓝猫"));
        list.add(new UserModel("淘气"));
        list.add(new UserModel("叶峰"));
        list.add(new UserModel("楚天歌"));
        list.add(new UserModel("江流儿"));
        list.add(new UserModel("Tom"));
        list.add(new UserModel("Jerry"));
        list.add(new UserModel("12345"));
        list.add(new UserModel("54321"));
        list.add(new UserModel("_(:з」∠)_"));
        list.add(new UserModel("……%￥#￥%#"));
        Collections.sort(list); // 对list进行排序，需要让User实现Comparable接口重写compareTo方法
        SortAdapter adapter = new SortAdapter(mActivity, list);
        listView.setAdapter(adapter);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(mActivity).inflate(R.layout.friend_header, null);
        listView.addHeaderView(view);
    }

    private void initView(View view) {
        listView = view.findViewById(R.id.listView);
        sideBar = view.findViewById(R.id.side_bar);
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
