package com.skdirect.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GenerateOtpModel {
    @Expose
    @SerializedName("MobileNumber")
    private String MobileNumber;

    @Expose
    @SerializedName("DeviceId")
    private String DeviceId;

    public GenerateOtpModel(String mobileNumber, String deviceId) {
      this.  MobileNumber = mobileNumber;
        this.  DeviceId = deviceId;
    }
}
