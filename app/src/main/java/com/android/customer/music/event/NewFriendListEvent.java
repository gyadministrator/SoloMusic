package com.android.customer.music.event;

import com.android.customer.music.model.NewFriendVo;

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
