package com.skdirect.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.skdirect.R;
import com.skdirect.api.CommonClassForAPI;
import com.skdirect.broadcast.SmsBroadcastReceiver;
import com.skdirect.databinding.ActivityGenerateOtpBinding;
import com.skdirect.interfacee.OtpReceivedInterface;
import com.skdirect.location.EasyWayLocation;
import com.skdirect.model.GenerateOtpModel;
import com.skdirect.model.GenerateOtpResponseModel;
import com.skdirect.model.LoginWithPasswordModel;
import com.skdirect.model.OtpResponceModel;
import com.skdirect.model.OtpVerificationModel;
import com.skdirect.model.UpdateTokenModel;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.OTPVerificationViewModel;

import io.reactivex.observers.DisposableObserver;

public class GenerateOTPActivity extends AppCompatActivity implements OtpReceivedInterface, View.OnClickListener {
    final String TAG = getClass().getSimpleName();
    private ActivityGenerateOtpBinding Binding;
    private SmsBroadcastReceiver mSmsBroadcastReceiver;
    private String latsFiveNumber,otpString,mobileNumber;
    private OTPVerificationViewModel otpVerificationViewModel;
    private CountDownTimer cTimer = null;
    private CommonClassForAPI commonClassForAPI;
    private String fcmToken;
    private EasyWayLocation easyWayLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Binding = DataBindingUtil.setContentView(this, R.layout.activity_generate_otp);
        otpVerificationViewModel = ViewModelProviders.of(this).get(OTPVerificationViewModel.class);
        getIntentData();
        initView();

    }

    private void getIntentData() {
        mobileNumber = getIntent().getStringExtra("MobileNumber");
        latsFiveNumber = mobileNumber.substring(mobileNumber.length()-5);
        fcmToken = Utils.getFcmToken();
    }

    private void initView() {
        commonClassForAPI = CommonClassForAPI.getInstance(this);
        Binding.btLoddingOtp.setOnClickListener(this);
        Binding.tvVerifactionText.setText("Enter Verification code which we have\n" +
                "send to XXXXX "+latsFiveNumber);
        startTimer(Binding.resendOtpTimer, Binding.resendotp);
        // init broadcast receiver
        mSmsBroadcastReceiver = new SmsBroadcastReceiver();
        mSmsBroadcastReceiver.setOnOtpListeners(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(mSmsBroadcastReceiver, intentFilter);

        Binding.etOtp.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    Binding.btLoddingOtp.setText("Loading ...");
                    checkVerification();

                }

                return false;
            }
        });

        Binding.resendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTimer();
                callResendOTPApi(mobileNumber);

            }
        });


    }

    void startTimer(TextView tvResendOtpTimer, TextView resendotp) {
        cTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                //  Logger.logD("WelcomeActivity", "Timer:" + millisUntilFinished / 1000);
                tvResendOtpTimer.setText("Resend OTP" + ":" + "" + millisUntilFinished / 1000);
                Binding.resendotp.setVisibility(View.GONE);
            }
            public void onFinish() {
                resendotp.setEnabled(true);
                resendotp.setBackgroundResource(R.drawable.rectangle);
                resendotp.setPadding(8, 8, 8, 8);
                Binding.resendotp.setVisibility(View.VISIBLE);
                resendotp.setTextColor(getResources().getColor(R.color.colorAccent));
                //Toast.makeText(context, getString(R.string.resendotp), Toast.LENGTH_SHORT).show();
            }
        };
        cTimer.start();
    }


    // cancel timer
    void cancelTimer() {
        if (cTimer != null)
            cTimer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSmsBroadcastReceiver != null) {
            unregisterReceiver(mSmsBroadcastReceiver);
        }
    }

    @Override
    public void onOtpReceived(String otp) {
        Binding.etOtp.setText(otp + "");
        Binding.btLoddingOtp.callOnClick();
    }

    @Override
    public void onOtpTimeout() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_lodding_otp:
                Binding.btLoddingOtp.setText("Loading ...");
                checkVerification();

                break;
        }
    }

    private void checkVerification() {
        otpString = Binding.etOtp.getText().toString().trim();
        if (otpString.isEmpty()) {
            Utils.setToast(this, "Please Enter OTP");
        }else {
            callOTPVerfiyAPI(otpString);
        }

    }

    private void callOTPVerfiyAPI(String otpString) {
        if (Utils.isNetworkAvailable(this)) {
            if (commonClassForAPI!=null){
                commonClassForAPI.VerfiyOtp(OTPVerfiyData, new OtpVerificationModel(mobileNumber,otpString,Utils.getDeviceUniqueID(this)));
            }
        } else {
            Utils.setToast(this, "No Internet Connection Please connect.");
        }
    }



    private void callResendOTPApi(String mobileNumberString) {
        if (Utils.isNetworkAvailable(this)) {
            Utils.showProgressDialog(this);
            GenerateOtpModel model =new GenerateOtpModel(mobileNumberString,Utils.getDeviceUniqueID(this));
            getResendOTPData(model);
        } else {
            Utils.setToast(this, "No Internet Connection Please connect.");
        }

    }

    private void getResendOTPData(GenerateOtpModel generateOtpModel) {
        otpVerificationViewModel.getLogin(generateOtpModel).observe(this, new Observer<GenerateOtpResponseModel>() {
            @Override
            public void onChanged(GenerateOtpResponseModel model) {
                Utils.hideProgressDialog();
                if (model!=null){
                    if (model.isSuccess()){
                        SharePrefs.getInstance(GenerateOTPActivity.this).putBoolean(SharePrefs.IS_USER_EXISTS, model.getResultItem().isUserExists());
                        SharePrefs.getInstance(GenerateOTPActivity.this).putBoolean(SharePrefs.RESULT, model.getResultItem().isResult());
                        startTimer(Binding.resendOtpTimer, Binding.resendotp);
                    }
                    else{
                        Utils.setToast(GenerateOTPActivity.this,model.getErrorMessage());
                    }
                }
            }
        });

    }

    private DisposableObserver<OtpResponceModel> OTPVerfiyData = new DisposableObserver<OtpResponceModel>() {
        @Override
        public void onNext(OtpResponceModel model) {
            Utils.hideProgressDialog();
                if (model!=null){
                    if (model.isSuccess()) {
                        SharePrefs.getInstance(GenerateOTPActivity.this).putBoolean(SharePrefs.IS_USER_EXISTS, model.getResultItem().getIsUserExist());
                        SharePrefs.getInstance(GenerateOTPActivity.this).putString(SharePrefs.USER_ID, model.getResultItem().getUserid());
                      /*  commonClassForAPI.getToken(callToken, "password", mobileNumber, otpString, true, true, "BUYERAPP",true,Utils.getDeviceUniqueID(GenerateOTPActivity.this),);*/

                        /*if (SharePrefs.getInstance(GenerateOTPActivity.this).getBoolean(SharePrefs.IS_LOGIN)){

                        }else {
                            startActivity(new Intent(GenerateOTPActivity.this,PlaceSearchActivity.class));
                        }*/  if (SharePrefs.getInstance(GenerateOTPActivity.this).getBoolean(SharePrefs.IS_REGISTRATIONCOMPLETE))
                        {
                            startActivity(new Intent(GenerateOTPActivity.this,MainActivity.class));
                        }
                        else{
                            startActivity(new Intent(GenerateOTPActivity.this,RegisterationActivity.class));
                        }

                      //  finish();

                    }else {
                        Binding.btLoddingOtp.setText("Next");
                        Utils.setToast(GenerateOTPActivity.this, "Please enter valid OTP.");
                    }
                }else {
                    Utils.setToast(GenerateOTPActivity.this, "Please enter valid OTP.");
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

    private DisposableObserver<LoginWithPasswordModel> callToken = new DisposableObserver<LoginWithPasswordModel>() {
        @Override
        public void onNext(LoginWithPasswordModel model) {
            try {
                Utils.hideProgressDialog();
                if (model != null) {
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.TOKEN, model.getAccess_token());
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.USER_NAME, model.getUserName());
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.USER_NAME, model.getUserName());
                    commonClassForAPI.getUpdateToken(updatecallToken,fcmToken);


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


    private DisposableObserver<JsonObject> updatecallToken = new DisposableObserver<JsonObject>() {
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
