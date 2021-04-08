package com.skdirect.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.skdirect.BuildConfig;
import com.skdirect.R;
import com.skdirect.databinding.ActivityMainBinding;
import com.skdirect.firebase.FirebaseLanguageFetch;
import com.skdirect.fragment.BasketFragment;
import com.skdirect.fragment.HomeFragment;
import com.skdirect.utils.AppSignatureHelper;
import com.skdirect.utils.DBHelper;
import com.skdirect.utils.GPSTracker;
import com.skdirect.utils.MyApplication;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.TextUtils;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.MainActivityViewMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public ActivityMainBinding mBinding;
    private boolean doubleBackToExitPressedOnce = false;
    private SharedPreferences sharedPreferences;
    public boolean positionChanged = false;
    public Toolbar appBarLayout;
    public TextView userNameTV, mobileNumberTV, setLocationTV;
    private MainActivityViewMode mainActivityViewMode;
    public DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainActivityViewMode = ViewModelProviders.of(this).get(MainActivityViewMode.class);
        openFragment(new HomeFragment());
        Log.e("key: ", new AppSignatureHelper(getApplicationContext()).getAppSignatures() + "");
        initView();
        openFragment(new HomeFragment());
        setlocationInHader();
        clickListener();
        setupBadge();
        ///callRunTimePermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupBadge();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (SharePrefs.getInstance(getApplicationContext()).getBoolean(SharePrefs.IS_FETCH_LANGUAGE)) {
            new FirebaseLanguageFetch(getApplicationContext()).fetchLanguage();
        }
    }


    private void clickListener() {
        mBinding.llProfile.setOnClickListener(this);
        mBinding.llChnagePassword.setOnClickListener(this);
        mBinding.llChet.setOnClickListener(this);
        mBinding.llLogout.setOnClickListener(this);
        mBinding.llSignIn.setOnClickListener(this);
        mBinding.llHowtoUse.setOnClickListener(this);
        mBinding.llChangeLanguage.setOnClickListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 2) {
            if (requestCode == 2 && resultCode == RESULT_OK) {
                Address address = data.getParcelableExtra("address");
                double lat = address.getLatitude();
                double log = address.getLongitude();
                setLocationTV.setText(Utils.getCityName(this, lat, log));
            }

        }
    }


    private void initView() {
        dbHelper = MyApplication.getInstance().dbHelper;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        appBarLayout = mBinding.toolbarId.toolbar;
        userNameTV = mBinding.navTop.tvUserName;
        mobileNumberTV = mBinding.navTop.tvMobileName;
        setLocationTV = mBinding.toolbarId.tvLoction;
        if (!TextUtils.isNullOrEmpty(SharePrefs.getInstance(this).getString(SharePrefs.FIRST_NAME))) {
            userNameTV.setText(SharePrefs.getInstance(this).getString(SharePrefs.FIRST_NAME));
        }
        mobileNumberTV.setText(SharePrefs.getInstance(this).getString(SharePrefs.MOBILE_NUMBER));

        mBinding.toolbarId.ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.drawer.openDrawer(GravityCompat.START);
            }
        });

        mBinding.toolbarId.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    openFragment(new HomeFragment());
                    break;
                case R.id.nav_profile:
                    positionChanged = true;
                    if (SharePrefs.getSharedPreferences(getApplicationContext(), SharePrefs.IS_REGISTRATIONCOMPLETE) && SharePrefs.getInstance(getApplicationContext()).getBoolean(SharePrefs.IS_LOGIN)) {
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    } else {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                    break;
                case R.id.nav_my_order:
                    positionChanged = true;
                    if (SharePrefs.getSharedPreferences(getApplicationContext(), SharePrefs.IS_REGISTRATIONCOMPLETE) && SharePrefs.getInstance(getApplicationContext()).getBoolean(SharePrefs.IS_LOGIN)) {
                        startActivity(new Intent(MainActivity.this, MyOrderActivity.class));
                    } else {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }

                    break;
                case R.id.nav_chat:
                    positionChanged = true;
                    openFragment(new BasketFragment());
                    break;

            }
            return true;
        });

        mBinding.toolbarId.notifictionCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
            }
        });

        setLocationTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, MapsExtendedActivity.class), 2);
            }
        });

        if (!TextUtils.isNullOrEmpty(SharePrefs.getInstance(getApplicationContext()).getString(SharePrefs.USER_IMAGE))) {
            Glide.with(this)
                    .load(SharePrefs.getInstance(getApplicationContext()).getString(SharePrefs.USER_IMAGE))
                    .centerCrop()
                    .into(mBinding.navTop.profileImageNav);
        } else {
            mBinding.navTop.profileImageNav.setImageDrawable(getResources().getDrawable(R.drawable.profile_round));
        }

        getCartItemApi();
        mBinding.tvProfileHead.setText(dbHelper.getString(R.string.profile));
        mBinding.tvProfileTitle.setText(dbHelper.getString(R.string.profile));
        mBinding.tvChangePassTitle.setText(dbHelper.getString(R.string.change_pass));
        mBinding.tvChatTitle.setText(dbHelper.getString(R.string.chat));
        mBinding.tvOtherSettingsHead.setText(dbHelper.getString(R.string.other_settings));
        mBinding.tvChangeLangTitle.setText(dbHelper.getString(R.string.change_language));
        mBinding.tvRateAppTitle.setText(dbHelper.getString(R.string.rate_app));
        mBinding.tvPrivacyTitle.setText(dbHelper.getString(R.string.privacy_policy));
        mBinding.tvAboutTitle.setText(dbHelper.getString(R.string.about_direct));
        mBinding.tvHelpTitle.setText(dbHelper.getString(R.string.help));
        mBinding.tvHowToTitle.setText(dbHelper.getString(R.string.how_to_use));
        mBinding.tvLogoutTitle.setText(dbHelper.getString(R.string.logout));
        mBinding.tvSigninTitle.setText(dbHelper.getString(R.string.sign_in));

    }

    private void setlocationInHader() {
        setLocationTV.setText(Utils.getCityName(this, Double.parseDouble(SharePrefs.getStringSharedPreferences(this, SharePrefs.LAT)), Double.parseDouble(SharePrefs.getStringSharedPreferences(this, SharePrefs.LON))));
    }

    @Override
    public void onBackPressed() {
        if (mBinding.drawer.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    finishAffinity();
                    return;
                }
                doubleBackToExitPressedOnce = true;
                Snackbar.make(mBinding.drawer, getString(R.string.back_again),
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
        switch (view.getId()) {
            case R.id.ll_profile:
                if (SharePrefs.getSharedPreferences(getApplicationContext(), SharePrefs.IS_REGISTRATIONCOMPLETE) && SharePrefs.getInstance(getApplicationContext()).getBoolean(SharePrefs.IS_LOGIN)) {
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                mBinding.drawer.closeDrawers();
                break;

            case R.id.ll_chnage_password:
                if (SharePrefs.getSharedPreferences(getApplicationContext(), SharePrefs.IS_REGISTRATIONCOMPLETE) && SharePrefs.getInstance(getApplicationContext()).getBoolean(SharePrefs.IS_LOGIN)) {
                    startActivity(new Intent(MainActivity.this, ChangePasswordActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                mBinding.drawer.closeDrawers();
                break;

            case R.id.ll_chet:
                mBinding.drawer.closeDrawers();
                break;
            case R.id.ll_rate_this_app:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.playStoreURL)));
                finish();
                mBinding.drawer.closeDrawers();
                break;

            case R.id.ll_logout:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                clearSharePrefs();
                finish();
                mBinding.drawer.closeDrawers();
                break;
            case R.id.ll_sign_in:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                mBinding.drawer.closeDrawers();
                break;
            case R.id.ll_howto_use:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UChGqYYqXeuGdNVqQ9MQS2Fw?app=desktop"));
                startActivity(intent);
                finish();
                mBinding.drawer.closeDrawers();
                break;
            case R.id.llChangeLanguage:
                startActivity(new Intent(getApplicationContext(), ChangeLanguageActivity.class));
                mBinding.drawer.closeDrawers();
                break;
        }
    }

    public void clearSharePrefs() {
        sharedPreferences.edit().clear().apply();
    }

    private void setupBadge() {
        int count = MyApplication.getInstance().cartRepository.getCartCount1();
        if (count == 0) {
            mBinding.toolbarId.cartBadge.setVisibility(View.GONE);
        } else {
            mBinding.toolbarId.cartBadge.setVisibility(View.VISIBLE);
            mBinding.toolbarId.cartBadge.setText(String.valueOf(Math.min(count, 99)));
        }
    }

    private void getCartItemApi() {
        mainActivityViewMode.getCartItemsRequest();
        mainActivityViewMode.getCartItemsVM().observe(this, model -> {
            System.out.println("SellerId " + model.getSellerId());
            Utils.hideProgressDialog();
            if (model != null && model.getCart() != null) {
                MyApplication.getInstance().cartRepository.addToCart(model.getCart());
            } else {
                MyApplication.getInstance().cartRepository.truncateCart();
            }
            setupBadge();
        });
    }

    private void callLocationAPI(double latitude, double longitude) {
        mainActivityViewMode.getMapViewModelRequest(latitude, longitude);
        mainActivityViewMode.getMapViewModel().observe(this, new Observer<JsonObject>() {
            @Override
            public void onChanged(JsonObject data) {
                Utils.hideProgressDialog();
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(data.toString());
                    JSONObject components = jsonResponse.getJSONObject("geometry");
                    JSONObject location = components.getJSONObject("location");
                    double lat = location.getDouble("lat");
                    double lng = location.getDouble("lng");
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(lat, lng, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String cityName = addresses.get(0).getLocality();
                    Log.e("cityName", "cityName  " + cityName);
                    setLocationTV.setText(cityName);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void callRunTimePermissions() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                Log.e("onDenied", "onGranted");
                GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                if (gpsTracker != null) {
                    callLocationAPI(gpsTracker.getLatitude(), gpsTracker.getLongitude());
                }
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
                Log.e("onDenied", "onDenied" + deniedPermissions);

            }
        });
    }
}