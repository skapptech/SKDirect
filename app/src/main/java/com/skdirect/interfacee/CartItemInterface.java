package com.skdirect.interfacee;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.skdirect.model.CartItemModel;

public interface CartItemInterface {
    void plusButtonOnClick(CartItemModel.CartModel sellerProductModel, TextView tvSelectedQty);
    void minusButtonOnClick( CartItemModel.CartModel sellerProductModel, TextView selectedQty, LinearLayout LLPlusMinus);
    void removeButtonOnClick(CartItemModel.CartModel cartModel, int position);




}
