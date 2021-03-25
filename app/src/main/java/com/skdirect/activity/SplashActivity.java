package com.skdirect.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessaging;
import com.skdirect.BuildConfig;
import com.skdirect.R;
import com.skdirect.api.CommonClassForAPI;
import com.skdirect.databinding.ActivitySplashBinding;
import com.skdirect.model.AppVersionModel;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.VersionViewModel;

import java.util.List;

import io.reactivex.observers.DisposableObserver;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding mBinding;
   private SplashActivity activity;
   private VersionViewModel versionViewModel;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        versionViewModel = ViewModelProviders.of(this).get(VersionViewModel.class);
        activity = this;

        initViews();

    }

    private void initViews() {
        Glide.with(activity).load("")
                .placeholder(R.drawable.splash)
                .into(mBinding.imSplash);
    }

    @Override
    protected void onPostResume() {
        callAPI();
        super.onPostResume();
    }

    private void callAPI() {
        try {
            if (Utils.isNetworkAvailable(activity)) {
                getAppVersionApi();
            } else {
                Utils.setToast(getBaseContext(),"No Internet Connection!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAppVersionApi() {
        versionViewModel.getVersion().observe(this, new Observer<AppVersionModel>() {
            @Override
            public void onChanged(AppVersionModel appVersionModels) {
                mBinding.pBar.setVisibility(View.GONE);
                if (appVersionModels != null) {
                    if (BuildConfig.VERSION_NAME.equals(appVersionModels.getVersion())){
                        launchHomeScreen();
                        finish();
                    }else{
                        checkVersionData(appVersionModels);
                    }
                }
            }
        });

    }

    private void launchHomeScreen() {
        if (SharePrefs.getInstance(SplashActivity.this).getBoolean(SharePrefs.IS_LOGIN)){
            startActivity(new Intent(this,MainActivity.class));
        }else {
            startActivity(new Intent(this,PlaceSearchActivity.class));
        }
    }

    private void checkVersionData(AppVersionModel appVersionModels) {
        try {
            SharePrefs.getInstance(activity).putString(SharePrefs.SELLER_URL,appVersionModels.getSellerUrl());
            SharePrefs.getInstance(activity).putString(SharePrefs.BUYER_URL,appVersionModels.getBuyerUrl());
            if (BuildConfig.VERSION_NAME.equalsIgnoreCase(appVersionModels.getVersion())) {
                SharePrefs.setStringSharedPreference(getApplicationContext(), SharePrefs.APP_VERSION, appVersionModels.getVersion());

            }else {
                @SuppressLint("RestrictedApi") AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.Base_Theme_AppCompat_Dialog));
                alertDialogBuilder.setTitle("Update Available");
                alertDialogBuilder.setMessage("Please update the latest version " + appVersionModels.getVersion() + " from play store");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton("Update", (dialog, id) -> {
                    dialog.cancel();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+activity.getPackageName())));
                });
                alertDialogBuilder.setNegativeButton("Cancel", (dialog, i) -> {
                    dialog.cancel();
                    finish();
                });
                alertDialogBuilder.show();
            }

        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

}