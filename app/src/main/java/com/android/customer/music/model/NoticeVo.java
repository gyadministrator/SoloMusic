package com.android.customer.music.model;

import java.io.Serializable;

/**
 * Date:2019/12/11
 * TIME:15:30
 * author:fldserver
 * email:1984629668@qq.com
 **/
public class NoticeVo implements Serializable {

    /**
     * id : 1
     * title : 测试1
     * addTime : 2019-12-12 11:08:31
     * url : http://www.baidu.com/
     * image : https://back-1252357563.cos.ap-chengdu.myqcloud.com/x5n1ayn4byxqapa040od.png
     * content : dfdjmcm mcc .dkdl . ..x
     */

    private int id;
    private String title;
    private String addTime;
    private String url;
    private String image;
    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
