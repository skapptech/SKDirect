package com.skdirect.application;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;
import com.skdirect.activity.SplashActivity;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MyApplication extends Application {
    private static MyApplication mInstance;

    //Production
    //private static final String AF_DEV_KEY = "govkuzbhqtFWx8WYppYa6P";

    //Test
    private static final String AF_DEV_KEY = "govkuzbhqtF";
    private static final String ONESIGNAL_APP_ID = "eda68699-028d-403f-8ef8-cd7306db0a09";
    private static final String GOOGLE_PROJECT_ID = "1043986372556";

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

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
                    String actionId = result.getAction().getActionId();
                    String title = result.getNotification().getTitle();
                    System.out.println("Result - "+ result.toString());
                    System.out.println("accept-button - "+actionId);
                    try {
                        String link = result.getNotification().getAdditionalData().getString("URL");
                        System.out.println("Result - "+ link);
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
                    if (data!=null) {
                        notificationReceivedEvent.complete(notification);
                    } else {
                        notificationReceivedEvent.complete(null);
                    }
        });

    }
}