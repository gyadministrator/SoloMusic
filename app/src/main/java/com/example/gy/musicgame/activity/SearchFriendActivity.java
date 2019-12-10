package com.example.gy.musicgame.activity;

import android.content.SharedPreferences;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.api.Api;
import com.example.gy.musicgame.constant.Constants;
import com.example.gy.musicgame.helper.DialogHelper;
import com.example.gy.musicgame.helper.LoadingDialogHelper;
import com.example.gy.musicgame.helper.RetrofitHelper;
import com.example.gy.musicgame.listener.InputDialogListener;
import com.example.gy.musicgame.model.IMVo;
import com.example.gy.musicgame.utils.HandlerUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SearchFriendActivity extends BaseActivity implements TextWatcher, View.OnClickListener {
    private EditText etSearch;
    private TextView tvSearch;
    private LinearLayout llResult;
    private TextView tvResult;
    private ImageView ivImage;
    private TextView tvName;
    private ImageView ivAdd;

    @Override
    protected void initView() {
        etSearch = fd(R.id.et_search);
        tvSearch = fd(R.id.tv_search);
        llResult = fd(R.id.ll_result);
        ivImage = fd(R.id.iv_image);
        tvName = fd(R.id.tv_name);
        ivAdd = fd(R.id.iv_add);
        tvResult = fd(R.id.tv_result);
    }

    @Override
    protected void initData() {
        tvSearch.setText("返回");
        etSearch.addTextChangedListener(this);
        tvSearch.setOnClickListener(this);
    }

    @Override
    protected void initAction() {
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });
    }

    private void add() {
        SharedPreferences sharedPreferences = mActivity.getSharedPreferences("login", MODE_PRIVATE);
        //TODO 设置用户数据
        final String username = sharedPreferences.getString("username", null);
        if (!TextUtils.isEmpty(username)) {
            //添加朋友
            if (username.equals(etSearch.getText().toString().trim())) {
                ToastUtils.showShort("不能添加自己为好友");
            } else {
                addFriend(username);
            }
        }
    }

    private void addFriend(final String username) {
        DialogHelper dialogHelper = DialogHelper.getInstance();
        dialogHelper.showInputDialog(mActivity, "请输入添加" + username + "的理由", new InputDialogListener() {
            @Override
            public void sure(String result) {
                //参数为要添加的好友的username和添加理由
                try {
                    EMClient.getInstance().contactManager().addContact(username, result);
                    ToastUtils.showShort("发送请求成功");
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    ToastUtils.showShort("添加好友失败：" + e.getErrorCode() + e.getMessage());
                }
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_search_friend;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        tvSearch.setText("返回");
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.length() == 0) {
            tvSearch.setText("返回");
        } else {
            tvSearch.setText("搜索");
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.toString().length() == 0) {
            tvSearch.setText("返回");
        } else {
            tvSearch.setText("搜索");
        }
    }

    @Override
    public void onClick(View view) {
        String s = tvSearch.getText().toString();
        String key = etSearch.getText().toString();
        if ("返回".equals(s)) {
            onBackPressed();
        } else if ("搜索".equals(s)) {
            if ("".equals(key)) {
                ToastUtils.showShort("请输入内容");
                return;
            }
            //搜索
            search(key);
        }
    }

    @Override
    protected void hasNet() {
        super.hasNet();
        search(etSearch.getText().toString());
    }

    private void search(final String key) {
        LoadingDialogHelper.show(mActivity, "搜索中...");
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.SERVER_URL);
        Observable<Map> observable = api.queryByAccount(key);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Map>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Map map) {
                        boolean handler = HandlerUtils.isHandler(map, mActivity);
                        if (!handler) {
                            Gson gson = new Gson();
                            String json = gson.toJson(map.get("data"));
                            Type type = new TypeToken<IMVo>() {
                            }.getType();
                            IMVo imVo = gson.fromJson(json, type);
                            llResult.setVisibility(View.VISIBLE);
                            tvResult.setVisibility(View.GONE);
                            if (imVo != null && !TextUtils.isEmpty(imVo.getAvatar())) {
                                Glide.with(mActivity).load(imVo.getAvatar()).into(ivImage);
                            }
                            if (imVo != null) {
                                tvName.setText(key);
                            }
                        } else {
                            tvResult.setVisibility(View.VISIBLE);
                            llResult.setVisibility(View.GONE);
                        }
                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onError(Throwable e) {
                        LoadingDialogHelper.dismiss();
                        ToastUtils.showShort(Objects.requireNonNull(e.getMessage()));
                    }

                    @Override
                    public void onComplete() {
                        LoadingDialogHelper.dismiss();
                    }
                });
    }
}
