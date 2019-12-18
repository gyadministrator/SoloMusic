package com.android.customer.music.model;

/**
 * Description: SoloMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/12/8 12:19
 */
public class ApkModel {

    /**
     * id : 2
     * apkName : Solo音乐
     * apkVersion : 2.0
     * apkCode : 3
     * downloadUrl : http://123.207.93.53/MusicWebServer/apk/music_game.apk
     * apkMd5 : 1212
     * apkPackage : com.android.customer.music
     * isUpdate : 1
     * addTime : 2019-12-08 12:02:33
     * updateTime : 2019-12-08 12:02:35
     * content : 更新2
     */

    private int id;
    private String apkName;
    private String apkVersion;
    private int apkCode;
    private String downloadUrl;
    private String apkMd5;
    private String apkPackage;
    private int isUpdate;
    private String addTime;
    private String updateTime;
    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    public String getApkVersion() {
        return apkVersion;
    }

    public void setApkVersion(String apkVersion) {
        this.apkVersion = apkVersion;
    }

    public int getApkCode() {
        return apkCode;
    }

    public void setApkCode(int apkCode) {
        this.apkCode = apkCode;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getApkMd5() {
        return apkMd5;
    }

    public void setApkMd5(String apkMd5) {
        this.apkMd5 = apkMd5;
    }

    public String getApkPackage() {
        return apkPackage;
    }

    public void setApkPackage(String apkPackage) {
        this.apkPackage = apkPackage;
    }

    public int getIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(int isUpdate) {
        this.isUpdate = isUpdate;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
