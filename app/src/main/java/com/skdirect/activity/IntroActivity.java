package com.skdirect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.onesignal.OneSignal;
import com.skdirect.R;
import com.skdirect.databinding.ActivityIntroBinding;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.Utils;


public class IntroActivity extends AppCompatActivity {
    ActivityIntroBinding mBinding;
    IntroActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_intro);
        activity = this;
        initViews();
    }

    private void initViews() {
        Utils.logAppsFlayerEventApp(getApplicationContext(),"IntroScreen", "Introduction Screen");

        mBinding.tvBuyer.setOnClickListener(view -> {
            OneSignal.sendTag("user_type","Buyer");
            Utils.logAppsFlayerEventApp(getApplicationContext(),"IntroScreen", "Introduction Screen Buyer Button Click");
            SharePrefs.getInstance(activity).putBoolean(SharePrefs.IS_SELLER, false);
            SharePrefs.getInstance(activity).putBoolean(SharePrefs.IS_SHOW_INTRO, true);
            Intent i = new Intent(activity, MainActivity.class);
            startActivity(i);
            finish();
        });
        mBinding.tvSeller.setOnClickListener(view -> {
            OneSignal.sendTag("user_type","Seller");
            Utils.logAppsFlayerEventApp(getApplicationContext(),"IntroScreen", "Introduction Screen Seller Button Click");
            SharePrefs.getInstance(activity).putBoolean(SharePrefs.IS_SELLER, true);
            SharePrefs.getInstance(activity).putBoolean(SharePrefs.IS_SHOW_INTRO, true);
            Intent i = new Intent(activity, MainActivity.class);
            startActivity(i);
            finish();
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
