package com.android.customer.music.utils;

import com.android.customer.music.listener.SSMListener;
import com.android.customer.music.listener.ValidateListener;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * **Create By gy
 * **Time 15:13
 * **Description Recipe
 **/
public class SSMUtils {
    private static SSMListener listener;
    private static ValidateListener validateListener;
    private static boolean isFirst = true;

    public static void setValidateListener(ValidateListener validateListener) {
        SSMUtils.validateListener = validateListener;
    }

    public static void setSsmListener(SSMListener ssmListener) {
        listener = ssmListener;
    }

    /**
     * 请求验证码，其中country表示国家代码，如“86”；phone表示手机号码，如“13800138000”
     * 短信验证   无视图的:
     *
     * @param country 国家
     * @param phone   电话号码
     */

    public static void sendCode(String country, String phone) {
        // 注册一个事件回调，用于处理发送验证码操作的结果
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // TODO 处理成功得到验证码的结果
                    // 请注意，此时只是完成了发送验证码的请求，验证码短信还需要几秒钟之后才送达
                    if (isFirst) {
                        listener.success("发送验证码成功");
                        isFirst = false;
                    }
                } else {
                    // TODO 处理错误的结果
                    listener.error("发送验证码失败");
                }
            }
        });
        // 触发操作
        SMSSDK.getVerificationCode(country, phone);
    }

    /**
     * 提交验证码，其中的code表示验证码，如“1357”
     *
     * @param country 国家
     * @param phone   电话号码
     * @param code    验证码
     */
    public static void submitCode(String country, String phone, String code) {
        // 注册一个事件回调，用于处理提交验证码操作的结果
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // TODO 处理验证成功的结果
                    validateListener.success("验证码正确");
                } else {
                    // TODO 处理错误的结果
                    validateListener.error("验证码不正确");
                }
            }
        });
        // 触发操作
        SMSSDK.submitVerificationCode(country, phone, code);
    }

    /**
     * 销毁回调
     */
    public static void destroy() {
        //用完回调要注销掉，否则可能会出现内存泄露
        SMSSDK.unregisterAllEventHandler();
    }
}
