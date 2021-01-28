package com.skdirect.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;
import com.skdirect.api.RestClient;
import com.skdirect.model.AllCategoriesModel;
import com.skdirect.model.CartItemModel;
import com.skdirect.model.PaginationModel;
import com.skdirect.utils.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityViewMode extends ViewModel {
    final String TAG = getClass().getSimpleName();

    private MutableLiveData<CartItemModel> CardItemVM;
    private MutableLiveData<JsonObject> mapViewModel;

    public LiveData<CartItemModel> getCartItemsVM() {
        CardItemVM=null;
        CardItemVM = new MutableLiveData<>();
        return CardItemVM;
    }


    public LiveData<JsonObject> getMapViewModel() {
        mapViewModel=null;
        mapViewModel = new MutableLiveData<>();
        return mapViewModel;
    }

    public MutableLiveData<CartItemModel> getCartItemsRequest(String CooKiValue) {
        RestClient.getInstance().getService().GetCartItem(CooKiValue).enqueue(new Callback<CartItemModel>() {
            @Override
            public void onResponse(Call<CartItemModel> call, Response<CartItemModel> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    CardItemVM.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<CartItemModel> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + t.toString());
                Utils.hideProgressDialog();
            }
        });

        return CardItemVM;
    }

    public MutableLiveData<JsonObject> getMapViewModelRequest(double lat,double log) {
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



}
