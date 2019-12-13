package com.example.gy.musicgame.activity;

import android.widget.ListView;

import com.example.gy.musicgame.R;
import com.example.gy.musicgame.adapter.NewFriendItemAdapter;
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
        getNewFriendNotice();
    }

    private void getNewFriendNotice() {
        List<NewFriendVo> newFriendVoList = getNewFriendVoList();
        if (newFriendVoList != null && newFriendVoList.size() > 0) {
            NewFriendItemAdapter itemAdapter = new NewFriendItemAdapter(newFriendVoList, mActivity);
            listView.setAdapter(itemAdapter);
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
