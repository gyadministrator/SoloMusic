package com.android.customer.music.helper;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.android.customer.music.listener.DialogListener;
import com.android.customer.music.listener.InputDialogListener;
import com.android.customer.music.listener.SheetDialogListener;
import com.hb.dialog.myDialog.ActionSheetDialog;
import com.hb.dialog.myDialog.MyAlertDialog;
import com.hb.dialog.myDialog.MyAlertInputDialog;

import java.util.List;

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
                if (TextUtils.isEmpty(myAlertInputDialog.getResult())) {
                    ToastUtils.showShort("请输入内容");
                    return;
                }
                if (dialogListener != null) {
                    dialogListener.sure(myAlertInputDialog.getResult());
                    myAlertInputDialog.dismiss();
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

    /**
     * 显示底部弹出框
     *
     * @param context context
     * @param items   条目
     */
    public void showBottomDialog(Context context, List<String> items, final SheetDialogListener sheetDialogListener) {
        ActionSheetDialog dialog = new ActionSheetDialog(context).builder().setTitle("请选择");
        if (items != null) {
            for (String item : items) {
                dialog.addSheetItem(item, null, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        if (sheetDialogListener != null) {
                            sheetDialogListener.selectPosition(which - 1);
                        }
                    }
                });
            }
        }
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
    }
}
