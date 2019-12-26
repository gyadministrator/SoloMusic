package com.android.chat.ui.bean;

import java.io.Serializable;

public class MsgBody implements Serializable {

    private MsgType localMsgType;

    public MsgType getLocalMsgType() {
        return localMsgType;
    }

    public void setLocalMsgType(MsgType localMsgType) {
        this.localMsgType = localMsgType;
    }
}
