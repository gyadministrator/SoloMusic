package com.example.gy.musicgame.utils;

/**
 * Created by Administrator on 2017/10/21.
 */

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.example.gy.musicgame.R;
import com.example.gy.musicgame.helper.DialogHelper;
import com.example.gy.musicgame.helper.LoadingDialogHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.refactor.lib.colordialog.ColorDialog;

public class UpdateManager {
    private Context mContext;

    private Dialog downloadDialog;
    /* 下载包安装路径 */
    @SuppressLint("SdCardPath")
    private static final String savePath = "/sdcard/music_game/";

    private static final String saveFileName = savePath
            + "musicGameUpdateRelease.apk";

    /* 进度条与通知ui刷新的handler和msg常量 */
    private ProgressBar mProgress;

    private static final int DOWN_UPDATE = 1;

    private static final int DOWN_OVER = 2;

    private int progress;

    private TextView progress_tv;

    private Runnable mdownLoadApkRunnable;

    private boolean interceptFlag = false;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    mProgress.setProgress(progress);
                    progress_tv.setText("正在下载中..." + progress + "%");
                    if (progress == 99) {
                        downloadDialog.dismiss();
                    }
                    break;
                case DOWN_OVER:
                    installApk();
                    break;
                default:
                    break;
            }
        }
    };

    public UpdateManager(Context context) {
        this.mContext = context;
    }

    // 外部接口让主Activity调用
    public void checkUpdateInfo(String apkPath, String updateMsg) {
        showNoticeDialog(apkPath, updateMsg);
    }

    private void showNoticeDialog(final String apkPath, String updateMsg) {
        LoadingDialogHelper.dismiss();
        final ColorDialog dialog = new ColorDialog(mContext);
        dialog.setTitle("发现新版本");
        dialog.setContentText(updateMsg);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setPositiveListener("立即下载", new ColorDialog.OnPositiveListener() {
            @Override
            public void onClick(ColorDialog colorDialog) {
                dialog.dismiss();
                showDownloadDialog(apkPath);
            }
        }).setNegativeListener("以后再说", new ColorDialog.OnNegativeListener() {
            @Override
            public void onClick(ColorDialog colorDialog) {
                dialog.dismiss();
            }
        }).show();
    }

    @SuppressLint("CutPasteId")
    private void showDownloadDialog(String apkPath) {
        Builder builder = new Builder(mContext);
        builder.setTitle("新版本更新");

        final LayoutInflater inflater = LayoutInflater.from(mContext);
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.download_progress, null);
        progress_tv = v.findViewById(R.id.progress_tv);
        mProgress = v.findViewById(R.id.progress);
        builder.setView(v);
        builder.setCancelable(false);
        builder.setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                interceptFlag = true;
            }
        });
        downloadDialog = builder.create();
        downloadDialog.show();

        downloadApk(apkPath);
    }

    private void downloadApp(final String apkUrl) {
        mdownLoadApkRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(apkUrl);

                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.connect();
                    int length = conn.getContentLength();
                    InputStream is = conn.getInputStream();

                    File file = new File(savePath);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    File ApkFile = new File(saveFileName);
                    FileOutputStream out = new FileOutputStream(ApkFile);

                    int count = 0;
                    byte buf[] = new byte[1024];
                    do {
                        int read = is.read(buf);
                        count += read;
                        progress = (int) (((float) count / length) * 100);
                        // 更新进度
                        mHandler.sendEmptyMessage(DOWN_UPDATE);
                        if (read <= 0) {
                            // 下载完成通知安装
                            mHandler.sendEmptyMessage(DOWN_OVER);
                            break;
                        }
                        out.write(buf, 0, read);
                    } while (!interceptFlag);// 点击取消就停止下载.

                    out.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };

    }

    /**
     * 下载apk
     */

    private void downloadApk(String apkPath) {
        downloadApp(apkPath);
        Thread downLoadThread = new Thread(mdownLoadApkRunnable);
        downLoadThread.start();
    }

    /**
     * 安装apk
     */
    private void installApk() {
        File apkFile = new File(saveFileName);
        if (!apkFile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(mContext, "com.example.gy.musicgame.fileProvider", apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.parse("file://" + apkFile.toString()), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        mContext.startActivity(intent);

    }
}
