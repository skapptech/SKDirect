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
import com.google.gson.JsonObject;
import com.skdirect.R;
import com.skdirect.api.CommonClassForAPI;
import com.skdirect.broadcast.SmsBroadcastReceiver;
import com.skdirect.databinding.ActivityGenerateOtpBinding;
import com.skdirect.interfacee.OtpReceivedInterface;
import com.skdirect.location.EasyWayLocation;
import com.skdirect.model.GenerateOtpModel;
import com.skdirect.model.GenerateOtpResponseModel;
import com.skdirect.model.OtpResponceModel;
import com.skdirect.model.OtpVerificationModel;
import com.skdirect.model.TokenModel;
import com.skdirect.utils.DBHelper;
import com.skdirect.utils.MyApplication;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.TextUtils;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.OTPVerificationViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import io.reactivex.observers.DisposableObserver;

public class GenerateOTPActivity extends AppCompatActivity implements OtpReceivedInterface, View.OnClickListener {
    final String TAG = getClass().getSimpleName();
    private ActivityGenerateOtpBinding Binding;
    private SmsBroadcastReceiver mSmsBroadcastReceiver;
    private String latsFiveNumber, otpString, mobileNumber;
    private OTPVerificationViewModel otpVerificationViewModel;
    private CountDownTimer cTimer = null;
    private CommonClassForAPI commonClassForAPI;
    private String fcmToken;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Binding = DataBindingUtil.setContentView(this, R.layout.activity_generate_otp);
        otpVerificationViewModel = ViewModelProviders.of(this).get(OTPVerificationViewModel.class);
        dbHelper = MyApplication.getInstance().dbHelper;
        getIntentData();
        initView();

    }

    private void getIntentData() {
        mobileNumber = getIntent().getStringExtra("MobileNumber");
        latsFiveNumber = mobileNumber.substring(mobileNumber.length() - 5);
        fcmToken = Utils.getFcmToken();
    }

    private void initView() {
        commonClassForAPI = CommonClassForAPI.getInstance(this);
        Binding.btLoddingOtp.setOnClickListener(this);
        Binding.tvVerifactionText.setText(dbHelper.getString(R.string.enter_no_txt) + latsFiveNumber);
        Binding.tvVerificationHead.setText(dbHelper.getString(R.string.verification));
        Binding.resendotp.setText(dbHelper.getString(R.string.resend_otp));
        Binding.btLoddingOtp.setText(dbHelper.getString(R.string.next));
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
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                    Binding.btLoddingOtp.setText(dbHelper.getString(R.string.loading));
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
                tvResendOtpTimer.setText(dbHelper.getString(R.string.resend_otp) + ":" + "" + millisUntilFinished / 1000);
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
                checkVerification();

                break;
        }
    }

    private void checkVerification() {
        otpString = Binding.etOtp.getText().toString().trim();
        if (otpString.isEmpty()) {
            Utils.setToast(this, dbHelper.getString(R.string.enter_otp));
        } else {
            Binding.btLoddingOtp.setText(dbHelper.getString(R.string.loading));
            callOTPVerfiyAPI(otpString);
        }

    }

    private void callOTPVerfiyAPI(String otpString) {
        if (Utils.isNetworkAvailable(this)) {
            if (commonClassForAPI != null) {
                commonClassForAPI.VerfiyOtp(OTPVerfiyData, new OtpVerificationModel(mobileNumber, otpString, Utils.getDeviceUniqueID(this)));
            }
        } else {
            Utils.setToast(this, dbHelper.getString(R.string.no_connection));
        }
    }


    private void callResendOTPApi(String mobileNumberString) {
        if (Utils.isNetworkAvailable(this)) {
            Utils.showProgressDialog(this);
            GenerateOtpModel model = new GenerateOtpModel(mobileNumberString, Utils.getDeviceUniqueID(this));
            getResendOTPData(model);
        } else {
            Utils.setToast(this, dbHelper.getString(R.string.no_connection));
        }

    }

    private void getResendOTPData(GenerateOtpModel generateOtpModel) {
        otpVerificationViewModel.getLogin(generateOtpModel).observe(this, new Observer<GenerateOtpResponseModel>() {
            @Override
            public void onChanged(GenerateOtpResponseModel model) {
                Utils.hideProgressDialog();
                if (model != null) {
                    if (model.isSuccess()) {
                        SharePrefs.getInstance(GenerateOTPActivity.this).putBoolean(SharePrefs.IS_USER_EXISTS, model.getResultItem().isUserExists());
                        SharePrefs.getInstance(GenerateOTPActivity.this).putBoolean(SharePrefs.RESULT, model.getResultItem().isResult());
                        startTimer(Binding.resendOtpTimer, Binding.resendotp);
                    } else {
                        Utils.setToast(GenerateOTPActivity.this, model.getErrorMessage());
                    }
                }
            }
        });

    }

    private DisposableObserver<OtpResponceModel> OTPVerfiyData = new DisposableObserver<OtpResponceModel>() {
        @Override
        public void onNext(OtpResponceModel model) {
            Utils.hideProgressDialog();
            if (model != null) {
                if (model.isSuccess()) {
                    SharePrefs.getInstance(GenerateOTPActivity.this).putBoolean(SharePrefs.IS_USER_EXISTS, model.getResultItem().getIsUserExist());
                    SharePrefs.getInstance(GenerateOTPActivity.this).putString(SharePrefs.USER_ID, model.getResultItem().getUserid());
                    commonClassForAPI
                            .getTokenwithphoneNo(callToken, "password", Utils.getDeviceUniqueID(GenerateOTPActivity.this),
                                    Utils.getDeviceUniqueID(GenerateOTPActivity.this), true, true, "BUYERAPP", true,
                                    Utils.getDeviceUniqueID(GenerateOTPActivity.this), Double.parseDouble(SharePrefs.getStringSharedPreferences(GenerateOTPActivity.this, SharePrefs.LAT)), Double.parseDouble(SharePrefs.getStringSharedPreferences(GenerateOTPActivity.this, SharePrefs.LON)), SharePrefs.getInstance(GenerateOTPActivity.this).getString(SharePrefs.PIN_CODE), "GET", mobileNumber);


                } else {
                    Binding.btLoddingOtp.setText(dbHelper.getString(R.string.next));
                    Utils.setToast(GenerateOTPActivity.this, dbHelper.getString(R.string.valid_otp));
                }
            } else {
                Utils.setToast(GenerateOTPActivity.this, dbHelper.getString(R.string.valid_otp));
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

    private DisposableObserver<TokenModel> callToken = new DisposableObserver<TokenModel>() {
        @Override
        public void onNext(TokenModel model) {
            try {
                Utils.hideProgressDialog();
                if (model != null) {
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.TOKEN, model.getAccess_token());
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.USER_NAME, model.getUserName());
                    SharePrefs.setSharedPreference(getApplicationContext(), SharePrefs.IS_REGISTRATIONCOMPLETE, model.getIsRegistrationComplete());
                    SharePrefs.setStringSharedPreference(getApplicationContext(),SharePrefs.LAT, String.valueOf(model.getLatitiute()));
                    SharePrefs.setStringSharedPreference(getApplicationContext(),SharePrefs.LON, String.valueOf(model.getLongitude()));
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.BUSINESS_TYPE, model.getBusinessType());
                    SharePrefs.getInstance(getApplicationContext()).putBoolean(SharePrefs.IS_CONTACTREAD, model.getIscontactRead());
                    SharePrefs.getInstance(getApplicationContext()).putBoolean(SharePrefs.IS_SUPER_ADMIN, model.getIsSuperAdmin());
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.ASP_NET_USER_ID, model.getAspNetuserId());
                    try {
                        JSONObject jsonObject = new JSONObject(model.getUserDetail());
                        SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.FIRST_NAME, jsonObject.getString("FirstName"));
                        SharePrefs.getInstance(getApplicationContext()).putInt(SharePrefs.ID, jsonObject.getInt("Id"));
                        SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.MOBILE_NUMBER, jsonObject.getString("MobileNo"));
                        SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.EMAIL_ID, jsonObject.getString("Email"));
                        SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.STATE, jsonObject.getString("State"));
                        SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.CITYNAME, jsonObject.getString("City"));
                        SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.PIN_CODE, jsonObject.getString("Pincode"));
                        SharePrefs.getInstance(getApplicationContext()).putInt(SharePrefs.PIN_CODE_master, jsonObject.getInt("PinCodeMasterId"));
                        SharePrefs.getInstance(getApplicationContext()).putBoolean(SharePrefs.IS_ACTIVE, jsonObject.getBoolean("IsActive"));
                        SharePrefs.getInstance(getApplicationContext()).putBoolean(SharePrefs.IS_DELETE, jsonObject.getBoolean("IsDelete"));
                        JSONArray jsonArray = jsonObject.getJSONArray("UserDeliveryDC");
                        if (jsonArray != null && jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                SharePrefs.getInstance(getApplicationContext()).putBoolean(SharePrefs.USER_IS_DELETE, object.getBoolean("IsDelete"));
                                SharePrefs.getInstance(getApplicationContext()).putBoolean(SharePrefs.USER_IS_ACTIVE, object.getBoolean("IsActive"));
                                SharePrefs.getInstance(getApplicationContext()).putInt(SharePrefs.USER_DC_ID, object.getInt("Id"));
                                SharePrefs.getInstance(getApplicationContext()).putInt(SharePrefs.USER_DC_USER_ID, object.getInt("UserId"));
                                SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.DELIVERY, object.getString("Delivery"));

                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    commonClassForAPI.getUpdateToken(updatecallToken, fcmToken);
                    if(!TextUtils.isNullOrEmpty(MyApplication.getInstance().cartRepository.getCartId())){
                        commonClassForAPI.assignCart(new DisposableObserver<JsonObject>() {
                            @Override
                            public void onNext(JsonObject model) { }
                            @Override
                            public void onError(Throwable e) {
                                Utils.hideProgressDialog();
                                e.printStackTrace();
                            }
                            @Override
                            public void onComplete() {
                                Utils.hideProgressDialog();
                            }
                        }, MyApplication.getInstance().cartRepository.getCartId());
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
                Binding.btLoddingOtp.setText(dbHelper.getString(R.string.next));
                Utils.setToast(getApplicationContext(), dbHelper.getString(R.string.invalid_pass));

            }
        }

        @Override
        public void onError(Throwable e) {
            Binding.btLoddingOtp.setText(dbHelper.getString(R.string.next));
            Utils.setToast(getApplicationContext(), dbHelper.getString(R.string.invalid_pass));
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
                    if (SharePrefs.getSharedPreferences(getApplicationContext(), SharePrefs.IS_REGISTRATIONCOMPLETE)) {
                        startActivity(new Intent(GenerateOTPActivity.this, MainActivity.class));
                        SharePrefs.getInstance(getApplicationContext()).putBoolean(SharePrefs.IS_LOGIN, true);
                    } else {
                        startActivity(new Intent(GenerateOTPActivity.this, RegisterationActivity.class));
                    }
                    finish();
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
