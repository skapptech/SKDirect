package com.skdirect.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class VerifyPaymentModel {

    private String razorpay_payment_id;
    private String razorpay_order_id;
    private String razorpay_signature;
    private String status;

    public VerifyPaymentModel(String razorpay_payment_id, String razorpay_order_id, String razorpay_signature, String status) {
        this.razorpay_payment_id = razorpay_payment_id;
        this.razorpay_order_id = razorpay_order_id;
        this.razorpay_signature = razorpay_signature;
        this.status = status;
    }
}