package com.skdirect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.skdirect.R;
import com.skdirect.api.CommonClassForAPI;
import com.skdirect.databinding.ActivityProfileBinding;
import com.skdirect.model.CustomerDataModel;
import com.skdirect.model.UserDetailResponseModel;
import com.skdirect.utils.DBHelper;
import com.skdirect.utils.MyApplication;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.TextUtils;

import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityProfileBinding mBinding;
    private DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        dbHelper = MyApplication.getInstance().dbHelper;
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
       // getProfileData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_press:
                onBackPressed();
                break;
            case R.id.rl_seller_info:
                startActivity(new Intent(ProfileActivity.this, UpdateProfileActivity.class));
                break;

            case R.id.rl_my_order:
                startActivity(new Intent(ProfileActivity.this, MyOrderActivity.class));
                break;

            case R.id.rl_my_address:
                startActivity(new Intent(ProfileActivity.this, ProfilePrimaryAddressActivity.class));
                break;

            case R.id.rl_chnage_password:
                startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class));
                break;
        }
    }


    private void initView() {
        mBinding.tvSellerName.setText(dbHelper.getString(R.string.user_name));
        mBinding.tvMyOrder.setText(dbHelper.getString(R.string.my_order));
        mBinding.tvCheckOrderStatus.setText(dbHelper.getString(R.string.check_your_order_status));
        mBinding.tvMyAddress.setText(dbHelper.getString(R.string.my_address));
        mBinding.tvSavedAddress.setText(dbHelper.getString(R.string.save_address_for_a_hassle_free_checkout));
        mBinding.tvChnagePassword.setText(dbHelper.getString(R.string.change_password));
        mBinding.tvMangePassword.setText(dbHelper.getString(R.string.manage_your_password));

        mBinding.toolbarTittle.tvTittle.setText(dbHelper.getString(R.string.profile));
        mBinding.toolbarTittle.ivBackPress.setOnClickListener(this);
        mBinding.rlMyAddress.setOnClickListener(this);
        mBinding.rlSellerInfo.setOnClickListener(this);
        mBinding.rlChnagePassword.setOnClickListener(this);
        mBinding.rlMyOrder.setOnClickListener(this);
        if (!TextUtils.isNullOrEmpty(SharePrefs.getInstance(getApplicationContext()).getString(SharePrefs.FIRST_NAME))) {
            mBinding.tvSellerName.setText(SharePrefs.getInstance(getApplicationContext()).getString(SharePrefs.FIRST_NAME));
        }
        mBinding.tvMobileNumber.setText(SharePrefs.getInstance(getApplicationContext()).getString(SharePrefs.MOBILE_NUMBER));
    }
}
