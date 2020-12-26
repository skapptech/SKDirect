package com.skdirect.model;

import com.google.gson.annotations.SerializedName;

public class SearchRequestModel {

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

    @SerializedName("ProductName")
    private String ProductName;

    @SerializedName("CateogryName")
    private String CateogryName;

    @SerializedName("BrandName")
    private String BrandName;

    @SerializedName("TagId")
    private int TagId;

    @SerializedName("Keyword")
    private String Keyword;

    public SearchRequestModel(int sellerId, int parentProductId, int id, int skip, int take, double latitude, double longitude, String productName, String cateogryName, String brandName, int tagId, String keyword) {
        SellerId = sellerId;
        ParentProductId = parentProductId;
        Id = id;
        Skip = skip;
        Take = take;
        Latitude = latitude;
        Longitude = longitude;
        ProductName = productName;
        CateogryName = cateogryName;
        BrandName = brandName;
        TagId = tagId;
        Keyword = keyword;
    }
}
