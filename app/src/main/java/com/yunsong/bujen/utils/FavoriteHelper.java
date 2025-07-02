package com.yunsong.bujen.utils;

import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.util.Log;

import com.yunsong.bujen.BuildConfig;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Locale;

public class FavoriteHelper {
    public interface Callback {
        void onSuccess();
        void onFailure(String errorMsg);
    }
    private static final String FAVORITES_URL = BuildConfig.API_SERVER + "/system/favorites/addRemove";

    public static void updateFavoriteStatus(boolean isCollect,String token, int userId, String resourceType, int resourceId, Callback callback) {
        new AsyncTask<Void, Void, Boolean>() {

            String errorMsg = null;

            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    URL url = new URL(FAVORITES_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setRequestProperty("Authorization", "Bearer " + token);
                    conn.setDoOutput(true);

                    JSONObject json = new JSONObject();
                    json.put("userId", userId);
                    json.put("resourceType", resourceType);
                    json.put("resourceId", resourceId);
                    json.put("favoriteTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                    json.put("isFavorite",isCollect);

                    Log.d("收藏请求参数", json.toString());
                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(json.toString().getBytes(StandardCharsets.UTF_8));
                        os.flush();
                    }

                    int code = conn.getResponseCode();
                    Log.d("收藏操作",(isCollect ? "收藏" : "取消收藏") + " 状态码：" + code);
                    if (code >= 200 && code < 300) {
                        return true;
                    } else {
                        try (BufferedReader errorReader = new BufferedReader(
                                new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                            StringBuilder errorMsgBuilder = new StringBuilder();
                            String line;
                            while ((line = errorReader.readLine()) != null) {
                                errorMsgBuilder.append(line);
                            }
                            errorMsg = "状态码: " + code + "，错误信息: " + errorMsgBuilder.toString();
                        }
                        return false;
                    }

                } catch (Exception e) {
                    errorMsg = e.getMessage();
                    Log.e("收藏异常", "失败: " + errorMsg);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    if (callback != null) callback.onSuccess();
                } else {
                    if (callback != null) callback.onFailure(errorMsg != null ? errorMsg : "未知错误");
                }
            }
        }.execute();
    }
}
