package com.example.gy.musicgame.helper;

import android.content.Context;
import android.view.View;

import com.example.gy.musicgame.listener.DialogListener;
import com.example.gy.musicgame.listener.InputDialogListener;
import com.hb.dialog.myDialog.ActionSheetDialog;
import com.hb.dialog.myDialog.MyAlertDialog;
import com.hb.dialog.myDialog.MyAlertInputDialog;

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
        final MyAlertDialog myAlertDialog = new MyAlertDialog(context);
        myAlertDialog.builder();
        myAlertDialog.setTitle(title);
        myAlertDialog.setMsg(content);
        myAlertDialog.setPositiveButton("确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.clickSure();
                }
            }
        });
        myAlertDialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.clickCancel();
                }
            }
        });
        myAlertDialog.setCanceledOnTouchOutside(false);
        myAlertDialog.setCancelable(false);
        myAlertDialog.show();
    }

    public void showInputDialog(Context context, String title, final InputDialogListener dialogListener) {
        final MyAlertInputDialog myAlertInputDialog = new MyAlertInputDialog(context).builder()
                .setTitle(title)
                .setEditText("");
        myAlertInputDialog.setPositiveButton("确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogListener != null) {
                    dialogListener.sure(myAlertInputDialog.getResult());
                }
            }
        }).setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        myAlertInputDialog.setCanceledOnTouchOutside(false);
        myAlertInputDialog.setCancelable(false);
        myAlertInputDialog.show();

    }

    public void showBottomDialog(Context context) {
        ActionSheetDialog dialog = new ActionSheetDialog(context).builder().setTitle("请选择")
                .addSheetItem("相册", null, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {

                    }
                }).addSheetItem("拍照", null, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {

                    }
                });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
