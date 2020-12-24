package com.skdirect.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.skdirect.api.RestClient;
import com.skdirect.model.NearBySallerModel;
import com.skdirect.model.NearProductListModel;
import com.skdirect.model.PaginationModel;
import com.skdirect.utils.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerProductListViewMode extends ViewModel {
    final String TAG = getClass().getSimpleName();

    private MutableLiveData<ArrayList<NearBySallerModel>> sellerProductListVM;

    public LiveData<ArrayList<NearBySallerModel>> getSellerProductListVM() {
        sellerProductListVM=null;
        sellerProductListVM = new MutableLiveData<>();
        return sellerProductListVM;
    }

    public MutableLiveData<ArrayList<NearBySallerModel>> getSellerProductListRequest(int skipCount, int takeCount, String s) {
        RestClient.getInstance().getService().GetSellerListForBuyer(skipCount,takeCount,s).enqueue(new Callback<ArrayList<NearBySallerModel>>() {
            @Override
            public void onResponse(Call<ArrayList<NearBySallerModel>> call, Response<ArrayList<NearBySallerModel>> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    sellerProductListVM.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<NearBySallerModel>> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + call.toString());
                Utils.hideProgressDialog();
            }
        });

        return sellerProductListVM;
    }
}
