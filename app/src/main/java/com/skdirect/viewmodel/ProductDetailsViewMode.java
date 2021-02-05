package com.skdirect.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.skdirect.api.RestClient;
import com.skdirect.model.AddCartItemModel;
import com.skdirect.model.AllCategoriesModel;
import com.skdirect.model.CartItemModel;
import com.skdirect.model.ItemAddModel;
import com.skdirect.model.PaginationModel;
import com.skdirect.model.ProductDataModel;
import com.skdirect.model.TopNearByItemModel;
import com.skdirect.model.TopSellerModel;
import com.skdirect.utils.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsViewMode extends ViewModel {
    final String TAG = getClass().getSimpleName();

    private MutableLiveData<ProductDataModel> productDetailsVM;
    private MutableLiveData<ArrayList<TopNearByItemModel>> similarProductVM;
    private MutableLiveData<ArrayList<TopSellerModel>> topSellerLiveData;
    private MutableLiveData<ArrayList<TopNearByItemModel>> sallerOtherProducsVM;
    private MutableLiveData<CartItemModel> cartItemsVM;
    private MutableLiveData<Boolean> addProductVM;
    private MutableLiveData<AddCartItemModel> addItemsInCardVM;
    private MutableLiveData<Object> clearCartItemVM;

    public LiveData<ProductDataModel> getProductDetailsVM() {
        productDetailsVM=null;
        productDetailsVM = new MutableLiveData<>();
        return productDetailsVM;
    }

    public LiveData<ArrayList<TopNearByItemModel>> getSimilarProductVM() {
        similarProductVM=null;
        similarProductVM = new MutableLiveData<>();
        return similarProductVM;
    }

    public LiveData<ArrayList<TopSellerModel>> GetTopSellerLiveData() {
        if(topSellerLiveData==null){
            topSellerLiveData = new MutableLiveData<>();
        }
        return topSellerLiveData;
    }

    public LiveData<ArrayList<TopNearByItemModel>> getSallerOtherProducsVM() {
        sallerOtherProducsVM=null;
        sallerOtherProducsVM = new MutableLiveData<>();
        return sallerOtherProducsVM;
    }

    public LiveData<CartItemModel> getCartItemsVM() {
        cartItemsVM=null;
        cartItemsVM = new MutableLiveData<>();
        return cartItemsVM;
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

    public LiveData<Object> getClearCartItemVM() {
        clearCartItemVM=null;
        clearCartItemVM = new MutableLiveData<>();
        return clearCartItemVM;
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


    public MutableLiveData<ArrayList<TopNearByItemModel>> getSimilarProductVMRequest(int productID) {
        RestClient.getInstance().getService().GetTopSimilarproduct(productID).enqueue(new Callback<ArrayList<TopNearByItemModel>>() {
            @Override
            public void onResponse(Call<ArrayList<TopNearByItemModel>> call, Response<ArrayList<TopNearByItemModel>> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    similarProductVM.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<TopNearByItemModel>> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + t.toString());
                Utils.hideProgressDialog();
            }
        });

        return similarProductVM;
    }

    public MutableLiveData<ArrayList<TopSellerModel>> GetTopSellerLiveRequest(int productID) {
        RestClient.getInstance().getService().GetSimilarProductTopSeller(productID).enqueue(new Callback<ArrayList<TopSellerModel>>() {
            @Override
            public void onResponse(Call<ArrayList<TopSellerModel>> call, Response<ArrayList<TopSellerModel>> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    topSellerLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<TopSellerModel>> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + t.toString());
                Utils.hideProgressDialog();
            }
        });

        return topSellerLiveData;
    }

    public MutableLiveData<ArrayList<TopNearByItemModel>> getSellarOtherVMRequest(int productID) {
        RestClient.getInstance().getService().GetMoreSimilarSellerProduct(productID).enqueue(new Callback<ArrayList<TopNearByItemModel>>() {
            @Override
            public void onResponse(Call<ArrayList<TopNearByItemModel>> call, Response<ArrayList<TopNearByItemModel>> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    sallerOtherProducsVM.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<TopNearByItemModel>> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + t.toString());
                Utils.hideProgressDialog();
            }
        });

        return sallerOtherProducsVM;
    }


    public MutableLiveData<CartItemModel> getCartItemsVMRequest(String productID) {
        RestClient.getInstance().getService().GetCartItems(productID).enqueue(new Callback<CartItemModel>() {
            @Override
            public void onResponse(Call<CartItemModel> call, Response<CartItemModel> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    cartItemsVM.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<CartItemModel> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + t.toString());
                Utils.hideProgressDialog();
            }
        });

        return cartItemsVM;
    }

    public MutableLiveData<Boolean> getAddProductVMRequest(PaginationModel paginationModel) {
        RestClient.getInstance().getService().AddProductView(paginationModel).enqueue(new Callback<Boolean>() {
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
        RestClient.getInstance().getService().AddCartItem(paginationModel).enqueue(new Callback<AddCartItemModel>() {
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

    public MutableLiveData<Object> getClearCartItemVMRequest(String id) {
        RestClient.getInstance().getService().ClearCart(id).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    clearCartItemVM.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + t.toString());
                Utils.hideProgressDialog();
            }
        });

        return clearCartItemVM;
    }
}
