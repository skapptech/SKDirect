package com.skdirect.model;

import com.google.gson.annotations.SerializedName;

public  class ChangePasswordRequestModel {

    @SerializedName("ConfirmPassword")
    private String ConfirmPassword;
    @SerializedName("Password")
    private String Password;

    public ChangePasswordRequestModel(String confirmPassword, String password) {
        ConfirmPassword = confirmPassword;
        Password = password;
    }
}
