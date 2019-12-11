package com.example.gy.musicgame.model;

import java.io.Serializable;

/**
 * Date:2019/12/9
 * TIME:10:53
 * author:fldserver
 * email:1984629668@qq.com
 **/
public class BottomBarVo implements Serializable {
    private String image;
    private String name;
    private String author;
    private String path;
    private String songId;
    private String tingUid;

    public String getTingUid() {
        return tingUid;
    }

    public void setTingUid(String tingUid) {
        this.tingUid = tingUid;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
