package com.example.gy.musicgame.activity;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.gy.musicgame.R;
import com.example.gy.musicgame.adapter.NewFriendItemAdapter;
import com.example.gy.musicgame.event.NewFriendListEvent;
import com.example.gy.musicgame.model.NewFriendVo;

import java.util.List;

public class NewFriendActivity extends BaseActivity {
    private ListView listView;
    private LinearLayout llNoData;

    @Override
    protected void initView() {
        listView = fd(R.id.listView);
        llNoData = fd(R.id.ll_no_data);
    }

    @Override
    protected void initData() {
    }

    private void getNewFriendNotice(List<NewFriendVo> newFriendVoList) {
        if (newFriendVoList != null && newFriendVoList.size() > 0) {
            llNoData.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            NewFriendItemAdapter itemAdapter = new NewFriendItemAdapter(newFriendVoList, mActivity);
            listView.setAdapter(itemAdapter);
            itemAdapter.setOnNewFriendListener(new NewFriendItemAdapter.OnNewFriendListener() {
                @Override
                public void handler() {
                    clearMsgList();
                }
            });
        } else {
            llNoData.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
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
