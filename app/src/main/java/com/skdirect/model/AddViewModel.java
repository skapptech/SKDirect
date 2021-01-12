package com.skdirect.model;

import com.google.gson.annotations.SerializedName;

public class AddViewModel {

    @SerializedName("SellerId")
    private String  SellerId;

    public AddViewModel(String productId) {
        SellerId = productId;
    }
}
