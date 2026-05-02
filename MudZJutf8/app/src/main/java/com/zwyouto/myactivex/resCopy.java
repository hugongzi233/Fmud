package com.zwyouto.myactivex;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;
import com.zwyouto.main.logind;
import com.zwyouto.mud.BuildConfig;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class resCopy extends AsyncTask<Void, Integer, Long> {
    private final Context context;
    private final ProgressDialog progressDialog;
    private final File outputDir;

    private final class ProgressReportingOutputStream extends FileOutputStream {
        public ProgressReportingOutputStream(File file) throws FileNotFoundException {
            super(file);
        }

        @Override
        public void write(byte[] buffer, int offset, int length) throws IOException {
            super.write(buffer, offset, length);
        }
    }

    public resCopy(String outputPath, Context context, boolean isUpdate) {
        this.context = context;
        this.outputDir = new File(outputPath);
        if (!this.outputDir.exists() && !this.outputDir.mkdirs()) {
            Toast.makeText(context, "目录创建失败！", Toast.LENGTH_SHORT).show();
        }
        if (context != null) {
            this.progressDialog = new ProgressDialog(context);
        } else {
            this.progressDialog = null;
        }
    }

    private long copyResourcesToSdCard() {
        String[] subFiles;
        long totalBytesCopied = 0;
        try {
            String[] rootAssets = this.context.getResources().getAssets().list(BuildConfig.FLAVOR);
            File versionFile = new File(this.outputDir, "resver.txt");
            if (!versionFile.getParentFile().exists()) {
                versionFile.getParentFile().mkdirs();
            }
            InputStream versionInputStream = this.context.getAssets().open("resver.txt");
            ProgressReportingOutputStream versionOutputStream = new ProgressReportingOutputStream(versionFile);
            totalBytesCopied += copy(versionInputStream, versionOutputStream);
            versionInputStream.close();
            versionOutputStream.close();
            if (rootAssets != null) {
                for (String rootAsset : rootAssets) {
                    if (!rootAsset.equals("images") && !rootAsset.equals("sounds") && !rootAsset.equals("webkit") && (subFiles = this.context.getAssets().list(rootAsset)) != null) {
                        publishProgress(0, subFiles.length);
                        for (int j = 0; j < subFiles.length; j++) {
                            File targetFile = new File(this.outputDir.getAbsolutePath() + File.separator + rootAsset + File.separator, subFiles[j]);
                            if (!targetFile.getParentFile().exists()) {
                                targetFile.getParentFile().mkdirs();
                            }
                            publishProgress(j);
                            InputStream assetInputStream = this.context.getAssets().open(rootAsset + "/" + subFiles[j]);
                            ProgressReportingOutputStream assetOutputStream = new ProgressReportingOutputStream(targetFile);
                            totalBytesCopied += copy(assetInputStream, assetOutputStream);
                            assetInputStream.close();
                            assetOutputStream.close();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return totalBytesCopied;
    }

    public int copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[1024];
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 1024);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream, 1024);
        int totalBytes = 0;
        try {
            int bytesRead;
            while ((bytesRead = bufferedInputStream.read(buffer, 0, 1024)) != -1) {
                bufferedOutputStream.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
            }
            bufferedOutputStream.flush();
        } finally {
            try {
                bufferedOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bufferedInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return totalBytes;
    }

    @Override
    protected Long doInBackground(Void... voids) {
        return copyResourcesToSdCard();
    }

    @Override
    protected void onPostExecute(Long result) {
        if (this.progressDialog != null && this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
        if (isCancelled()) {
            return;
        }
        if (this.context instanceof logind) {
            ((logind) this.context).unzipok();
        }
    }

    @Override
    protected void onPreExecute() {
        if (this.progressDialog != null) {
            this.progressDialog.setTitle("资源更新中...");
            this.progressDialog.setCanceledOnTouchOutside(false);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    resCopy.this.cancel(true);
                }
            });
            this.progressDialog.show();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (this.progressDialog == null) {
            return;
        }
        if (values.length <= 1) {
            this.progressDialog.setProgress(values[0]);
        } else {
            this.progressDialog.setMax(values[1]);
        }
    }
}