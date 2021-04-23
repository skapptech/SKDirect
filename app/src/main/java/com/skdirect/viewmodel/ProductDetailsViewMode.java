package com.skdirect.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.skdirect.api.RestClient;
import com.skdirect.model.AddCartItemModel;
import com.skdirect.model.AddViewMainModel;
import com.skdirect.model.CartItemModel;
import com.skdirect.model.ItemAddModel;
import com.skdirect.model.MainSimilarTopSellerModel;
import com.skdirect.model.MainTopSimilarSellerModel;
import com.skdirect.model.ProductDataModel;
import com.skdirect.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsViewMode extends ViewModel {
    final String TAG = getClass().getSimpleName();

    private MutableLiveData<MainTopSimilarSellerModel> similarProductVM;
    private MutableLiveData<MainSimilarTopSellerModel> topSellerLiveData;
    private MutableLiveData<MainTopSimilarSellerModel> sallerOtherProducsVM;

    public LiveData<MainTopSimilarSellerModel> getSimilarProductVM() {
        similarProductVM=null;
        similarProductVM = new MutableLiveData<>();
        return similarProductVM;
    }

    public LiveData<MainSimilarTopSellerModel> GetTopSellerLiveData() {
        if(topSellerLiveData==null){
            topSellerLiveData = new MutableLiveData<>();
        }
        return topSellerLiveData;
    }

    public LiveData<MainTopSimilarSellerModel> getSallerOtherProducsVM() {
        sallerOtherProducsVM=null;
        sallerOtherProducsVM = new MutableLiveData<>();
        return sallerOtherProducsVM;
    }





    public MutableLiveData<MainTopSimilarSellerModel> getSimilarProductVMRequest(int productID) {
        RestClient.getInstance().getService().GetTopSimilarproduct(productID).enqueue(new Callback<MainTopSimilarSellerModel>() {
            @Override
            public void onResponse(Call<MainTopSimilarSellerModel> call, Response<MainTopSimilarSellerModel> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    similarProductVM.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<MainTopSimilarSellerModel> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + t.toString());
                Utils.hideProgressDialog();
            }
        });

        return similarProductVM;
    }

    public MutableLiveData<MainSimilarTopSellerModel> GetTopSellerLiveRequest(int productID) {
        RestClient.getInstance().getService().GetSimilarProductTopSeller(productID).enqueue(new Callback<MainSimilarTopSellerModel>() {
            @Override
            public void onResponse(Call<MainSimilarTopSellerModel> call, Response<MainSimilarTopSellerModel> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    topSellerLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<MainSimilarTopSellerModel> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + t.toString());
                Utils.hideProgressDialog();
            }
        });

        return topSellerLiveData;
    }


}
