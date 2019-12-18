package com.android.customer.music.model;

import org.jetbrains.annotations.NotNull;

/**
 * **Create By gy
 * **Time 9:23
 * **Description MusicGame
 **/
public class ItemModel {
    private int icon;
    private String name;
    private String data;
    private int right;

    public ItemModel(int icon, String name, String data) {
        this.icon = icon;
        this.name = name;
        this.data = data;
    }

    @NotNull
    @Override
    public String toString() {
        return "Item{" +
                "icon=" + icon +
                ", name='" + name + '\'' +
                ", data='" + data + '\'' +
                ", right=" + right +
                '}';
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }
}
