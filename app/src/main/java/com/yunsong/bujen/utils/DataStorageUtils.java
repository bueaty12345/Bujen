package com.yunsong.bujen.utils;

import static com.yunsong.bujen.fragment.HomeFragment.gdd_cont;

import android.content.Context;
import android.content.SharedPreferences;

public class DataStorageUtils {

    public static void  saveGddCount(Context context, int count) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("gdd_cont", gdd_cont).apply();
    }

    public static int getGddCount(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("gdd_cont", 0);
    }
}
