package com.skdirect.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonArray;
import com.skdirect.api.RestClient;
import com.skdirect.model.AllCategoriesModel;
import com.skdirect.model.PaginationModel;
import com.skdirect.model.SearchDataModel;
import com.skdirect.model.SearchRequestModel;
import com.skdirect.model.TopSellerModel;
import com.skdirect.utils.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchViewMode extends ViewModel {
    final String TAG = getClass().getSimpleName();

    private MutableLiveData<SearchDataModel> searchViewModel;

    private MutableLiveData<ArrayList<TopSellerModel>> shopDataViewModel;

    public LiveData<SearchDataModel> getSearchViewModel() {
        searchViewModel=null;
        searchViewModel = new MutableLiveData<>();
        return searchViewModel;
    }

    public LiveData<ArrayList<TopSellerModel>> getShopDataViewModel() {
        shopDataViewModel=null;
        shopDataViewModel = new MutableLiveData<>();
        return shopDataViewModel;
    }

    public MutableLiveData<SearchDataModel> getSearchRequest(SearchRequestModel searchRequestModel) {
        RestClient.getInstance().getService().GetSellerListWithItem(searchRequestModel).enqueue(new Callback<SearchDataModel>() {
            @Override
            public void onResponse(Call<SearchDataModel> call, Response<SearchDataModel> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    searchViewModel.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<SearchDataModel> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + t.toString());
                Utils.hideProgressDialog();
            }
        });

        return searchViewModel;
    }


    public MutableLiveData<ArrayList<TopSellerModel>> getShopDataViewModelRequest(int skipCount, int takeCount, String s, int cateogryId) {
        RestClient.getInstance().getService().GetTopSellerItem(skipCount,takeCount,s,cateogryId).enqueue(new Callback<ArrayList<TopSellerModel>>() {
            @Override
            public void onResponse(Call<ArrayList<TopSellerModel>> call, Response<ArrayList<TopSellerModel>> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    shopDataViewModel.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<TopSellerModel>> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + t.toString());
                Utils.hideProgressDialog();
            }
        });

        return shopDataViewModel;
    }
}
