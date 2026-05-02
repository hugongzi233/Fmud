package com.zwyouto.myactivex;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SyncHttp {
    public static void uploadFile(String actionUrl, String uploadFilePath) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(actionUrl).openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=******");
        DataOutputStream outputStream = getDataOutputStream(uploadFilePath, connection);
        InputStream inputStream = connection.getInputStream();
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(reader);
        bufferedReader.readLine();
        reader.close();
        bufferedReader.close();
        outputStream.close();
        inputStream.close();
    }

    private static @NotNull DataOutputStream getDataOutputStream(String uploadFilePath, HttpURLConnection connection) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.writeBytes("--******\r\n");
        outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\"" + uploadFilePath.substring(uploadFilePath.lastIndexOf("/") + 1) + "\"\r\n");
        outputStream.writeBytes("\r\n");
        FileInputStream fileInputStream = new FileInputStream(uploadFilePath);
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        fileInputStream.close();
        outputStream.writeBytes("\r\n");
        outputStream.writeBytes("--******--\r\n");
        outputStream.flush();
        return outputStream;
    }
}