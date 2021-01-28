package com.skdirect.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.skdirect.api.RestClient;
import com.skdirect.model.AllCategoriesModel;
import com.skdirect.model.PaginationModel;
import com.skdirect.utils.Utils;

import org.json.JSONArray;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewAddressViewMode extends ViewModel {
    final String TAG = getClass().getSimpleName();
    private MutableLiveData<JsonObject> mapViewModel;
    private MutableLiveData<Boolean> addLocationVM;

    public LiveData<JsonObject> getMapViewModel() {
        mapViewModel=null;
        mapViewModel = new MutableLiveData<>();
        return mapViewModel;
    }

    public LiveData<Boolean> getAddLocationVM() {
        addLocationVM=null;
        addLocationVM = new MutableLiveData<>();
        return addLocationVM;
    }

    public MutableLiveData<JsonObject> getMapViewModelRequest(double lat, double log) {
        RestClient.getInstance().getService().GetLocation(lat,log).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    mapViewModel.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + call.toString());
                Utils.hideProgressDialog();
            }
        });

        return mapViewModel;
    }

    public MutableLiveData<Boolean> getAddLocationVMRequest(JsonArray jsonArray) {
        RestClient.getInstance().getService().AddLocation(jsonArray).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    addLocationVM.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + call.toString());
                Utils.hideProgressDialog();
            }
        });

        return addLocationVM;
    }
}
