package com.skdirect.model;

import com.google.gson.annotations.SerializedName;

public class MyOrderRequestModel {


    @SerializedName("Filter")
    private String Filter;
    @SerializedName("SellerId")
    private int SellerId;
    @SerializedName("Take")
    private int Take;
    @SerializedName("Skip")
    private int Skip;
    @SerializedName("Status")
    private String Status;
    @SerializedName("Id")
    private int Id;

    public MyOrderRequestModel(String filter, int sellerId, int take, int skip, String status, int id) {
        Filter = filter;
        SellerId = sellerId;
        Take = take;
        Skip = skip;
        Status = status;
        Id = id;
    }
}
