package com.skdirect.activity;

import android.Manifest;
import android.annotation.SuppressLint;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import com.skdirect.filter.FileUtils;
import com.skdirect.interfacee.OtpReceivedInterface;
import com.skdirect.model.AppVersionModel;
import com.skdirect.model.UpdateTokenModel;
import com.skdirect.utils.AppSignatureHelper;
import com.skdirect.utils.ContactService;
import com.skdirect.utils.GPSTracker;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.Utils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import iamutkarshtiwari.github.io.ananas.editimage.EditImageActivity;
import iamutkarshtiwari.github.io.ananas.editimage.ImageEditorIntentBuilder;
import iamutkarshtiwari.github.io.ananas.picchooser.SelectPictureActivity;
import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

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
    private final int PHOTO_EDITOR_REQUEST_CODE = 231;
    public static final int SELECT_GALLERY_IMAGE_CODE = 7;
    public static final int TAKE_PHOTO_CODE = 8;
    public static final int ACTION_REQUEST_EDITIMAGE = 9;
    private String uploadFilePath;
    private File photoFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activity = this;
        initViews();
        //initiateImageCropping();
        callRunTimePermissions();
        Log.e("key: ", new AppSignatureHelper(getApplicationContext()).getAppSignatures() + "");

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    firebaseToken = task.getResult();
                });

       /* mBinding.refreshLayout.setOnRefreshListener(() -> webView.reload()
        );

        webView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (webView.getScrollY() == 0) {
                    mBinding.refreshLayout.setEnabled(true);
                } else {
                    mBinding.refreshLayout.setEnabled(false);
                }
            }
        });*/
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
                Log.e("PAGE HISTORY: ", view.getUrl());
                if (view.getUrl().equalsIgnoreCase("https://skdirectbuyer.shopkirana.in/ui/app-home/1")) {
                    SharePrefs.getInstance(activity).putString(SharePrefs.LAST_VISITED_PAGE, "");
                } else {
                    SharePrefs.getInstance(activity).putString(SharePrefs.LAST_VISITED_PAGE, view.getUrl());
                }

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mBinding.pBar.setVisibility(View.GONE);
               /* if (SharePrefs.getInstance(activity).getBoolean(SharePrefs.IS_SELLER)) {
                    SharePrefs.getInstance(activity).putString(SharePrefs.SELLER_URL, url);
                } else {
                    SharePrefs.getInstance(activity).putString(SharePrefs.BUYER_URL, url);
                }*/
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
               /* if (newProgress == 100) {
                    mBinding.pBar.setVisibility(View.GONE);
                } else {
                    mBinding.pBar.setVisibility(View.VISIBLE);
                }*/
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
            //initiateImageCropping();
            showImageSelectDialog();
        }


        @JavascriptInterface
        public String getOTP() {
            mSmsBroadcastReceiver = new SmsBroadcastReceiver();
            mSmsBroadcastReceiver.setOnOtpListeners(MainActivity.this);
            startSMSListener();

            return otpNumber;
        }
    }

    //Start JS Function's Method

    public void loadUrlfromSession() {
        //if (SharePrefs.getInstance(activity).getString(SharePrefs.LAST_VISITED_PAGE).equalsIgnoreCase("")) {
        if (SharePrefs.getInstance(activity).getBoolean(SharePrefs.IS_SELLER)) {
            webView.loadUrl(SharePrefs.getInstance(activity).getString(SharePrefs.SELLER_URL));
        } else {
            webView.loadUrl(SharePrefs.getInstance(activity).getString(SharePrefs.BUYER_URL));
        }
        /*}else {
            webView.loadUrl(SharePrefs.getInstance(activity).getString(SharePrefs.LAST_VISITED_PAGE));
        }*/
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
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1)
                .start(activity);
    }

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


    private void showImageSelectDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(activity, R.style.BottomTheme);
        dialog.setContentView(R.layout.dialog_image_select);
        dialog.setCanceledOnTouchOutside(true);
        LinearLayout llCamera = dialog.findViewById(R.id.llCamera);
        LinearLayout llGallery = dialog.findViewById(R.id.llGallery);
        llCamera.setOnClickListener(view -> {
            cameraPermission();
            dialog.dismiss();
        });
        llGallery.setOnClickListener(view -> {
            internalStoragePermission();
            dialog.dismiss();
        });
        dialog.show();
    }

    public void internalStoragePermission() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                activity.startActivityForResult(new Intent(
                                MainActivity.this, SelectPictureActivity.class),
                        SELECT_GALLERY_IMAGE_CODE);
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
            }
        });
    }

    public void cameraPermission() {
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    photoFile = FileUtils.genEditFile();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                } else {
                    photoFile = FileUtils.genEditFile();
                    Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                }
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                    startActivityForResult(intent, TAKE_PHOTO_CODE);
                }
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
            }
        });
    }


    private void initiateNewImageEditor(String path) {
        File outputFile = FileUtils.genEditFile();
        try {
            Intent intent = new ImageEditorIntentBuilder(this, path, outputFile.getAbsolutePath())
                    .withAddText()
                    //.withPaintFeature()
                    .withFilterFeature()
                    .withRotateFeature()
                    .withCropFeature()
                    .withBrightnessFeature()
                    .withSaturationFeature()
                    //.withBeautyFeature()
                    //.withStickerFeature()
                    //.withEditorTitle("Photo Editor")
                    .forcePortrait(true)
                    .setSupportActionBarVisibility(false)
                    .build();
            EditImageActivity.start(this, intent, ACTION_REQUEST_EDITIMAGE);
        } catch (Exception e) {
            Toast.makeText(this, R.string.iamutkarshtiwari_github_io_ananas_not_selected, Toast.LENGTH_SHORT).show();
            Log.e("Demo App", e.getMessage());
        }
    }

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
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_GALLERY_IMAGE_CODE && resultCode == RESULT_OK && null != data) {
            try {
                String path = data.getStringExtra("imgPath");
                initiateNewImageEditor(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            try {
                if (photoFile != null) {
                    String path = photoFile.getPath();
                    initiateNewImageEditor(path);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == ACTION_REQUEST_EDITIMAGE && resultCode == RESULT_OK) {
            String newFilePath = data.getStringExtra(ImageEditorIntentBuilder.OUTPUT_PATH);
            boolean isImageEdit = data.getBooleanExtra(EditImageActivity.IS_IMAGE_EDITED, false);
            if (isImageEdit) {
                uploadMultipart(newFilePath);
            } else {
                newFilePath = data.getStringExtra(ImageEditorIntentBuilder.SOURCE_PATH);
                uploadMultipart(newFilePath);
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Intent i = new Intent(activity, ImageEditorActivity.class);
                i.putExtra("ImageUri", resultUri.toString());
                startActivityForResult(i, 1000);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        } else if (resultCode == RESULT_OK && requestCode == 1000) {
            /*byte[] byteArray = getIntent().getByteArrayExtra("image");
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);*/
            //webView.loadUrl("javascript:getImageFile(\"abc\")");
            String path = data.getStringExtra("image");
            webView.loadUrl("javascript:getImageFile(\"" + path + "\")");

            System.out.println("abc = " + path);

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

    @SuppressLint("CheckResult")
    private void uploadMultipart(String path) {
        final File fileToUpload = new File(path);
        new Compressor(this)
                .compressToFileAsFlowable(fileToUpload)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::uploadImagePath, throwable -> {
                    throwable.printStackTrace();
                    showError(throwable.getMessage());
                });
    }

    private void showError(String errorMessage) {
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void uploadImagePath(File file) {
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("Image", file.getName(), requestFile);
        Utils.showProgressDialog(this);
        commonClassForAPI.uploadImage(imageObserver, body);
    }

    // upload image
    private final DisposableObserver<String> imageObserver = new DisposableObserver<String>() {
        @Override
        public void onNext(@NotNull String response) {
            try {
                Utils.hideProgressDialog();
                if (response == null) {
                    Log.e("Failed", "Failed ####  " + response);
                    Utils.setToast(getApplicationContext(), "Image Not Uploaded");
                } else {
                    Log.e("Uploaded - ", "" + response);
                    webView.loadUrl("javascript:getImageFile(\"" + response + "\")");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
            Utils.hideProgressDialog();
        }

        @Override
        public void onComplete() {
            Utils.hideProgressDialog();
        }
    };
}
