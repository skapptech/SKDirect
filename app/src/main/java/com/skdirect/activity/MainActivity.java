package com.skdirect.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.attribution.AppsFlyerRequestListener;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.skdirect.BuildConfig;
import com.skdirect.R;
import com.skdirect.api.CommonClassForAPI;
import com.skdirect.broadcast.SmsBroadcastReceiver;
import com.skdirect.databinding.ActivityMainBinding;
import com.skdirect.editor.EditImageActivity;
import com.skdirect.interfacee.OtpReceivedInterface;
import com.skdirect.model.AppVersionModel;
import com.skdirect.model.UpdateTokenModel;
import com.skdirect.utils.AppSignatureHelper;
import com.skdirect.utils.ContactService;
import com.skdirect.utils.CreateContact;
import com.skdirect.utils.GPSTracker;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import io.reactivex.observers.DisposableObserver;

import static com.skdirect.utils.SharePrefs.clearPref;

public class MainActivity extends AppCompatActivity implements OtpReceivedInterface {
    private final int INPUT_FILE_REQUEST_CODE = 1, FILECHOOSER_RESULTCODE = 1, MY_PERMISSION_REQUEST_CODE = 123;
    private ActivityMainBinding mBinding;

    private MainActivity activity;
    private final Uri mCapturedImageURI = null;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath, otpNumber, firebaseToken = "";

    public static WebView webView;
    private CommonClassForAPI commonClassForAPI;
    private SmsBroadcastReceiver mSmsBroadcastReceiver;
    private CountDownTimer cTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activity = this;
        initViews();
        callRunTimePermissions();
        Log.e("key: ", new AppSignatureHelper(getApplicationContext()).getAppSignatures() + "");

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    firebaseToken = task.getResult();
                });
    }

    private void initViews() {
        webView = mBinding.webView;
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setUserAgentString("Android Mozilla/5.0 AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.addJavascriptInterface(new JavaScriptInterface(this), "Android");

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains(".png") || url.contains(".jpg")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(url), "application/file");
                    try {
                        view.getContext().startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    webView.loadUrl(url);
                }
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mBinding.pBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mBinding.pBar.setVisibility(View.GONE);
            }

            @Override
            public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP)
                    if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        webView.loadUrl("javascript:onEnter()");
                    }
            }

        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }

            public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, WebChromeClient.FileChooserParams fileChooserParams) {
                // Double check that we don't have any existing callbacks
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePath;

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    } catch (IOException ex) {
                        Log.e("Common.TAG", "Unable to create Image File", ex);
                    }

                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");

                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

                return true;

            }
        });

        if (getIntent().getData() != null) {
            webView.loadUrl(getIntent().getData().toString());
        } else if (getIntent().getExtras() != null && getIntent().hasExtra("url")) {
            String url = getIntent().getStringExtra("url");
            if (url.equals("")) {
                loadUrlfromSession();
            } else {
                webView.loadUrl(url);
            }
        } else {
            loadUrlfromSession();
        }

        commonClassForAPI = CommonClassForAPI.getInstance();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            FirebaseMessaging.getInstance().subscribeToTopic("uat_testing");
        }
        FirebaseMessaging.getInstance().subscribeToTopic("global");
       /* mSmsBroadcastReceiver = new SmsBroadcastReceiver();
        mSmsBroadcastReceiver.setOnOtpListeners(MainActivity.this);
        startSMSListener();*/
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getData() != null) {
            webView.loadUrl(intent.getData().toString());
        }
    }

    private class JavaScriptInterface {
        private final Context context;

        JavaScriptInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void setHeader(String title) {
            setTitle(title);
        }

        @JavascriptInterface
        public void showToast(String toast) {
            showToastMessage(toast);
        }

        @JavascriptInterface
        public void downloadPath(String url) {
            downloadFileFromUrl(url);
        }

        @JavascriptInterface
        public void toneSoundPath() {
            ringtone();
        }

        @JavascriptInterface
        public void openSellerBuyerSelectActivity(String data) {
            startActivity(new Intent(context, IntroActivity.class));
        }

        @JavascriptInterface
        public void sendNotification(String data) {
            setNotification(data, "SKDirect");
        }

        @JavascriptInterface
        public void shareWhatsapp(String message, String number) {
            showShareWhatsappDialog(message, number);
        }

        @JavascriptInterface
        public void exitApp() {
            closeApp();
        }

        @JavascriptInterface
        public void clearCache() {
            clearWebviewCache();
        }

        @JavascriptInterface
        public void clearSession() {
            clearLocalSession();
        }

        @JavascriptInterface
        public void openApp(String AppName, String PackageName) {
            Open(PackageName);
        }

        @JavascriptInterface
        public void shareText(String text) {
            ShareText(text);
        }

        @JavascriptInterface
        public void callNumber(String text) {
            Call(text);
        }

        @JavascriptInterface
        public void callLogout() {
            Logout();
        }

        @JavascriptInterface
        public void updateToken(String token) {
            callUpdateToken(token);
        }

        @JavascriptInterface
        public boolean isOpenInApp() {
            return true;
        }

        @JavascriptInterface
        public void reloadPage() {
            reloadPageview();
        }

        @JavascriptInterface
        public void redirectPage(String url) {
            redirectPageview(url);
        }

        @JavascriptInterface
        public void openUrlInBrowser(String url) {
            urlOpenInBrowser(url);
        }

        @JavascriptInterface
        public void askPermission() {
            callRunTimePermissions();
        }

        @JavascriptInterface
        public String getCurrentLocation() {
            return getCurrentLatLong();
        }

        @JavascriptInterface
        public void otpBroadCastReg(boolean value) {
            registerBroadcast(value);
        }

        @JavascriptInterface
        public void uploadAllContact(String token) {
            callContactPermissions(token);
        }

        @JavascriptInterface
        public void uploadAllContact2(String token) {
            uploadContact(token);
        }

        @JavascriptInterface
        public void openImageSelector() {
            initiateImageCropping();
        }

        @JavascriptInterface
        public void setIsSeller(boolean isSeller) {
            updateIsSeller(isSeller);
        }

        @JavascriptInterface
        public void saveSupportContact(String name, String number) {
            saveContact(name, number);
        }

        @JavascriptInterface
        public String getOTP() {
            mSmsBroadcastReceiver = new SmsBroadcastReceiver();
            mSmsBroadcastReceiver.setOnOtpListeners(MainActivity.this);
            startSMSListener();

            return otpNumber;
        }
        @JavascriptInterface
        public void addAppsFlayersEvent(String eventName, String value) {
            Utils.logAppsFlayerEventApp(getApplicationContext(),eventName, value);
        }

        @JavascriptInterface
        public void addAppsFlayersJsonEvent(String eventName, String value) {
            Utils.logAppsFlayerJSONEventApp(getApplicationContext(),eventName, value);
        }

        @JavascriptInterface
        public void shareItemWithImage(String imagePath, String msg) {
            shareProductwithImage(imagePath, msg);
        }
    }

    //Start JS Function's Method

    public void loadUrlfromSession() {
        if (SharePrefs.getInstance(activity).getBoolean(SharePrefs.IS_SELLER)) {
            webView.loadUrl(SharePrefs.getInstance(activity).getString(SharePrefs.SELLER_URL));
        } else {
            webView.loadUrl(SharePrefs.getInstance(activity).getString(SharePrefs.BUYER_URL));
        }
    }

    private String getCurrentLatLong() {
        JSONObject jsonObject = new JSONObject();
        GPSTracker gpsTracker = new GPSTracker(MainActivity.this);
        if (gpsTracker.canGetLocation()) {
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            try {
                jsonObject.put("lat", latitude);
                jsonObject.put("long", longitude);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            gpsTracker.showSettingsAlert();
        }
        return jsonObject.toString();
    }

    private void Open(String PackageName) {
        Intent intent = getPackageManager().getLaunchIntentForPackage(PackageName);
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + PackageName));
            startActivity(intent);
        }
    }

    private void Logout() {
        Intent i = new Intent(activity, SplashActivity.class);
        startActivity(i);
    }

    private void downloadFileFromUrl(String url) {
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        String fileName = url.substring(url.lastIndexOf("/") + 1);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDescription("Download " + fileName + " from " + url);
        request.setTitle("Document Downloading");
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/Trade");
        downloadManager.enqueue(request);
        Toast.makeText(this, "Document Downloaded in", Toast.LENGTH_SHORT).show();
    }

    private void setNotification(String messageBody, String title) {
        try {
            final String CHANNEL_ID = "chat";
            final String CHANNEL_NAME = "chat";

            Intent intent = new Intent(this, MainActivity.class);
            //intent.putExtra("list", poModel);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Main PendingIntent that restarts
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            // create notification
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.notification)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.notification))
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setContentInfo(title)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(pendingIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                mChannel.enableLights(true);
                mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                notificationManager.createNotificationChannel(mChannel);
            }
            notificationManager.notify(1, builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ringtone() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ShareText(String text) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(share, "Share"));
    }

    public void Call(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission(text);
        } else {
            callContact(text);
        }
    }

    public void checkPermission(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                    ActivityCompat.requestPermissions(
                            activity,
                            new String[]{Manifest.permission.CALL_PHONE},
                            MY_PERMISSION_REQUEST_CODE);
                } else { // Request permission
                    ActivityCompat.requestPermissions(
                            activity,
                            new String[]{Manifest.permission.CALL_PHONE},
                            MY_PERMISSION_REQUEST_CODE
                    );
                }
            } else {
                callContact(text);
            }
        }
    }

    private void callContact(String text) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel: " + text));
        if (ActivityCompat.checkSelfPermission
                (activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CALL_PHONE)) {

            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSION_REQUEST_CODE);
            }
        }
        startActivity(callIntent);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }

    private void callUpdateToken(String token) {
        try {
            if (Utils.isNetworkAvailable(activity)) {
                commonClassForAPI.getUpdateFirebaseToken(firebaseObserver, new UpdateTokenModel(firebaseToken), token);
            } else {
                Utils.setToast(activity, "No Internet Connection!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeApp() {
        finish();
        finishAffinity();
    }

    private void reloadPageview() {
        webView.reload();
    }

    private void redirectPageview(String url) {
        webView.loadUrl(url);
    }

    private void urlOpenInBrowser(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    private void clearWebviewCache() {
        webView.clearCache(true);
        webView.reload();
    }

    private void clearLocalSession() {
        clearPref(activity);
        Intent i = new Intent(activity, SplashActivity.class);
        startActivity(i);
    }

    private void showToastMessage(String msg) {
        Utils.setToast(activity, msg);
    }

    private void registerBroadcast(boolean value) {
        mSmsBroadcastReceiver = new SmsBroadcastReceiver();
        mSmsBroadcastReceiver.setOnOtpListeners(MainActivity.this);
        try {
            if (value) {
                LocalBroadcastManager.getInstance(this).registerReceiver(mSmsBroadcastReceiver, new IntentFilter("otp"));
                startSMSListener();
                otpTimer();
            } else {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(mSmsBroadcastReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startSMSListener() {
        try {
            getApplicationContext().unregisterReceiver(mSmsBroadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        getApplicationContext().registerReceiver(mSmsBroadcastReceiver, intentFilter);
        SmsRetrieverClient mClient = SmsRetriever.getClient(this);
        Task<Void> mTask = mClient.startSmsRetriever();
        mTask.addOnSuccessListener(aVoid -> {
        });
        mTask.addOnFailureListener(e -> {
        });
    }

    public void callRunTimePermissions() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS};
        Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
            }
        });
    }

    public void otpTimer() {
        cTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long mills) {
            }

            public void onFinish() {
                if (mSmsBroadcastReceiver != null) {
                    LocalBroadcastManager.getInstance(activity).unregisterReceiver(mSmsBroadcastReceiver);
                }
            }
        };
        cTimer.start();
    }

    private boolean appInstalledOrNot(String packageManager) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(packageManager, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showShareWhatsappDialog(String textMsg, String number) {
        BottomSheetDialog dialog = new BottomSheetDialog(activity, R.style.BottomTheme);
        dialog.setContentView(R.layout.dialog_whatsapp_share);
        dialog.setCanceledOnTouchOutside(true);
        LinearLayout llWhatsapp = dialog.findViewById(R.id.llWhatsapp);
        LinearLayout llWhatsappBusiness = dialog.findViewById(R.id.llWhatsappBusiness);
        if (appInstalledOrNot("com.whatsapp") && appInstalledOrNot("com.whatsapp.w4b")) {
            dialog.show();
        } else shareOnWhatsapp(textMsg, number, !appInstalledOrNot("com.whatsapp"));
        llWhatsapp.setOnClickListener(view -> {
            shareOnWhatsapp(textMsg, number, false);
            dialog.dismiss();
        });
        llWhatsappBusiness.setOnClickListener(view -> {
            shareOnWhatsapp(textMsg, number, true);
            dialog.dismiss();
        });
    }

    private void shareOnWhatsapp(String textMsg, String number, boolean isWB) {
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        if (isWB) {
            whatsappIntent.setPackage("com.whatsapp.w4b");
        } else {
            whatsappIntent.setPackage("com.whatsapp");
        }
        if (number != null && !number.equals("")) {
            whatsappIntent.putExtra("jid", PhoneNumberUtils.stripSeparators("91" + number) + "@s.whatsapp.net");
        }
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, textMsg);
        try {
            activity.startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Utils.setToast(activity, "Whatsapp not installed.");
        }
    }

    public void uploadContact(String token) {
        startService(new Intent(getApplicationContext(), ContactService.class).putExtra("token", token));
    }

    public void callContactPermissions(String token) {
        String[] permissions = {Manifest.permission.READ_CONTACTS};
        Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                uploadContact(token);
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
            }
        });
    }

    public void initiateImageCropping() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1)
                        .start(activity);
            }
            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
            }
        });
    }

    public void updateIsSeller(boolean isSeller) {
        SharePrefs.getInstance(activity).putBoolean(SharePrefs.IS_SELLER, isSeller);
    }

    public void saveContact(String name, String number){
        try {
            new CreateContact(activity, number, name);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void shareProductwithImage(String imagePath, String body) {
        runOnUiThread(() -> {
            try {
                Picasso.get().load(imagePath).into(target);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                BuildConfig.APPLICATION_ID + ".provider", new File(Environment.getExternalStorageDirectory()
                        + "/Download/image.png")));
        share.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(share, "Share Product"));
    }

    private final Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            try {
                String filename = "/Download/";
                File sd = Environment.getExternalStorageDirectory();
                File dest = new File(sd, filename);
                if (!dest.exists()) {
                    dest.mkdirs();
                }
                FileOutputStream out = new FileOutputStream(dest + "/image.png");
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    //End JS Function's Method


    //Otp Fetch Methods
    private final BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity, "direct")
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle("File Downloaded")
                    .setContentText("All Download completed");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(455, mBuilder.build());
        }
    };

    @Override
    public void onOtpReceived(String otp) {
        otpNumber = otp.replaceAll("[^0-9]", "").substring(0, 4);
        webView.loadUrl("javascript:setOtp(" + otpNumber + ")");
        if (mSmsBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mSmsBroadcastReceiver);
        }
        //webView.loadUrl("javascript:angularFunctionCalled(" + otpNumber + ")");
        /*webView.loadUrl("javascript: (function() {setOtp();}) ();");
        webView.loadUrl("javascript: (function() {document.getElementById('id2').value= '123';}) ();");*/
    }

    @Override
    public void onOtpTimeout() {

    }


    private final DisposableObserver<AppVersionModel> firebaseObserver = new DisposableObserver<AppVersionModel>() {
        @Override
        public void onNext(AppVersionModel response) {
            try {

            } catch (Exception ex) {
                ex.printStackTrace();

            }
        }

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public void onComplete() {
        }
    };

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            super.onActivityResult(requestCode, resultCode, data);
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Intent i = new Intent(activity, EditImageActivity.class);
                i.putExtra("ImageUri", resultUri.toString());
                startActivityForResult(i, 1000);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        } else if (resultCode == RESULT_OK && requestCode == 1000) {
            super.onActivityResult(requestCode, resultCode, data);
            /*byte[] byteArray = getIntent().getByteArrayExtra("image");
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);*/
            //webView.loadUrl("javascript:getImageFile(\"abc\")");
            String path = data.getStringExtra("image");
            webView.loadUrl("javascript:getImageFile(\"" + path + "\")");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }

            Uri[] results = null;

            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }

            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;

        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (null == this.mUploadMessage) {
                    return;
                }
                Uri result = null;
                try {
                    if (resultCode != RESULT_OK) {
                        result = null;
                    } else {
                        // retrieve from the private variable if the intent is null
                        result = data == null ? mCapturedImageURI : data.getData();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "activity :" + e,
                            Toast.LENGTH_LONG).show();
                }
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
    }
}
