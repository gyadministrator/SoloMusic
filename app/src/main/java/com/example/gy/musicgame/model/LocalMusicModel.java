package com.example.gy.musicgame.model;

/**
 * Date:2019/12/11
 * TIME:16:38
 * author:fldserver
 * email:1984629668@qq.com
 **/
public class LocalMusicModel {
    private String name;//歌曲名
    private String singer;//歌手
    private long size;//歌曲所占空间大小
    private int duration;//歌曲时间长度
    private String path;//歌曲地址
    private long albumId;//图片id
    private long id;//歌曲id

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
