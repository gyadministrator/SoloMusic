package com.android.customer.music.dao;

import android.content.Context;

import com.android.customer.music.helper.DBHelper;
import com.android.customer.music.model.BottomBarVo;
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
public class BottomBarDao {
    private DBHelper dbHelper;
    private Dao<BottomBarVo, Integer> dao;

    /**
     * 构造方法
     * 获得数据库帮助类实例，通过传入Class对象得到相应的Dao
     *
     * @param context context
     */
    public BottomBarDao(Context context) {
        try {
            dbHelper = DBHelper.getHelper(context);
            dao = dbHelper.getDao(BottomBarVo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加一条记录
     *
     * @param bottomBarVo
     */
    public void add(BottomBarVo bottomBarVo) {
        try {
            dao.create(bottomBarVo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除一条记录
     *
     * @param bottomBarVo
     */
    public void delete(BottomBarVo bottomBarVo) {
        try {
            dao.delete(bottomBarVo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 更新一条记录
     *
     * @param bottomBarVo
     */
    public void update(BottomBarVo bottomBarVo) {
        try {
            dao.update(bottomBarVo);
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
    public BottomBarVo queryForId(int id) {
        BottomBarVo bottomBarVo = null;
        try {
            bottomBarVo = dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bottomBarVo;
    }

    public List<BottomBarVo> queryForSongId(String songId) {
        List<BottomBarVo> list = null;
        try {
            list = dao.queryForEq("songId", songId);
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
    public List<BottomBarVo> queryForAll() {
        List<BottomBarVo> list = new ArrayList<>();
        try {
            list = dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
