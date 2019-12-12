package com.example.gy.musicgame.utils;
import com.example.gy.musicgame.listener.IMessageListener;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

/**
 * Description: Recipe
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/3/22 14:05
 */
public class MessageUtils {
    public static void sendTextMessage(String content, String toChatUserName, IMessageListener messageListener) {
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id
        EMMessage message = EMMessage.createTxtSendMessage(content, toChatUserName);
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
        messageEvent(message, messageListener);
    }

    private static void messageEvent(final EMMessage message, final IMessageListener messageListener) {
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                messageListener.success();
            }

            @Override
            public void onError(int code, String error) {
                messageListener.error();
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }

    public static void sendVoiceMessage(final String filePath, final int length, final String toChatUserName, final IMessageListener messageListener) {
        //filePath为语音文件路径，length为录音时间(秒)
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, toChatUserName);
        EMClient.getInstance().chatManager().sendMessage(message);
        messageEvent(message, messageListener);
    }

    public static void sendImageMessage(final String imagePath, final String toChatUsername, final IMessageListener messageListener) {
        //imagePath为图片本地路径，false为不发送原图（默认超过100k的图片会压缩后发给对方），需要发送原图传true
        EMMessage message = EMMessage.createImageSendMessage(imagePath, false, toChatUsername);
        EMClient.getInstance().chatManager().sendMessage(message);
        messageEvent(message, messageListener);
    }
}
