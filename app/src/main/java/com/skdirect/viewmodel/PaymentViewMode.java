package com.skdirect.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.skdirect.api.RestClient;
import com.skdirect.model.AllCategoriesModel;
import com.skdirect.model.DeliveryOptionModel;
import com.skdirect.model.OrderPlaceModel;
import com.skdirect.model.OrderPlaceRequestModel;
import com.skdirect.model.PaginationModel;
import com.skdirect.model.UserLocationModel;
import com.skdirect.utils.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentViewMode extends ViewModel {
    final String TAG = getClass().getSimpleName();
    private MutableLiveData<ArrayList<UserLocationModel>> userLocationVM;
    private MutableLiveData<ArrayList<DeliveryOptionModel>> DeliveryOptionVM;
    private MutableLiveData<JsonObject> checkOutItemVM;
    private MutableLiveData<JsonObject> mapViewModel;
    private MutableLiveData<OrderPlaceModel> orderPlaceVM;
    private MutableLiveData<Object> clearCartDataVM;

    public LiveData<ArrayList<UserLocationModel>> getUserLocationVM() {
        userLocationVM=null;
        userLocationVM = new MutableLiveData<>();
        return userLocationVM;
    }

    public LiveData<ArrayList<DeliveryOptionModel>> getDeliveryOptionVM() {
        DeliveryOptionVM=null;
        DeliveryOptionVM = new MutableLiveData<>();
        return DeliveryOptionVM;
    }

    public LiveData<JsonObject> getCheckOutItemVM() {
        checkOutItemVM=null;
        checkOutItemVM = new MutableLiveData<>();
        return checkOutItemVM;
    }

    public LiveData<JsonObject> getMapViewModel() {
        mapViewModel=null;
        mapViewModel = new MutableLiveData<>();
        return mapViewModel;
    }

    public LiveData<OrderPlaceModel> getOrderPlaceVM() {
        orderPlaceVM=null;
        orderPlaceVM = new MutableLiveData<>();
        return orderPlaceVM;
    }

    public LiveData<Object> getClearCartData() {
        clearCartDataVM=null;
        clearCartDataVM = new MutableLiveData<>();
        return clearCartDataVM;
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

    public MutableLiveData<ArrayList<DeliveryOptionModel>> getDeliveryOptionVMRequest(String sellerID) {
        RestClient.getInstance().getService().GetDeliveryOption(sellerID).enqueue(new Callback<ArrayList<DeliveryOptionModel>>() {
            @Override
            public void onResponse(Call<ArrayList<DeliveryOptionModel>> call, Response<ArrayList<DeliveryOptionModel>> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    DeliveryOptionVM.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DeliveryOptionModel>> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + t.toString());
                Utils.hideProgressDialog();
            }
        });

        return DeliveryOptionVM;
    }

    public MutableLiveData<JsonObject> getCheckOutItemVMRequest(String sellerID) {
        RestClient.getInstance().getService().GetCheckOutItem(sellerID).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    checkOutItemVM.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + t.toString());
                Utils.hideProgressDialog();
            }
        });

        return checkOutItemVM;
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

    public MutableLiveData<OrderPlaceModel> getOrderPlaceVMRequest(OrderPlaceRequestModel placeRequestModel) {
        RestClient.getInstance().getService().PlaceOrder(placeRequestModel).enqueue(new Callback<OrderPlaceModel>() {
            @Override
            public void onResponse(Call<OrderPlaceModel> call, Response<OrderPlaceModel> response) {
                if (response.isSuccessful()) {
                    Log.e(TAG, "request response="+response.body());
                    orderPlaceVM.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<OrderPlaceModel> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + call.toString());
                Utils.hideProgressDialog();
            }
        });

        return orderPlaceVM;
    }

    public MutableLiveData<Object> getClearCartDataRequest(String id) {
        RestClient.getInstance().getService().ClearCart(id).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    clearCartDataVM.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + call.toString());
                Utils.hideProgressDialog();
            }
        });

        return clearCartDataVM;
    }
}