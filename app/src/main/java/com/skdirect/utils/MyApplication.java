package com.skdirect.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LifecycleObserver;

import com.facebook.soloader.SoLoader;
import com.google.gson.JsonObject;
import com.skdirect.activity.LoginActivity;
import com.skdirect.api.CommonClassForAPI;
import com.skdirect.db.CartRepository;
import com.skdirect.model.TokenModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

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
        new CommonClassForAPI().getToken(callToken, "password",!TextUtils.isNullOrEmpty(SharePrefs.getInstance(getApplicationContext()).getString(SharePrefs.MOBILE_NUMBER))?SharePrefs.getInstance(getApplicationContext()).getString(SharePrefs.MOBILE_NUMBER): Utils.getDeviceUniqueID(getApplicationContext()),
                "", false, true, "BUYERAPP", true,
                Utils.getDeviceUniqueID(getApplicationContext()),
                Double.parseDouble(SharePrefs.getStringSharedPreferences(this,SharePrefs.LAT)),
                Double.parseDouble(SharePrefs.getStringSharedPreferences(this,SharePrefs.LON)),
                SharePrefs.getInstance(getApplicationContext()).getString(SharePrefs.PIN_CODE),"");
    }


    private final DisposableObserver<TokenModel> callToken = new DisposableObserver<TokenModel>() {
        @Override
        public void onNext(@NotNull TokenModel model) {
            try {
                Utils.hideProgressDialog();
                if (model != null) {
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.TOKEN, model.getAccess_token());
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.USER_NAME, model.getUserName());
                    SharePrefs.setSharedPreference(activity, SharePrefs.IS_REGISTRATIONCOMPLETE, model.getIsRegistrationComplete());
                    SharePrefs.setStringSharedPreference(getApplicationContext(),SharePrefs.LAT, String.valueOf(model.getLatitiute()));
                    SharePrefs.setStringSharedPreference(getApplicationContext(),SharePrefs.LON, String.valueOf(model.getLongitude()));
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.BUSINESS_TYPE, model.getBusinessType());
                    SharePrefs.getInstance(getApplicationContext()).putBoolean(SharePrefs.IS_CONTACTREAD, model.getIscontactRead());
                    SharePrefs.getInstance(getApplicationContext()).putBoolean(SharePrefs.IS_SUPER_ADMIN, model.getIsSuperAdmin());
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.ASP_NET_USER_ID, model.getAspNetuserId());
                    new CommonClassForAPI().getUpdateToken(updatecallToken, Utils.getFcmToken());
                    SharePrefs.setSharedPreference(getApplicationContext(), SharePrefs.IS_LOGIN,true);
                    try {
                        JSONObject jsonObject = new JSONObject(model.getUserDetail());
                        SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.FIRST_NAME, jsonObject.getString("FirstName"));
                        SharePrefs.getInstance(getApplicationContext()).putInt(SharePrefs.ID, jsonObject.getInt("Id"));
                        SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.MOBILE_NUMBER, jsonObject.getString("MobileNo"));
                        SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.EMAIL_ID, jsonObject.getString("Email"));
                        SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.STATE, jsonObject.getString("State"));
                        SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.CITYNAME, jsonObject.getString("City"));
                        SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.PIN_CODE, jsonObject.getString("Pincode"));
                        SharePrefs.getInstance(getApplicationContext()).putInt(SharePrefs.PIN_CODE_master, jsonObject.getInt("PinCodeMasterId"));
                        SharePrefs.getInstance(getApplicationContext()).putBoolean(SharePrefs.IS_ACTIVE, jsonObject.getBoolean("IsActive"));
                        SharePrefs.getInstance(getApplicationContext()).putBoolean(SharePrefs.IS_DELETE, jsonObject.getBoolean("IsDelete"));
                        JSONArray jsonArray = jsonObject.getJSONArray("UserDeliveryDC");
                        if (jsonArray != null && jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                SharePrefs.getInstance(getApplicationContext()).putBoolean(SharePrefs.USER_IS_DELETE, object.getBoolean("IsDelete"));
                                SharePrefs.getInstance(getApplicationContext()).putBoolean(SharePrefs.USER_IS_ACTIVE, object.getBoolean("IsActive"));
                                SharePrefs.getInstance(getApplicationContext()).putInt(SharePrefs.USER_DC_ID, object.getInt("Id"));
                                SharePrefs.getInstance(getApplicationContext()).putInt(SharePrefs.USER_DC_USER_ID, object.getInt("UserId"));
                                SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.DELIVERY, object.getString("Delivery"));

                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

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