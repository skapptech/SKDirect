package com.skdirect.model;

import com.google.gson.annotations.SerializedName;

public class PaginationModel {

    @SerializedName("SellerId")
    private int SellerId;

    @SerializedName("ParentProductId")
    private int ParentProductId;

    @SerializedName("Id")
    private int Id;

    @SerializedName("Latitude")
    private double Latitude;

    @SerializedName("Longitude")
    private double Longitude;

    @SerializedName("TagId")
    private int TagId;

    @SerializedName("Keyword")
    private String  Keyword;

    @SerializedName("Skip")
    private int Skip;

    @SerializedName("Take")
    private int Take;

    @SerializedName("ParentCategoryId")
    private int  ParentCategoryId;

    @SerializedName("CategoryName")
    private String  CategoryName;

    @SerializedName("CateogryId")
    private int  CateogryId;

    @SerializedName("IsParentCategory")
    private boolean  IsParentCategory;

    @SerializedName("ProductId")
    private int  ProductId;

    @SerializedName("BrandId")
    private int  BrandId;


    @SerializedName("ProductName")
    private String  ProductName;


    public PaginationModel(int productId) {
        ProductId = productId;
    }



    public PaginationModel(int skip, int take, int parentCategoryId, String categoryName, boolean isParentCategory) {
        Skip = skip;
        Take = take;
        ParentCategoryId = parentCategoryId;
        CategoryName = categoryName;
        IsParentCategory = isParentCategory;
    }

    public PaginationModel(int sellerId, int cateogryId, int brandId,String productName, int skip, int take, int parentProductId, String keyword) {
        SellerId = sellerId;
        CateogryId = cateogryId;
        BrandId = brandId;
        ProductName = productName;
        Skip =skip;
        Take = take;
        ParentProductId = parentProductId;
        Keyword = keyword;

    }

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
