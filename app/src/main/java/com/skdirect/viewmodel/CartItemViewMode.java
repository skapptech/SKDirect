package com.skdirect.viewmodel;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.skdirect.api.RestClient;
import com.skdirect.model.AddCartItemModel;
import com.skdirect.model.CartMainModel;
import com.skdirect.model.ItemAddModel;
import com.skdirect.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartItemViewMode extends ViewModel {
    final String TAG = getClass().getSimpleName();

    private MutableLiveData<CartMainModel> CartItemModelVM;
    private MutableLiveData<CartMainModel> removeItemFromCartVM;


    public LiveData<CartMainModel> getCartItemModelVM() {
        if (CartItemModelVM == null) {
            CartItemModelVM = new MutableLiveData<>();
        }
        if (CartItemModelVM.getValue() != null) {
            CartItemModelVM.setValue(null);
        }
        return CartItemModelVM;
    }



    public LiveData<CartMainModel> getRemoveItemFromCartVM() {
        if (removeItemFromCartVM == null)
            removeItemFromCartVM = new MutableLiveData<>();
        if (removeItemFromCartVM.getValue() != null) {
            removeItemFromCartVM.setValue(null);
        }
        return removeItemFromCartVM;
    }



    public MutableLiveData<CartMainModel> getCartItemModelVMRequest(RecyclerView rvCartItem, LinearLayout blankBasket) {
        RestClient.getInstance().getService().CartItems().enqueue(new Callback<CartMainModel>() {
            @Override
            public void onResponse(Call<CartMainModel> call, Response<CartMainModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e(TAG, "request response=" + response.body());
                    CartItemModelVM.setValue(response.body());
                } else {
                    Utils.hideProgressDialog();
                    rvCartItem.setVisibility(View.GONE);
                    blankBasket.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<CartMainModel> call, Throwable t) {
                Log.e(TAG, "onFailure Responce" + t.toString());
                rvCartItem.setVisibility(View.GONE);
                blankBasket.setVisibility(View.VISIBLE);
                Utils.hideProgressDialog();
            }
        });

        return CartItemModelVM;
    }


    public MutableLiveData<CartMainModel> getRemoveItemFromCartVMRequest(int id) {
//        RestClient.getInstance().getService().deleteCartItems(id).enqueue(new Callback<CartMainModel>() {
//            @Override
//            public void onResponse(Call<CartMainModel> call, Response<CartMainModel> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    Log.e(TAG, "request response=" + response.body());
//                    removeItemFromCartVM.setValue(response.body());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<CartMainModel> call, Throwable t) {
//                Log.e(TAG, "onFailure Responce" + t.toString());
//                Utils.hideProgressDialog();
//            }
//        });

        return removeItemFromCartVM;
    }


}