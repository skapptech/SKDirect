package com.skdirect.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.skdirect.api.RestClient;
import com.skdirect.model.AddViewModel;
import com.skdirect.model.AddCartItemModel;
import com.skdirect.model.CartItemModel;
import com.skdirect.model.ItemAddModel;
import com.skdirect.model.SellerDetailsModel;
import com.skdirect.model.SellerProductModel;
import com.skdirect.model.SellerProfileDataModel;
import com.skdirect.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerProfileViewMode extends ViewModel {
    final String TAG = getClass().getSimpleName();

    private MutableLiveData<SellerDetailsModel> sellerDetailsVM;
    private MutableLiveData<SellerProductModel> sellerProductVM;
    private MutableLiveData<Boolean> addProductVM;
    private MutableLiveData<AddCartItemModel> addItemsInCardVM;


    public LiveData<SellerDetailsModel> getSellerDetailsVM() {
        sellerDetailsVM=null;
        sellerDetailsVM = new MutableLiveData<>();
        return sellerDetailsVM;
    }

    public LiveData<SellerProductModel> getSellerProductVM() {
        sellerProductVM=null;
        sellerProductVM = new MutableLiveData<>();
        return sellerProductVM;
    }

    public LiveData<Boolean> getAddProductVM() {
        addProductVM=null;
        addProductVM = new MutableLiveData<>();
        return addProductVM;
    }


    public LiveData<AddCartItemModel> getAddItemsInCardVM() {
        addItemsInCardVM=null;
        addItemsInCardVM = new MutableLiveData<>();
        return addItemsInCardVM;
    }


    public MutableLiveData<SellerDetailsModel> getSellerDetailsRequest(String id) {
        RestClient.getInstance().getService().GetSellerDetail(id).enqueue(new Callback<SellerDetailsModel>() {
            @Override
            public void onResponse(Call<SellerDetailsModel> call, Response<SellerDetailsModel> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    sellerDetailsVM.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<SellerDetailsModel> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + call.toString());
                Utils.hideProgressDialog();
            }
        });

        return sellerDetailsVM;
    }

    public MutableLiveData<SellerProductModel> getSellerProductRequest(SellerProfileDataModel paginationModel) {
        RestClient.getInstance().getService().GetSellerProduct(paginationModel).enqueue(new Callback<SellerProductModel>() {
            @Override
            public void onResponse(Call<SellerProductModel> call, Response<SellerProductModel> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.code());
                    sellerProductVM.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<SellerProductModel> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + t.toString());
                Utils.hideProgressDialog();
            }
        });

        return sellerProductVM;
    }

    public MutableLiveData<Boolean> getAddProductVMRequest(AddViewModel paginationModel) {
        RestClient.getInstance().getService().AddStoreView(paginationModel).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    addProductVM.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + t.toString());
                Utils.hideProgressDialog();
            }
        });

        return addProductVM;
    }

    public MutableLiveData<AddCartItemModel> getAddItemsInCardVMRequest(ItemAddModel paginationModel) {
        RestClient.getInstance().getService().AddCart(paginationModel).enqueue(new Callback<AddCartItemModel>() {
            @Override
            public void onResponse(Call<AddCartItemModel> call, Response<AddCartItemModel> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    addItemsInCardVM.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<AddCartItemModel> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + t.toString());
                Utils.hideProgressDialog();
            }
        });

        return addItemsInCardVM;
    }


}
