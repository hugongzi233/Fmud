package com.zwyouto.myactivex;

import android.media.MediaRecorder;
import android.os.Environment;
import java.io.File;
import java.io.IOException;

public class AudioRecorder {
    private static final int SAMPLE_RATE_IN_HZ=8000;
    final String path;
    final MediaRecorder recorder = new MediaRecorder();

    public AudioRecorder(String str) {
        this.path = sanitizePath(str);
    }

    private String sanitizePath(String str) {
        if (!str.startsWith("/")) {
            str = "/" + str;
        }
        if (!str.contains(".")) {
            str = str + ".amr";
        }
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/my" + str;
    }

    public double getAmplitude() {
        return this.recorder.getMaxAmplitude();
    }

    public void start() throws IOException {
        String externalStorageState = Environment.getExternalStorageState();
        if (!externalStorageState.equals("mounted")) {
            throw new IOException("SD Card is not mounted,It is  " + externalStorageState + ".");
        }
        File parentFile = new File(this.path).getParentFile();
        if (parentFile != null && !parentFile.exists() && !parentFile.mkdirs()) {
            throw new IOException("Path to file could not be created");
        }
        this.recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        this.recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        this.recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        this.recorder.setAudioSamplingRate(SAMPLE_RATE_IN_HZ);
        this.recorder.setOutputFile(this.path);
        this.recorder.prepare();
        this.recorder.start();
    }

    public void stop() throws IOException {
        this.recorder.stop();
        this.recorder.release();
    }
}