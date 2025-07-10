package com.yunsong.bujen.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.yunsong.bujen.BuildConfig;
import com.yunsong.bujen.fragment.SettingsViewModel;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GddManager {
    private static final int UPLOAD_THRESHOLD = 10;
    private static final long UPLOAD_INTERVAL_MS = 5 * 60 * 1000; // 5分钟

    private static int gddCont = 0;
    private static int unuploadedGdd = 0;
    private static long lastUploadTime = 0;

    private static SettingsViewModel sharedViewModel;

    // 初始化，在 Homepage 或 Application 中调用一次
    public static void init(Context context, SettingsViewModel viewModel) {
        gddCont = DataStorageUtils.getGddCount(context);
        sharedViewModel = viewModel;
        updateViewModel();
    }

    // 获取当前功德点数
    public static int getGddCount() {
        return gddCont;
    }

    // 设备敲击 +1
    public static void onDeviceKnock(Context context) {
        gddCont++;
        unuploadedGdd++;

        // 本地保存
        DataStorageUtils.saveGddCount(context, gddCont);

        // 通知 UI
        updateViewModel();

        // 判断是否需要上传
        maybeUpload(context);
    }

    // 设置当前功德点（用于初始化或同步）
    public static void setGddCount(Context context, int count) {
        gddCont = count;
        unuploadedGdd = 0;
        DataStorageUtils.saveGddCount(context, count);
        context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                .edit().putInt("user_virtuePoints", count).apply();
        updateViewModel();
    }

    // 通知 ViewModel（触发 UI 更新）
    private static void updateViewModel() {
        if (sharedViewModel != null) {
            sharedViewModel.setGddCont(gddCont);
        }
    }

    // 判断是否满足上传条件
    private static void maybeUpload(Context context) {
        long now = System.currentTimeMillis();
        boolean shouldUpload = unuploadedGdd >= UPLOAD_THRESHOLD || (now - lastUploadTime) > UPLOAD_INTERVAL_MS;

        if (shouldUpload) {
            uploadToServer(context);
        }
    }

    private static void uploadToServer(Context context) {
        int userId = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE).getInt("id", 0);
        int totalGdd = gddCont;
        String token=UserInfoUtils.getToken(context);
        new Thread(() -> {
            try {
                URL url = new URL(BuildConfig.API_SERVER + "/system/users");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setDoOutput(true);

                JSONObject payload = new JSONObject();
                payload.put("id", userId);
                payload.put("virtuePoints", totalGdd);

                OutputStream os = conn.getOutputStream();
                os.write(payload.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    lastUploadTime = System.currentTimeMillis();
                    unuploadedGdd = 0;

                    // 同步更新本地缓存
                    SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                    prefs.edit().putInt("user_virtuePoints", totalGdd).apply();

                    Log.d("GDD_UPLOAD", "上传成功：" + totalGdd);
                } else {
                    Log.e("GDD_UPLOAD", "上传失败：" + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
