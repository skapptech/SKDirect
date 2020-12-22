package com.skdirect.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OtpResponceModel {
    @Expose
    @SerializedName("Userid")
    private String Userid;

    @Expose
    @SerializedName("IsUserExist")
    private Boolean IsUserExist;

    @Expose
    @SerializedName("AspNetUserId")
    private String AspNetUserId;

    public String getUserid() {
        return Userid;
    }

    public void setUserid(String userid) {
        Userid = userid;
    }

    public Boolean getIsUserExist() {
        return IsUserExist;
    }

    public void setIsUserExist(Boolean isUserExist) {
        IsUserExist = isUserExist;
    }

    public String getAspNetUserId() {
        return AspNetUserId;
    }

    public void setAspNetUserId(String aspNetUserId) {
        AspNetUserId = aspNetUserId;
    }
}
