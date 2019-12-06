package com.example.gy.musicgame.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.example.gy.musicgame.R;

import static android.icu.lang.UScript.getCode;

public class MobileActivity extends BaseActivity implements View.OnClickListener {
    private EditText etMobile;
    private EditText etCode;
    private TextView tvCode;
    private TextView tvNext;
    private int mType;

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

    private boolean validate(String mobile) {
        if (TextUtils.isEmpty(mobile)) {
            ToastUtils.showShort("手机号不能为空！");
            return false;
        }
        if (!RegexUtils.isTel(mobile)) {
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
                    if (mType == 1) {
                        //注册
                    } else if (mType == 2) {
                        //忘记密码
                    }
                }
                break;
        }
    }
}
