package com.skdirect.model.response;

import com.google.gson.annotations.SerializedName;
import com.skdirect.model.InvoiceModel;
import com.skdirect.model.MallResultItemModel;

import java.util.ArrayList;

public class InvoiceMainModel {

    @SerializedName("IsSuccess")
    private boolean IsSuccess;

    @SerializedName("ResultItem")
    ArrayList<InvoiceModel>ResultItem;

    @SerializedName("SuccessMessage")
    private String SuccessMessage;

    @SerializedName("ErrorMessage")
    private String ErrorMessage;

    public boolean isSuccess() {
        return IsSuccess;
    }

    public void setSuccess(boolean success) {
        IsSuccess = success;
    }

    public ArrayList<InvoiceModel> getResultItem() {
        return ResultItem;
    }

    public void setResultItem(ArrayList<InvoiceModel> resultItem) {
        ResultItem = resultItem;
    }

    public String getSuccessMessage() {
        return SuccessMessage;
    }

    public void setSuccessMessage(String successMessage) {
        SuccessMessage = successMessage;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }
}
