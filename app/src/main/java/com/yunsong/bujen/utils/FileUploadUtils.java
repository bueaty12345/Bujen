package com.yunsong.bujen.utils;

import android.content.Context;

import com.yunsong.bujen.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FileUploadUtils{
    public interface UploadCallback {
        void onSuccess(String url);
        void onFailure(String errorMsg);
    }

    public static void uploadFile(Context context, File file, UploadCallback callback) {
        String token = UserInfoUtils.getToken(context);

        RequestBody fileBody = RequestBody.create(file, MediaType.parse("image/jpeg"));
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(BuildConfig.API_SERVER + "/common/upload")
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("网络异常：" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                try {
                    JSONObject json = new JSONObject(result);
                    if (json.optInt("code") == 200) {
                        String url = json.optString("url");
                        callback.onSuccess(url);
                    } else {
                        callback.onFailure("上传失败：" + json.optString("msg"));
                    }
                } catch (JSONException e) {
                    callback.onFailure("解析失败：" + e.getMessage());
                }
            }
        });
    }
}
