package com.dewii.tracker.database;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

public class Preferences {
    public static final String TAG = "PreferenceUtils";

    public static final String PREFERENCES = "dewii_preferences";
    public static final String LAST_TESTING_DATA = PREFERENCES + "_last_testing_data";
    
    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    public static void saveString(Context context, String key, String value) {
        getPreferences(context)
                .edit().putString(key, value).apply();
    }

    public static void deleteString(Context context, String key) {
        getPreferences(context)
                .edit().remove(key).apply();
    }

    public static String getString(Context context, String key) {
        return getPreferences(context)
                .getString(key, null);
    }

    public static void saveBoolean(Context context, String key, boolean value) {
        getPreferences(context)
                .edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(Context context, String key) {
        return getPreferences(context)
                .getBoolean(key, false);
    }

    public static void saveInt(Context context, String key, int value) {
        getPreferences(context)
                .edit().putInt(key, value).apply();
    }

    public static int getInt(Context context, String key) {
        return getPreferences(context)
                .getInt(key, 0);
    }

    public static void saveJsonObject(Context context, String key, JSONObject jsonObject) {
        saveString(context, key, jsonObject.toString());
    }

    public static JSONObject getJsonObject(Context context, String key) {
        try {
            return new JSONObject(getString(context, key));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void clearPreference(Context context) {
        getPreferences(context)
                .edit().clear().apply();
    }
}
