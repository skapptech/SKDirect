package com.skdirect.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.skdirect.BuildConfig;
import com.skdirect.R;
import com.skdirect.databinding.ActivitySplashBinding;
import com.skdirect.firebase.FirebaseLanguageFetch;
import com.skdirect.model.AppVersionModel;
import com.skdirect.utils.MyApplication;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.TextUtils;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.VersionViewModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
                Utils.setToast(getBaseContext(), MyApplication.getInstance().dbHelper.getString(R.string.no_internet_connection));
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
                    if (appVersionModels.isSuccess()) {
                        if (BuildConfig.VERSION_NAME.equals(appVersionModels.getResultItem().getVersion())) {

                            String localLastLanguageUpdateDate = SharePrefs.getInstance(getApplicationContext()).getString(SharePrefs.LAST_LANGUAGE_UPDATE_DATE);
                            String serverLastLanguageUpdateDate = "";//appVersionModels.getCompanyDetails().getLanguageLastUpdated();
                            DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                            try {
                                if (TextUtils.isNullOrEmpty(localLastLanguageUpdateDate)) {
                                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.LAST_LANGUAGE_UPDATE_DATE, serverLastLanguageUpdateDate);
                                    SharePrefs.getInstance(getApplicationContext()).putBoolean(SharePrefs.IS_FETCH_LANGUAGE, true);
                                } else {
                                    Date serverdate = originalFormat.parse(serverLastLanguageUpdateDate);
                                    Date localdate = originalFormat.parse(localLastLanguageUpdateDate);
                                    if (serverdate.after(localdate)) {
                                        SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.LAST_LANGUAGE_UPDATE_DATE, serverLastLanguageUpdateDate);
                                        SharePrefs.getInstance(getApplicationContext()).putBoolean(SharePrefs.IS_FETCH_LANGUAGE, true);
                                    }
                                }

                                if (SharePrefs.getInstance(getApplicationContext()).getBoolean(SharePrefs.IS_FETCH_LANGUAGE)) {
                                    new FirebaseLanguageFetch(getApplicationContext()).fetchLanguage();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (appVersionModels.isSuccess()) {
                                if (BuildConfig.VERSION_NAME.equals(appVersionModels.getResultItem().getVersion())) {
                                    launchHomeScreen();
                                    finish();
                                } else {
                                    checkVersionData(appVersionModels);
                                }
                            }

                        }
                    }
                }
            }
        });
    }

    private void launchHomeScreen() {
      /*  if (SharePrefs.getSharedPreferences(getApplicationContext(), SharePrefs.IS_REGISTRATIONCOMPLETE)) {
            if (SharePrefs.getInstance(getApplicationContext()).getBoolean(SharePrefs.IS_LOGIN)) {
                startActivity(new Intent(activity, MainActivity.class));
            } else {
                startActivity(new Intent(activity, LoginActivity.class));
            }

        } else {
            if (SharePrefs.getSharedPreferences(getApplicationContext(), SharePrefs.Is_First_Time)) {
                if (SharePrefs.getInstance(getApplicationContext()).getBoolean(SharePrefs.IS_LOGIN)) {

                    startActivity(new Intent(activity, MainActivity.class));
                } else {
                    startActivity(new Intent(activity, LoginActivity.class));

                }
            } else {
                startActivity(new Intent(activity, IntroActivity.class));
            }


        }*/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor prefEditor = prefs.edit();
        prefEditor.remove(SharePrefs.FILTER_CATEGORY_LIST);
        prefEditor.apply();
        prefEditor.clear();
        if (SharePrefs.getSharedPreferences(getApplicationContext(), SharePrefs.Is_First_Time)) {
            startActivity(new Intent(activity, MainActivity.class));
        } else {
            startActivity(new Intent(activity, IntroActivity.class));
        }
    }

    private void checkVersionData(AppVersionModel appVersionModels) {
        try {
            SharePrefs.getInstance(activity).putString(SharePrefs.SELLER_URL, appVersionModels.getResultItem().getSellerUrl());
            SharePrefs.getInstance(activity).putString(SharePrefs.BUYER_URL, appVersionModels.getResultItem().getBuyerUrl());
            if (BuildConfig.VERSION_NAME.equalsIgnoreCase(appVersionModels.getResultItem().getVersion())) {

            } else {
                @SuppressLint("RestrictedApi") AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.Base_Theme_AppCompat_Dialog));
                alertDialogBuilder.setTitle(R.string.update_available);
                alertDialogBuilder.setMessage(MyApplication.getInstance().dbHelper.getString(R.string.update_to_latest_version)+" " + appVersionModels.getResultItem().getVersion() + " "+MyApplication.getInstance().dbHelper.getString(R.string.from_play_store));
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton(MyApplication.getInstance().dbHelper.getString(R.string.update), (dialog, id) -> {
                    dialog.cancel();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName())));
                });
                alertDialogBuilder.setNegativeButton(MyApplication.getInstance().dbHelper.getString(R.string.skip), (dialog, i) -> {
                    startActivity(new Intent(activity, MainActivity.class));
                    finish();
                });
                alertDialogBuilder.show();
            }

        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }
}