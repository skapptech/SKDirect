package com.skdirect.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonArray;
import com.skdirect.api.RestClient;
import com.skdirect.model.LoginResponseModel;
import com.skdirect.model.NearProductListModel;
import com.skdirect.model.PaginationModel;
import com.skdirect.utils.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearProductListViewMode extends ViewModel {
    final String TAG = getClass().getSimpleName();

    private MutableLiveData<ArrayList<NearProductListModel>> nearProductListViewModel;

    public LiveData<ArrayList<NearProductListModel>> getNearProductList() {
          nearProductListViewModel=null;
            nearProductListViewModel = new MutableLiveData<>();
        return nearProductListViewModel;
    }

    public MutableLiveData<ArrayList<NearProductListModel>> getNearProductListRequest(PaginationModel paginationModel) {
        RestClient.getInstance().getService().getNearItem(paginationModel).enqueue(new Callback<ArrayList<NearProductListModel>>() {
            @Override
            public void onResponse(Call<ArrayList<NearProductListModel>> call, Response<ArrayList<NearProductListModel>> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    nearProductListViewModel.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<NearProductListModel>> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + call.toString());
                Utils.hideProgressDialog();
            }
        });

        return nearProductListViewModel;
    }
}
