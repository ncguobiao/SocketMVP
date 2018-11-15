package com.example.baselibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.baselibrary.common.BaseApplication;

/**
 * 缓存工具类
 *
 * @author XXX
 */
public class SpUtils {
    public static SharedPreferences getSP(Context context) {
        return context.getSharedPreferences("socket_info", Context.MODE_PRIVATE);
    }

    public static void put(Context context, String key, Object value) {
        SharedPreferences sp = getSP(context);
        SharedPreferences.Editor edit = sp.edit();
        if (value instanceof String) {
            edit.putString(key, (String) value);
        } else if (value instanceof Boolean) {
            edit.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            edit.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            edit.putLong(key, (Long) value);
        }
        edit.apply();
    }

    public static String getString(Context context, String key) {
        SharedPreferences sp = getSP(context);
        return sp.getString(key, "");
    }

    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sp = getSP(context);
        return sp.getBoolean(key, false);
    }

    public static int getInt(Context context, String key) {
        SharedPreferences sp = getSP(context);
        return sp.getInt(key, 0);
    }

    public static long getLong(Context context, String key) {
        SharedPreferences sp = getSP(context);
        return sp.getLong(key, 0);
    }

    public static void remove(String key) {

        getSP(BaseApplication.Companion.getApplication()).edit().remove(key).apply();
    }
}
