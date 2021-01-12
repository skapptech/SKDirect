package com.skdirect.activity;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.skdirect.R;
import com.skdirect.databinding.ActivityMainBinding;
import com.skdirect.fragment.BasketFragment;
import com.skdirect.fragment.HomeFragment;
import com.skdirect.fragment.MyOrderFragment;
import com.skdirect.fragment.ProfileFragment;
import com.skdirect.model.CartItemModel;
import com.skdirect.utils.AppSignatureHelper;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.MainActivityViewMode;
import com.skdirect.viewmodel.ProductDetailsViewMode;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityMainBinding mBinding;
    private boolean doubleBackToExitPressedOnce = false;
    private SharedPreferences sharedPreferences;
    public boolean positionChanged = false;
    public Toolbar appBarLayout;
    public TextView userNameTV, mobileNumberTV;
    private MainActivityViewMode mainActivityViewMode;
    public static CartItemModel cartItemModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainActivityViewMode = ViewModelProviders.of(this).get(MainActivityViewMode.class);
        Log.e("key: ", new AppSignatureHelper(getApplicationContext()).getAppSignatures() + "");
        openFragment(new HomeFragment());
        initView();
        clickListener();
        setupBadge();

    }


    private void clickListener() {
        mBinding.llProfile.setOnClickListener(this);
        mBinding.llChnagePassword.setOnClickListener(this);
        mBinding.llChet.setOnClickListener(this);
        mBinding.llLogout.setOnClickListener(this);
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
                    openFragment(new ProfileFragment());
                    break;
                case R.id.nav_my_order:
                    positionChanged = true;
                    openFragment(new MyOrderFragment());
                    break;
                case R.id.nav_basket:
                    positionChanged = true;
                    openFragment(new BasketFragment());
                    break;

            }
            return true;
        });
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
                Utils.setToast(getApplicationContext(), "Profile");
                mBinding.drawer.closeDrawers();
                break;

            case R.id.ll_chnage_password:
                Utils.setToast(getApplicationContext(), "Change Password");
                mBinding.drawer.closeDrawers();
                break;

            case R.id.ll_chet:
                Utils.setToast(getApplicationContext(), "Chet");
                mBinding.drawer.closeDrawers();
                break;

            case R.id.ll_logout:
                clearSharePrefs();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
                mBinding.drawer.closeDrawers();
                break;
        }
    }

    public void clearSharePrefs() {
        sharedPreferences.edit().clear().apply();
    }

    private void setupBadge() {
        int CartItemCount = SharePrefs.getInstance(MainActivity.this).getInt(SharePrefs.COUNT);
        if (CartItemCount == 0) {
            if (mBinding.toolbarId.cartBadge.getVisibility() != View.GONE) {
                mBinding.toolbarId.cartBadge.setVisibility(View.GONE);
            }
        } else {
            mBinding.toolbarId.cartBadge.setText(String.valueOf(Math.min(CartItemCount, 99)));
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
                Utils.hideProgressDialog();
                if (model != null) {
                    cartItemModel = model;
                }
            }
        });

    }

}
