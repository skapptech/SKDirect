package com.skdirect.model;

import com.google.gson.annotations.SerializedName;

public class PaginationModel {

    @SerializedName("SellerId")
    private int SellerId;

    @SerializedName("ParentProductId")
    private int ParentProductId;

    @SerializedName("Id")
    private int Id;

    @SerializedName("Skip")
    private int Skip;

    @SerializedName("Take")
    private int Take;

    @SerializedName("Latitude")
    private double Latitude;

    @SerializedName("Longitude")
    private double Longitude;

    @SerializedName("TagId")
    private int TagId;

    @SerializedName("Keyword")
    private String  Keyword;

    public PaginationModel(int sellerId, int parentProductId, int id, int skip, int take, double latitude, double longitude, int tagId, String keyword) {
        SellerId = sellerId;
        ParentProductId = parentProductId;
        Id = id;
        Skip = skip;
        Take = take;
        Latitude = latitude;
        Longitude = longitude;
        TagId = tagId;
        Keyword = keyword;
    }
}
