package com.skdirect.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.skdirect.R;
import com.skdirect.databinding.ActivityIntroBinding;
import com.skdirect.utils.DBHelper;
import com.skdirect.utils.MyApplication;
import com.skdirect.utils.SharePrefs;

public class IntroActivity extends AppCompatActivity {
    private ActivityIntroBinding mBinding;
    private IntroActivity activity;
    private DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_intro);
        activity = this;
        dbHelper = MyApplication.getInstance().dbHelper;
        initViews();
    }

    private void initViews() {

        mBinding.tvBuyerHead.setText(dbHelper.getString(R.string.buyer));
        mBinding.tvBuyerTitle1.setText(dbHelper.getString(R.string.browse_products));
        mBinding.tvBuyerTitle2.setText(dbHelper.getString(R.string.place_order));
        mBinding.tvBuyerTitle3.setText(dbHelper.getString(R.string.add_your_favourite_store));
        mBinding.tvSellerHead.setText(dbHelper.getString(R.string.seller));
        mBinding.tvSellerTitle1.setText(dbHelper.getString(R.string.display_your_product));
        mBinding.tvSellerTitle2.setText(dbHelper.getString(R.string.accept_and_manage_order));
        mBinding.tvSellerTitle3.setText(dbHelper.getString(R.string.invite_customer_to_store));
        mBinding.tvBuyer.setText(dbHelper.getString(R.string.i_m_buyer));
        mBinding.tvSeller.setText(dbHelper.getString(R.string.i_m_seller));


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