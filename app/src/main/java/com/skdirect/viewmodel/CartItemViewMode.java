package com.skdirect.viewmodel;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.skdirect.activity.MainActivity;
import com.skdirect.adapter.CartListAdapter;
import com.skdirect.api.RestClient;
import com.skdirect.model.AddCartItemModel;
import com.skdirect.model.AllCategoriesModel;
import com.skdirect.model.CartItemModel;
import com.skdirect.model.ItemAddModel;
import com.skdirect.model.PaginationModel;
import com.skdirect.model.RemoveItemRequestModel;
import com.skdirect.utils.Utils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartItemViewMode extends ViewModel {
    final String TAG = getClass().getSimpleName();

    private MutableLiveData<CartItemModel> CartItemModelVM;
    private MutableLiveData<AddCartItemModel> addItemsInCardVM;
    private MutableLiveData<JsonObject> removeItemFromCartVM;

    public LiveData<CartItemModel> getCartItemModelVM() {
        CartItemModelVM=null;
        CartItemModelVM = new MutableLiveData<>();
        return CartItemModelVM;
    }

    public LiveData<AddCartItemModel> getAddItemsInCardVM() {
        addItemsInCardVM=null;
        addItemsInCardVM = new MutableLiveData<>();
        return addItemsInCardVM;
    }

    public LiveData<JsonObject> getRemoveItemFromCartVM() {
        removeItemFromCartVM=null;
        removeItemFromCartVM = new MutableLiveData<>();
        return removeItemFromCartVM;
    }

    public MutableLiveData<CartItemModel> getCartItemModelVMRequest(String id, RecyclerView rvCartItem, LinearLayout blankBasket) {
        RestClient.getInstance().getService().CartItems(id).enqueue(new Callback<CartItemModel>() {
            @Override
            public void onResponse(Call<CartItemModel> call, Response<CartItemModel> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    CartItemModelVM.setValue(response.body());
                }else {
                    Utils.hideProgressDialog();
                    rvCartItem.setVisibility(View.GONE);
                    blankBasket.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<CartItemModel> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + t.toString());
                rvCartItem.setVisibility(View.GONE);
                blankBasket.setVisibility(View.VISIBLE);
                Utils.hideProgressDialog();
            }
        });

        return CartItemModelVM;
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

    public MutableLiveData<JsonObject> getRemoveItemFromCartVMRequest(RemoveItemRequestModel itemRequestModel) {
        RestClient.getInstance().getService().deleteCartItems(itemRequestModel).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body()!=null ) {
                    Log.e(TAG, "request response="+response.body());
                    removeItemFromCartVM.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + t.toString());
                MainActivity.cartItemModel = null;
                Utils.hideProgressDialog();
            }
        });

        return removeItemFromCartVM;
    }
}