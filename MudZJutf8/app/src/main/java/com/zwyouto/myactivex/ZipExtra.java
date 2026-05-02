package com.zwyouto.myactivex;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import com.zwyouto.main.logind;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipExtra extends AsyncTask<Void, Integer, Long> {
    private final Context context;
    private final ProgressDialog progressDialog;
    private final File inputFile;
    private final File outputDir;
    private int progress = 0;
    private final boolean replaceAll;

    private final class ProgressReportingOutputStream extends FileOutputStream {
        public ProgressReportingOutputStream(File file) throws FileNotFoundException {
            super(file);
        }

        @Override
        public void write(byte[] buffer, int offset, int length) throws IOException {
            super.write(buffer, offset, length);
            ZipExtra.this.progress += length;
            publishProgress(ZipExtra.this.progress);
        }
    }

    public ZipExtra(String inputPath, String outputPath, Context context, boolean replaceAll) {
        this.context = context;
        this.replaceAll = replaceAll;
        this.inputFile = new File(inputPath);
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

    private int copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[8192];
        int totalBytesRead = 0;
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 8192);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream, 8192)) {
            int bytesRead;
            while ((bytesRead = bufferedInputStream.read(buffer, 0, 8192)) != -1) {
                bufferedOutputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
            }
            bufferedOutputStream.flush();
        }
        return totalBytesRead;
    }

    private long getOriginalSize(ZipFile zipFile) {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        long size = 0;
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.getSize() >= 0) {
                size += entry.getSize();
            }
        }
        return size;
    }

    private long unzip() {
        long totalBytesCopied = 0;
        try (ZipFile zipFile = new ZipFile(inputFile)) {
            publishProgress(0, (int) getOriginalSize(zipFile));
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory()) {
                    File destinationFile = new File(outputDir, entry.getName());
                    File parentDir = destinationFile.getParentFile();
                    if (parentDir != null && !parentDir.exists()) {
                        parentDir.mkdirs();
                    }

                    if (destinationFile.exists() && !replaceAll) {
                        continue;
                    }

                    try (InputStream inputStream = zipFile.getInputStream(entry);
                         OutputStream outputStream = new ProgressReportingOutputStream(destinationFile)) {
                        totalBytesCopied += copy(inputStream, outputStream);
                    }
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return totalBytesCopied;
    }

    @Override
    protected Long doInBackground(Void... voids) {
        return unzip();
    }

    @Override
    protected void onPreExecute() {
        if (progressDialog != null) {
            progressDialog.setTitle("Extracting");
            progressDialog.setMessage(inputFile.getName());
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setOnCancelListener(dialog -> cancel(true));
            progressDialog.show();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (progressDialog == null) return;
        if (values.length > 1) {
            progressDialog.setMax(values[1]);
        } else {
            progressDialog.setProgress(values[0]);
        }
    }

    @Override
    protected void onPostExecute(Long result) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (isCancelled()) {
            return;
        }
        if (context instanceof logind) {
            ((logind) context).unzipok();
        }
    }
}