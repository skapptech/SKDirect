package com.skdirect.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.skdirect.api.RestClient;
import com.skdirect.model.CustomerDataModel;
import com.skdirect.model.LoginResponseModel;
import com.skdirect.model.TopNearByItemModel;
import com.skdirect.utils.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {
    final String TAG = getClass().getSimpleName();
    private MutableLiveData<CustomerDataModel> userDetailViewModel;
    private MutableLiveData<ArrayList<TopNearByItemModel>> topNearByItem;


    public LiveData<CustomerDataModel> GetUserDetail() {
        if(userDetailViewModel==null){
            userDetailViewModel = new MutableLiveData<>();
            userDetailViewModel = getUserDetailRequest();
        }
        return userDetailViewModel;
    }

    public LiveData<ArrayList<TopNearByItemModel>> GetTopNearByItem() {
        if(topNearByItem==null){
            topNearByItem = new MutableLiveData<>();
            topNearByItem = getGetTopNearByItemRequest();
        }
        return topNearByItem;
    }

    public MutableLiveData<CustomerDataModel> getUserDetailRequest() {
        RestClient.getInstance().getService().GetUserDetail().enqueue(new Callback<CustomerDataModel>() {
            @Override
            public void onResponse(Call<CustomerDataModel> call, Response<CustomerDataModel> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    userDetailViewModel.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<CustomerDataModel> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + call.toString());
                Utils.hideProgressDialog();
            }
        });

        return userDetailViewModel;
    }

    public MutableLiveData<ArrayList<TopNearByItemModel>> getGetTopNearByItemRequest() {
        RestClient.getInstance().getService().GetTopNearByItem().enqueue(new Callback<ArrayList<TopNearByItemModel>>() {
            @Override
            public void onResponse(Call<ArrayList<TopNearByItemModel>> call, Response<ArrayList<TopNearByItemModel>> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    topNearByItem.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<TopNearByItemModel>> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + call.toString());
                Utils.hideProgressDialog();
            }
        });

        return topNearByItem;
    }



}
