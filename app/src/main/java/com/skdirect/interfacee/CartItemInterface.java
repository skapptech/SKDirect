package com.skdirect.interfacee;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.skdirect.model.CartModel;

public interface CartItemInterface {
    void plusButtonOnClick(CartModel sellerProductModel);

    void minusButtonOnClick(CartModel sellerProductModel, LinearLayout LLPlusMinus);

    void removeButtonOnClick(CartModel cartModel, int position);
}