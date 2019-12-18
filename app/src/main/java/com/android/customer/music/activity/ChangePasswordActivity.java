package com.android.customer.music.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.ToastUtils;
import com.android.customer.music.R;
import com.android.customer.music.api.Api;
import com.android.customer.music.constant.Constants;
import com.android.customer.music.helper.LoadingDialogHelper;
import com.android.customer.music.helper.RetrofitHelper;
import com.android.customer.music.utils.HandlerUtils;
import com.android.customer.music.utils.SharedPreferenceUtil;

import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {
    private EditText etOldPassword;
    private EditText etNewPassword;
    private TextView tvChange;
    private String token;

    @Override
    protected void initView() {
        etOldPassword = fd(R.id.et_old_password);
        etNewPassword = fd(R.id.et_new_password);
        tvChange = fd(R.id.tv_change);
        tvChange.setOnClickListener(this);
    }

    /**
     * 启动活动
     *
     * @param activity 活动
     * @param token    token
     */
    public static void startActivity(Activity activity, String token) {
        Intent intent = new Intent(activity, ChangePasswordActivity.class);
        intent.putExtra("token", token);
        activity.startActivity(intent);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_change_password;
    }

    private String getOldPassword() {
        return etOldPassword.getText().toString();
    }

    private String getNewPassword() {
        return etNewPassword.getText().toString();
    }

    private boolean validate() {
        if (TextUtils.isEmpty(getOldPassword()) || TextUtils.isEmpty(getNewPassword())) {
            ToastUtils.showShort("信息不能为空！");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onClick(View view) {
        if (validate()) {
            changePassword(getOldPassword(), getNewPassword());
        }
    }

    private void changePassword(String oldPassword, String newPassword) {
        LoadingDialogHelper.show(mActivity, "修改密码中...");
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.SERVER_URL);
        Observable<Map> observable = api.changePassword(token, oldPassword, newPassword);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Map>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Map map) {
                        HandlerUtils.isHandler(map, mActivity);
                        if (map.containsKey("errno")) {
                            double errno = (double) map.get("errno");
                            if (errno == 0) {
                                //修改成功，重新登录
                                SharedPreferenceUtil preferenceUtil = new SharedPreferenceUtil();
                                preferenceUtil.saveObject(null, mActivity, Constants.CURRENT_TOKEN);
                                LoginActivity.startActivity(mActivity);
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
}
