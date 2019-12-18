package com.android.customer.music.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.android.customer.music.R;
import com.android.customer.music.model.NewFriendVo;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

/**
 * Date:2019/12/11
 * TIME:14:57
 * author:fldserver
 * email:1984629668@qq.com
 **/
public class NewFriendItemAdapter extends BaseAdapter {
    private List<NewFriendVo> list;
    private Context context;
    private OnNewFriendListener onNewFriendListener;

    public void setOnNewFriendListener(OnNewFriendListener onNewFriendListener) {
        this.onNewFriendListener = onNewFriendListener;
    }

    public interface OnNewFriendListener {
        void handler();
    }

    public NewFriendItemAdapter(List<NewFriendVo> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.new_friend_adapter, null);
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = convertView.findViewById(R.id.tv_title);
            viewHolder.tvReason = convertView.findViewById(R.id.tv_reason);
            viewHolder.tvAccept = convertView.findViewById(R.id.tv_accept);
            viewHolder.tvDecline = convertView.findViewById(R.id.tv_declare);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        NewFriendVo newFriendVo = list.get(position);
        if (newFriendVo != null) {
            viewHolder.tvTitle.setText(newFriendVo.getTitle());
            viewHolder.tvReason.setText("理由：" + newFriendVo.getReason());
        }

        viewHolder.tvAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (newFriendVo != null) {
                        EMClient.getInstance().contactManager().acceptInvitation(newFriendVo.getUsername());
                    }
                    if (newFriendVo != null) {
                        ToastUtils.showShort("你接受了" + newFriendVo.getUsername() + "的请求");
                    }
                    list.remove(position);
                    notifyDataSetChanged();
                    if (onNewFriendListener != null) {
                        onNewFriendListener.handler();
                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    ToastUtils.showShort("发生了异常，请稍后再试...");
                }
            }
        });

        viewHolder.tvDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (newFriendVo != null) {
                        EMClient.getInstance().contactManager().declineInvitation(newFriendVo.getUsername());
                    }
                    if (newFriendVo != null) {
                        ToastUtils.showShort("你拒绝了" + newFriendVo.getUsername() + "的请求");
                    }
                    list.remove(position);
                    notifyDataSetChanged();
                    if (onNewFriendListener != null) {
                        onNewFriendListener.handler();
                    }
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    ToastUtils.showShort("发生了异常，请稍后再试...");
                }
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView tvTitle;
        TextView tvReason;
        TextView tvAccept;
        TextView tvDecline;
    }
}
