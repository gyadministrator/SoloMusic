package com.example.gy.musicgame.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.blankj.utilcode.util.ToastUtils;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.activity.LoginActivity;
import com.example.gy.musicgame.activity.MainActivity;
import com.example.gy.musicgame.activity.NewFriendActivity;
import com.example.gy.musicgame.activity.SearchFriendActivity;
import com.example.gy.musicgame.api.Api;
import com.example.gy.musicgame.chatui.ui.activity.ChatActivity;
import com.example.gy.musicgame.constant.Constants;
import com.example.gy.musicgame.event.FriendChangeEvent;
import com.example.gy.musicgame.event.NewFriendEvent;
import com.example.gy.musicgame.friend.SideBar;
import com.example.gy.musicgame.friend.SortAdapter;
import com.example.gy.musicgame.helper.DialogHelper;
import com.example.gy.musicgame.helper.RetrofitHelper;
import com.example.gy.musicgame.listener.DialogListener;
import com.example.gy.musicgame.listener.SheetDialogListener;
import com.example.gy.musicgame.model.UserModel;
import com.example.gy.musicgame.service.AcceptMessageService;
import com.example.gy.musicgame.utils.HandlerUtils;
import com.example.gy.musicgame.utils.LogUtils;
import com.example.gy.musicgame.utils.SharedPreferenceUtil;
import com.example.gy.musicgame.view.TitleView;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;

public class FriendFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private FriendViewModel mViewModel;
    private Activity mActivity;
    private ListView listView;
    private SideBar sideBar;
    private ArrayList<UserModel> list;
    private static final String TAG = "FriendFragment";
    private List<String> friendList;
    private MyReceiver myReceiver;
    private TitleView titleView;
    private SortAdapter adapter;
    private TextView tvNum;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (friendList != null && friendList.size() > 0) {
                    list = new ArrayList<>();
                    for (String username : friendList) {
                        UserModel userModel = new UserModel(username);
                        userModel.setBoot(false);
                        list.add(userModel);
                    }
                    setData();
                    //设置未读消息
                    //((MainActivity) mActivity).setMsgPoint(2, 6);
                } else {
                    setEmpty();
                }
            }
        }
    };

    private void setEmpty() {
        list = new ArrayList<>();
        UserModel userModel = new UserModel("小歌机器人");
        userModel.setBoot(true);
        list.add(userModel);
        Collections.sort(list); // 对list进行排序，需要让User实现Comparable接口重写compareTo方法
        adapter = new SortAdapter(mActivity, list);
        listView.setAdapter(adapter);
        @SuppressLint("InflateParams") View header = LayoutInflater.from(mActivity).inflate(R.layout.friend_header, null);
        LinearLayout llNewFriend = header.findViewById(R.id.ll_new_friend);
        LinearLayout llGroup = header.findViewById(R.id.ll_group);
        tvNum = header.findViewById(R.id.tv_num);
        llNewFriend.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                //新朋友
                tvNum.setText("");
                tvNum.setVisibility(View.GONE);
                ((MainActivity) mActivity).clearMsgPoint(2);
                startActivity(new Intent(mActivity, NewFriendActivity.class));
            }
        });
        llGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //群聊
            }
        });
        if (listView.getHeaderViewsCount() == 0) {
            listView.addHeaderView(header);
        }
    }


    private void setData() {
        if (list.size() == 0) setEmpty();
        Collections.sort(list); // 对list进行排序，需要让User实现Comparable接口重写compareTo方法
        adapter = new SortAdapter(mActivity, list);
        listView.setAdapter(adapter);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(mActivity).inflate(R.layout.friend_header, null);
        LinearLayout llNewFriend = view.findViewById(R.id.ll_new_friend);
        LinearLayout llGroup = view.findViewById(R.id.ll_group);
        tvNum = view.findViewById(R.id.tv_num);
        llNewFriend.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                //新朋友
                tvNum.setText("");
                tvNum.setVisibility(View.GONE);
                ((MainActivity) mActivity).clearMsgPoint(2);
                startActivity(new Intent(mActivity, NewFriendActivity.class));
            }
        });
        llGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //群聊
            }
        });
        if (listView.getHeaderViewsCount() == 0) {
            listView.addHeaderView(view);
        }
    }

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
        SharedPreferenceUtil preferenceUtil = new SharedPreferenceUtil();
        String token = preferenceUtil.getObject(mActivity, Constants.CURRENT_TOKEN);
        getUserInfo(token);
    }

    private void initData() {
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

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Object o) {
        if (o instanceof NewFriendEvent) {
            NewFriendEvent newFriendEvent = (NewFriendEvent) o;
            if (newFriendEvent.getNum() != 0) {
                tvNum.setVisibility(View.VISIBLE);
                if (newFriendEvent.getNum() > 99) {
                    tvNum.setText("99+");
                } else {
                    tvNum.setText(String.valueOf(newFriendEvent.getNum()));
                }
            }
        } else if (o instanceof FriendChangeEvent) {
            getContactList();
            adapter.notifyDataSetChanged();
        }
    }

    private void getContactList() {
        if (friendList != null) {
            friendList.clear();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    friendList = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    mHandler.sendEmptyMessage(0);
                    LogUtils.d(TAG, "getContactList: " + friendList.size());
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    LogUtils.d(TAG, "getContactList: 获取好友列表失败" + e.getErrorCode() + e.getMessage());
                    setEmpty();
                }
            }
        }).start();
    }

    private void initView(View view) {
        listView = view.findViewById(R.id.listView);
        sideBar = view.findViewById(R.id.side_bar);
        titleView = view.findViewById(R.id.titleView);
        titleView.setRightClickListener(new TitleView.OnRightClickListener() {
            @Override
            public void clickRight(View view) {
                mActivity.startActivity(new Intent(mActivity, SearchFriendActivity.class));
            }

            @Override
            public void clickLeft(View view) {

            }
        });
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        EventBus.getDefault().register(this);
    }

    private void getUserInfo(String token) {
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.SERVER_URL);
        Observable<Map> observable = api.userInfo(token);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Map>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Map map) {
                        boolean handler = HandlerUtils.isHandler(map, mActivity);
                        if (!handler) {
                            loginIM();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoginActivity.startActivity(mActivity);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
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
                        getContactList();

                        //启动服务，监听消息
                        Intent acceptMessageService = new Intent(mActivity, AcceptMessageService.class);
                        mActivity.startService(acceptMessageService);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position != 0) {
            UserModel userModel = list.get(position - 1);
            ChatActivity.startActivity(mActivity, userModel.getName());
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        UserModel userModel = list.get(position - 1);
        List<String> items = new ArrayList<>();
        items.add("删除好友");
        DialogHelper.getInstance().showBottomDialog(mActivity, items, new SheetDialogListener() {
            @Override
            public void selectPosition(int position) {
                if (position == 0) {
                    deleteFriend(userModel.getName());
                }
            }
        });
        return true;
    }

    private void deleteFriend(String username) {
        DialogHelper.getInstance().showSureDialog(mActivity, "温馨提示", "你确定要删除" + username + "吗？", new DialogListener() {
            @Override
            public void clickSure() {
                try {
                    EMClient.getInstance().contactManager().deleteContact(username);
                    getContactList();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    ToastUtils.showShort("删除好友失败");
                }
            }

            @Override
            public void clickCancel() {

            }
        });
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals("accept")) {
                getContactList();
            }
            if (action != null && action.equals("deleted")) {
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

}
