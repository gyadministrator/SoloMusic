package com.example.gy.musicgame.model;

/**
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/11/2 22:00
 */
public class MusicVo {
    private String imageUrl;
    private String path;
    private String title;
    private String author;
    private String songId;

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "MusicVo{" +
                "imageUrl='" + imageUrl + '\'' +
                ", path='" + path + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", songId='" + songId + '\'' +
                '}';
    }
}
