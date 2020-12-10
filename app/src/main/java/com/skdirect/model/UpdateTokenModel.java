package com.skdirect.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UpdateTokenModel implements Serializable {
    @SerializedName("FcmId")
    private String FcmId;

    @SerializedName("UserId")
    private long UserId;

    public UpdateTokenModel(String fcmId, long userId) {
        FcmId = fcmId;
        UserId = userId;
    }

    public String getFcmId() {
        return FcmId;
    }

    public void setFcmId(String fcmId) {
        FcmId = fcmId;
    }

    public long getUserId() {
        return UserId;
    }

    public void setUserId(long userId) {
        UserId = userId;
    }
}
