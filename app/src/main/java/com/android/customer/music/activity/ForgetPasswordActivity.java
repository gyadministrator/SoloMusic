package com.android.customer.music.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.ActivityUtils;
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

public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener {
    private EditText etNewPassword;
    private TextView tvReset;
    private String mobile;

    @Override
    protected void initView() {
        etNewPassword = fd(R.id.et_new_password);
        tvReset = fd(R.id.tv_reset);
        tvReset.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        mobile = intent.getStringExtra("mobile");
    }

    @Override
    protected void initAction() {

    }

    public static void startActivity(Activity activity, String mobile) {
        Intent intent = new Intent(activity, ForgetPasswordActivity.class);
        intent.putExtra("mobile", mobile);
        activity.startActivity(intent);
    }

    private String getNewPassword() {
        return etNewPassword.getText().toString();
    }

    private boolean validate() {
        if (TextUtils.isEmpty(getNewPassword())) {
            ToastUtils.showShort("信息不能为空！");
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_forget_password;
    }

    @Override
    public void onClick(View view) {
        if (validate()) {
            resetPassword();
        }
    }

    private void resetPassword() {
        LoadingDialogHelper.show(mActivity, "重置中...");
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.SERVER_URL);
        Observable<Map> observable = api.forgetPassword(mobile, getNewPassword());
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
                                //重置成功，重新登录
                                SharedPreferenceUtil preferenceUtil = new SharedPreferenceUtil();
                                preferenceUtil.saveObject(null, mActivity, Constants.CURRENT_TOKEN);
                                ActivityUtils.finishActivity(MobileActivity.class);
                                finish();
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
