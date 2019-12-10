package com.example.gy.musicgame.model;

import androidx.annotation.NonNull;

import com.example.gy.musicgame.friend.Cn2Spell;

/**
 * Created by Administrator on 2016/5/25.
 */
public class UserModel implements Comparable<UserModel> {

    private String name; // 姓名
    private String pinyin; // 姓名对应的拼音
    private String firstLetter; // 拼音的首字母
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public UserModel() {
    }

    public UserModel(String name) {
        this.name = name;
        pinyin = Cn2Spell.getPinYin(name); // 根据姓名获取拼音
        firstLetter = pinyin.substring(0, 1).toUpperCase(); // 获取拼音首字母并转成大写
        if (!firstLetter.matches("[A-Z]")) { // 如果不在A-Z中则默认为“#”
            firstLetter = "#";
        }
    }

    public String getName() {
        return name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public String getFirstLetter() {
        return firstLetter;
    }


    @Override
    public int compareTo(@NonNull UserModel another) {
        if (firstLetter.equals("#") && !another.getFirstLetter().equals("#")) {
            return 1;
        } else if (!firstLetter.equals("#") && another.getFirstLetter().equals("#")){
            return -1;
        } else {
            return pinyin.compareToIgnoreCase(another.getPinyin());
        }
    }
}
