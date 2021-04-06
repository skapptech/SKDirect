package com.skdirect.interfacee;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.skdirect.model.CartModel;

public interface CartItemInterface {
    void plusButtonOnClick(CartModel sellerProductModel, TextView tvSelectedQty);

    void minusButtonOnClick(CartModel sellerProductModel, TextView selectedQty, LinearLayout LLPlusMinus);

    void removeButtonOnClick(CartModel cartModel, int position);
}