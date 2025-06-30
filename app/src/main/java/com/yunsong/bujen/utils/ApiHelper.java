package com.yunsong.bujen.utils;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.yunsong.bujen.BuildConfig;
import com.yunsong.bujen.databean.BlessingBean;
import com.yunsong.bujen.databean.MusicBean;
import com.yunsong.bujen.databean.MyLightBean;
import com.yunsong.bujen.databean.MyTutorialBean;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiHelper {
    private static String MUSIC_INFO_URL = BuildConfig.API_SERVER+"/system/music/app/list"; //获取音乐接口URL
    private static String LIGHT_INFO_URL=BuildConfig.API_SERVER+"/system/background/app/list";//灯光
    private static String TUTORIAL_INFO_URL=BuildConfig.API_SERVER+"/system/tutorial/app/list";
    private static String PRAY_INFO_URL=BuildConfig.API_SERVER+"/system/blessing/app/list";
    private static final MediaType MEDIA_TYPE_JSON  = MediaType.parse("application/json; charset=utf-8");
    public static void sendJsonRequest(Context context, String url, JSONObject bodyJson, String token, String method, SimpleCallback callback) {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(bodyJson.toString(), MEDIA_TYPE_JSON);

        Request.Builder builder = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token);

        if ("PUT".equalsIgnoreCase(method)) {
            builder.put(body);
        } else {
            builder.post(body); // 默认为 POST
        }

        Request request = builder.build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("响应失败：" + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onError(e.getMessage());
            }
        });
    }
    public static void fetchMusicList(Context context, String token, ApiHelper.Callback<MusicBean> callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(MUSIC_INFO_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Authorization", "Bearer " + token);

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = in.readLine()) != null) {
                            response.append(line);
                        }
                        in.close();
                        return response.toString();
                    } else {
                        return "Error: response code " + responseCode;
                    }
                } catch (Exception e) {
                    return "Error: " + e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result.startsWith("Error:")) {
                    callback.onError(result);
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        List<MusicBean> list = JSON.parseArray(jsonObject.getString("rows"), MusicBean.class);
                        callback.onSuccess(list);
                    } catch (Exception e) {
                        callback.onError("解析失败：" + e.getMessage());
                    }
                }
            }
        }.execute();
    }

    public static void fetchLightList(Context context, String token, ApiHelper.Callback<MyLightBean> callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(LIGHT_INFO_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Authorization", "Bearer " + token);

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = in.readLine()) != null) {
                            response.append(line);
                        }
                        in.close();
                        return response.toString();
                    } else {
                        return "Error: response code " + responseCode;
                    }
                } catch (Exception e) {
                    return "Error: " + e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result.startsWith("Error:")) {
                    callback.onError(result);
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        List<MyLightBean> list = JSON.parseArray(jsonObject.getString("rows"), MyLightBean.class);
                        callback.onSuccess(list);
                    } catch (Exception e) {
                        callback.onError("解析失败：" + e.getMessage());
                    }
                }
            }
        }.execute();
    }

    public static void fetchTutorialList(Context context, String token, ApiHelper.Callback<MyTutorialBean> callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(TUTORIAL_INFO_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Authorization", "Bearer " + token);

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = in.readLine()) != null) {
                            response.append(line);
                        }
                        in.close();
                        return response.toString();
                    } else {
                        return "Error: response code " + responseCode;
                    }
                } catch (Exception e) {
                    return "Error: " + e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result.startsWith("Error:")) {
                    callback.onError(result);
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        List<MyTutorialBean> list = JSON.parseArray(jsonObject.getString("rows"), MyTutorialBean.class);
                        callback.onSuccess(list);
                    } catch (Exception e) {
                        callback.onError("解析失败：" + e.getMessage());
                    }
                }
            }
        }.execute();
    }

    public static void fetchPrayList(Context context, String token, ApiHelper.Callback<BlessingBean> callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(PRAY_INFO_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Authorization", "Bearer " + token);

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = in.readLine()) != null) {
                            response.append(line);
                        }
                        in.close();
                        return response.toString();
                    } else {
                        return "Error: response code " + responseCode;
                    }
                } catch (Exception e) {
                    return "Error: " + e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result.startsWith("Error:")) {
                    callback.onError(result);
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        List<BlessingBean> list = JSON.parseArray(jsonObject.getString("rows"), BlessingBean.class);
                        callback.onSuccess(list);
                    } catch (Exception e) {
                        callback.onError("解析失败：" + e.getMessage());
                    }
                }
            }
        }.execute();
    }
    public interface Callback<T> {
        void onSuccess(List<T> list);
        void onError(String message);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onError(String message);
    }

}
