package com.skdirect.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderPlaceMainModel {

    @SerializedName("IsSuccess")
    private boolean IsSuccess;

    @SerializedName("ResultItem")
    private ResultItemEntity ResultItem;

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

    public ResultItemEntity getResultItem() {
        return ResultItem;
    }

    public void setResultItem(ResultItemEntity resultItem) {
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

    public class ResultItemEntity {
        @Expose
        @SerializedName("BuyerId")
        private int BuyerId;
        @Expose
        @SerializedName("SellerId")
        private int SellerId;
        @Expose
        @SerializedName("AmountInPaisa")
        private float AmountInPaisa;
        @Expose
        @SerializedName("RazorpayOrderId")
        private String RazorpayOrderId;
        @Expose
        @SerializedName("GivenMobile")
        private String GivenMobile;
        @Expose
        @SerializedName("GivenEmail")
        private String GivenEmail;
        @Expose
        @SerializedName("Id")
        private String Id;

        public int getBuyerId() {
            return BuyerId;
        }

        public void setBuyerId(int buyerId) {
            BuyerId = buyerId;
        }

        public int getSellerId() {
            return SellerId;
        }

        public void setSellerId(int sellerId) {
            SellerId = sellerId;
        }

        public float getAmountInPaisa() {
            return AmountInPaisa;
        }

        public void setAmountInPaisa(float amountInPaisa) {
            AmountInPaisa = amountInPaisa;
        }

        public String getRazorpayOrderId() {
            return RazorpayOrderId;
        }

        public void setRazorpayOrderId(String razorpayOrderId) {
            RazorpayOrderId = razorpayOrderId;
        }

        public String getGivenMobile() {
            return GivenMobile;
        }

        public void setGivenMobile(String givenMobile) {
            GivenMobile = givenMobile;
        }

        public String getGivenEmail() {
            return GivenEmail;
        }

        public void setGivenEmail(String givenEmail) {
            GivenEmail = givenEmail;
        }

        public String getId() {
            return Id;
        }

        public void setId(String id) {
            Id = id;
        }
    }
}
