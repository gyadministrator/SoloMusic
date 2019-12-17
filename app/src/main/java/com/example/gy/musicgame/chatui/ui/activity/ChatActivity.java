package com.example.gy.musicgame.chatui.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.recipe.chatui.adapter.CommonFragmentPagerAdapter;
import com.android.recipe.chatui.enity.FullImageInfo;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.activity.BaseActivity;
import com.example.gy.musicgame.chatui.adapter.ChatAdapter;
import com.example.gy.musicgame.chatui.enity.MessageInfo;
import com.example.gy.musicgame.chatui.ui.fragment.ChatEmotionFragment;
import com.example.gy.musicgame.chatui.ui.fragment.ChatFunctionFragment;
import com.example.gy.musicgame.chatui.util.Constants;
import com.example.gy.musicgame.chatui.util.GlobalOnItemClickManagerUtils;
import com.example.gy.musicgame.chatui.util.MediaManager;
import com.example.gy.musicgame.chatui.widget.EmotionInputDetector;
import com.example.gy.musicgame.chatui.widget.NoScrollViewPager;
import com.example.gy.musicgame.chatui.widget.StateButton;
import com.example.gy.musicgame.dao.MessageInfoDao;
import com.example.gy.musicgame.listener.IMessageListener;
import com.example.gy.musicgame.model.User;
import com.example.gy.musicgame.model.UserInfoVo;
import com.example.gy.musicgame.utils.MessageUtils;
import com.example.gy.musicgame.utils.UserManager;
import com.example.gy.musicgame.view.TitleView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.jude.easyrecyclerview.EasyRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 作者：Rance on 2016/11/29 10:47
 * 邮箱：rance935@163.com
 */
public class ChatActivity extends BaseActivity {

    @BindView(R.id.chat_list)
    EasyRecyclerView chatList;
    @BindView(R.id.emotion_voice)
    ImageView emotionVoice;
    @BindView(R.id.edit_text)
    EditText editText;
    @BindView(R.id.voice_text)
    TextView voiceText;
    @BindView(R.id.emotion_button)
    ImageView emotionButton;
    @BindView(R.id.emotion_add)
    ImageView emotionAdd;
    @BindView(R.id.emotion_send)
    StateButton emotionSend;
    @BindView(R.id.viewpager)
    NoScrollViewPager viewpager;
    @BindView(R.id.emotion_layout)
    RelativeLayout emotionLayout;
    @BindView(R.id.titleView)
    TitleView titleView;

    private EmotionInputDetector mDetector;
    private ArrayList<Fragment> fragments;
    private ChatEmotionFragment chatEmotionFragment;
    private ChatFunctionFragment chatFunctionFragment;
    private CommonFragmentPagerAdapter adapter;

    private ChatAdapter chatAdapter;
    private LinearLayoutManager layoutManager;
    private List<MessageInfo> messageInfos = new ArrayList<>();
    //录音相关
    int animationRes = 0;
    int res = 0;
    AnimationDrawable animationDrawable = null;
    private ImageView animView;
    private UserInfoVo userInfoVo;
    private String username;
    private MessageReceiver messageReceiver;


    @Override
    protected void initView() {
        ButterKnife.bind(this);
        initWidget();
        messageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("accept_message");
        registerReceiver(messageReceiver, filter);
        getRecoredMessage(username);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        userInfoVo = UserManager.getUserInfoVo(mActivity);
        titleView.setTitle(username);
    }

    @Override
    protected void initAction() {
        //获取历史消息
        //保存数据库
        MessageInfoDao messageInfoDao = new MessageInfoDao(mActivity);
        List<MessageInfo> list = messageInfoDao.queryForAll();
        messageInfos.addAll(list);
        if (chatAdapter != null) {
            chatAdapter.addAll(messageInfos);
            chatAdapter.notifyDataSetChanged();
        }
    }


    public static void startActivity(Activity activity, String username) {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra("username", username);
        activity.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_chat_main;
    }

    private void getRecoredMessage(final String username) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);
                //获取此会话的所有消息
                if (conversation != null) {
                    List<EMMessage> messages = conversation.getAllMessages();
                    for (EMMessage emMessage : messages) {
                        acceptMessage(emMessage);
                    }
                }
            }
        }).start();
    }


    private void initWidget() {
        fragments = new ArrayList<>();
        chatEmotionFragment = new ChatEmotionFragment();
        fragments.add(chatEmotionFragment);
        chatFunctionFragment = new ChatFunctionFragment();
        fragments.add(chatFunctionFragment);
        adapter = new CommonFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(0);

        mDetector = EmotionInputDetector.with(this)
                .setEmotionView(emotionLayout)
                .setViewPager(viewpager)
                .bindToContent(chatList)
                .bindToEditText(editText)
                .bindToEmotionButton(emotionButton)
                .bindToAddButton(emotionAdd)
                .bindToSendButton(emotionSend)
                .bindToVoiceButton(emotionVoice)
                .bindToVoiceText(voiceText)
                .build();

        GlobalOnItemClickManagerUtils globalOnItemClickListener = GlobalOnItemClickManagerUtils.getInstance(this);
        globalOnItemClickListener.attachToEditText(editText);

        chatAdapter = new ChatAdapter(this);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        chatList.setLayoutManager(layoutManager);
        chatList.setAdapter(chatAdapter);
        chatList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        chatAdapter.handler.removeCallbacksAndMessages(null);
                        //chatAdapter.notifyDataSetChanged();
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        chatAdapter.handler.removeCallbacksAndMessages(null);
                        mDetector.hideEmotionLayout(false);
                        mDetector.hideSoftInput();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        chatAdapter.addItemClickListener(itemClickListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mDetector.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * item点击事件
     */
    private ChatAdapter.onItemClickListener itemClickListener = new ChatAdapter.onItemClickListener() {
        @Override
        public void onHeaderClick(int position) {
            //头像点击事件处理
        }

        @Override
        public void onImageClick(View view, int position) {
            int location[] = new int[2];
            view.getLocationOnScreen(location);
            FullImageInfo fullImageInfo = new FullImageInfo();
            fullImageInfo.setLocationX(location[0]);
            fullImageInfo.setLocationY(location[1]);
            fullImageInfo.setWidth(view.getWidth());
            fullImageInfo.setHeight(view.getHeight());
            fullImageInfo.setImageUrl(messageInfos.get(position).getImageUrl());
            EventBus.getDefault().postSticky(fullImageInfo);
            startActivity(new Intent(ChatActivity.this, FullImageActivity.class));
            overridePendingTransition(0, 0);
        }

        @Override
        public void onVoiceClick(final ImageView imageView, final int position) {
            if (animView != null) {
                animView.setImageResource(res);
                animView = null;
            }
            switch (messageInfos.get(position).getType()) {
                case 1:
                    animationRes = R.drawable.voice_left;
                    res = R.mipmap.icon_voice_left3;
                    break;
                case 2:
                    animationRes = R.drawable.voice_right;
                    res = R.mipmap.icon_voice_right3;
                    break;
            }
            animView = imageView;
            animView.setImageResource(animationRes);
            animationDrawable = (AnimationDrawable) imageView.getDrawable();
            animationDrawable.start();
            MediaManager.playSound(messageInfos.get(position).getFilepath(), new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    animView.setImageResource(res);
                }
            });
        }
    };

    /**
     * 处理输入的信息，发送消息
     *
     * @param messageInfo 消息实体
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEventBus(final MessageInfo messageInfo) {
        if (userInfoVo != null) {
            if (!TextUtils.isEmpty(userInfoVo.getAvatarUrl())) {
                messageInfo.setHeader(userInfoVo.getAvatarUrl());
            } else {
                messageInfo.setHeader(null);
            }
            messageInfo.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
            messageInfo.setSendState(Constants.CHAT_ITEM_SENDING);
            messageInfos.add(messageInfo);
            chatAdapter.add(messageInfo);
            chatList.scrollToPosition(chatAdapter.getCount() - 1);
            if (messageInfo.getMsgType() == MessageInfo.TYPE_TEXT) {
                //发送消息
                MessageUtils.sendTextMessage(messageInfo.getContent(), username, new IMessageListener() {
                    @Override
                    public void success() {
                        messageInfo.setSendState(Constants.CHAT_ITEM_SEND_SUCCESS);
                        chatAdapter.notifyItemChanged(chatAdapter.getCount() - 1);
                    }

                    @Override
                    public void error() {
                        messageInfo.setSendState(Constants.CHAT_ITEM_SEND_ERROR);
                        chatAdapter.notifyItemChanged(chatAdapter.getCount() - 1);
                    }
                });
            }
        }
        if (messageInfo.getMsgType() == MessageInfo.TYPE_VOICE) {
            MessageUtils.sendVoiceMessage(messageInfo.getFilepath(), (int) messageInfo.getVoiceTime(), username,
                    new IMessageListener() {
                        @Override
                        public void success() {
                            messageInfo.setSendState(Constants.CHAT_ITEM_SEND_SUCCESS);
                            chatAdapter.notifyItemChanged(chatAdapter.getCount() - 1);
                        }

                        @Override
                        public void error() {
                            messageInfo.setSendState(Constants.CHAT_ITEM_SEND_ERROR);
                            chatAdapter.notifyItemChanged(chatAdapter.getCount() - 1);
                        }
                    });
        }
        if (messageInfo.getMsgType() == MessageInfo.TYPE_IMAGE) {
            MessageUtils.sendImageMessage(messageInfo.getImageUrl(), username, new IMessageListener() {
                @Override
                public void success() {
                    messageInfo.setSendState(Constants.CHAT_ITEM_SEND_SUCCESS);
                    chatAdapter.notifyItemChanged(chatAdapter.getCount() - 1);
                }

                @Override
                public void error() {
                    messageInfo.setSendState(Constants.CHAT_ITEM_SEND_ERROR);
                    chatAdapter.notifyItemChanged(chatAdapter.getCount() - 1);
                }
            });
        }
        //保存数据库
        MessageInfoDao messageInfoDao = new MessageInfoDao(mActivity);
        messageInfoDao.add(messageInfo);
    }

    /**
     * 收到消息
     *
     * @param emMessage 消息实体
     */
    private void acceptMessage(EMMessage emMessage) {
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
                String thumbnailUrl = emImageMessageBody.getRemoteUrl();
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
        message.setHeader(null);
        messageInfos.add(message);
        chatAdapter.add(message);
        chatList.scrollToPosition(chatAdapter.getCount() - 1);

        //保存数据库
        MessageInfoDao messageInfoDao = new MessageInfoDao(mActivity);
        messageInfoDao.add(message);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mDetector.interceptBackPress()) return mDetector.interceptBackPress();
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().removeStickyEvent(this);
        EventBus.getDefault().unregister(this);
        unregisterReceiver(messageReceiver);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals("accept_message")) {
                    Bundle bundle = intent.getBundleExtra("message");
                    EMMessage message = null;
                    if (bundle != null) {
                        message = bundle.getParcelable("message");
                    }
                    if (message != null) {
                        acceptMessage(message);
                    }
                }
            }
        }
    }
}
