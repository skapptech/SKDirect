package com.skdirect.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class InvoiceModel implements Serializable {

    @Expose
    @SerializedName("DeliveryOption")
    private String deliveryoption;
    @Expose
    @SerializedName("BuyerContactNo")
    private String buyercontactno;
    @Expose
    @SerializedName("BuyerName")
    private String buyername;
    @Expose
    @SerializedName("PaymentMode")
    private String paymentmode;
    @Expose
    @SerializedName("ShopName")
    private String shopname;
    @Expose
    @SerializedName("SellerName")
    private String sellername;
    @Expose
    @SerializedName("TotalSavingAmount")
    private double totalsavingamount;
    @Expose
    @SerializedName("TotalDeliveryCharges")
    private double totaldeliverycharges;
    @Expose
    @SerializedName("TotalDiscountAmount")
    private double totaldiscountamount;
    @Expose
    @SerializedName("TotalPrice")
    private double totalprice;
    @Expose
    @SerializedName("TotalItemAmount")
    private double totalitemamount;
    @Expose
    @SerializedName("InvoiceNumber")
    private String invoicenumber;
    @Expose
    @SerializedName("InvoiceId")
    private int invoiceid;

    @Expose
    @SerializedName("TotalPayableAmount")
    private double TotalPayableAmount;

    @Expose
    @SerializedName("InvoiceDate")
    private String InvoiceDate;

    public double getTotalPayableAmount() {
        return TotalPayableAmount;
    }

    public void setTotalPayableAmount(double totalPayableAmount) {
        TotalPayableAmount = totalPayableAmount;
    }

    public String getInvoiceDate() {
        return InvoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        InvoiceDate = invoiceDate;
    }

    public String getDeliveryoption() {
        return deliveryoption;
    }

    public void setDeliveryoption(String deliveryoption) {
        this.deliveryoption = deliveryoption;
    }

    public String getBuyercontactno() {
        return buyercontactno;
    }

    public void setBuyercontactno(String buyercontactno) {
        this.buyercontactno = buyercontactno;
    }

    public String getBuyername() {
        return buyername;
    }

    public void setBuyername(String buyername) {
        this.buyername = buyername;
    }

    public String getPaymentmode() {
        return paymentmode;
    }

    public void setPaymentmode(String paymentmode) {
        this.paymentmode = paymentmode;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getSellername() {
        return sellername;
    }

    public void setSellername(String sellername) {
        this.sellername = sellername;
    }

    public double getTotalsavingamount() {
        return totalsavingamount;
    }

    public void setTotalsavingamount(double totalsavingamount) {
        this.totalsavingamount = totalsavingamount;
    }

    public double getTotaldeliverycharges() {
        return totaldeliverycharges;
    }

    public void setTotaldeliverycharges(double totaldeliverycharges) {
        this.totaldeliverycharges = totaldeliverycharges;
    }

    public double getTotaldiscountamount() {
        return totaldiscountamount;
    }

    public void setTotaldiscountamount(double totaldiscountamount) {
        this.totaldiscountamount = totaldiscountamount;
    }

    public double getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(double totalprice) {
        this.totalprice = totalprice;
    }

    public double getTotalitemamount() {
        return totalitemamount;
    }

    public void setTotalitemamount(double totalitemamount) {
        this.totalitemamount = totalitemamount;
    }

    public String getInvoicenumber() {
        return invoicenumber;
    }

    public void setInvoicenumber(String invoicenumber) {
        this.invoicenumber = invoicenumber;
    }

    public int getInvoiceid() {
        return invoiceid;
    }

    public void setInvoiceid(int invoiceid) {
        this.invoiceid = invoiceid;
    }
}
