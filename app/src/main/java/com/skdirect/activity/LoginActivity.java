package com.skdirect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.skdirect.R;
import com.skdirect.databinding.ActivityLoginBinding;
import com.skdirect.model.LoginResponseModel;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityLoginBinding Binding;
    private String mobileNumberString;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        initView();

    }


    private void initView() {
        Binding.btGetOtp.setOnClickListener(this);
        Binding.btLoginWithPassword.setOnClickListener(this);
        Binding.etMobileNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
                    getOTP();
                }

                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_get_otp:
                getOTP();
                break;
            case R.id.bt_login_with_password:
                loginWithPassword();
                break;
        }

    }

    private void loginWithPassword() {
        mobileNumberString = Binding.etMobileNumber.getText().toString().trim();
        if (mobileNumberString.isEmpty()) {
            Utils.setToast(this, "Please enter Valid  Mobile Number.");
        } else{
            startActivity(new Intent(LoginActivity.this, LoginWithPasswordActivity.class).putExtra("MobileNumber",mobileNumberString));
        }
    }

    private void getOTP() {
        Binding.btGetOtp.setText("Loading ...");
        mobileNumberString = Binding.etMobileNumber.getText().toString().trim();
        if (mobileNumberString.isEmpty()) {
            Utils.setToast(this, "Please Enter Mobile");
            Binding.btGetOtp.setText(" Get OTP");
        } else if (Utils.isValidMobile(mobileNumberString)) {
            callOTPApi(mobileNumberString);
        } else {
            Utils.setToast(this, "Please enter Valid  Mobile Number.");
        }
    }

    private void callOTPApi(String mobileNumberString) {
        if (Utils.isNetworkAvailable(this)) {
            Utils.showProgressDialog(this);
            getLoginData(mobileNumberString);
        } else {
            Utils.setToast(this, "No Internet Connection Please connect.");
        }

    }

    private void getLoginData(String mobileNumberString) {
        loginViewModel.getLogin(mobileNumberString).observe(this, new Observer<LoginResponseModel>() {
            @Override
            public void onChanged(LoginResponseModel loginResponseModel) {
                Utils.hideProgressDialog();
                if (loginResponseModel!=null){
                    if (loginResponseModel.isResult()){
                        SharePrefs.getInstance(LoginActivity.this).putBoolean(SharePrefs.IS_USER_EXISTS, loginResponseModel.isUserExists());
                        SharePrefs.getInstance(LoginActivity.this).putBoolean(SharePrefs.RESULT, loginResponseModel.isResult());
                        startActivity(new Intent(LoginActivity.this, GenerateOTPActivity.class).putExtra("MobileNumber",mobileNumberString));
                        finish();
                    }
                }


            }
        });

    }
}
