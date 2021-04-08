package com.skdirect.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.skdirect.R;
import com.skdirect.databinding.ActivityIntroBinding;
import com.skdirect.utils.SharePrefs;

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

        mBinding.tvBuyer.setOnClickListener(view -> {
            SharePrefs.getInstance(activity).putBoolean(SharePrefs.IS_SELLER, false);
            SharePrefs.getInstance(activity).putBoolean(SharePrefs.IS_SHOW_INTRO, true);
            Intent i = new Intent(activity, PlaceSearchActivity.class);
            startActivity(i);
            finish();
        });
        mBinding.tvSeller.setOnClickListener(view -> {
            SharePrefs.getInstance(activity).putBoolean(SharePrefs.IS_SELLER, true);
            SharePrefs.getInstance(activity).putBoolean(SharePrefs.IS_SHOW_INTRO, true);
            Intent i = new Intent(activity, PlaceSearchActivity.class);
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