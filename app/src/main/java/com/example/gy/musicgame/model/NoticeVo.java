package com.example.gy.musicgame.model;

/**
 * Date:2019/12/11
 * TIME:15:30
 * author:fldserver
 * email:1984629668@qq.com
 **/
public class NoticeVo {
    private String title;
    private String content;
    private String time;
    private String url;
    private int noticeId;

    public int getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(int noticeId) {
        this.noticeId = noticeId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public NoticeVo(String title, String content, String time) {
        this.title = title;
        this.content = content;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
