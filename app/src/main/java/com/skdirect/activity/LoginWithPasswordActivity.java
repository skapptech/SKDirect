package com.skdirect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.skdirect.R;
import com.skdirect.api.CommonClassForAPI;
import com.skdirect.databinding.ActivityLoginWithPasswordBinding;
import com.skdirect.model.LoginWithPasswordModel;
import com.skdirect.model.UpdateTokenModel;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.Utils;

import io.reactivex.observers.DisposableObserver;

public class LoginWithPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityLoginWithPasswordBinding mBinding;
    private String passwordString, mobileNumber;

    private CommonClassForAPI commonClassForAPI;
    private String fcmToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login_with_password);
        getIntentData();
        initView();
    }

    private void getIntentData() {
        mobileNumber = getIntent().getStringExtra("MobileNumber");

    }

    private void initView() {
        fcmToken = Utils.getFcmToken();
        commonClassForAPI = CommonClassForAPI.getInstance(this);
        mBinding.btLoddingOtp.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_lodding_otp:
                Utils.hideKeyboard(this, view);
                loginWithPassword();
                break;
        }
    }

    private void loginWithPassword() {

        mBinding.btLoddingOtp.setText("Loading ...");
        passwordString = mBinding.etPassword.getText().toString().trim();
        if (passwordString.isEmpty()) {
            mBinding.btLoddingOtp.setText("Next");
            Utils.setToast(this, "Please Enter Password");
        } else {
            checkPasswordApi();
        }
    }

    private void checkPasswordApi() {
        if (Utils.isNetworkAvailable(this)) {
            if (commonClassForAPI != null) {
                /*commonClassForAPI.getToken(callToken, "password", mobileNumber, passwordString, false, true, "BUYERAPP");*/
            } else {
                Utils.setToast(this, "No Internet Connection Please connect.");
            }
        }
    }

    private final DisposableObserver<LoginWithPasswordModel> callToken = new DisposableObserver<LoginWithPasswordModel>() {
        @Override
        public void onNext(LoginWithPasswordModel model) {
            try {
                Utils.hideProgressDialog();
                if (model != null) {
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.TOKEN, model.getAccess_token());
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.USER_NAME, model.getUserName());
                    SharePrefs.getInstance(getApplicationContext()).putBoolean(SharePrefs.IS_REGISTRATIONCOMPLETE, model.isRegistrationComplete());
                    SharePrefs.getInstance(getApplicationContext()).putBoolean(SharePrefs.IS_CONTACTREAD, model.isIscontactRead());
                    SharePrefs.getInstance(getApplicationContext()).putBoolean(SharePrefs.IS_LOGIN, true);
                    commonClassForAPI.getUpdateToken(updatecallToken, fcmToken);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utils.setToast(getApplicationContext(), "Invalid Password");

            }
        }

        @Override
        public void onError(Throwable e) {
            Utils.setToast(getApplicationContext(), "Invalid Password");
            Utils.hideProgressDialog();
            e.printStackTrace();
        }

        @Override
        public void onComplete() {
            Utils.hideProgressDialog();
        }
    };

    private final DisposableObserver<JsonObject> updatecallToken = new DisposableObserver<JsonObject>() {
        @Override
        public void onNext(JsonObject model) {
            try {
                Utils.hideProgressDialog();
                if (model != null) {

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




