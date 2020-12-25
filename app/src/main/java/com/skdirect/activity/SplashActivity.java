package com.skdirect.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.skdirect.BuildConfig;
import com.skdirect.R;
import com.skdirect.api.CommonClassForAPI;
import com.skdirect.databinding.ActivitySplashBinding;
import com.skdirect.model.AppVersionModel;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.Utils;

import org.jetbrains.annotations.NotNull;

import io.reactivex.observers.DisposableObserver;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding mBinding;
    private SplashActivity activity;
    private CommonClassForAPI commonClassForAPI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        activity = this;
        commonClassForAPI = CommonClassForAPI.getInstance(this);
        initViews();

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        callAPI();
    }


    private void initViews() {
        Glide.with(activity).load("")
                .placeholder(R.drawable.splash)
                .into(mBinding.imSplash);
    }

    private void callAPI() {
        try {
            if (Utils.isNetworkAvailable(activity)) {
                if (commonClassForAPI != null) {
                    mBinding.pBar.setVisibility(View.VISIBLE);
                    commonClassForAPI.getAppVersionApi(versionObserver);
                } else {
                    mBinding.pBar.setVisibility(View.VISIBLE);
                    commonClassForAPI = CommonClassForAPI.getInstance(activity);
                    commonClassForAPI.getAppVersionApi(versionObserver);
                }
            } else {
                Utils.setToast(getBaseContext(), "No Internet Connection!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final DisposableObserver<AppVersionModel> versionObserver = new DisposableObserver<AppVersionModel>() {
        @Override
        public void onNext(@NotNull AppVersionModel response) {
            mBinding.pBar.setVisibility(View.GONE);
            try {
                SharePrefs.getInstance(activity).putString(SharePrefs.SELLER_URL, response.getSellerUrl());
                SharePrefs.getInstance(activity).putString(SharePrefs.BUYER_URL, response.getBuyerUrl());
                if (BuildConfig.VERSION_NAME.equalsIgnoreCase(response.getVersion())) {
                    SharePrefs.setStringSharedPreference(getApplicationContext(), SharePrefs.APP_VERSION, response.getVersion());
                    goHome();
                } else {
                    @SuppressLint("RestrictedApi") AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.Base_Theme_AppCompat_Dialog));
                    alertDialogBuilder.setTitle("Update Available");
                    alertDialogBuilder.setMessage("Please update the latest version " + response.getVersion() + " from play store");
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setPositiveButton("Update", (dialog, id) -> {
                        dialog.cancel();
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName())));
                    });
                    if (response.isCompulsory()) {
                        alertDialogBuilder.setNegativeButton("Cancel", (dialog, i) -> {
                            dialog.cancel();
                            finish();
                        });
                    } else {
                        alertDialogBuilder.setNegativeButton("Skip", (dialog, i) -> {
                            goHome();
                        });
                    }
                    alertDialogBuilder.show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onError(@NotNull Throwable e) {
            mBinding.pBar.setVisibility(View.GONE);
        }

        @Override
        public void onComplete() {
            mBinding.pBar.setVisibility(View.GONE);
        }
    };

    public void goHome() {
        Intent i = null;
        if (!SharePrefs.getInstance(activity).getBoolean(SharePrefs.IS_SHOW_INTRO)) {
            i = new Intent(activity, IntroActivity.class);
            startActivity(i);
            finish();
        } else {
            if (getIntent().getExtras() != null && getIntent().getStringExtra("url") != null) {
                String url = getIntent().getStringExtra("url");
                i = new Intent(activity, MainActivity.class);
                i.putExtra("url", url);
                startActivity(i);
                finish();
            } else {
                i = new Intent(activity, MainActivity.class);
                startActivity(i);
                finish();
            }

        }
    }
}