package com.skdirect.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SellerProductModel {

    @SerializedName("SellerProductDC")
   private ArrayList<SellerProductList> sellerProductLists;

    public ArrayList<SellerProductList> getSellerProductLists() {
        return sellerProductLists;
    }

    public void setSellerProductLists(ArrayList<SellerProductList> sellerProductLists) {
        this.sellerProductLists = sellerProductLists;
    }


}
