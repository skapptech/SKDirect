package com.skdirect.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.skdirect.api.RestClient;
import com.skdirect.model.AllCategoriesModel;
import com.skdirect.model.PaginationModel;
import com.skdirect.model.UserLocationModel;
import com.skdirect.utils.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrimaryAddressViewMode extends ViewModel {
    final String TAG = getClass().getSimpleName();

    private MutableLiveData<ArrayList<UserLocationModel>> userLocationVM;
    private MutableLiveData<Boolean> makeDefaultLocationVM;
    private MutableLiveData<Boolean> deleteLocationVM;

    public LiveData<ArrayList<UserLocationModel>> getUserLocationVM() {
        userLocationVM=null;
        userLocationVM = new MutableLiveData<>();
        return userLocationVM;
    }

    public LiveData<Boolean> getMakeDefaultLocationVM() {
        makeDefaultLocationVM=null;
        makeDefaultLocationVM = new MutableLiveData<>();
        return makeDefaultLocationVM;
    }

    public LiveData<Boolean> getDeleteLocationVM() {
        deleteLocationVM=null;
        deleteLocationVM = new MutableLiveData<>();
        return deleteLocationVM;
    }
    public MutableLiveData<ArrayList<UserLocationModel>> getUserLocationVMRequest() {
        RestClient.getInstance().getService().GetUserLocation().enqueue(new Callback<ArrayList<UserLocationModel>>() {
            @Override
            public void onResponse(Call<ArrayList<UserLocationModel>> call, Response<ArrayList<UserLocationModel>> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    userLocationVM.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<UserLocationModel>> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + call.toString());
                Utils.hideProgressDialog();
            }
        });

        return userLocationVM;
    }

    public MutableLiveData<Boolean> getMakeDefaultLocationVMRequest(int id) {
        RestClient.getInstance().getService().MakeDefaultAddress(id).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    makeDefaultLocationVM.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + call.toString());
                Utils.hideProgressDialog();
            }
        });

        return makeDefaultLocationVM;
    }

    public MutableLiveData<Boolean> getDeleteLocationVMRequest( ArrayList<UserLocationModel> locationModelsl) {
        RestClient.getInstance().getService().UpdateUserLocation(locationModelsl).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    deleteLocationVM.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + call.toString());
                Utils.hideProgressDialog();
            }
        });

        return deleteLocationVM;
    }
}
