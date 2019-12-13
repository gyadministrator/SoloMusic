package com.example.gy.musicgame.activity;

import android.widget.ListView;

import com.example.gy.musicgame.R;
import com.example.gy.musicgame.adapter.NewFriendItemAdapter;
import com.example.gy.musicgame.event.NewFriendListEvent;
import com.example.gy.musicgame.model.NewFriendVo;

import java.util.List;

public class NewFriendActivity extends BaseActivity {
    private ListView listView;

    @Override
    protected void initView() {
        listView = fd(R.id.listView);
    }

    @Override
    protected void initData() {
    }

    private void getNewFriendNotice(List<NewFriendVo> newFriendVoList) {
        if (newFriendVoList != null && newFriendVoList.size() > 0) {
            NewFriendItemAdapter itemAdapter = new NewFriendItemAdapter(newFriendVoList, mActivity);
            listView.setAdapter(itemAdapter);

            itemAdapter.setOnNewFriendListener(new NewFriendItemAdapter.OnNewFriendListener() {
                @Override
                public void handler() {
                    clearMsgList();
                }
            });
        }
    }

    @Override
    public void onEvent(Object object) {
        super.onEvent(object);
        if (object instanceof NewFriendListEvent) {
            NewFriendListEvent newFriendListEvent = (NewFriendListEvent) object;
            getNewFriendNotice(newFriendListEvent.getList());
        }
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_new_friend;
    }
}
