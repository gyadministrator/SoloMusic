package com.android.chat.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerTitleStrip;

import com.android.chat.ui.R;
import com.android.chat.ui.adapter.ChatAdapter;
import com.android.chat.ui.bean.AudioMsgBody;
import com.android.chat.ui.bean.FileMsgBody;
import com.android.chat.ui.bean.ImageMsgBody;
import com.android.chat.ui.bean.Message;
import com.android.chat.ui.bean.MsgSendStatus;
import com.android.chat.ui.bean.MsgType;
import com.android.chat.ui.bean.TextMsgBody;
import com.android.chat.ui.bean.VideoMsgBody;
import com.android.chat.ui.util.ChatUiHelper;
import com.android.chat.ui.util.FileUtils;
import com.android.chat.ui.util.LogUtil;
import com.android.chat.ui.util.PictureFileUtil;
import com.android.chat.ui.widget.MediaManager;
import com.android.chat.ui.widget.RecordButton;
import com.android.chat.ui.widget.SetPermissionDialog;
import com.android.chat.ui.widget.StateButton;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.functions.Consumer;


public class ChatActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private LinearLayout mLlContent;
    private RecyclerView mRvChat;
    private EditText mEtContent;
    private RelativeLayout mRlBottomLayout;//表情,添加底部布局
    private ImageView mIvAdd;
    private ImageView mIvEmo;
    private StateButton mBtnSend;//发送按钮
    private ImageView mIvAudio;//录音图片
    private RecordButton mBtnAudio;//录音按钮
    private LinearLayout mLlEmotion;//表情布局
    private LinearLayout mLlAdd;//添加布局
    private SwipeRefreshLayout mSwipeRefresh;//下拉刷新
    private ChatAdapter mAdapter;
    private RelativeLayout rlPhoto, rlVideo, rlLocation, rlFile;
    public static final String mSenderId = "right";
    public static final String mTargetId = "left";
    public static final int REQUEST_CODE_IMAGE = 0000;
    public static final int REQUEST_CODE_VEDIO = 1111;
    public static final int REQUEST_CODE_FILE = 2222;
    private ImmersionBar immersionBar;
    private String chatTitle;
    private TextView tvChatTitle;
    private RelativeLayout rlBack;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initPermission();
        initView();
        initContent();
    }

    public static void startActivity(Activity activity, String title) {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra("title", title);
        activity.startActivity(intent);
    }

    private void initPermission() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestPermission();
            }
        }, 100);
    }

    @SuppressLint("CheckResult")
    private void requestPermission() {
        RxPermissions rxPermission = new RxPermissions(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            rxPermission
                    .request(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,//存储权限
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO
                    )
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) {
                            if (!aBoolean) {
                                SetPermissionDialog mSetPermissionDialog = new SetPermissionDialog(ChatActivity.this);
                                mSetPermissionDialog.show();
                                mSetPermissionDialog.setConfirmCancelListener(new SetPermissionDialog.OnConfirmCancelClickListener() {
                                    @Override
                                    public void onLeftClick() {
                                        finish();
                                    }

                                    @Override
                                    public void onRightClick() {
                                        finish();
                                    }
                                });
                            }
                        }
                    });
        }
    }

    private void initView() {
        mLlContent = findViewById(R.id.llContent);
        mRvChat = findViewById(R.id.rv_chat_list);
        mEtContent = findViewById(R.id.et_content);
        mRlBottomLayout = findViewById(R.id.bottom_layout);
        mIvAdd = findViewById(R.id.ivAdd);
        mIvEmo = findViewById(R.id.ivEmo);
        mBtnSend = findViewById(R.id.btn_send);
        mIvAudio = findViewById(R.id.ivAudio);
        mBtnAudio = findViewById(R.id.btnAudio);
        mLlEmotion = findViewById(R.id.rlEmotion);
        mLlAdd = findViewById(R.id.llAdd);
        mSwipeRefresh = findViewById(R.id.swipe_chat);
        rlPhoto = findViewById(R.id.rlPhoto);
        rlFile = findViewById(R.id.rlFile);
        rlLocation = findViewById(R.id.rlLocation);
        rlVideo = findViewById(R.id.rlVideo);
        tvChatTitle = findViewById(R.id.common_toolbar_title);
        rlBack = findViewById(R.id.common_toolbar_back);

        mBtnSend.setOnClickListener(this);
        rlVideo.setOnClickListener(this);
        rlLocation.setOnClickListener(this);
        rlFile.setOnClickListener(this);
        rlPhoto.setOnClickListener(this);
        rlBack.setOnClickListener(this);

        immersionBar = ImmersionBar.with(this);
        immersionBar.statusBarDarkFont(true, 0.2f)
                .barColor(R.color.top_color)
                .init();

        Intent intent = getIntent();
        chatTitle = intent.getStringExtra("title");
        if (!TextUtils.isEmpty(chatTitle)) {
            tvChatTitle.setText(chatTitle);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (immersionBar != null) {
            immersionBar.destroy();
        }
    }

    private ImageView ivAudio;

    protected void initContent() {
        mAdapter = new ChatAdapter(this, new ArrayList<Message>());
        LinearLayoutManager mLinearLayout = new LinearLayoutManager(this);
        mRvChat.setLayoutManager(mLinearLayout);
        mRvChat.setAdapter(mAdapter);
        mSwipeRefresh.setOnRefreshListener(this);
        initChatUi();
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                Message item = mAdapter.getItem(position);
                if (item != null) {
                    final boolean isSend = item.getSenderId().equals(ChatActivity.mSenderId);
                    if (ivAudio != null) {
                        if (isSend) {
                            ivAudio.setBackgroundResource(R.mipmap.audio_animation_list_right_3);
                        } else {
                            ivAudio.setBackgroundResource(R.mipmap.audio_animation_list_left_3);
                        }
                        ivAudio = null;
                        MediaManager.reset();
                    } else {
                        ivAudio = view.findViewById(R.id.ivAudio);
                        MediaManager.reset();
                        if (isSend) {
                            ivAudio.setBackgroundResource(R.drawable.audio_animation_right_list);
                        } else {
                            ivAudio.setBackgroundResource(R.drawable.audio_animation_left_list);
                        }
                        AnimationDrawable drawable = (AnimationDrawable) ivAudio.getBackground();
                        drawable.start();
                        MediaManager.playSound(ChatActivity.this, ((AudioMsgBody) mAdapter.getData().get(position).getBody()).getLocalPath(), new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                if (isSend) {
                                    ivAudio.setBackgroundResource(R.mipmap.audio_animation_list_right_3);
                                } else {
                                    ivAudio.setBackgroundResource(R.mipmap.audio_animation_list_left_3);
                                }

                                MediaManager.release();
                            }
                        });
                    }
                }
            }
        });
    }


    @Override
    public void onRefresh() {
        //下拉刷新模拟获取历史消息
        List<Message> mReceiveMsgList = new ArrayList<>();
        //构建文本消息
        Message mMessgaeText = getBaseReceiveMessage(MsgType.TEXT);
        TextMsgBody mTextMsgBody = new TextMsgBody();
        mTextMsgBody.setMessage("收到的消息");
        mMessgaeText.setBody(mTextMsgBody);
        mReceiveMsgList.add(mMessgaeText);
        //构建图片消息
        Message mMessgaeImage = getBaseReceiveMessage(MsgType.IMAGE);
        ImageMsgBody mImageMsgBody = new ImageMsgBody();
        mImageMsgBody.setThumbUrl("https://c-ssl.duitang.com/uploads/item/201208/30/20120830173930_PBfJE.thumb.700_0.jpeg");
        mMessgaeImage.setBody(mImageMsgBody);
        mReceiveMsgList.add(mMessgaeImage);
        //构建文件消息
        Message mMessgaeFile = getBaseReceiveMessage(MsgType.FILE);
        FileMsgBody mFileMsgBody = new FileMsgBody();
        mFileMsgBody.setDisplayName("收到的文件");
        mFileMsgBody.setSize(12);
        mMessgaeFile.setBody(mFileMsgBody);
        mReceiveMsgList.add(mMessgaeFile);
        mAdapter.addData(0, mReceiveMsgList);
        mSwipeRefresh.setRefreshing(false);
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initChatUi() {
        //mBtnAudio
        final ChatUiHelper mUiHelper = ChatUiHelper.with(this);
        mUiHelper.bindContentLayout(mLlContent)
                .bindttToSendButton(mBtnSend)
                .bindEditText(mEtContent)
                .bindBottomLayout(mRlBottomLayout)
                .bindEmojiLayout(mLlEmotion)
                .bindAddLayout(mLlAdd)
                .bindToAddButton(mIvAdd)
                .bindToEmojiButton(mIvEmo)
                .bindAudioBtn(mBtnAudio)
                .bindAudioIv(mIvAudio)
                .bindEmojiData(this);
        //底部布局弹出,聊天列表上滑
        mRvChat.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    mRvChat.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mAdapter.getItemCount() > 0) {
                                mRvChat.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                            }
                        }
                    });
                }
            }
        });
        //点击空白区域关闭键盘
        mRvChat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mUiHelper.hideBottomLayout(false);
                mUiHelper.hideSoftInput();
                mEtContent.clearFocus();
                mIvEmo.setImageResource(R.mipmap.ic_emoji);
                return false;
            }
        });
        //
        mBtnAudio.setOnFinishedRecordListener(new RecordButton.OnFinishedRecordListener() {
            @Override
            public void onFinishedRecord(String audioPath, int time) {
                LogUtil.d("录音结束回调");
                File file = new File(audioPath);
                if (file.exists()) {
                    sendAudioMessage(audioPath, time);
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_FILE:
                    String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                    LogUtil.d("获取到的文件路径:" + filePath);
                    sendFileMessage(mSenderId, mTargetId, filePath);
                    break;
                case REQUEST_CODE_IMAGE:
                    // 图片选择结果回调
                    List<LocalMedia> selectListPic = PictureSelector.obtainMultipleResult(data);
                    for (LocalMedia media : selectListPic) {
                        LogUtil.d("获取图片路径成功:" + media.getPath());
                        sendImageMessage(media);
                    }
                    break;
                case REQUEST_CODE_VEDIO:
                    // 视频选择结果回调
                    List<LocalMedia> selectListVideo = PictureSelector.obtainMultipleResult(data);
                    for (LocalMedia media : selectListVideo) {
                        LogUtil.d("获取视频路径成功:" + media.getPath());
                        sendVedioMessage(media);
                    }
                    break;
            }
        }
    }


    //文本消息
    private void sendTextMsg(String hello) {
        final Message mMessgae = getBaseSendMessage(MsgType.TEXT);
        TextMsgBody mTextMsgBody = new TextMsgBody();
        mTextMsgBody.setMessage(hello);
        mMessgae.setBody(mTextMsgBody);
        //开始发送
        mAdapter.addData(mMessgae);
        //模拟两秒后发送成功
        updateMsg(mMessgae);
    }


    //图片消息
    private void sendImageMessage(final LocalMedia media) {
        final Message mMessgae = getBaseSendMessage(MsgType.IMAGE);
        ImageMsgBody mImageMsgBody = new ImageMsgBody();
        mImageMsgBody.setThumbUrl(media.getCompressPath());
        mMessgae.setBody(mImageMsgBody);
        //开始发送
        mAdapter.addData(mMessgae);
        //模拟两秒后发送成功
        updateMsg(mMessgae);
    }


    //视频消息
    private void sendVedioMessage(final LocalMedia media) {
        final Message mMessgae = getBaseSendMessage(MsgType.VIDEO);
        //生成缩略图路径
        String vedioPath = media.getPath();
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(vedioPath);
        Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime();
        String imgname = System.currentTimeMillis() + ".jpg";
        String urlpath = Environment.getExternalStorageDirectory() + "/" + imgname;
        File f = new File(urlpath);
        try {
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            LogUtil.d("视频缩略图路径获取失败：" + e.toString());
            e.printStackTrace();
        }
        VideoMsgBody mImageMsgBody = new VideoMsgBody();
        mImageMsgBody.setExtra(urlpath);
        mMessgae.setBody(mImageMsgBody);
        //开始发送
        mAdapter.addData(mMessgae);
        //模拟两秒后发送成功
        updateMsg(mMessgae);

    }

    //文件消息
    private void sendFileMessage(String from, String to, final String path) {
        final Message mMessgae = getBaseSendMessage(MsgType.FILE);
        FileMsgBody mFileMsgBody = new FileMsgBody();
        mFileMsgBody.setLocalPath(path);
        mFileMsgBody.setDisplayName(FileUtils.getFileName(path));
        mFileMsgBody.setSize(FileUtils.getFileLength(path));
        mMessgae.setBody(mFileMsgBody);
        //开始发送
        mAdapter.addData(mMessgae);
        //模拟两秒后发送成功
        updateMsg(mMessgae);

    }

    //语音消息
    private void sendAudioMessage(final String path, int time) {
        final Message mMessgae = getBaseSendMessage(MsgType.AUDIO);
        AudioMsgBody mFileMsgBody = new AudioMsgBody();
        mFileMsgBody.setLocalPath(path);
        mFileMsgBody.setDuration(time);
        mMessgae.setBody(mFileMsgBody);
        //开始发送
        mAdapter.addData(mMessgae);
        //模拟两秒后发送成功
        updateMsg(mMessgae);
    }


    private Message getBaseSendMessage(MsgType msgType) {
        Message mMessgae = new Message();
        mMessgae.setUuid(UUID.randomUUID() + "");
        mMessgae.setSenderId(mSenderId);
        mMessgae.setTargetId(mTargetId);
        mMessgae.setSentTime(System.currentTimeMillis());
        mMessgae.setSentStatus(MsgSendStatus.SENDING);
        mMessgae.setMsgType(msgType);
        return mMessgae;
    }


    private Message getBaseReceiveMessage(MsgType msgType) {
        Message mMessgae = new Message();
        mMessgae.setUuid(UUID.randomUUID() + "");
        mMessgae.setSenderId(mTargetId);
        mMessgae.setTargetId(mSenderId);
        mMessgae.setSentTime(System.currentTimeMillis());
        mMessgae.setSentStatus(MsgSendStatus.SENDING);
        mMessgae.setMsgType(msgType);
        return mMessgae;
    }


    private void updateMsg(final Message mMessgae) {
        mRvChat.scrollToPosition(mAdapter.getItemCount() - 1);
        //模拟2秒后发送成功
        new Handler().postDelayed(new Runnable() {
            public void run() {
                int position = 0;
                mMessgae.setSentStatus(MsgSendStatus.SENT);
                //更新单个子条目
                for (int i = 0; i < mAdapter.getData().size(); i++) {
                    Message mAdapterMessage = mAdapter.getData().get(i);
                    if (mMessgae.getUuid().equals(mAdapterMessage.getUuid())) {
                        position = i;
                    }
                }
                mAdapter.notifyItemChanged(position);
            }
        }, 2000);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_send) {
            sendTextMsg(mEtContent.getText().toString());
            mEtContent.setText("");
        } else if (id == R.id.rlPhoto) {
            PictureFileUtil.openGalleryPic(ChatActivity.this, REQUEST_CODE_IMAGE);
        } else if (id == R.id.rlVideo) {
            PictureFileUtil.openGalleryAudio(ChatActivity.this, REQUEST_CODE_VEDIO);
        } else if (id == R.id.rlFile) {
            PictureFileUtil.openFile(ChatActivity.this, REQUEST_CODE_FILE);
        } else if (id == R.id.rlLocation) {
            Toast.makeText(this, "位置", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.common_toolbar_back) {
            onBackPressed();
        }
    }
}
