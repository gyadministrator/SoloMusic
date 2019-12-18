package com.android.customer.music.helper;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.android.customer.music.chatui.enity.MessageInfo;
import com.android.customer.music.model.BottomBarVo;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Date:2019/12/17
 * TIME:11:26
 * author:fldserver
 * email:1984629668@qq.com
 **/
public class DBHelper extends OrmLiteSqliteOpenHelper {
    /**
     * 数据库名字
     */
    private static final String DB_NAME = "Solo.db";
    /**
     * 数据库版本
     */
    private static final int DB_VERSION = 1;

    /**
     * 用来存放Dao
     */
    private Map<String, Dao> map = new HashMap<>();


    private static DBHelper instance;


    /**
     * 获取单例
     *
     * @param context context
     * @return
     */
    public static synchronized DBHelper getHelper(Context context) {
        context = context.getApplicationContext();
        if (instance == null) {
            synchronized (DBHelper.class) {
                if (instance == null) {
                    instance = new DBHelper(context);
                }
            }
        }
        return instance;
    }


    /**
     * 构造方法
     *
     * @param context context
     */
    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * 这里创建表
     */
    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
        // 创建表
        try {
            TableUtils.createTable(connectionSource, MessageInfo.class);
            TableUtils.createTable(connectionSource, BottomBarVo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 这里进行更新表操作
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion,
                          int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, MessageInfo.class, true);
            TableUtils.dropTable(connectionSource, BottomBarVo.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过类来获得指定的Dao
     */
    public synchronized Dao getDao(Class clazz) throws SQLException {
        Dao dao;
        String className = clazz.getSimpleName();
        dao = super.getDao(clazz);
        if (!map.containsKey(className)) {
            map.put(className, dao);
        }
        return dao;
    }


    /**
     * 释放资源
     */
    @Override
    public void close() {
        super.close();
        for (String key : map.keySet()) {
            Dao dao = map.get(key);
            dao = null;
        }
    }
}