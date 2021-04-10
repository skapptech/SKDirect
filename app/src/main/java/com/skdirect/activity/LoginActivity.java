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
import com.skdirect.api.CommonClassForAPI;
import com.skdirect.databinding.ActivityLoginBinding;
import com.skdirect.model.GenerateOtpModel;
import com.skdirect.model.GenerateOtpResponseModel;
import com.skdirect.model.TokenModel;
import com.skdirect.utils.DBHelper;
import com.skdirect.utils.MyApplication;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.LoginViewModel;
import com.skdirect.viewmodel.OTPVerificationViewModel;

import org.jetbrains.annotations.NotNull;

import io.reactivex.observers.DisposableObserver;
import okhttp3.internal.Util;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityLoginBinding Binding;
    private String mobileNumberString;
    private LoginViewModel loginViewModel;
    DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        dbHelper = MyApplication.getInstance().dbHelper;
        initView();
    }


    private void initView() {
        Binding.btGetOtp.setOnClickListener(this);
        Binding.btLoginWithPassword.setOnClickListener(this);
        Binding.btSkip.setOnClickListener(this);
        Binding.tvYourno.setText(dbHelper.getString(R.string.what_s_your_number));
        Binding.etMobileNumber.setHint(dbHelper.getString(R.string.enter_phone_number));
        Binding.tvOtpMsg.setText(dbHelper.getString(R.string.we_will_send_an_otp_to_nabove_number));
        Binding.btGetOtp.setText(dbHelper.getString(R.string.get_otp));
        Binding.btSkip.setText(dbHelper.getString(R.string.skip));
        Binding.btLoginWithPassword.setText(dbHelper.getString(R.string.login_using_password));
        Binding.etMobileNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
                    getOTP();
                }
                return false;
            }
        });
        if(SharePrefs.getInstance(getApplicationContext()).getString(SharePrefs.TOKEN).isEmpty()){
            Gettoken();
        }
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
                case R.id.bt_skip:
                    startActivity(new Intent(this,MainActivity.class));
                    break;
        }

    }

    private void loginWithPassword() {
        mobileNumberString = Binding.etMobileNumber.getText().toString().trim();
        if (mobileNumberString.isEmpty()) {
            Utils.setToast(this,dbHelper.getString(R.string.enter_valid_mobno));
        } else{
            startActivity(new Intent(LoginActivity.this, LoginWithPasswordActivity.class).putExtra("MobileNumber",mobileNumberString));
        }
    }

    private void getOTP() {
        Binding.btGetOtp.setText(dbHelper.getString(R.string.loading));
        mobileNumberString = Binding.etMobileNumber.getText().toString().trim();
        if (mobileNumberString.isEmpty()) {
            Utils.setToast(this, dbHelper.getString(R.string.enter_mobno));
            Binding.btGetOtp.setText(dbHelper.getString(R.string.get_otp));
        } else if (Utils.isValidMobile(mobileNumberString)) {
            callOTPApi(mobileNumberString);
        } else {
            Utils.setToast(this, dbHelper.getString(R.string.enter_valid_mobno));
        }
    }

    private void callOTPApi(String mobileNumberString) {
        if (Utils.isNetworkAvailable(this)) {
            Utils.showProgressDialog(this);
            GenerateOtpModel model =new GenerateOtpModel(mobileNumberString,Utils.getDeviceUniqueID(this));
            getLoginData(model);
        } else {
            Utils.setToast(this, dbHelper.getString(R.string.no_connection));
        }

    }
     public void Gettoken() {
         new CommonClassForAPI().getToken(callToken, "password", Utils.getDeviceUniqueID(this),
                 "", true, true, "BUYERAPP", true,
                 Utils.getDeviceUniqueID(this),
                 Double.parseDouble(SharePrefs.getStringSharedPreferences(this,SharePrefs.LAT)),
                 Double.parseDouble(SharePrefs.getStringSharedPreferences(this,SharePrefs.LON)),
                 SharePrefs.getInstance(LoginActivity.this).getString(SharePrefs.PIN_CODE));
     }

     private final DisposableObserver<TokenModel> callToken = new DisposableObserver<TokenModel>() {
         @Override
         public void onNext(@NotNull TokenModel model) {
             try {
                 Utils.hideProgressDialog();
                 if (model != null) {
                     SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.TOKEN, model.getAccess_token());
                     SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.USER_NAME, model.getUserName());
                     SharePrefs.setSharedPreference(LoginActivity.this, SharePrefs.IS_REGISTRATIONCOMPLETE, model.getIsRegistrationComplete());
                     SharePrefs.setStringSharedPreference(getApplicationContext(),SharePrefs.LAT, model.getLatitiute());
                     SharePrefs.setStringSharedPreference(getApplicationContext(),SharePrefs.LON, model.getLongitude());
                     SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.BUSINESS_TYPE, model.getBusinessType());
                     SharePrefs.getInstance(getApplicationContext()).putBoolean(SharePrefs.IS_CONTACTREAD, model.getIscontactRead());
                     SharePrefs.getInstance(getApplicationContext()).putBoolean(SharePrefs.IS_SUPER_ADMIN, model.getIsSuperAdmin());

                 }
             } catch (Exception e) {
                 e.printStackTrace();
                 Utils.setToast(getApplicationContext(), dbHelper.getString(R.string.invalid_pass));

             }
         }

         @Override
         public void onError(Throwable e) {
             Utils.setToast(getApplicationContext(), dbHelper.getString(R.string.invalid_pass));
             Utils.hideProgressDialog();
             e.printStackTrace();
         }

         @Override
         public void onComplete() {
             Utils.hideProgressDialog();
         }
     };
    private void getLoginData( GenerateOtpModel model) {
        loginViewModel.getLogin(model).observe(this, new Observer<GenerateOtpResponseModel>() {
            @Override
            public void onChanged(GenerateOtpResponseModel model) {
                Utils.hideProgressDialog();
                if (model!=null){
                    if (model.isSuccess()){
                        SharePrefs.getInstance(LoginActivity.this).putBoolean(SharePrefs.IS_USER_EXISTS, model.getResultItem().isUserExists());
                        SharePrefs.getInstance(LoginActivity.this).putBoolean(SharePrefs.RESULT, model.getResultItem().isResult());
                        startActivity(new Intent(LoginActivity.this, GenerateOTPActivity.class).putExtra("MobileNumber",mobileNumberString));
                        finish();
                    }
                    else{
                        Utils.setToast(LoginActivity.this,model.getErrorMessage());
                    }
                }


            }
        });

    }
}
