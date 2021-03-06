package com.android.customer.music.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.android.customer.music.R;
import com.android.customer.music.api.Api;
import com.android.customer.music.constant.Constants;
import com.android.customer.music.helper.RetrofitHelper;
import com.android.customer.music.model.SplashModel;
import com.android.customer.music.utils.SharedPreferenceUtil;
import com.gyf.barlibrary.ImmersionBar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SplashActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_clock;
    private ImageView splash_image;
    private TextView tv_copy;
    private ImmersionBar immersionBar;
    private static Map<String, Object> params = new HashMap<>();
    private String token;

    @Override
    public void onClick(View v) {
        countDownTimer.cancel();
        jumpActivity();
    }


    @Override
    protected void initView() {
        tv_clock = fd(R.id.tv_clock);
        splash_image = fd(R.id.splash_image);
        tv_copy = fd(R.id.tv_copy);

        tv_clock.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        setSwipeBackEnable(false);
        immersionBar = ImmersionBar.with(mActivity);
        immersionBar.statusBarDarkFont(true, 0.2f)
                .statusBarColor(R.color.transparent)
                .navigationBarColor(R.color.splash_top_color)
                .init();
    }

    @Override
    protected void initAction() {
        SharedPreferenceUtil preferenceUtil = new SharedPreferenceUtil();
        token = preferenceUtil.getObject(mActivity, Constants.CURRENT_TOKEN);
        initLogo();
        getSplash();
        countDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (immersionBar != null) {
            immersionBar.destroy();
        }
    }


    private void getSplash() {
        final RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Api api = retrofitHelper.initRetrofit(Constants.SPLASH_URL);
        params.put("format", "js");
        params.put("idx", "0");
        params.put("n", "1");
        Observable<SplashModel> observable = api.getSplash(params);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SplashModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(SplashModel splashModel) {
                        if (splashModel != null) {
                            if (splashModel.getImages() != null && splashModel.getImages().size() > 0) {
                                SplashModel.ImagesBean imagesBean = splashModel.getImages().get(0);
                                if (imagesBean != null) {
                                    setSplash(imagesBean.getUrl());
                                    retrofitHelper.destroy();
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void setSplash(String url) {
        Glide.with(mActivity).load(Constants.SPLASH_URL + url).into(splash_image);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_splash;
    }

    @SuppressLint("SetTextI18n")
    private void initLogo() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        String now_date = simpleDateFormat.format(new Date());
        tv_copy.setText(getString(R.string.copyright_2018) + "-" + now_date);
    }

    private CountDownTimer countDownTimer = new CountDownTimer(4000, 1000) {
        @SuppressLint("SetTextI18n")
        @Override
        public void onTick(long millisUntilFinished) {
            String value = String.valueOf((int) (millisUntilFinished / 1000));
            tv_clock.setText(value + "s 跳过");
        }

        @Override
        public void onFinish() {
            jumpActivity();
        }
    };

    private void jumpActivity() {
        Intent intent;
        if (!TextUtils.isEmpty(token)) {
            intent = new Intent(mActivity, DrawerActivity.class);
            startActivity(intent);
            finish();
        } else {
            LoginActivity.startActivity(mActivity);
        }
    }
}
