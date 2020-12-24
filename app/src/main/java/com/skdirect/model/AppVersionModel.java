package com.skdirect.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AppVersionModel implements Serializable {
    @SerializedName("Version")
    private String Version;

    @SerializedName("SellerUrl")
    private String SellerUrl;

    @SerializedName("BuyerUrl")
    private String BuyerUrl;

    @SerializedName("IsCompulsory")
    private boolean IsCompulsory;

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }

    public String getSellerUrl() {
        return SellerUrl;
    }

    public void setSellerUrl(String sellerUrl) {
        SellerUrl = sellerUrl;
    }

    public String getBuyerUrl() {
        return BuyerUrl;
    }

    public void setBuyerUrl(String buyerUrl) {
        BuyerUrl = buyerUrl;
    }

    public boolean isCompulsory() {
        return IsCompulsory;
    }

    public void setCompulsory(boolean compulsory) {
        IsCompulsory = compulsory;
    }
}
