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
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.skdirect.R;
import com.skdirect.api.CommonClassForAPI;
import com.skdirect.broadcast.SmsBroadcastReceiver;
import com.skdirect.databinding.ActivityMainBinding;
import com.skdirect.fragment.HomeFragment;
import com.skdirect.interfacee.OtpReceivedInterface;
import com.skdirect.model.AppVersionModel;
import com.skdirect.model.UpdateTokenModel;
import com.skdirect.utils.AppSignatureHelper;
import com.skdirect.utils.BottomNavigationBehavior;
import com.skdirect.utils.GPSTracker;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.Utils;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.observers.DisposableObserver;

import static com.skdirect.utils.SharePrefs.clearPref;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityMainBinding mBinding;
    private boolean doubleBackToExitPressedOnce = false;
    public boolean positionChanged = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Log.e("key: ", new AppSignatureHelper(getApplicationContext()).getAppSignatures() + "");
        openFragment(new HomeFragment());
        initView();
        clickListener();

    }

    private void clickListener() {
        mBinding.llProfile.setOnClickListener(this);
        mBinding.llChnagePassword.setOnClickListener(this);
        mBinding.llChet.setOnClickListener(this);
        mBinding.llLogout.setOnClickListener(this);
    }

    private void initView() {
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mBinding.toolbarId.bottomNavigation.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

        mBinding.toolbarId.ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.drawer.openDrawer(Gravity.START);
            }
        });

        mBinding.toolbarId.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    openFragment(new HomeFragment());
                    break;
                case R.id.m:
                    positionChanged = true;
                    openFragment(new HomeFragment());
                    break;
                case R.id.n:
                    positionChanged = true;
                    openFragment(new HomeFragment());
                    break;
                case R.id.v:
                    positionChanged = true;
                    openFragment(new HomeFragment());
                    break;
                case R.id.cart:
                    positionChanged = true;
                    openFragment(new HomeFragment());
                    break;

            }
            return true;
        });
    }

    @Override
    public void onBackPressed() {
        if ( mBinding.drawer.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    finishAffinity();
                    return;
                }
                doubleBackToExitPressedOnce = true;
                Snackbar.make( mBinding.drawer, "Please click back again to exit",
                        Snackbar.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
            } else {
                if (positionChanged) {
                    positionChanged = false;
                    mBinding.toolbarId.bottomNavigation.getMenu().getItem(0).setChecked(true);
                    super.onBackPressed();
                } else {
                    super.onBackPressed();
                }
            }
        }
    }
    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_frame, fragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_profile:
                Utils.setToast(getApplicationContext(),"Profile");
                mBinding.drawer.closeDrawers();
                break;

            case R.id.ll_chnage_password:
                Utils.setToast(getApplicationContext(),"Change Password");
                mBinding.drawer.closeDrawers();
                break;

            case R.id.ll_chet:
                Utils.setToast(getApplicationContext(),"Chet");
                mBinding.drawer.closeDrawers();
                break;

            case R.id.ll_logout:
                Utils.setToast(getApplicationContext(),"Logout");
                mBinding.drawer.closeDrawers();
                break;
        }

    }
}
