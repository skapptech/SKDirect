package com.skdirect.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.skdirect.api.RestClient;
import com.skdirect.model.AllCategoriesModel;
import com.skdirect.model.CartItemModel;
import com.skdirect.model.CustomerDataModel;
import com.skdirect.model.LoginResponseModel;
import com.skdirect.model.MallMainModel;
import com.skdirect.model.TopNearByItemModel;
import com.skdirect.model.TopSellerModel;
import com.skdirect.utils.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {
    final String TAG = getClass().getSimpleName();
    private MutableLiveData<CustomerDataModel> userDetailViewModel;
    private MutableLiveData<ArrayList<TopNearByItemModel>> topNearByItem;
    private MutableLiveData<ArrayList<TopSellerModel>> topSellerLiveData;
    private MutableLiveData<ArrayList<AllCategoriesModel>> allCategoriesLiveData;

    private MutableLiveData<MallMainModel> mallDataViewModel;



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


    public LiveData<ArrayList<TopSellerModel>> GetTopSellerLiveData() {
        if(topSellerLiveData==null){
            topSellerLiveData = new MutableLiveData<>();
            topSellerLiveData = GetTopSellerLiveRequest();
        }
        return topSellerLiveData;
    }

    public LiveData<ArrayList<AllCategoriesModel>> getAllCategoriesLiveData() {
        if(allCategoriesLiveData==null){
            allCategoriesLiveData = new MutableLiveData<>();
            allCategoriesLiveData = getAllCategoriesRequest();
        }
        return allCategoriesLiveData;
    }

    public LiveData<MallMainModel> getMallData() {
        if(mallDataViewModel==null){
            mallDataViewModel = new MutableLiveData<>();
            mallDataViewModel = getMallDataRequest();
        }
        return mallDataViewModel;
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

    public MutableLiveData<ArrayList<TopSellerModel>> GetTopSellerLiveRequest() {
        RestClient.getInstance().getService().GetTopSeller().enqueue(new Callback<ArrayList<TopSellerModel>>() {
            @Override
            public void onResponse(Call<ArrayList<TopSellerModel>> call, Response<ArrayList<TopSellerModel>> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    topSellerLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<TopSellerModel>> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + call.toString());
                Utils.hideProgressDialog();
            }
        });

        return topSellerLiveData;
    }

    public MutableLiveData<ArrayList<AllCategoriesModel>> getAllCategoriesRequest() {
        RestClient.getInstance().getService().GetTopCategory().enqueue(new Callback<ArrayList<AllCategoriesModel>>() {
            @Override
            public void onResponse(Call<ArrayList<AllCategoriesModel>> call, Response<ArrayList<AllCategoriesModel>> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    allCategoriesLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<AllCategoriesModel>> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + call.toString());
                Utils.hideProgressDialog();
            }
        });

        return allCategoriesLiveData;
    }

    public MutableLiveData<MallMainModel> getMallDataRequest() {
        RestClient.getInstance().getService().getMall().enqueue(new Callback<MallMainModel>() {
            @Override
            public void onResponse(Call<MallMainModel> call, Response<MallMainModel> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    mallDataViewModel.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<MallMainModel> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + call.toString());
                Utils.hideProgressDialog();
            }
        });

        return mallDataViewModel;
    }



}
