package com.android.customer.music.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Process;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.android.customer.music.R;
import com.android.customer.music.api.Api;
import com.android.customer.music.constant.Constants;
import com.android.customer.music.helper.LoadingDialogHelper;
import com.android.customer.music.helper.RetrofitHelper;
import com.android.customer.music.model.LoginVo;
import com.android.customer.music.model.UserInfoVo;
import com.android.customer.music.utils.SharedPreferenceUtil;
import com.android.customer.music.utils.UserManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.android.customer.music.utils.HandlerUtils.isHandler;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvProtocol;
    private EditText etUser;
    private EditText etPassword;
    private TextView tvLogin;
    private TextView tvForget;
    private TextView tvRegister;
    private SharedPreferenceUtil preferenceUtil;

    @Override
    protected void initView() {
        tvProtocol = fd(R.id.tv_protocol);
        etUser = fd(R.id.et_user);
        etPassword = fd(R.id.et_password);
        tvLogin = fd(R.id.tv_login);
        tvForget = fd(R.id.tv_forget);
        tvRegister = fd(R.id.tv_register);
        tvForget.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
    }

    /**
     * 启动活动
     *
     * @param activity 活动
     */
    public static void startActivity(Activity activity) {
        ActivityUtils.finishAllActivitiesExceptNewest();
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    private String getUser() {
        return etUser.getText().toString();
    }

    private String getPassword() {
        return etPassword.getText().toString();
    }

    @Override
    protected void initData() {
        preferenceUtil = new SharedPreferenceUtil();
        String username = preferenceUtil.getObject(mActivity, Constants.CURRENT_USER_NAME);
        if (!TextUtils.isEmpty(username)) {
            etUser.setText(username);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ActivityUtils.finishAllActivities();
            System.exit(0);
            Process.killProcess(Process.myPid());
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void initAction() {
        setProtocolEvent();
    }

    private void setProtocolEvent() {
        String protocol = getString(R.string.protocol);
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(protocol);
        UserProtocol userProtocol = new UserProtocol();
        stringBuilder.setSpan(userProtocol, 16, 20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SecretProtocol secretProtocol = new SecretProtocol();
        stringBuilder.setSpan(secretProtocol, 23, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        MobSecretProtocol mobSecretProtocol = new MobSecretProtocol();
        stringBuilder.setSpan(mobSecretProtocol, 32, 37, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        MobServiceProtocol mobServiceProtocol = new MobServiceProtocol();
        stringBuilder.setSpan(mobServiceProtocol, 39, 44, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvProtocol.setText(stringBuilder);
        tvProtocol.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_login:
                boolean flag = validate();
                if (flag) {
                    login(getUser(), getPassword());
                }
                break;
            case R.id.tv_forget:
                MobileActivity.startActivity(mActivity, 2);
                break;
            case R.id.tv_register:
                MobileActivity.startActivity(mActivity, 1);
                break;
        }
    }

    private void login(final String user, final String password) {
        LoadingDialogHelper.show(mActivity, "登录中...");
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.SERVER_URL);
        LoginVo loginVo = new LoginVo();
        loginVo.setUsername(user);
        loginVo.setPassword(password);
        Observable<Map> observable = api.login(loginVo);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Map>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Map map) {
                        LoadingDialogHelper.dismiss();
                        boolean handler = isHandler(map, mActivity);
                        if (!handler) {
                            Map<String, Object> data = (Map<String, Object>) map.get("data");
                            if (data != null) {
                                if (data.containsKey("token")) {
                                    String token = (String) data.get("token");
                                    Gson gson = new Gson();
                                    String json = gson.toJson(data.get("userInfo"));
                                    Type type = new TypeToken<UserInfoVo>() {
                                    }.getType();
                                    UserInfoVo userInfoVo = gson.fromJson(json, type);
                                    UserManager.setUserInfoVo(json, mActivity);
                                    if (!TextUtils.isEmpty(token) && userInfoVo != null) {
                                        //设置IM账号数据
                                        setIMData(userInfoVo.getNickName(), password);
                                        preferenceUtil.saveObject(token, mActivity, Constants.CURRENT_TOKEN);
                                        preferenceUtil.saveObject(user, mActivity, Constants.CURRENT_USER_NAME);
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


    private boolean validate() {
        if (TextUtils.isEmpty(getUser()) || TextUtils.isEmpty(getPassword())) {
            ToastUtils.showShort("登录信息不能为空！");
            return false;
        } else {
            return true;
        }
    }


    /**
     * 用户协议
     */
    private class UserProtocol extends ClickableSpan {
        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            ds.setColor(getResources().getColor(R.color.black));
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(@NonNull View view) {
            WebActivity.startActivity(mActivity, "file:///android_asset/userProtocol.html");
        }
    }

    /**
     * 隐私政策
     */
    private class SecretProtocol extends ClickableSpan {
        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            ds.setColor(getResources().getColor(R.color.black));
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(@NonNull View view) {
           WebActivity.startActivity(mActivity,"file:///android_asset/userSecret.html");
        }
    }

    /**
     * 第三方隐私政策
     */
    private class MobSecretProtocol extends ClickableSpan {
        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            ds.setColor(getResources().getColor(R.color.black));
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(@NonNull View view) {
            WebActivity.startActivity(mActivity,"http://www.mob.com/about/policy");
        }
    }

    /**
     * 第三方服务条款
     */
    private class MobServiceProtocol extends ClickableSpan {
        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            ds.setColor(getResources().getColor(R.color.black));
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(@NonNull View view) {
           WebActivity.startActivity(mActivity,"http://www.mob.com/about/service");
        }
    }
}
