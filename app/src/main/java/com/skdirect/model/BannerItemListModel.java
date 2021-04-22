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
