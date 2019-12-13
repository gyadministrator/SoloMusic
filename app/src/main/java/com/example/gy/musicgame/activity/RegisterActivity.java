package com.example.gy.musicgame.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.ToastUtils;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.api.Api;
import com.example.gy.musicgame.constant.Constants;
import com.example.gy.musicgame.helper.LoadingDialogHelper;
import com.example.gy.musicgame.helper.RetrofitHelper;
import com.example.gy.musicgame.model.RegisterVo;
import com.example.gy.musicgame.utils.HandlerUtils;
import com.example.gy.musicgame.utils.SharedPreferenceUtil;

import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private EditText etUser;
    private EditText etPassword;
    private EditText etSurePassword;
    private TextView tvRegister;
    private String code;
    private String mobile;

    @Override
    protected void initView() {
        etUser = fd(R.id.et_user);
        etPassword = fd(R.id.et_password);
        etSurePassword = fd(R.id.et_sure_password);
        tvRegister = fd(R.id.tv_register);
        tvRegister.setOnClickListener(this);
    }

    /**
     * 启动活动
     *
     * @param activity 活动
     * @param mobile   手机号
     * @param code     验证码
     */
    public static void startActivity(Activity activity, String mobile, String code) {
        Intent intent = new Intent(activity, RegisterActivity.class);
        intent.putExtra("mobile", mobile);
        intent.putExtra("code", code);
        activity.startActivity(intent);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        mobile = intent.getStringExtra("mobile");
        code = intent.getStringExtra("code");
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_register;
    }

    private String getUser() {
        return etUser.getText().toString();
    }

    private String getPassword() {
        return etPassword.getText().toString();
    }

    private String getSurePassword() {
        return etSurePassword.getText().toString();
    }

    private boolean validate() {
        if (TextUtils.isEmpty(getUser()) || TextUtils.isEmpty(getPassword()) || TextUtils.isEmpty(getSurePassword())) {
            ToastUtils.showShort("注册信息不能为空！");
            return false;
        }
        if (!getPassword().equals(getSurePassword())) {
            ToastUtils.showShort("两次密码不一致！");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        boolean validate = validate();
        if (validate) {
            RegisterVo registerVo = new RegisterVo();
            registerVo.setMobile(mobile);
            registerVo.setCode(code);
            registerVo.setUsername(getUser());
            registerVo.setPassword(getPassword());
            register(registerVo);
        }
    }

    private void register(final RegisterVo registerVo) {
        LoadingDialogHelper.show(mActivity, "注册中...");
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.SERVER_URL);
        Observable<Map> observable = api.register(registerVo);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Map>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Map map) {
                        boolean handler = HandlerUtils.isHandler(map, mActivity);
                        if (!handler) {
                            Map<String, Object> data = (Map<String, Object>) map.get("data");
                            if (data != null) {
                                if (data.containsKey("token")) {
                                    String token = (String) data.get("token");
                                    if (!TextUtils.isEmpty(token)) {
                                        //设置IM账号数据
                                        setIMData(registerVo.getUsername(), registerVo.getPassword());
                                        SharedPreferenceUtil preferenceUtil = new SharedPreferenceUtil();
                                        preferenceUtil.saveObject(token, mActivity, Constants.CURRENT_TOKEN);
                                        startActivity(new Intent(mActivity, MainActivity.class));
                                        finish();
                                    }
                                } else {
                                    ToastUtils.showShort("数据异常，token未获取");
                                }
                            }
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

    private void setIMData(String user, String password) {
        SharedPreferences sharedPreferences = mActivity.getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("username", user);
        edit.putString("password", password);
        edit.apply();
    }
}
