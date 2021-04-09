package com.skdirect.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FilterCategoryDetails {

    @SerializedName("value")
    private int value;

    @SerializedName("label")
    private String label;

    public int getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }
}
