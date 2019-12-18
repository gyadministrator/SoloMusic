package com.android.customer.music.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Date:2019/12/9
 * TIME:10:53
 * author:fldserver
 * email:1984629668@qq.com
 **/
@DatabaseTable
public class BottomBarVo implements Serializable {
    @DatabaseField(generatedId = true)
    public int id;
    @DatabaseField
    private String image;
    @DatabaseField
    private String name;
    @DatabaseField
    private String author;
    @DatabaseField
    private String path;
    @DatabaseField
    private String songId;
    @DatabaseField
    private String tingUid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
