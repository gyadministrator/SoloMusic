package com.example.gy.musicgame.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gy.musicgame.event.FriendChangeEvent;
import com.example.gy.musicgame.event.NewFriendEvent;
import com.example.gy.musicgame.event.NewFriendListEvent;
import com.example.gy.musicgame.model.NewFriendVo;
import com.example.gy.musicgame.utils.LogUtils;
import com.example.gy.musicgame.utils.NotificationPermissionUtil;
import com.example.gy.musicgame.utils.NotificationUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: SoloMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/12/13 21:46
 */
public class AcceptApplyService extends Service {
    private String acceptUserName;
    private String acceptReason;
    private FriendReceiver friendReceiver;
    private List<NewFriendVo> newFriendVoList = new ArrayList<>();

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                NewFriendVo newFriendVo = new NewFriendVo();
                newFriendVo.setTitle(acceptUserName + "请求添加你为好友");
                newFriendVo.setReason(acceptReason);
                newFriendVo.setUsername(acceptUserName);
                newFriendVoList.add(newFriendVo);
                if (NotificationPermissionUtil.isNotificationEnabled(getApplicationContext())) {
                    NotificationUtils.sendApplyCustomNotification(getApplicationContext(), newFriendVo);
                }
                acceptApply();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (friendReceiver != null) {
            unregisterReceiver(friendReceiver);
        }
    }

    private void acceptApply() {
        NewFriendEvent newFriendEvent = new NewFriendEvent();
        if (newFriendVoList != null && newFriendVoList.size() > 0) {
            newFriendEvent.setNum(newFriendVoList.size());
            EventBus.getDefault().post(newFriendEvent);
            NewFriendListEvent newFriendListEvent = new NewFriendListEvent();
            newFriendListEvent.setList(newFriendVoList);
            EventBus.getDefault().postSticky(newFriendListEvent);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void registerReceiver() {
        friendReceiver = new FriendReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("invited");
        filter.addAction("accepted");
        filter.addAction("declined");
        filter.addAction("deleted");
        filter.addAction("added");
        registerReceiver(friendReceiver, filter);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver();
    }

    private class FriendReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtils.d("onReceive", "onReceive: " + action);
            String msg = intent.getStringExtra("msg");
            String username = intent.getStringExtra("username");
            String reason = intent.getStringExtra("reason");
            if (action != null) {
                switch (action) {
                    case "invited":
                        if (!TextUtils.isEmpty(username)) {
                            acceptUserName = username;
                        }
                        if (!TextUtils.isEmpty(reason)) {
                            acceptReason = reason;
                        }
                        mHandler.sendEmptyMessage(0);
                        break;
                    case "accepted":
                    case "declined":
                    case "deleted":
                    case "added":
                        EventBus.getDefault().postSticky(new FriendChangeEvent());
                        break;
                }
            }
        }
    }
}
