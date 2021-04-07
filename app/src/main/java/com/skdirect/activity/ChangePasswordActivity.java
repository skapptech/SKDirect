package com.skdirect.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.skdirect.R;
import com.skdirect.databinding.ActivityChnagePasswordBinding;
import com.skdirect.model.ChangePasswordRequestModel;
import com.skdirect.model.CommonResponseModel;
import com.skdirect.utils.TextUtils;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.ChangePasswordViewMode;
import com.skdirect.viewmodel.UpdateProfileViewMode;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityChnagePasswordBinding mBinding;
    private ChangePasswordViewMode changePasswordViewMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_chnage_password);
        changePasswordViewMode = ViewModelProviders.of(this).get(ChangePasswordViewMode.class);
        initView();
    }

    private void initView() {
        mBinding.toolbarTittle.tvTittle.setText("Change Password");
        mBinding.toolbarTittle.ivBackPress.setOnClickListener(this);
        mBinding.btSavePassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back_press:
                onBackPressed();
                break;

            case R.id.bt_save_password:
               changePassword();
                break;
        }
    }

    private void changePassword() {
        if (TextUtils.isNullOrEmpty(mBinding.etNewPassword.getText().toString().trim())) {
            Utils.setLongToast(getApplicationContext(), "Please Enter New Password");
        } else if (TextUtils.isNullOrEmpty(mBinding.etConfirmPassword.getText().toString().trim())) {
            Utils.setLongToast(getApplicationContext(), "Please Enter Confirm Password");
        }
        else if (!mBinding.etConfirmPassword.getText().toString().trim().equalsIgnoreCase(mBinding.etNewPassword.getText().toString().trim())) {
            Utils.setLongToast(getApplicationContext(), "Confirm Password does not match with New Password");
        }
        else {
            if (Utils.isNetworkAvailable(getApplicationContext())) {
                Utils.showProgressDialog(ChangePasswordActivity.this);
                changePasswordAPI();

            } else {
                Utils.setToast(getApplicationContext(), "No Internet Connection Please connect.");
            }
        }
    }

    private void changePasswordAPI() {
        ChangePasswordRequestModel changePasswordRequestModel = new ChangePasswordRequestModel(mBinding.etNewPassword.getText().toString().trim(),mBinding.etConfirmPassword.getText().toString().trim());

        changePasswordViewMode.getChangePasswordRequest(changePasswordRequestModel);
        changePasswordViewMode.getChangePasswordVM().observe(this, new Observer<CommonResponseModel>() {
            @Override
            public void onChanged(CommonResponseModel model) {
                Utils.hideProgressDialog();
                if (model != null) {
                    if (model.isSuccess()) {
                        changePasswordDialog(model.getSuccessMessage());
                    }
                }

            }

        });
    }

    private void changePasswordDialog(String successMessage) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Congratulation");
        dialog.setMessage(successMessage);
        dialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(ChangePasswordActivity.this, MainActivity.class));
                        finish();

                    }
                });
        dialog.show();
    }

}
