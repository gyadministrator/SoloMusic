package com.example.gy.musicgame.friend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.api.Api;
import com.example.gy.musicgame.constant.Constants;
import com.example.gy.musicgame.helper.RetrofitHelper;
import com.example.gy.musicgame.model.IMVo;
import com.example.gy.musicgame.model.UserModel;
import com.example.gy.musicgame.utils.HandlerUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SortAdapter extends BaseAdapter {

    private List<UserModel> list;
    private Context mContext;

    public SortAdapter(Context mContext, List<UserModel> list) {
        this.mContext = mContext;
        this.list = list;
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder;
        UserModel user = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.friend_item, null);
            viewHolder.image = view.findViewById(R.id.iv_image);
            viewHolder.name = view.findViewById(R.id.name);
            viewHolder.catalog = view.findViewById(R.id.catalog);
            viewHolder.view = view.findViewById(R.id.line);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (list.size() == 1 || position == list.size() - 1) {
            viewHolder.view.setVisibility(View.GONE);
        }
        //根据position获取首字母作为目录catalog
        String catalog = user.getFirstLetter();
        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(catalog)) {
            viewHolder.catalog.setVisibility(View.VISIBLE);
            viewHolder.catalog.setText(user.getFirstLetter().toUpperCase());
        } else {
            viewHolder.catalog.setVisibility(View.GONE);
        }

        viewHolder.name.setText(user.getName());
        if (!TextUtils.isEmpty(user.getImage())) {
            Glide.with(mContext).load(user.getImage()).into(viewHolder.image);
        } else {
            if (user.isBoot()) {
                Glide.with(mContext).load(R.mipmap.reboot).into(viewHolder.image);
            } else {
                Glide.with(mContext).load(R.mipmap.default_user).into(viewHolder.image);
            }
        }
        requestImage(viewHolder.image, user.getName());
        return view;

    }

    private void requestImage(final ImageView image, final String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
                Api api = retrofitHelper.initRetrofit(Constants.SERVER_URL);
                Observable<Map> observable = api.queryByAccount(name);
                observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Map>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Map map) {
                                Gson gson = new Gson();
                                String json = gson.toJson(map.get("data"));
                                Type type = new TypeToken<IMVo>() {
                                }.getType();
                                IMVo imVo = gson.fromJson(json, type);

                                if (imVo != null && !TextUtils.isEmpty(imVo.getAvatar())) {
                                    Glide.with(mContext).load(imVo.getAvatar()).into(image);
                                }
                                notifyDataSetChanged();
                            }

                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onError(Throwable e) {
                                ToastUtils.showShort(Objects.requireNonNull(e.getMessage()));
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        }).start();
    }

    final static class ViewHolder {
        TextView catalog;
        TextView name;
        ImageView image;
        View view;
    }

    /**
     * 获取catalog首次出现位置
     */
    private int getPositionForSection(String catalog) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getFirstLetter();
            if (catalog.equalsIgnoreCase(sortStr)) {
                return i;
            }
        }
        return -1;
    }

}