package com.android.customer.music.dao;

import android.content.Context;

import com.android.customer.music.chatui.enity.MessageInfo;
import com.android.customer.music.helper.DBHelper;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Date:2019/12/17
 * TIME:11:40
 * author:fldserver
 * email:1984629668@qq.com
 **/
public class MessageInfoDao {
    private DBHelper dbHelper;
    private Dao<MessageInfo, Integer> dao;

    /**
     * 构造方法
     * 获得数据库帮助类实例，通过传入Class对象得到相应的Dao
     *
     * @param context context
     */
    public MessageInfoDao(Context context) {
        try {
            dbHelper = DBHelper.getHelper(context);
            dao = dbHelper.getDao(MessageInfo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加一条记录
     *
     * @param messageInfo
     */
    public void add(MessageInfo messageInfo) {
        try {
            dao.create(messageInfo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除一条记录
     *
     * @param messageInfo
     */
    public void delete(MessageInfo messageInfo) {
        try {
            dao.delete(messageInfo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 更新一条记录
     *
     * @param messageInfo
     */
    public void update(MessageInfo messageInfo) {
        try {
            dao.update(messageInfo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询一条记录
     *
     * @param id
     * @return
     */
    public MessageInfo queryForId(int id) {
        MessageInfo messageInfo = null;
        try {
            messageInfo = dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messageInfo;
    }

    public List<MessageInfo> queryForMsgId(String msgId) {
        List<MessageInfo> list = null;
        try {
            list = dao.queryForEq("msgId", msgId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 查询所有记录
     *
     * @return
     */
    public List<MessageInfo> queryForAll() {
        List<MessageInfo> list = new ArrayList<>();
        try {
            list = dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
