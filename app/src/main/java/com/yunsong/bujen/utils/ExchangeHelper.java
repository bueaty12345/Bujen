package com.yunsong.bujen.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.yunsong.bujen.BuildConfig;
import com.yunsong.bujen.ConfirmDialog;
import com.yunsong.bujen.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExchangeHelper {
    private static ConfirmDialog dialog;
    public static void showExchangeDialog(Activity activity, int requiredGdd, String resourceType, int resourceId, Button btn_dh) {
        ConfirmDialog.Builder builder = new ConfirmDialog.Builder(activity);
            dialog = builder.cancelTouchout(false)
                .view(R.layout.dialog_confirm)
                .style(R.style.Dialog)
                .addViewOnclick(R.id.txt_confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int userId = UserInfoUtils.getUserId(activity);
                        String token = UserInfoUtils.getToken(activity);

                        JSONObject recordBody = new JSONObject();
                        try {
                            recordBody.put("userId", userId);
                            recordBody.put("resourceType", resourceType);
                            recordBody.put("resourceId", resourceId);
                            recordBody.put("exchangeTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                            recordBody.put("exchangeQuantity", 1);
                            recordBody.put("meritPoints", requiredGdd);
                            recordBody.put("resourceValid", 1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ApiHelper.sendJsonRequest(activity, BuildConfig.API_SERVER + "/system/record", recordBody, token, "POST", new ApiHelper.SimpleCallback() {
                            @Override
                            public void onSuccess() {
                                int newGdd = com.yunsong.bujen.utils.DataStorageUtils.getGddCount(activity) - requiredGdd;
                                com.yunsong.bujen.utils.DataStorageUtils.saveGddCount(activity, newGdd);

                                JSONObject userBody = new JSONObject();
                                try {
                                    userBody.put("id", userId);
                                    userBody.put("virtuePoints", newGdd);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                ApiHelper.sendJsonRequest(activity, BuildConfig.API_SERVER + "/system/users", userBody, token, "PUT", new ApiHelper.SimpleCallback() {
                                    @Override
                                    public void onSuccess() {
                                        activity.runOnUiThread(() -> {
                                            btn_dh.setText("已拥有");
                                            btn_dh.setEnabled(false);
                                            dialog.dismiss();
                                            Toast.makeText(activity, "兑换成功", Toast.LENGTH_SHORT).show();
                                            // 存储兑换状态
                                            markAsExchanged(activity, resourceType, resourceId);
                                            activity.setResult(Activity.RESULT_OK);
                                            activity.finish();
                                        });
                                    }

                                    @Override
                                    public void onError(String errorMsg) {
                                        activity.runOnUiThread(() ->
                                                Toast.makeText(activity, "更新用户信息失败：" + errorMsg, Toast.LENGTH_SHORT).show());
                                    }
                                });
                            }

                            @Override
                            public void onError(String errorMsg) {
                                activity.runOnUiThread(() ->
                                        Toast.makeText(activity, "兑换失败：" + errorMsg, Toast.LENGTH_SHORT).show());
                            }
                        });
                    }
                }).build();

        dialog.show();
    }

    public static void showPrayExchangeDialog(Activity activity, int requiredGdd, String resourceType, int resourceId, Button btn_dh) {
        ConfirmDialog.Builder builder = new ConfirmDialog.Builder(activity);
        dialog = builder.cancelTouchout(false)
                .view(R.layout.dialog_confirm)
                .style(R.style.Dialog)
                .addViewOnclick(R.id.txt_confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int userId = UserInfoUtils.getUserId(activity);
                        String token = UserInfoUtils.getToken(activity);
                        int currentCount = getExchangeCount(activity, resourceType, resourceId);

                        JSONObject recordBody = new JSONObject();
                        try {
                            recordBody.put("userId", userId);
                            recordBody.put("resourceType", resourceType);
                            recordBody.put("resourceId", resourceId);
                            recordBody.put("exchangeTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                            recordBody.put("exchangeQuantity", currentCount+1);
                            recordBody.put("meritPoints", requiredGdd);
                            recordBody.put("resourceValid", 1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ApiHelper.sendJsonRequest(activity, BuildConfig.API_SERVER + "/system/record", recordBody, token, "POST", new ApiHelper.SimpleCallback() {
                            @Override
                            public void onSuccess() {
                                int newGdd = com.yunsong.bujen.utils.DataStorageUtils.getGddCount(activity) - requiredGdd;
                                com.yunsong.bujen.utils.DataStorageUtils.saveGddCount(activity, newGdd);

                                JSONObject userBody = new JSONObject();
                                try {
                                    userBody.put("id", userId);
                                    userBody.put("virtuePoints", newGdd);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                ApiHelper.sendJsonRequest(activity, BuildConfig.API_SERVER + "/system/users", userBody, token, "PUT", new ApiHelper.SimpleCallback() {
                                    @Override
                                    public void onSuccess() {
                                        activity.runOnUiThread(() -> {
                                            btn_dh.setText("已拥有");
                                            btn_dh.setEnabled(false);
                                            dialog.dismiss();
                                            Toast.makeText(activity, "兑换成功", Toast.LENGTH_SHORT).show();
                                            // 存储兑换状态
                                            incrementExchangeCount(activity, resourceType, resourceId);
                                            activity.setResult(Activity.RESULT_OK);
                                            activity.finish();
                                        });
                                    }

                                    @Override
                                    public void onError(String errorMsg) {
                                        activity.runOnUiThread(() ->
                                                Toast.makeText(activity, "更新用户信息失败：" + errorMsg, Toast.LENGTH_SHORT).show());
                                    }
                                });
                            }

                            @Override
                            public void onError(String errorMsg) {
                                activity.runOnUiThread(() ->
                                        Toast.makeText(activity, "兑换失败：" + errorMsg, Toast.LENGTH_SHORT).show());
                            }
                        });
                    }
                }).build();

        dialog.show();
    }

    public static void incrementExchangeCount(Context context, String resourceType, int resourceId) {
        SharedPreferences sp = context.getSharedPreferences("ExchangedPrefs", Context.MODE_PRIVATE);
        String key = "count_" + resourceType + "_" + resourceId;
        int currentCount = sp.getInt(key, 0);
        sp.edit().putInt(key, currentCount + 1).apply();
    }


    public static int getExchangeCount(Context context, String resourceType, int resourceId) {
        SharedPreferences sp = context.getSharedPreferences("ExchangedPrefs", Context.MODE_PRIVATE);
        String key = "count_" + resourceType + "_" + resourceId; // 👈 新 key
        return sp.getInt(key, 0);
    }

    public static void markAsExchanged(Context context, String resourceType, int resourceId) {
        SharedPreferences sp = context.getSharedPreferences("ExchangedPrefs", Context.MODE_PRIVATE);
        String key = resourceType + "_" + resourceId;
        sp.edit().putBoolean(key, true).apply();
    }

    public static boolean isExchanged(Context context, String resourceType, int resourceId) {
        SharedPreferences sp = context.getSharedPreferences("ExchangedPrefs", Context.MODE_PRIVATE);
        String key = resourceType + "_" + resourceId;
        return sp.getBoolean(key, false);
    }
}
