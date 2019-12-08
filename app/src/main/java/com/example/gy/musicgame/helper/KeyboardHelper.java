package com.example.gy.musicgame.helper;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Description: SoloMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/12/8 11:14
 */
public class KeyboardHelper {
    private static KeyboardHelper instance;

    private KeyboardHelper() {
    }

    public static KeyboardHelper getInstance() {
        if (instance == null) {
            instance = new KeyboardHelper();
        }
        return instance;
    }

    /**
     * 隐藏软键盘
     *
     * @param editText 输入框
     */
    public void hideKeyBoard(EditText editText, Activity activity) {
        InputMethodManager imm =
                (InputMethodManager)
                        activity
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    /**
     * 弹起软键盘
     *
     * @param editText 输入框
     */
    public void openKeyBoard(final EditText editText, final Activity activity) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm =
                        (InputMethodManager)
                                activity
                                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(editText, 0);
                }
                editText.setSelection(editText.getText().length());
            }
        }, 200);


    }
}
