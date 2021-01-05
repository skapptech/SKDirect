package com.skdirect.model;

import com.google.gson.annotations.SerializedName;

public class ContactUploadModel {
    @SerializedName("contact")
    private final String contact;
    @SerializedName("contactName")
    private final String contactName;

    public ContactUploadModel(String contactName,String contact) {
        this.contact = contact;
        this.contactName = contactName;
    }

    public String getContact() {
        return contact;
    }
}