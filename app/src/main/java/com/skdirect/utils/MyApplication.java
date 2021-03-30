package com.skdirect.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;


import com.facebook.soloader.SoLoader;

import java.io.IOException;


public class MyApplication extends Application implements LifecycleObserver {
    public   Context appContext;
    private static MyApplication mInstance;
    private static MediaRecorder mediaRecorder;
    public Activity activity;

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SoLoader.init(this, false);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        appContext = this;
        mInstance = this;
    }



}