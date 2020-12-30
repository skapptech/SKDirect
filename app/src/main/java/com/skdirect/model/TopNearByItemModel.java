package com.skdirect.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TopNearByItemModel implements Serializable {

    @SerializedName("Id")
    private int Id;

    @SerializedName("ProductName")
    private String ProductName;

    @SerializedName("SellingPrice")
    private Double SellingPrice;

    @SerializedName("Mrp")
    private Double Mrp;

    @SerializedName("ImagePath")
    private String ImagePath;

    @SerializedName("SellerName")
    private String SellerName;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public Double getSellingPrice() {
        return SellingPrice;
    }

    public void setSellingPrice(Double sellingPrice) {
        SellingPrice = sellingPrice;
    }

    public Double getMrp() {
        return Mrp;
    }

    public void setMrp(Double mrp) {
        Mrp = mrp;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    public String getSellerName() {
        return SellerName;
    }

    public void setSellerName(String sellerName) {
        SellerName = sellerName;
    }
}
