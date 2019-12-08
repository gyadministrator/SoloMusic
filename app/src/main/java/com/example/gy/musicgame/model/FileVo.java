package com.example.gy.musicgame.model;

/**
 * Description: SoloMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/12/8 18:36
 */
public class FileVo {

    /**
     * id : 71
     * key : 6wjk02mrn1ef0pk8hzow.png
     * name : 话费充值.png
     * type : image/png
     * size : 854
     * url : https://back-1252357563.cos.ap-chengdu.myqcloud.com/6wjk02mrn1ef0pk8hzow.png
     * addTime : 2019-12-08 18:25:30
     * updateTime : 2019-12-08 18:25:30
     */

    private int id;
    private String key;
    private String name;
    private String type;
    private int size;
    private String url;
    private String addTime;
    private String updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
