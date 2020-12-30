package com.skdirect.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonArray;
import com.skdirect.api.RestClient;
import com.skdirect.model.AllCategoriesModel;
import com.skdirect.model.PaginationModel;
import com.skdirect.model.ProductDataModel;
import com.skdirect.utils.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsViewMode extends ViewModel {
    final String TAG = getClass().getSimpleName();

    private MutableLiveData<ProductDataModel> productDetailsVM;

    public LiveData<ProductDataModel> getProductDetailsVM() {
        productDetailsVM=null;
        productDetailsVM = new MutableLiveData<>();
        return productDetailsVM;
    }

    public MutableLiveData<ProductDataModel> getCategoriesViewModelRequest(int productID) {
        RestClient.getInstance().getService().GetSellerProductById(productID).enqueue(new Callback<ProductDataModel>() {
            @Override
            public void onResponse(Call<ProductDataModel> call, Response<ProductDataModel> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    productDetailsVM.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<ProductDataModel> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + t.toString());
                Utils.hideProgressDialog();
            }
        });

        return productDetailsVM;
    }
}
