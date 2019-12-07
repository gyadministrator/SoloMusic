package com.example.gy.musicgame.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.PhoneUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.api.Api;
import com.example.gy.musicgame.constant.Constants;
import com.example.gy.musicgame.helper.LoadingDialogHelper;
import com.example.gy.musicgame.helper.RetrofitHelper;
import com.example.gy.musicgame.listener.SSMListener;
import com.example.gy.musicgame.listener.ValidateListener;
import com.example.gy.musicgame.utils.HandlerUtils;
import com.example.gy.musicgame.utils.SSMUtils;

import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MobileActivity extends BaseActivity implements View.OnClickListener, SSMListener {
    private EditText etMobile;
    private EditText etCode;
    private TextView tvCode;
    private TextView tvNext;
    private int mType;
    private boolean isRegister = false;

    @Override
    protected void initView() {
        etMobile = fd(R.id.et_mobile);
        etCode = fd(R.id.et_code);
        tvCode = fd(R.id.tv_code);
        tvNext = fd(R.id.tv_next);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        mType = intent.getIntExtra("type", 1);
    }

    private String getMobile() {
        return etMobile.getText().toString();
    }

    private String getCode() {
        return etCode.getText().toString();
    }

    private boolean validate(String mobile) {
        if (TextUtils.isEmpty(mobile)) {
            ToastUtils.showShort("手机号不能为空！");
            return false;
        }
        if (!PhoneUtils.isPhone()) {
            ToastUtils.showShort("手机号格式不对！");
            return false;
        }
        return true;
    }

    @Override
    protected void initAction() {
        tvCode.setOnClickListener(this);
        tvNext.setOnClickListener(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_mobile;
    }


    /**
     * 跳转活动
     *
     * @param activity 活动
     * @param type     类型 1 注册 2 忘记密码
     */
    public static void startActivity(Activity activity, int type) {
        Intent intent = new Intent(activity, MobileActivity.class);
        intent.putExtra("type", type);
        activity.startActivity(intent);
    }

    private void getCode(String mobile) {
        //注册，先查询手机号是否已经注册了
        selectMobile(mobile);
    }

    private void selectMobile(final String mobile) {
        LoadingDialogHelper.show(mActivity, "检测手机号...");
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.SERVER_URL);
        Observable<Map> observable = api.checkMobile(mobile);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Map>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Map map) {
                        boolean handler = HandlerUtils.isHandler(map,mActivity);
                        if (!handler) {
                            isRegister = (boolean) map.get("data");
                        }
                        if (mType == 1 && isRegister) {
                            SSMUtils.sendCode("86", mobile);
                            SSMUtils.setSsmListener(MobileActivity.this);
                        } else if (mType == 2 && !isRegister) {
                            SSMUtils.sendCode("86", mobile);
                            SSMUtils.setSsmListener(MobileActivity.this);
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

    @Override
    public void onClick(View view) {
        boolean validate = validate(getMobile());
        switch (view.getId()) {
            case R.id.tv_code:
                //获取验证码
                if (validate) {
                    getCode(getMobile());
                }
                break;
            case R.id.tv_next:
                //下一步
                if (validate) {
                    validateCode(getMobile(), getCode());
                }
                break;
        }
    }

    private void validateCode(final String mobile, String code) {
        //验证验证码是否正确
        SSMUtils.submitCode("86", mobile, code);
        SSMUtils.setValidateListener(new ValidateListener() {
            @Override
            public void success(String msg) {
                //验证成功
                if (mType == 1) {
                    //注册
                    if (isRegister) {
                        countDownTimer.cancel();
                        RegisterActivity.startActivity(mActivity, getMobile(), getCode());
                    }
                } else if (mType == 2) {
                    //忘记密码
                    if (!isRegister) {
                        countDownTimer.cancel();
                    }
                }
            }

            @Override
            public void error(String msg) {
                ToastUtils.showShort(msg);
            }
        });
    }

    @Override
    public void success(String msg) {
        ToastUtils.showShort(msg);
        countDownTimer.start();
    }

    @Override
    public void error(String msg) {
        ToastUtils.showShort(msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        SSMUtils.destroy();
    }

    private CountDownTimer countDownTimer = new CountDownTimer(60 * 1000, 1000) {
        @SuppressLint("SetTextI18n")
        @Override
        public void onTick(long millisUntilFinished) {
            tvCode.setEnabled(false);
            String value = String.valueOf((int) (millisUntilFinished / 1000));
            tvCode.setText(value + "s");
        }

        @Override
        public void onFinish() {
            tvCode.setText("重新获取");
            tvCode.setEnabled(true);
            countDownTimer.cancel();
        }
    };

}
