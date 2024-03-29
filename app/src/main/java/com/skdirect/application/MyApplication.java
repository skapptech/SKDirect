package com.skdirect.application;

import android.app.Application;
import android.content.Intent;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.google.firebase.messaging.FirebaseMessaging;
import com.onesignal.OSDeviceState;
import com.onesignal.OSNotification;
import com.onesignal.OneSignal;
import com.skdirect.BuildConfig;
import com.skdirect.activity.SplashActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyApplication extends Application {
    private static MyApplication mInstance;

    //Production
    private static String AF_DEV_KEY = "govkuzbhqtFWx8WYppYa6P";
    private static String ONESIGNAL_APP_ID = "eda68699-028d-403f-8ef8-cd7306db0a09";


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        if (BuildConfig.DEBUG) {
            FirebaseMessaging.getInstance().subscribeToTopic("uat_testing");
        }
        FirebaseMessaging.getInstance().subscribeToTopic("global");

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        OneSignal.setNotificationOpenedHandler(
                result -> {
                    try {
                        String link = result.getNotification().getAdditionalData().getString("URL");
                        if (link != null && !link.equals("")) {
                            Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                            intent.putExtra("url", link);
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
        OSDeviceState device = OneSignal.getDeviceState();
        String deviceId = "";
        try {
            deviceId = device.getUserId();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, Object> oneSignalData = new HashMap<>();
        oneSignalData.put("onesignalCustomerId", deviceId);

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

        AppsFlyerLib.getInstance().setAdditionalData(oneSignalData);
        AppsFlyerLib.getInstance().init(AF_DEV_KEY, conversionListener, this);
        AppsFlyerLib.getInstance().start(this);

    }
}