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
import com.skdirect.utils.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchViewMode extends ViewModel {
    final String TAG = getClass().getSimpleName();

    private MutableLiveData<SearchDataModel> searchViewModel;

    public LiveData<SearchDataModel> getSearchViewModel() {
        searchViewModel=null;
        searchViewModel = new MutableLiveData<>();
        return searchViewModel;
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
}
