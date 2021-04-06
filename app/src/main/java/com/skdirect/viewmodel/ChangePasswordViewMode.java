package com.skdirect.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.skdirect.api.RestClient;
import com.skdirect.model.AllCategoriesModel;
import com.skdirect.model.ChangePasswordRequestModel;
import com.skdirect.model.PaginationModel;
import com.skdirect.utils.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordViewMode extends ViewModel {
    final String TAG = getClass().getSimpleName();

    private MutableLiveData<Boolean> changePasswordVM;

    public LiveData<Boolean> getChangePasswordVM() {
        if(changePasswordVM==null)
        changePasswordVM = new MutableLiveData<>();
        if (changePasswordVM.getValue() != null)
        {
            changePasswordVM.setValue(null);
        }
        return changePasswordVM;
    }

    public MutableLiveData<Boolean> getChangePasswordRequest(ChangePasswordRequestModel passwordRequestModel) {
        RestClient.getInstance().getService().ChangePassword(passwordRequestModel).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    changePasswordVM.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + call.toString());
                Utils.hideProgressDialog();
            }
        });

        return changePasswordVM;
    }
}
