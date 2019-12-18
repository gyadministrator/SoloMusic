package com.android.customer.music.view;
/**
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/11/2 18:33
 */
public interface BaseView<T> {
    /**
     * 请求成功
     *
     * @param result 结果
     */
    void success(T result);

    /**
     * 请求失败
     *
     * @param msg 错误信息
     */
    void fail(String msg);

    /**
     * 显示加载框
     *
     * @param msg 加载框提示的信息
     */
    void showLoading(String msg);

    /**
     * 隐藏加载框
     */
    void dismissLoading();
}
