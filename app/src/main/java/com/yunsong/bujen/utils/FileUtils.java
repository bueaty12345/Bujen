package com.yunsong.bujen.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    /**
     * 将 Uri 转换为 File 对象（选图时使用）
     */
    public static File uriToFile(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        if (inputStream == null) throw new FileNotFoundException("无法读取图片流");

        File tempFile = File.createTempFile("upload_", ".jpg", context.getCacheDir());
        FileOutputStream outputStream = new FileOutputStream(tempFile);

        byte[] buffer = new byte[4096];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.close();
        inputStream.close();
        return tempFile;
    }

    /**
     * 将 Bitmap 转为临时文件（拍照时使用）
     */
    public static File bitmapToTempFile(Context context, Bitmap bitmap) throws IOException {
        File tempFile = File.createTempFile("camera_", ".jpg", context.getCacheDir());
        FileOutputStream outputStream = new FileOutputStream(tempFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
        outputStream.flush();
        outputStream.close();
        return tempFile;
    }
}
