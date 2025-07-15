package com.yunsong.bujen.utils;

import static com.thingclips.smart.utils.ToastUtil.showToast;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.thingclips.smart.android.user.api.ILoginCallback;
import com.thingclips.smart.android.user.api.IRegisterCallback;
import com.thingclips.smart.android.user.bean.User;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.yunsong.bujen.BuildConfig;


import java.io.IOException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserAuthManager {
    public interface ResultCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    private final Context context;
    private final OkHttpClient client = new OkHttpClient();
    private static final String REGISTER_URL = BuildConfig.API_SERVER + "/app/register";
    private static final String LOGIN_URL = BuildConfig.API_SERVER + "/app/login";
    private final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public UserAuthManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public void registerAndLogin(String phone, String password, String code, ResultCallback callback) {
        ThingHomeSdk.getUserInstance().registerAccountWithPhone("86", phone, password, code, new IRegisterCallback() {
            @Override
            public void onSuccess(User user) {
                String uid = user.getUid();
                registerBackend(phone, password, code, uid, new ResultCallback() {
                    @Override
                    public void onSuccess() {
                        loginTuya(phone, password, code, uid, callback);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        postToMain(() -> callback.onFailure("后台注册失败：" + errorMessage));
                    }
                });
            }

            @Override
            public void onError(String code, String error) {
                postToMain(() -> callback.onFailure("涂鸦注册失败：" + error));
            }
        });
    }

    private void loginTuya(String phone, String password, String code, String uid, ResultCallback callback) {
        ThingHomeSdk.getUserInstance().loginWithPhonePassword("86", phone, password, new ILoginCallback() {
            @Override
            public void onSuccess(User user) {
                loginBackend(phone, password, code, uid, callback);
            }

            @Override
            public void onError(String code, String error) {
                postToMain(() -> callback.onFailure("涂鸦登录失败：" + error));
            }
        });
    }

    private void registerBackend(String phone, String password, String code, String uid, ResultCallback callback) {
        JSONObject json = new JSONObject();
        json.put("username", phone);
        json.put("password", password);
        json.put("code", code);
        json.put("uuid", uid);

        postJson(REGISTER_URL, json.toString(), callback, false);
    }

    private void loginBackend(String phone, String password, String code, String uid, ResultCallback callback) {
        JSONObject json = new JSONObject();
        json.put("username", phone);
        json.put("password", password);
        json.put("code", code);
        json.put("uuid", uid);

        postJson(LOGIN_URL, json.toString(), callback, true);
    }

    private void postJson(String url, String jsonStr, ResultCallback callback, boolean saveToken) {
        new Thread(() -> {
            try {
                RequestBody body = RequestBody.create(jsonStr, JSON);
                Request request = new Request.Builder().url(url).post(body).build();
                Response response = client.newCall(request).execute();

                if (!response.isSuccessful()) {
                    postToMain(() -> callback.onFailure("HTTP错误：" + response.code()));
                    return;
                }

                String resp = Objects.requireNonNull(response.body()).string();
                JSONObject json = JSONObject.parseObject(resp);

                if (saveToken) {
                    String token = json.getString("token");
                    saveToken(token);
                }

                postToMain(callback::onSuccess);
            } catch (IOException e) {
                postToMain(() -> callback.onFailure("网络错误：" + e.getMessage()));
            }
        }).start();
    }

    private void saveToken(String token) {
        SharedPreferences sp = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        sp.edit().putString("user_token", token).apply();
    }

    private void postToMain(Runnable r) {
        new Handler(Looper.getMainLooper()).post(r);
    }
}
