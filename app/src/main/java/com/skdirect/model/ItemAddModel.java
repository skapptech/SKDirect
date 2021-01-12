package com.skdirect.model;

import com.google.gson.annotations.SerializedName;

public class ItemAddModel {

    @SerializedName("Quantity")
    private int Quantity;

    @SerializedName("CookieValue")
    private String CookieValue;

    @SerializedName("Id")
    private int Id;

    @SerializedName("ProductVariantAttributeId")
    private int ProductVariantAttributeId;

    @SerializedName("ProductVariantId")
    private int ProductVariantId;

    public ItemAddModel(int quantity, String cookieValue, int id, int productVariantAttributeId, int productVariantId) {
        Quantity = quantity;
        CookieValue = cookieValue;
        Id = id;
        ProductVariantAttributeId = productVariantAttributeId;
        ProductVariantId = productVariantId;
    }


}
