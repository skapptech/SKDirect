package com.skdirect.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.skdirect.api.RestClient;
import com.skdirect.model.NearProductListMainModel;
import com.skdirect.model.NearProductListModel;
import com.skdirect.model.PaginationModel;
import com.skdirect.utils.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilterViewModel extends ViewModel {
    final String TAG = getClass().getSimpleName();

    private MutableLiveData<NearProductListMainModel> nearProductListViewModel;

    public LiveData<NearProductListMainModel> getFilterList() {
          nearProductListViewModel=null;
            nearProductListViewModel = new MutableLiveData<>();
        return nearProductListViewModel;
    }

    public MutableLiveData<NearProductListMainModel> getFilterListRequest(PaginationModel paginationModel) {
        RestClient.getInstance().getService().getNearItem(paginationModel).enqueue(new Callback<NearProductListMainModel>() {
            @Override
            public void onResponse(Call<NearProductListMainModel> call, Response<NearProductListMainModel> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    nearProductListViewModel.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<NearProductListMainModel> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + call.toString());
                Utils.hideProgressDialog();
            }
        });

        return nearProductListViewModel;
    }
}
