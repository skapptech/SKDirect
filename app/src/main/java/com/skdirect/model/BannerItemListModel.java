package com.skdirect.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BannerItemListModel implements Serializable {


    @Expose
    @SerializedName("ImagePath")
    private String imagepath;

    @Expose
    @SerializedName("RedirectUrl")
    private String RedirectUrl;

    @Expose
    @SerializedName("Type")
    private String Type;

    @Expose
    @SerializedName("GivenId")
    private int GivenId;

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public int getGivenId() {
        return GivenId;
    }

    public void setGivenId(int givenId) {
        GivenId = givenId;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public String getRedirectUrl() {
        return RedirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        RedirectUrl = redirectUrl;
    }
}
