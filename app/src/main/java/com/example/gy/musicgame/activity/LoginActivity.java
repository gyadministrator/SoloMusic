package com.example.gy.musicgame.activity;

import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.ToastUtils;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.api.Api;
import com.example.gy.musicgame.constant.Constants;
import com.example.gy.musicgame.helper.LoadingDialogHelper;
import com.example.gy.musicgame.helper.RetrofitHelper;
import com.example.gy.musicgame.model.LoginVo;
import com.example.gy.musicgame.utils.LogUtils;

import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.gy.musicgame.utils.HandlerUtils.isHandler;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvProtocol;
    private EditText etUser;
    private EditText etPassword;
    private TextView tvLogin;
    private TextView tvForget;
    private TextView tvRegister;

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

    private String getUser() {
        return etUser.getText().toString();
    }

    private String getPassword() {
        return etPassword.getText().toString();
    }

    @Override
    protected void initData() {

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

    private void login(String user, String password) {
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
                        boolean isHandler = isHandler(map);
                        if (!isHandler) {
                            //未处理
                        }
                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showShort(Objects.requireNonNull(e.getMessage()));
                    }

                    @Override
                    public void onComplete() {
                        LoadingDialogHelper.dismiss();
                    }
                });
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
            ToastUtils.showShort("用户协议");
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
            ToastUtils.showShort("隐私政策");
        }
    }
}
