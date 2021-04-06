package com.skdirect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.skdirect.R;
import com.skdirect.api.CommonClassForAPI;
import com.skdirect.databinding.ActivityRegistrationBinding;
import com.skdirect.databinding.ActivityUpdateProfileBinding;
import com.skdirect.model.ProfileResponseModel;
import com.skdirect.model.UpdateProfilePostModel;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.TextUtils;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.UpdateProfileViewMode;

import io.reactivex.observers.DisposableObserver;

public class RegisterationActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityRegistrationBinding mBinding;
    private UpdateProfileViewMode updateProfileViewMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_registration);
        updateProfileViewMode = ViewModelProviders.of(this).get(UpdateProfileViewMode.class);

        initView();

    }

    private void initView() {
        mBinding.btSaveAddresh.setOnClickListener(this);
        mBinding.etName.setText(SharePrefs.getInstance(RegisterationActivity.this).getString(SharePrefs.FIRST_NAME));
        mBinding.etEmailId.setText(SharePrefs.getInstance(RegisterationActivity.this).getString(SharePrefs.EMAIL_ID));
        mBinding.etPinCode.setText(SharePrefs.getInstance(RegisterationActivity.this).getString(SharePrefs.PIN_CODE));
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
        } /*else if (TextUtils.isNullOrEmpty(mBinding.etPinCode.getText().toString().trim())) {
            Utils.setToast(getApplicationContext(), "Please Enter PinCode");
        }*/ else {
            if (Utils.isNetworkAvailable(getApplicationContext())) {
                Utils.showProgressDialog(RegisterationActivity.this);
                // updateUserData();
                UpdateProfilePostModel updateProfilePostModel = new UpdateProfilePostModel(mBinding.etName.getText().toString(), mBinding.etEmailId.getText().toString());
                new CommonClassForAPI().UpdateUserProfile(updateprofile, updateProfilePostModel);
            } else {
                Utils.setToast(getApplicationContext(), "No Internet Connection Please connect.");
            }
        }

    }

    private DisposableObserver<ProfileResponseModel> updateprofile = new DisposableObserver<ProfileResponseModel>() {
        @Override
        public void onNext(ProfileResponseModel model) {
            try {
                Utils.hideProgressDialog();
                if (model != null) {
                    if (model.isSuccess()) {
                        Utils.setToast(RegisterationActivity.this, model.getSuccessMessage());
                        startActivity(new Intent(RegisterationActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Utils.setToast(RegisterationActivity.this, model.getErrorMessage());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();


            }
        }

        @Override
        public void onError(Throwable e) {
            Utils.hideProgressDialog();
            e.printStackTrace();
        }

        @Override
        public void onComplete() {
            Utils.hideProgressDialog();
        }
    };

}
