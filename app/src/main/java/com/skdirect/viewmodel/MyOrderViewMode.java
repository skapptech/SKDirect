package com.skdirect.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonArray;
import com.skdirect.api.RestClient;
import com.skdirect.model.AllCategoriesModel;
import com.skdirect.model.MyOrderModel;
import com.skdirect.model.MyOrderRequestModel;
import com.skdirect.model.OrderModel;
import com.skdirect.model.PaginationModel;
import com.skdirect.utils.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrderViewMode extends ViewModel {
    final String TAG = getClass().getSimpleName();

    private MutableLiveData<OrderModel> myOrderViewModel;

    public LiveData<OrderModel> getCategoriesViewModel() {
        myOrderViewModel=null;
        myOrderViewModel = new MutableLiveData<>();
        return myOrderViewModel;
    }

    public MutableLiveData<OrderModel> getCategoriesViewModelRequest(MyOrderRequestModel myOrderRequestModel) {
        RestClient.getInstance().getService().GetOrderMaster(myOrderRequestModel).enqueue(new Callback<OrderModel>() {
            @Override
            public void onResponse(Call<OrderModel> call, Response<OrderModel> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    myOrderViewModel.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<OrderModel> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + call.toString());
                Utils.hideProgressDialog();
            }
        });

        return myOrderViewModel;
    }

}
