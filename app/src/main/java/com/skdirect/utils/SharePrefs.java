package com.skdirect.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class SharePrefs {

    public static String PREFERENCE = "Direct";

    public static final String IS_SHOW_INTRO = "IsShowIntro";
    public static final String BUYER_URL = "BuyerUrl";
    public static final String SELLER_URL = "SellerUrl";
    public static final String APP_VERSION = "AppVersion";
    public static final String IS_SELLER = "IsSeller";
    public static final String IS_USER_EXISTS = "UserExists";
    public static final String RESULT = "Result";
    public static final String USER_ID = "Userid";
    public static final String IS_LOGIN ="is_loggedIn";
    public static final String TOKEN ="access_token";
    public static final String USER_NAME ="user_name";
    public static final String FIRST_NAME ="first_name";
    public static final String MOBILE_NUMBER ="mobile";
    public static final String SHOP_NAME ="shop_name";
    public static final String EMAIL_ID ="email";
    public static final String STATE ="state";
    public static final String CITY ="city";
    public static final String IS_REGISTRACTION ="is";
    public static final String COUNT ="count";



    private Context ctx;
    private SharedPreferences sharedPreferences;
    private static SharePrefs instance;


    public SharePrefs(Context context) {
        ctx = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SharePrefs getInstance(Context ctx) {
        if (instance == null) {
            instance = new SharePrefs(ctx);
        }
        return instance;
    }

    public void putString(String key, String val) {
        sharedPreferences.edit().putString(key, val).apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void putInt(String key, Integer val) {
        sharedPreferences.edit().putInt(key, val).apply();
    }

    public void putBoolean(String key, Boolean val) {
        sharedPreferences.edit().putBoolean(key, val).apply();
    }

    public Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public static String getStringSharedPreferences(Context context, String name) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
        return settings.getString(name, "");
    }

    public static void setStringSharedPreference(Context context, String name, String value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(name, value);
        editor.apply();
    }

    public static void clearPref(Context context){
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.apply();
    }
}