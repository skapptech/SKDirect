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
        getProfileData();
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

    private void getProfileData() {
        CommonClassForAPI.getInstance(this).getUserDetail(new DisposableObserver<CustomerDataModel>() {
            @Override
            public void onNext(@NonNull CustomerDataModel customerDataModel) {
                if (customerDataModel != null) {
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.FIRST_NAME, customerDataModel.getFirstName());
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.MIDDLE_NAME, customerDataModel.getMiddleName());
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.USER_NAME, customerDataModel.getUserName());
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.LAST_NAME, customerDataModel.getLastName());
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.USER_ID, customerDataModel.getUserId());
                    SharePrefs.getInstance(getApplicationContext()).putInt(SharePrefs.ID, customerDataModel.getId());
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.ENCRIPTED_ID, customerDataModel.getEncryptedId());
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.MOBILE_NUMBER, customerDataModel.getMobileNo());
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.SHOP_NAME, customerDataModel.getShopName());
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.EMAIL_ID, customerDataModel.getEmail());
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.STATE, customerDataModel.getState());
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.CITYNAME, customerDataModel.getCity());
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.PIN_CODE, customerDataModel.getPincode());
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.IMAGE_PATH, customerDataModel.getImagePath());
                    SharePrefs.getInstance(getApplicationContext()).putInt(SharePrefs.PIN_CODE_master, customerDataModel.getPinCodeMasterId());
                    SharePrefs.getInstance(getApplicationContext()).putBoolean(SharePrefs.IS_ACTIVE, customerDataModel.isActive());
                    SharePrefs.getInstance(getApplicationContext()).putBoolean(SharePrefs.IS_DELETE, customerDataModel.isDelete());
                    SharePrefs.setSharedPreference(getApplicationContext(), SharePrefs.IS_REGISTRATIONCOMPLETE, customerDataModel.isRegistrationComplete());
                    if (!TextUtils.isNullOrEmpty(SharePrefs.getInstance(getApplicationContext()).getString(SharePrefs.FIRST_NAME))) {
                        mBinding.tvSellerName.setText(SharePrefs.getInstance(getApplicationContext()).getString(SharePrefs.FIRST_NAME));
                    }
                    mBinding.tvMobileNumber.setText(SharePrefs.getInstance(getApplicationContext()).getString(SharePrefs.MOBILE_NUMBER));
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
}
