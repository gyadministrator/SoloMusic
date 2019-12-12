package com.example.gy.musicgame.model;

import java.util.Date;

/**
 * author:Administrator
 * time:2018/12/1
 * decription:
 **/
public class User {
    private int code;
    private String message;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String id;

        /**
         * 用户名
         */
        private String username;

        /**
         * 密码
         */
        private String password;

        /**
         * 所在地址
         */
        private String address;

        /**
         * 性别
         */
        private String sex;

        /**
         * 头像
         */
        private String image;

        /**
         * APP的包名
         */
        private String applicationId;

        /**
         * 创建时间
         */
        private String createTime;

        @Override
        public String toString() {
            return "User{" +
                    "id='" + id + '\'' +
                    ", username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    ", address='" + address + '\'' +
                    ", sex='" + sex + '\'' +
                    ", image='" + image + '\'' +
                    ", applicationId='" + applicationId + '\'' +
                    ", createTime=" + createTime +
                    '}';
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getApplicationId() {
            return applicationId;
        }

        public void setApplicationId(String applicationId) {
            this.applicationId = applicationId;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
    }
}
