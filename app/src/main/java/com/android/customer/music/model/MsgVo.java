package com.android.customer.music.model;

/**
 * Date:2019/12/11
 * TIME:14:58
 * author:fldserver
 * email:1984629668@qq.com
 **/
public class MsgVo {
    private String image;
    private String name;
    private String content;
    private String time;

    public MsgVo(String image, String name, String content, String time) {
        this.image = image;
        this.name = name;
        this.content = content;
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
