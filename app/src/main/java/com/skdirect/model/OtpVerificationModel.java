package com.skdirect.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OtpVerificationModel {
    @Expose
    @SerializedName("MobileNumber")
    private String MobileNumber;

    @Expose
    @SerializedName("Otp")
    private String Otp;

    public OtpVerificationModel(String mobileNumber, String otp) {
        MobileNumber = mobileNumber;
        Otp = otp;
    }

    public String getMobileNumber() {
        return MobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        MobileNumber = mobileNumber;
    }

    public String getOtp() {
        return Otp;
    }

    public void setOtp(String otp) {
        Otp = otp;
    }
}
