package com.skdirect.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.LifecycleObserver;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.facebook.soloader.SoLoader;
import com.google.gson.JsonObject;
import com.onesignal.OSDeviceState;
import com.onesignal.OSInAppMessageAction;
import com.onesignal.OSNotification;
import com.onesignal.OneSignal;
import com.skdirect.activity.ProductDetailsActivity;
import com.skdirect.activity.SellerProfileActivity;
import com.skdirect.activity.SplashActivity;
import com.skdirect.api.CommonClassForAPI;
import com.skdirect.db.CartRepository;
import com.skdirect.model.TokenModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.observers.DisposableObserver;

public class MyApplication extends Application implements LifecycleObserver {
    public Context appContext;
    private static MyApplication mInstance;
    public Activity activity;
    public CartRepository cartRepository;
    public DBHelper dbHelper;
    public String otp = "1234";

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    //Production
    private static String AF_DEV_KEY = "govkuzbhqtFWx8WYppYa6P";
    private static String ONESIGNAL_APP_ID = "eda68699-028d-403f-8ef8-cd7306db0a09";

    @Override
    public void onCreate() {
        super.onCreate();
        SoLoader.init(this, false);
        appContext = this;
        mInstance = this;
        cartRepository = new CartRepository(getApplicationContext());
        dbHelper = new DBHelper(this);
        initializeOneSignalApps();
    }

    public void token() {
        new CommonClassForAPI().getToken(callToken, "password", !TextUtils.isNullOrEmpty(SharePrefs.getInstance(getApplicationContext()).getString(SharePrefs.MOBILE_NUMBER)) ? SharePrefs.getInstance(getApplicationContext()).getString(SharePrefs.MOBILE_NUMBER) : Utils.getDeviceUniqueID(getApplicationContext()),
                "", false, true, "BUYERAPP", true,
                Utils.getDeviceUniqueID(getApplicationContext()),
                Double.parseDouble(SharePrefs.getStringSharedPreferences(this, SharePrefs.LAT)),
                Double.parseDouble(SharePrefs.getStringSharedPreferences(this, SharePrefs.LON)),
                SharePrefs.getInstance(getApplicationContext()).getString(SharePrefs.PIN_CODE), "");
    }


    private final DisposableObserver<TokenModel> callToken = new DisposableObserver<TokenModel>() {
        @Override
        public void onNext(@NotNull TokenModel model) {
            try {
                Utils.hideProgressDialog();
                if (model != null) {
                    Utils.getTokenData(getApplicationContext(), model);
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

    public void initializeOneSignalApps() {

        AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {
            @Override
            public void onConversionDataSuccess(Map<String, Object> conversionData) {
                for (String attrName : conversionData.keySet()) {
                    //Log.d("LOG_TAG", "attribute: " + attrName + " = " + conversionData.get(attrName));
                }
            }

            @Override
            public void onConversionDataFail(String errorMessage) {
                //Log.d("LOG_TAG", "error getting conversion data: " + errorMessage);
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> attributionData) {
                for (String attrName : attributionData.keySet()) {
                    //Log.d("LOG_TAG", "attribute: " + attrName + " = " + attributionData.get(attrName));
                }
            }

            @Override
            public void onAttributionFailure(String errorMessage) {
                //Log.d("LOG_TAG", "error onAttributionFailure : " + errorMessage);
            }
        };

        AppsFlyerLib.getInstance().init(AF_DEV_KEY, conversionListener, this);
        AppsFlyerLib.getInstance().start(this);

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        OneSignal.setNotificationOpenedHandler(
                result -> {
                    try {
                        String type = result.getNotification().getAdditionalData().getString("TYPE");
                        String id = result.getNotification().getAdditionalData().getString("ID");

                        if (type != null && !type.equals("")) {
                            if (type.equals("Product")) {
                                Intent intent = new Intent(getApplicationContext(), ProductDetailsActivity.class);
                                intent.putExtra("ID", Integer.parseInt(id));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else if (type.equals("Seller")) {
                                Intent intent = new Intent(getApplicationContext(), SellerProfileActivity.class);
                                intent.putExtra("ID", Integer.parseInt(id));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        } else {
                            Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
        OneSignal.setNotificationWillShowInForegroundHandler(notificationReceivedEvent -> {
            OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "NotificationWillShowInForegroundHandler fired!" +
                    " with notification event: " + notificationReceivedEvent.toString());

            OSNotification notification = notificationReceivedEvent.getNotification();
            JSONObject data = notification.getAdditionalData();
            if (data != null) {
                notificationReceivedEvent.complete(notification);
            } else {
                notificationReceivedEvent.complete(null);
            }
        });

        if (OneSignal.getDeviceState().getUserId() != null) {
            OneSignal.setInAppMessageClickHandler(new OneSignal.OSInAppMessageClickHandler() {
                @Override
                public void inAppMessageClicked(OSInAppMessageAction result) {
                    String getData = result.getClickName();
                    getData = getData.substring(getData.lastIndexOf("/") + 1);
                    int id = Integer.parseInt(getData);
                    if (result.getClickName().contains("Seller")) {
                        Intent intent = new Intent(getApplicationContext(), SellerProfileActivity.class);
                        intent.putExtra("ID", id);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else if (result.getClickName().contains("Product")) {
                        Intent intent = new Intent(getApplicationContext(), ProductDetailsActivity.class);
                        intent.putExtra("ID", id);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            });
        }
    }
}