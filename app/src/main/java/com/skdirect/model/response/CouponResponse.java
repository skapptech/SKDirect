package com.skdirect.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CouponResponse {
    @SerializedName("ResultItem")
    private ArrayList<Coupon> ResultItem;
    @SerializedName("IsSuccess")
    private boolean isSuccess;

    public ArrayList<Coupon> getResultItem() {
        return ResultItem;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public static class Coupon {
        @SerializedName("Id")
        private int id;
        @SerializedName("CouponCode")
        private String couponCode;
        @SerializedName("CouponName")
        private String couponName;
        @SerializedName("MaxAmount")
        private double maxAmount;
        @SerializedName("Amount")
        private double amount;
        @SerializedName("AmountPercentage")
        private double amountPercentage;
        @SerializedName("MinOrderValue")
        private int minOrderValue;
        @SerializedName("NoOfTime")
        private int noOfTime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCouponCode() {
            return couponCode;
        }

        public void setCouponCode(String couponCode) {
            this.couponCode = couponCode;
        }

        public String getCouponName() {
            return couponName;
        }

        public void setCouponName(String couponName) {
            this.couponName = couponName;
        }

        public double getMaxAmount() {
            return maxAmount;
        }

        public void setMaxAmount(double maxAmount) {
            this.maxAmount = maxAmount;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public double getAmountPercentage() {
            return amountPercentage;
        }

        public void setAmountPercentage(double amountPercentage) {
            this.amountPercentage = amountPercentage;
        }

        public int getMinOrderValue() {
            return minOrderValue;
        }

        public void setMinOrderValue(int minOrderValue) {
            this.minOrderValue = minOrderValue;
        }

        public int getNoOfTime() {
            return noOfTime;
        }

        public void setNoOfTime(int noOfTime) {
            this.noOfTime = noOfTime;
        }
    }
}