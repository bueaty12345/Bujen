package com.yunsong.bujen.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class DataStorageUtils {

    public static void  saveGddCount(Context context, int count) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("gdd_cont", count).apply();
    }

    public static int getGddCount(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("gdd_cont", 0);
    }
}
