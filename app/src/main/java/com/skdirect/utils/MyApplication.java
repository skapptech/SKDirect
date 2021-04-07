package com.skdirect.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LifecycleObserver;

import com.facebook.soloader.SoLoader;
import com.google.gson.JsonObject;
import com.skdirect.api.CommonClassForAPI;
import com.skdirect.db.CartRepository;
import com.skdirect.model.TokenModel;

import org.jetbrains.annotations.NotNull;

import io.reactivex.observers.DisposableObserver;

public class MyApplication extends Application implements LifecycleObserver {
    public Context appContext;
    private static MyApplication mInstance;
    public Activity activity;
    public CartRepository cartRepository;
    public DBHelper dbHelper;

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SoLoader.init(this, false);
        appContext = this;
        mInstance = this;
        cartRepository = new CartRepository(getApplicationContext());
        dbHelper = new DBHelper(this);
    }

    public void token() {
        new CommonClassForAPI().getToken(callToken, "password", Utils.getDeviceUniqueID(activity),
                "", true, true, "BUYERAPP", true,
                Utils.getDeviceUniqueID(activity), 28.0, 72.00, "452011");
    }


    private final DisposableObserver<TokenModel> callToken = new DisposableObserver<TokenModel>() {
        @Override
        public void onNext(@NotNull TokenModel model) {
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
                    new CommonClassForAPI().getUpdateToken(updatecallToken, Utils.getFcmToken());
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

    private final DisposableObserver<JsonObject> updatecallToken = new DisposableObserver<JsonObject>() {
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