package com.skdirect.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.skdirect.R;
import com.skdirect.databinding.ActivityUpdateProfileBinding;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.TextUtils;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.UpdateProfileViewMode;

public class UpdateProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityUpdateProfileBinding mBinding;
    private UpdateProfileViewMode updateProfileViewMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_update_profile);
        updateProfileViewMode = ViewModelProviders.of(this).get(UpdateProfileViewMode.class);

        initView();

    }

    private void initView() {
        mBinding.toolbarTittle.ivBackPress.setOnClickListener(this);
        mBinding.btSaveAddresh.setOnClickListener(this);
        mBinding.toolbarTittle.tvTittle.setText("Update Profile");
        mBinding.etName.setText(SharePrefs.getInstance(UpdateProfileActivity.this).getString(SharePrefs.FIRST_NAME));
        mBinding.etEmailId.setText(SharePrefs.getInstance(UpdateProfileActivity.this).getString(SharePrefs.EMAIL_ID));
        mBinding.etPinCode.setText(SharePrefs.getInstance(UpdateProfileActivity.this).getString(SharePrefs.PIN_CODE));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_press:
                onBackPressed();
                break;
            case R.id.bt_save_addresh:
                upDateProfile();
                break;
        }
    }

    private void upDateProfile() {
        if (TextUtils.isNullOrEmpty(mBinding.etName.getText().toString().trim())) {
            Utils.setToast(getApplicationContext(), "Please Enter Name");
        } else if (TextUtils.isNullOrEmpty(mBinding.etPinCode.getText().toString().trim())) {
            Utils.setToast(getApplicationContext(), "Please Enter PinCode");
        } else {
            if (Utils.isNetworkAvailable(getApplicationContext())) {
                Utils.showProgressDialog(UpdateProfileActivity.this);
                updateUserData();

            } else {
                Utils.setToast(getApplicationContext(), "No Internet Connection Please connect.");
            }
        }

    }

    private void updateUserData() {


        updateProfileViewMode.updateProfileVMRequest(sendUserProfileData());
        updateProfileViewMode.updateProfileVM().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean customerDataModel) {
                Utils.hideProgressDialog();
                if (customerDataModel != null) {
                    if (customerDataModel) {
                        onBackPressed();
                    }
                }

            }

        });

    }

    public JsonObject sendUserProfileData() {
        JsonObject object = new JsonObject();
        object.addProperty("Id", SharePrefs.getInstance(UpdateProfileActivity.this).getInt(SharePrefs.ID));
        object.addProperty("IsActive", SharePrefs.getInstance(UpdateProfileActivity.this).getBoolean(SharePrefs.IS_ACTIVE));
        object.addProperty("IsDelete", SharePrefs.getInstance(UpdateProfileActivity.this).getBoolean(SharePrefs.IS_DELETE));
        object.addProperty("FirstName", SharePrefs.getInstance(UpdateProfileActivity.this).getString(SharePrefs.FIRST_NAME));
        object.addProperty("MiddleName", SharePrefs.getInstance(UpdateProfileActivity.this).getString(SharePrefs.MIDDLE_NAME));
        object.addProperty("MobileNo", SharePrefs.getInstance(UpdateProfileActivity.this).getString(SharePrefs.MOBILE_NUMBER));
        object.addProperty("LastName", SharePrefs.getInstance(UpdateProfileActivity.this).getString(SharePrefs.LAST_NAME));
        object.addProperty("UserId", SharePrefs.getInstance(UpdateProfileActivity.this).getString(SharePrefs.USER_ID));
        object.addProperty("Email", SharePrefs.getInstance(UpdateProfileActivity.this).getString(SharePrefs.EMAIL_ID));
        object.addProperty("Pincode", SharePrefs.getInstance(UpdateProfileActivity.this).getString(SharePrefs.PIN_CODE));
        object.addProperty("PinCodeMasterId", SharePrefs.getInstance(UpdateProfileActivity.this).getInt(SharePrefs.PIN_CODE_master));
        object.addProperty("ShopName", SharePrefs.getInstance(UpdateProfileActivity.this).getString(SharePrefs.SHOP_NAME));
        object.addProperty("ImagePath", SharePrefs.getInstance(UpdateProfileActivity.this).getString(SharePrefs.IMAGE_PATH));
        object.addProperty("State", SharePrefs.getInstance(UpdateProfileActivity.this).getString(SharePrefs.STATE));
        object.addProperty("City", SharePrefs.getInstance(UpdateProfileActivity.this).getString(SharePrefs.CITYNAME));
        object.addProperty("UserName", SharePrefs.getInstance(UpdateProfileActivity.this).getString(SharePrefs.USER_NAME));
        object.addProperty("IsRegistrationComplete", SharePrefs.getInstance(UpdateProfileActivity.this).getBoolean(SharePrefs.IS_REGISTRATIONCOMPLETE));
        object.addProperty("EncryptedId", SharePrefs.getInstance(UpdateProfileActivity.this).getString(SharePrefs.ENCRIPTED_ID));

        JsonArray jsonArray = new JsonArray();
        JsonObject objectDc = new JsonObject();
        objectDc.addProperty("Id", SharePrefs.getInstance(UpdateProfileActivity.this).getInt(SharePrefs.USER_DC_ID));
        objectDc.addProperty("UserId", SharePrefs.getInstance(UpdateProfileActivity.this).getInt(SharePrefs.USER_DC_USER_ID));
        objectDc.addProperty("Delivery", SharePrefs.getInstance(UpdateProfileActivity.this).getString(SharePrefs.DELIVERY));
        objectDc.addProperty("IsDelete", SharePrefs.getInstance(UpdateProfileActivity.this).getBoolean(SharePrefs.USER_IS_ACTIVE));
        objectDc.addProperty("IsActive", SharePrefs.getInstance(UpdateProfileActivity.this).getBoolean(SharePrefs.USER_IS_DELETE));
        jsonArray.add(objectDc);
        object.add("UserDeliveryDC", jsonArray);

        return object;
    }

}
