package com.skdirect.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.skdirect.R;
import com.skdirect.activity.SplashActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        System.out.println("From data payload: " + remoteMessage.getData());
        if (remoteMessage.getData().size() > 0) {
            System.out.println("Message data payload: " + remoteMessage.getData());
        }
        JSONObject object = new JSONObject(remoteMessage.getData());
        try {
            String redirectUrl = "";
            if (object.has("redirecturl")) {
                redirectUrl = object.getString("redirecturl");
            }
            if (!object.has("custom")){
                showNotification(object.getString("icon"),
                        object.getString("body"),
                        object.getString("title"), 0, redirectUrl);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showNotification(String imageUrl, String messageBody, String title, int actionId, String link) {
        Bitmap myBitmap = null;
        try {
            if (!TextUtils.isEmpty(imageUrl)) {
                if (imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {
                    myBitmap = getBitmapFromURL(imageUrl);
                }
            }
            Intent intent = null;
            if (link != null && !link.equals("")) {
                intent = new Intent(getApplicationContext(), SplashActivity.class);
                intent.putExtra("url", link);
                intent.setData(Uri.parse(link));
            } else {
                intent = new Intent(getApplicationContext(), SplashActivity.class);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(getResources().getString(R.string.app_name),
                        getResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
                mChannel.enableLights(true);
                mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                notificationManager.createNotificationChannel(mChannel);
            }

            NotificationCompat.Builder notificationBuilder;
            if (myBitmap != null) {
                notificationBuilder = new NotificationCompat.Builder(this, getResources().getString(R.string.app_name))
                        .setSmallIcon(R.mipmap.notification)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(myBitmap))
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setContentInfo(messageBody)
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setSound(defaultSoundUri)
                        .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                        .setChannelId(getResources().getString(R.string.app_name))
                        .setContentIntent(pendingIntent);
            } else {
                notificationBuilder = new NotificationCompat.Builder(this, getResources().getString(R.string.app_name))
                        .setSmallIcon(R.mipmap.notification)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setContentInfo(messageBody)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setSound(defaultSoundUri)
                        .setChannelId(getResources().getString(R.string.app_name))
                        .setColorized(true)
                        .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                        .setContentIntent(pendingIntent);
            }

            NotificationManagerCompat notificationManagerCompat =
                    NotificationManagerCompat.from(getApplicationContext());
            notificationManagerCompat.notify((int) System.currentTimeMillis(), notificationBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}