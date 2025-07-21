package com.yunsong.bujen.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class BlessingUtils {
    public static boolean isTodayConfirmed(Context context,String today){
        SharedPreferences prefs = context.getSharedPreferences("blessing", Context.MODE_PRIVATE);
        return prefs.getBoolean(today, false);
    }

    public static void markTodayConfirmed(Context context, String today) {
        SharedPreferences prefs = context.getSharedPreferences("blessing", Context.MODE_PRIVATE);
        prefs.edit().putBoolean(today, true).apply();
    }
}
