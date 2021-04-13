package com.skdirect.model;

import com.google.gson.annotations.SerializedName;

public class OrderPlaceRequestModel {

    @SerializedName("PaymentMode")
    private String PaymentMode;
    @SerializedName("DeliveryOption")
    private int DeliveryOption;
    @SerializedName("MongoId")
    private String MongoId;
    @SerializedName("UserLocationId")
    private int UserLocationId;

    @SerializedName("MallId")
    private String MallId;

    public OrderPlaceRequestModel(String paymentMode, int deliveryOption, String mongoId, int userLocationId,String mallId) {
        PaymentMode = paymentMode;
        DeliveryOption = deliveryOption;
        MongoId = mongoId;
        UserLocationId = userLocationId;
        MallId= mallId;
    }
}
