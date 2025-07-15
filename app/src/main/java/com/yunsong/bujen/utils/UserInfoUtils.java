package com.yunsong.bujen.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserInfoUtils {
    private static final String KEY_NICKNAME_TIME = "nickname_modify_time";

    // 读取用户昵称
    public static String getUserNickname(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user_nickname", "未设置昵称");
    }

    //注册时间
    public static String getUserRegisterTime(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user_registerTime", "");
    }

    // 读取用户签名
    public static String getUserSignature(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user_signature", " ");
    }

    // 读取手机号
    public static String getUserPhone(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user_phone", "未设置手机号");
    }

    //获取功德点
    public static String getVirtuePoints(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user_virtuePoints", "");
    }

    //token
    public static String getToken(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user_token", "");
    }

    //id
    public static Integer getUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("id",0);
    }

    //性别
    public static String getUserGender(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user_gender", " ");
    }

    //头像
    public static String getUserAvatar(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user_avatar", " ");
    }

    // 保存用户昵称
    public static void saveUserNickname(Context context, String nickname) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_nickname", nickname);
        editor.apply(); // 异步保存
    }

    public static void saveNicknameModifyTime(Context context) {
        SharedPreferences sp = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        sp.edit().putLong(KEY_NICKNAME_TIME, System.currentTimeMillis()).apply();
    }

    public static long getLastNicknameModifyTime(Context context) {
        SharedPreferences sp = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        return sp.getLong(KEY_NICKNAME_TIME, 0);
    }

    public static void saveUserGender(Context context, String gender) {
        SharedPreferences sp = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        sp.edit().putString("user_gender", gender).apply();
    }

    public static void saveUserSignature(Context context, String signature) {
        SharedPreferences sp = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        sp.edit().putString("user_signature", signature).apply();
    }

    public static void saveUserAvatarUrl(Context context, String url) {
        SharedPreferences spUser = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        spUser.edit().remove("avatar").apply();

        // 保存新头像 URL 到 AppPrefs
        SharedPreferences spPrefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        spPrefs.edit().putString("user_avatar", url).apply();
    }
}
