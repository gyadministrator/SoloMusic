package com.example.gy.musicgame.event;

import com.example.gy.musicgame.model.NewFriendVo;

import java.util.List;

/**
 * Date:2019/12/13
 * TIME:16:31
 * author:fldserver
 * email:1984629668@qq.com
 **/
public class NewFriendListEvent {
    private List<NewFriendVo> list;

    public List<NewFriendVo> getList() {
        return list;
    }

    public void setList(List<NewFriendVo> list) {
        this.list = list;
    }
}
