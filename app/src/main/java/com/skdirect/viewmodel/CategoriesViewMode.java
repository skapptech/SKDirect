package com.skdirect.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonArray;
import com.skdirect.api.RestClient;
import com.skdirect.model.AllCategoriesModel;
import com.skdirect.model.NearProductListModel;
import com.skdirect.model.PaginationModel;
import com.skdirect.utils.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriesViewMode extends ViewModel {
    final String TAG = getClass().getSimpleName();

    private MutableLiveData<ArrayList<AllCategoriesModel>> categoriesViewModel;

    public LiveData<ArrayList<AllCategoriesModel>> getCategoriesViewModel() {
        categoriesViewModel=null;
        categoriesViewModel = new MutableLiveData<>();
        return categoriesViewModel;
    }

    public MutableLiveData<ArrayList<AllCategoriesModel>> getCategoriesViewModelRequest(PaginationModel paginationModel) {
        RestClient.getInstance().getService().GetCategorybyfilter(paginationModel).enqueue(new Callback<ArrayList<AllCategoriesModel>>() {
            @Override
            public void onResponse(Call<ArrayList<AllCategoriesModel>> call, Response<ArrayList<AllCategoriesModel>> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    categoriesViewModel.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<AllCategoriesModel>> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + call.toString());
                Utils.hideProgressDialog();
            }
        });

        return categoriesViewModel;
    }
}
