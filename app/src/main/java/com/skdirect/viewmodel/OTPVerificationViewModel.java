package com.skdirect.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.skdirect.api.RestClient;
import com.skdirect.model.LoginResponseModel;
import com.skdirect.model.OtpResponceModel;
import com.skdirect.model.OtpVerificationModel;
import com.skdirect.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPVerificationViewModel extends ViewModel {
    final String TAG = getClass().getSimpleName();

    private MutableLiveData<OtpResponceModel> OTPVerificationViewModel;
    private MutableLiveData<LoginResponseModel> loginViewModel;



    public LiveData<OtpResponceModel> getLogin(OtpVerificationModel otpVerificationModel) {
        if(OTPVerificationViewModel==null){
            OTPVerificationViewModel = new MutableLiveData<>();
            OTPVerificationViewModel = loginRequest(otpVerificationModel);
        }
        return OTPVerificationViewModel;
    }

    public LiveData<LoginResponseModel> getLogin(String mobileNumberString) {
        if(loginViewModel==null){
            loginViewModel = new MutableLiveData<>();
            loginViewModel = loginRequest(mobileNumberString);
        }
        return loginViewModel;
    }

    public MutableLiveData<OtpResponceModel> loginRequest(OtpVerificationModel otpVerificationModel) {
        RestClient.getInstance().getService().getVerfiyOtp(otpVerificationModel).enqueue(new Callback<OtpResponceModel>() {
            @Override
            public void onResponse(Call<OtpResponceModel> call, Response<OtpResponceModel> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    OTPVerificationViewModel.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<OtpResponceModel> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + call.toString());
                Utils.hideProgressDialog();
            }
        });

        return OTPVerificationViewModel;
    }



    public MutableLiveData<LoginResponseModel> loginRequest(String mobileNumberString) {
        RestClient.getInstance().getService().GenerateOtp(mobileNumberString).enqueue(new Callback<LoginResponseModel>() {
            @Override
            public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    loginViewModel.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + call.toString());
                Utils.hideProgressDialog();
            }
        });

        return loginViewModel;
    }


}
