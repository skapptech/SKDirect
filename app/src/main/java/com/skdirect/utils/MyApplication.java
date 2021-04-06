package com.skdirect.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;


import com.facebook.soloader.SoLoader;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.skdirect.activity.MainActivity;
import com.skdirect.activity.PlaceSearchActivity;
import com.skdirect.api.CommonClassForAPI;
import com.skdirect.model.TokenModel;
import com.skdirect.model.UpdateTokenModel;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.observers.DisposableObserver;


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

    public void token() {
        new CommonClassForAPI().getToken(callToken, "password", Utils.getDeviceUniqueID(activity), "", true, true, "BUYERAPP", true, Utils.getDeviceUniqueID(activity), 28.0, 72.00,"452011");
    }
    private DisposableObserver<TokenModel> callToken = new DisposableObserver<TokenModel>() {
        @Override
        public void onNext(TokenModel model) {
            try {
                Utils.hideProgressDialog();
                if (model != null) {
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.TOKEN, model.getAccess_token());
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.USER_NAME, model.getUserName());
                    SharePrefs.getInstance(getApplicationContext()).putBoolean(SharePrefs.IS_REGISTRATIONCOMPLETE, model.getIsRegistrationComplete());
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.LAT, model.getLatitiute());
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.LON, model.getLongitude());
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.BUSINESS_TYPE, model.getBusinessType());
                    SharePrefs.getInstance(getApplicationContext()).putBoolean(SharePrefs.IS_CONTACTREAD, model.getIscontactRead());
                    SharePrefs.getInstance(getApplicationContext()).putBoolean(SharePrefs.IS_SUPER_ADMIN, model.getIsSuperAdmin());
                    new CommonClassForAPI().getUpdateToken(updatecallToken,  Utils.getFcmToken());
                    SharePrefs.getInstance(getApplicationContext()).putBoolean(SharePrefs.IS_LOGIN, true);
                    if (activity != null)
                        activity.recreate();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utils.setToast(getApplicationContext(), "Invalid Password");

            }
        }

        @Override
        public void onError(Throwable e) {
            Utils.setToast(getApplicationContext(), "Invalid Password");
            Utils.hideProgressDialog();
            e.printStackTrace();
        }

        @Override
        public void onComplete() {
            Utils.hideProgressDialog();
        }
    };
    private DisposableObserver<JsonObject> updatecallToken = new DisposableObserver<JsonObject>() {
        @Override
        public void onNext(JsonObject model) {
            try {
                Utils.hideProgressDialog();
                if (model != null) {

                }
            } catch (Exception e) {
                e.printStackTrace();


            }
        }

        @Override
        public void onError(Throwable e) {
            Utils.hideProgressDialog();
            e.printStackTrace();
        }

        @Override
        public void onComplete() {
            Utils.hideProgressDialog();
        }
    };
}