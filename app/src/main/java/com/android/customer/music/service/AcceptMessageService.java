package com.android.customer.music.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.android.customer.music.topmessage.view.WindowHeadToast;
import com.blankj.utilcode.util.ToastUtils;
import com.android.customer.music.api.Api;
import com.android.customer.music.chatui.enity.MessageInfo;
import com.android.customer.music.chatui.util.Constants;
import com.android.customer.music.helper.RetrofitHelper;
import com.android.customer.music.model.IMVo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Description: Recipe
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/3/25 9:23
 */
public class AcceptMessageService extends Service {
    private final String DEFAULT_HEADER_URL = "https://gyapp.oss-cn-beijing.aliyuncs.com/splash/2018.12.24-fbab92c3-4afb-4417-ac17-bc0655f9ccef.jpg";
    private List<MessageInfo> messageInfos = new ArrayList<>();
    private static final String TAG = "AcceptMessageService";
    private EMMessage currentMessage;
    private String header;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                //弹出顶部消息
                WindowHeadToast windowHeadToast = new WindowHeadToast(getApplicationContext());
                windowHeadToast.showCustomToast(currentMessage, header);
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                initFriendState();
                EMClient.getInstance().chatManager().addMessageListener(msgListener);
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }


    private void initFriendState() {
        final Intent receiver = new Intent();
        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onContactInvited(final String username, String reason) {
                //收到好友邀请
                receiver.setAction("invited");
                receiver.putExtra("username", username);
                receiver.putExtra("reason", reason);
                sendBroadcast(receiver);
            }

            @Override
            public void onFriendRequestAccepted(String username) {
                //好友请求被同意
                receiver.setAction("accepted");
                receiver.putExtra("msg", username + "同意了你的好友请求");
                sendBroadcast(receiver);
            }

            @Override
            public void onFriendRequestDeclined(String username) {
                //好友请求被拒绝
                receiver.setAction("declined");
                receiver.putExtra("msg", username + "拒绝了你的好友请求");
                sendBroadcast(receiver);
            }

            @Override
            public void onContactDeleted(String username) {
                //被删除时回调此方法
                receiver.setAction("deleted");
                receiver.putExtra("msg", "你删除了" + username);
                sendBroadcast(receiver);
            }


            @Override
            public void onContactAdded(String username) {
                //增加了联系人时回调此方法
                receiver.setAction("added");
                receiver.putExtra("msg", "你添加了" + username + "为好友");
                sendBroadcast(receiver);
            }
        });
    }

    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            //收到消息
            for (EMMessage emMessage : messages) {
                acceptMessage(emMessage);
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
            //收到已读回执
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
            //收到已送达回执
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
        }
    };

    /**
     * 收到消息
     *
     * @param emMessage 消息实体
     */
    private void acceptMessage(EMMessage emMessage) {
        //注册广播监听
        Intent receiver = new Intent();
        receiver.setAction("accept_message");
        Bundle bundle = new Bundle();
        bundle.putParcelable("message", emMessage);
        receiver.putExtra("message", bundle);
        sendBroadcast(receiver);

        receiver.setAction("accept");
        sendBroadcast(receiver);
        final MessageInfo message = new MessageInfo();
        EMMessageBody messageBody = emMessage.getBody();
        switch (emMessage.getType()) {
            //文本
            case TXT:
                EMTextMessageBody emTextMessageBody = (EMTextMessageBody) messageBody;
                String content = emTextMessageBody.getMessage();
                message.setContent(content);
                break;
            //图片
            case IMAGE:
                EMImageMessageBody emImageMessageBody = (EMImageMessageBody) messageBody;
                String thumbnailUrl = emImageMessageBody.getThumbnailUrl();
                message.setImageUrl(thumbnailUrl);
                break;
            //语音
            case VOICE:
                EMVoiceMessageBody emVoiceMessageBody = (EMVoiceMessageBody) messageBody;
                String remoteUrl = emVoiceMessageBody.getRemoteUrl();
                message.setFilepath(remoteUrl);
                message.setVoiceTime(emVoiceMessageBody.getLength());
                break;
        }
        if (emMessage.direct() == EMMessage.Direct.SEND) {
            message.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
        } else if (emMessage.direct() == EMMessage.Direct.RECEIVE) {
            message.setType(Constants.CHAT_ITEM_TYPE_LEFT);
        }
        message.setHeader(DEFAULT_HEADER_URL);
        //查询用户
        new Thread(new Runnable() {
            @Override
            public void run() {
                queryUser(emMessage.getUserName(), message);
            }
        }).start();
        messageInfos.add(message);
        header = message.getHeader();
        currentMessage = emMessage;
        mHandler.sendEmptyMessage(0);
    }

    private void queryUser(String username, MessageInfo messageInfo) {
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(com.android.customer.music.constant.Constants.SERVER_URL);
        Observable<Map> observable = api.queryByAccount(username);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Map>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Map map) {
                        Gson gson = new Gson();
                        String json = gson.toJson(map.get("data"));
                        Type type = new TypeToken<IMVo>() {
                        }.getType();
                        IMVo imVo = gson.fromJson(json, type);

                        if (imVo != null && !TextUtils.isEmpty(imVo.getAvatar())) {
                            messageInfo.setHeader(imVo.getAvatar());
                        }
                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showShort(Objects.requireNonNull(e.getMessage()));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
