package com.example.gy.musicgame.utils;

/**
 * Created by Administrator on 2017/10/21.
 */

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.blankj.utilcode.util.ActivityUtils;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.helper.LoadingDialogHelper;
import com.example.gy.musicgame.model.ApkModel;
import com.hb.dialog.dialog.ConfirmDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateManager {
    private Context mContext;

    private ConfirmDialog downloadDialog;
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

    private Runnable mDownLoadApkRunnable;

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
                        if (downloadDialog != null && downloadDialog.isShowing()) {
                            downloadDialog.dismiss();
                        }
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
    public void checkUpdateInfo(ApkModel apkModel) {
        showNoticeDialog(apkModel);
    }

    private void showNoticeDialog(final ApkModel apkModel) {
        LoadingDialogHelper.dismiss();
        if (apkModel == null) return;
        final ConfirmDialog confirmDialog = new ConfirmDialog(mContext);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(mContext).inflate(R.layout.update_notice, null);
        TextView tvVersion = view.findViewById(R.id.tv_version);
        TextView tvContent = view.findViewById(R.id.tv_content);
        TextView tvSure = view.findViewById(R.id.tv_sure);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        TextView tvTime = view.findViewById(R.id.tv_time);
        TextView tvUpdate = view.findViewById(R.id.tv_update);
        confirmDialog.setContentView(view);
        tvVersion.setText(apkModel.getApkVersion());
        tvTime.setText(apkModel.getAddTime());
        tvContent.setText(apkModel.getContent());
        if (apkModel.getIsUpdate() == 1) {
            tvUpdate.setVisibility(View.VISIBLE);
        }
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();
                if (apkModel.getIsUpdate() == 1) {
                    //强制更新
                    ActivityUtils.finishAllActivities();
                    System.exit(0);
                    Process.killProcess(Process.myPid());
                }
            }
        });
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();
                showDownloadDialog(apkModel);
            }
        });
        confirmDialog.setCanceledOnTouchOutside(false);
        confirmDialog.setCancelable(false);
        confirmDialog.show();
    }

    @SuppressLint("CutPasteId")
    private void showDownloadDialog(final ApkModel apkModel) {
        downloadDialog = new ConfirmDialog(mContext);
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.download_progress, null);
        progress_tv = v.findViewById(R.id.progress_tv);
        mProgress = v.findViewById(R.id.progress);
        TextView tvCancel = v.findViewById(R.id.tv_cancel);
        downloadDialog.setContentView(v);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadDialog.dismiss();
                interceptFlag = true;

                if (apkModel.getIsUpdate() == 1) {
                    //强制更新
                    ActivityUtils.finishAllActivities();
                    System.exit(0);
                    Process.killProcess(Process.myPid());
                }
            }
        });
        downloadDialog.setCanceledOnTouchOutside(false);
        downloadDialog.setCancelable(false);
        downloadDialog.show();
        downloadApk(apkModel.getDownloadUrl());
    }

    private void downloadApp(final String apkUrl) {
        mDownLoadApkRunnable = new Runnable() {
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
        Thread downLoadThread = new Thread(mDownLoadApkRunnable);
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
