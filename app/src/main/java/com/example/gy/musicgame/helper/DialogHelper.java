package com.example.gy.musicgame.helper;

import android.content.Context;

import com.example.gy.musicgame.listener.DialogListener;

import cn.refactor.lib.colordialog.ColorDialog;

/**
 * Description: SoloMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/12/7 18:14
 */
public class DialogHelper {
    private static DialogHelper instance;

    private DialogHelper() {
    }

    public static DialogHelper getInstance() {
        if (instance == null) {
            synchronized (DialogHelper.class) {
                if (instance == null) {
                    instance = new DialogHelper();
                }
            }
        }
        return instance;
    }

    public void showSureDialog(Context context, String title, String content, final DialogListener listener) {
        final ColorDialog dialog = new ColorDialog(context);
        dialog.setTitle(title);
        dialog.setContentText(content);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setPositiveListener("确定", new ColorDialog.OnPositiveListener() {
            @Override
            public void onClick(ColorDialog colorDialog) {
                dialog.dismiss();
                listener.clickSure();
            }
        }).setNegativeListener("取消", new ColorDialog.OnNegativeListener() {
            @Override
            public void onClick(ColorDialog colorDialog) {
                dialog.dismiss();
                listener.clickCancel();
            }
        }).show();
    }
}
