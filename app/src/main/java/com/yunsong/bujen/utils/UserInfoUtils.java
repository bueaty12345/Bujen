package com.yunsong.bujen.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserInfoUtils {
    // 读取用户昵称
    public static String getUserNickname(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user_nickname", "未设置昵称");
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

}
