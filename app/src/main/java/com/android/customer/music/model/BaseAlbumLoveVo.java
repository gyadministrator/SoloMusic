package com.android.customer.music.model;

/**
 * Date:2019/12/16
 * TIME:9:19
 * author:fldserver
 * email:1984629668@qq.com
 **/
public class BaseAlbumLoveVo extends LoveAlbumVo{
    private Integer id;
    private String addTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }
}
