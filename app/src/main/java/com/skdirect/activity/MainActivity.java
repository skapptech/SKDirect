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
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.skdirect.R;
import com.skdirect.databinding.ActivityMainBinding;
import com.skdirect.fragment.BasketFragment;
import com.skdirect.fragment.HomeFragment;
import com.skdirect.fragment.MyOrderFragment;
import com.skdirect.model.CartItemModel;
import com.skdirect.utils.AppSignatureHelper;
import com.skdirect.utils.GPSTracker;
import com.skdirect.utils.SharePrefs;
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
    public TextView userNameTV, mobileNumberTV,setLocationTV;
    private MainActivityViewMode mainActivityViewMode;
    public static CartItemModel cartItemModel;
    private int  itemQuatntiy;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainActivityViewMode = ViewModelProviders.of(this).get(MainActivityViewMode.class);
        Log.e("key: ", new AppSignatureHelper(getApplicationContext()).getAppSignatures() + "");
        openFragment(new HomeFragment());
        initView();
        setlocationInHader();
        clickListener();
        setupBadge();
        ///callRunTimePermissions();

    }


    private void clickListener() {
        mBinding.llProfile.setOnClickListener(this);
        mBinding.llChnagePassword.setOnClickListener(this);
        mBinding.llChet.setOnClickListener(this);
        mBinding.llLogout.setOnClickListener(this);
        mBinding.llSignIn.setOnClickListener(this);
        mBinding.llHowtoUse.setOnClickListener(this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        setupBadge();
        getCartItemApi();
    }

    private void initView() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        appBarLayout = mBinding.toolbarId.toolbar;
        userNameTV = mBinding.tvUserName;
        mobileNumberTV = mBinding.tvMobileName;
        setLocationTV = mBinding.toolbarId.tvLoction;
//        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mBinding.toolbarId.bottomNavigation.getLayoutParams();
//        layoutParams.setBehavior(new BottomNavigationBehavior());

        mBinding.toolbarId.ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.drawer.openDrawer(Gravity.START);
            }
        });

        mBinding.toolbarId.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id._nav_home:
                    openFragment(new HomeFragment());
                    break;
                case R.id.nav_profile:
                    positionChanged = true;
                    startActivity(new Intent(MainActivity.this,ProfileActivity.class));
                    break;
                case R.id.nav_my_order:
                    positionChanged = true;
                    startActivity(new Intent(MainActivity.this,MyOrderActivity.class));
                    break;
                case R.id.nav_basket:
                    positionChanged = true;
                    openFragment(new BasketFragment());
                    break;

            }
            return true;
        });

        mBinding.toolbarId.notifictionCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,CartActivity.class));
            }
        });

        setLocationTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this,MapsActivity.class),2);
            }
        });
    }

    private void setlocationInHader() {
        setLocationTV.setText(Utils.getCityName(this,Double.parseDouble(SharePrefs.getInstance(this).getString(SharePrefs.LAT)), Double.parseDouble(SharePrefs.getInstance(this).getString(SharePrefs.LON))));
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
                Snackbar.make(mBinding.drawer, "Please click back again to exit",
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
                startActivity(new Intent(MainActivity.this,ProfileActivity.class));
                mBinding.drawer.closeDrawers();
                break;

            case R.id.ll_chnage_password:
                startActivity(new Intent(MainActivity.this,ChangePasswordActivity.class));
                mBinding.drawer.closeDrawers();
                break;

            case R.id.ll_chet:
                Utils.setToast(getApplicationContext(), "Chet");
                mBinding.drawer.closeDrawers();
                break;

            case R.id.ll_logout:
                clearSharePrefs();
                startActivity(new Intent(getApplicationContext(), PlaceSearchActivity.class));
                finish();
                mBinding.drawer.closeDrawers();
                break;
                case  R.id.ll_sign_in:
                startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
                mBinding.drawer.closeDrawers();
                break;
                case R.id.ll_howto_use:
                    Intent intent = new Intent(Intent.ACTION_VIEW , Uri.parse("https://www.youtube.com/channel/UChGqYYqXeuGdNVqQ9MQS2Fw?app=desktop"));
                    startActivity(intent);
                finish();
                mBinding.drawer.closeDrawers();
                break;
        }
    }

    public void clearSharePrefs() {
        sharedPreferences.edit().clear().apply();
    }

    private void setupBadge() {
       // int CartItemCount = SharePrefs.getInstance(MainActivity.this).getInt(SharePrefs.COUNT);
        if (itemQuatntiy == 0) {
            if (mBinding.toolbarId.cartBadge.getVisibility() != View.GONE) {
                mBinding.toolbarId.cartBadge.setVisibility(View.GONE);
            }
        } else {
            mBinding.toolbarId.cartBadge.setText(String.valueOf(Math.min(itemQuatntiy, 99)));
            if (mBinding.toolbarId.cartBadge.getVisibility() != View.VISIBLE) {
                mBinding.toolbarId.cartBadge.setVisibility(View.VISIBLE);
            }
        }

    }

    private void getCartItemApi() {
        mainActivityViewMode.getCartItemsRequest("123");
        mainActivityViewMode.getCartItemsVM().observe(this, new Observer<CartItemModel>() {
            @Override
            public void onChanged(CartItemModel model) {
                itemQuatntiy =0;
                Utils.hideProgressDialog();
                if (model != null && model.getCart()!=null) {
                    cartItemModel = model;
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.CART_ITEM_ID, model.getId());
                    for (int i = 0; i < model.getCart().size(); i++) {
                        itemQuatntiy += model.getCart().get(i).getQuantity();
                    }
                    setupBadge();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==2)
        {
            if (requestCode == 2 && resultCode == RESULT_OK) {
                Address address=data.getParcelableExtra("address");
                double lat = address.getLatitude();
                double log = address.getLongitude();
                setLocationTV.setText(Utils.getCityName(this,lat,log));
            }

        }
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
                    Log.e("cityName", "cityName  "+cityName);
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
                GPSTracker  gpsTracker = new GPSTracker(getApplicationContext());
                if (gpsTracker!=null){
                    callLocationAPI(gpsTracker.getLatitude(),gpsTracker.getLongitude());
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
