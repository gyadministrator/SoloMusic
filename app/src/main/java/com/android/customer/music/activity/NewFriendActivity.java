package com.android.customer.music.activity;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.customer.music.R;
import com.android.customer.music.adapter.NewFriendItemAdapter;
import com.android.customer.music.event.NewFriendListEvent;
import com.android.customer.music.model.NewFriendVo;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.friendship.TIMFriendPendencyItem;
import com.tencent.imsdk.friendship.TIMFriendPendencyRequest;
import com.tencent.imsdk.friendship.TIMFriendPendencyResponse;
import com.tencent.imsdk.friendship.TIMPendencyType;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;

import java.util.ArrayList;
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
        initPendencyList();
    }

    private void initPendencyList() {
        TIMFriendPendencyRequest request = new TIMFriendPendencyRequest();
        request.setTimPendencyGetType(TIMPendencyType.TIM_PENDENCY_BOTH);
        request.setNumPerPage(100);
        TIMFriendshipManager.getInstance().getPendencyList(request, new TIMValueCallBack<TIMFriendPendencyResponse>() {
            @Override
            public void onError(int i, String s) {
                ToastUtil.toastShortMessage("获取未读事件失败：" + i + " " + s);
            }

            @Override
            public void onSuccess(TIMFriendPendencyResponse timFriendPendencyResponse) {
                List<TIMFriendPendencyItem> items = timFriendPendencyResponse.getItems();
                List<NewFriendVo> newFriendVoList = new ArrayList<>();
                if (items != null && items.size() > 0) {
                    for (TIMFriendPendencyItem timFriendPendencyItem : items) {
                        NewFriendVo newFriendVo = new NewFriendVo();
                        newFriendVo.setReason(timFriendPendencyItem.getAddWording());
                        newFriendVo.setTitle("歌友通知");
                        newFriendVo.setUsername(timFriendPendencyItem.getIdentifier());
                        newFriendVoList.add(newFriendVo);
                    }
                }

                getNewFriendNotice(newFriendVoList);
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_new_friend;
    }
}
