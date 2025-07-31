package com.yunsong.bujen.utils;

import android.content.Context;
import android.util.Log;

import com.yunsong.bujen.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TutorialHistory {
    private static final String TAG = "TutorialHistory";
    private static final String RECORD_HISTORY = BuildConfig.API_SERVER + "/system/recordh";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient();

    public static void recordMeditationHistory(Context context, int userId, int tutorialId) {
        String token= UserInfoUtils.getToken(context);
        if (token == null || token.isEmpty()) {
            Log.w(TAG, "token 为空，跳过历史上报");
            return;
        }

        // 当前时间
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", userId);
            jsonObject.put("tutorialId", tutorialId);
            jsonObject.put("watchTime", currentTime);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

        Request request = new Request.Builder()
                .url(RECORD_HISTORY)
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Meditation", "记录冥想失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("Meditation", "记录冥想成功"+response);
                } else {
                    Log.w("Meditation", "记录冥想失败: " + response.code());
                }
            }
        });
    }
}
