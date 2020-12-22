package com.skdirect.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;
import com.skdirect.api.RestClient;
import com.skdirect.model.AppVersionModel;
import com.skdirect.model.LoginResponseModel;
import com.skdirect.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {
    final String TAG = getClass().getSimpleName();

    private MutableLiveData<LoginResponseModel> loginViewModel;

    public LiveData<LoginResponseModel> getLogin(String mobileNumberString) {
        if(loginViewModel==null){
            loginViewModel = new MutableLiveData<>();
            loginViewModel = loginRequest(mobileNumberString);
        }
        return loginViewModel;
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
