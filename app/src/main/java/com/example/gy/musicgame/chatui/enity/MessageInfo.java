package com.example.gy.musicgame.chatui.enity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
/**
 * 作者：Rance on 2016/12/14 14:13
 * 邮箱：rance935@163.com
 */
@DatabaseTable
public class MessageInfo implements Serializable{
    @DatabaseField(id = true)
    public int id;
    @DatabaseField
    private int type;
    @DatabaseField
    private String content;
    @DatabaseField
    private String filepath;
    @DatabaseField
    private int sendState;
    @DatabaseField
    private String time;
    @DatabaseField
    private String header;
    @DatabaseField
    private String imageUrl;
    @DatabaseField
    private long voiceTime;
    @DatabaseField
    private String msgId;
    @DatabaseField
    private int msgType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_VOICE = 1;
    public static final int TYPE_IMAGE = 2;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public int getSendState() {
        return sendState;
    }

    public void setSendState(int sendState) {
        this.sendState = sendState;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getVoiceTime() {
        return voiceTime;
    }

    public void setVoiceTime(long voiceTime) {
        this.voiceTime = voiceTime;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    @Override
    public String toString() {
        return "MessageInfo{" +
                "type=" + type +
                ", content='" + content + '\'' +
                ", filepath='" + filepath + '\'' +
                ", sendState=" + sendState +
                ", time='" + time + '\'' +
                ", header='" + header + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", voiceTime=" + voiceTime +
                ", msgId='" + msgId + '\'' +
                ", msgType=" + msgType +
                '}';
    }
}
