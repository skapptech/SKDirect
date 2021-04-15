package com.skdirect.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public  class AppVersionModel implements Serializable {


    @Expose
    @SerializedName("ErrorMessage")
    private String ErrorMessage;
    @Expose
    @SerializedName("SuccessMessage")
    private String SuccessMessage;
    @Expose
    @SerializedName("IsSuccess")
    private boolean IsSuccess;
    @Expose
    @SerializedName("ResultItem")
    private ResultItemEntity ResultItem;

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public String getSuccessMessage() {
        return SuccessMessage;
    }

    public boolean isSuccess() {
        return IsSuccess;
    }

    public ResultItemEntity getResultItem() {
        return ResultItem;
    }
    public  class ResultItemEntity {
        @Expose
        @SerializedName("IsCompulsory")
        private boolean IsCompulsory;
        @Expose
        @SerializedName("BuyerUrl")
        private String BuyerUrl;
        @Expose
        @SerializedName("SellerUrl")
        private String SellerUrl;
        @Expose
        @SerializedName("Version")
        private String Version;

       /* @Expose
        @SerializedName("BuyerUrl")
        private String BuyerUrl;
        @Expose
        @SerializedName("SellerUrl")
        private String SellerUrl;
        @Expose
        @SerializedName("Version")
        private String Version;*/

        public boolean isCompulsory() {
            return IsCompulsory;
        }

        public String getBuyerUrl() {
            return BuyerUrl;
        }

        public String getSellerUrl() {
            return SellerUrl;
        }

        public String getVersion() {
            return Version;
        }

    }
}
