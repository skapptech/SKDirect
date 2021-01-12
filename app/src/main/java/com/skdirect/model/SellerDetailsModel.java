package com.skdirect.model;

import com.google.gson.annotations.SerializedName;

public class SellerDetailsModel {

    @SerializedName("SellerMobileNumber")
    private String SellerMobileNumber;
    @SerializedName("Distance")
    private double Distance;
    @SerializedName("Rating")
    private double Rating;
    @SerializedName("Longitude")
    private double Longitude;
    @SerializedName("Latitiute")
    private double Latitiute;
    @SerializedName("ShopName")
    private String ShopName;
    @SerializedName("FirstName")
    private String FirstName;

    public String getSellerMobileNumber() {
        return SellerMobileNumber;
    }

    public void setSellerMobileNumber(String sellerMobileNumber) {
        SellerMobileNumber = sellerMobileNumber;
    }

    public double getDistance() {
        return Distance;
    }

    public void setDistance(double distance) {
        Distance = distance;
    }

    public double getRating() {
        return Rating;
    }

    public void setRating(double rating) {
        Rating = rating;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getLatitiute() {
        return Latitiute;
    }

    public void setLatitiute(double latitiute) {
        Latitiute = latitiute;
    }

    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        ShopName = shopName;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }
}
