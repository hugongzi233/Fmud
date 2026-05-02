package com.zwyouto.myactivex;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.zwyouto.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/* loaded from: classes.dex */
public class Download {
    private static final int DOWNLOAD = 1;
    private static final int DOWNLOAD_FINISH = 2;
    private Context mContext;
    private Dialog mDownloadDialog;
    private ProgressBar mProgress;
    private String mSavePath;
    private String mfileName;
    private String mfilePath;
    private String mfileType;
    private boolean mflag;
    private int progress;
    private boolean cancelUpdate = false;
    private Handler mHandler = new Handler() {
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    Download.this.mProgress.setProgress(Download.this.progress);
                    break;
                case 2:
                    if (Download.this.mfileType.equals("apk")) {
                        Download.this.installApk();
                        break;
                    }
                    break;
            }
        }
    };

    private class downloadApkThread extends Thread {
        private downloadApkThread() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                if (Environment.getExternalStorageState().equals("mounted")) {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(Download.this.mfilePath).openConnection();
                    httpURLConnection.connect();
                    int contentLength = httpURLConnection.getContentLength();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    File file = new File(Download.this.mSavePath);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(new File(Download.this.mSavePath, Download.this.mfileName));
                    int i = 0;
                    byte[] bArr = new byte[1024];
                    while (true) {
                        int i2 = inputStream.read(bArr);
                        i += i2;
                        Download.this.progress = (int) ((i / contentLength) * 100.0f);
                        Download.this.mHandler.sendEmptyMessage(1);
                        if (i2 <= 0) {
                            Download.this.mHandler.sendEmptyMessage(2);
                            break;
                        } else {
                            fileOutputStream.write(bArr, 0, i2);
                            if (Download.this.cancelUpdate) {
                                break;
                            }
                        }
                    }
                    fileOutputStream.close();
                    inputStream.close();
                }
            } catch (MalformedURLException e) {
            } catch (IOException e2) {
            }
            Download.this.mDownloadDialog.dismiss();
        }
    }

    public Download(Context context, String str, String str2, String str3, Boolean bool) {
        this.mContext = context;
        this.mfilePath = "https://mud1.foxmoe.top" + str;
        this.mSavePath = str3;
        this.mfileType = str2;
        this.mflag = bool.booleanValue();
        this.mfileName = str.split("/")[str.length() - 1];
    }

    private void download() {
        new downloadApkThread().start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void installApk() {
        File file = new File(this.mSavePath, this.mfileName);
        if (file.exists()) {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setDataAndType(Uri.parse("file://" + file.toString()), "application/vnd.android.package-archive");
            this.mContext.startActivity(intent);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint({"InflateParams"})
    public void showDownloadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setTitle("正在更新");
        builder.setCancelable(false);
        View viewInflate = LayoutInflater.from(this.mContext).inflate(R.layout.softupdate_progress, (ViewGroup) null);
        this.mProgress = (ProgressBar) viewInflate.findViewById(R.id.update_progress);
        builder.setView(viewInflate);
        builder.setNegativeButton("取消下载", new DialogInterface.OnClickListener() { // from class: com.zwyouto.myactivex.Download.4

            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Download.this.cancelUpdate = true;
                System.exit(0);
            }
        });
        this.mDownloadDialog = builder.create();
        this.mDownloadDialog.show();
        download();
    }

    public void startDown() {
        if (!this.mflag) {
            download();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setTitle("软件更新");
        builder.setCancelable(false);
        builder.setMessage("检测到新版本，是否更新？");
        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Download.this.showDownloadDialog();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                System.exit(0);
            }
        });
        builder.create().show();
    }
}