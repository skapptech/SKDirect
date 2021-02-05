package com.skdirect.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CartItemModel implements Serializable {

    @SerializedName("Cart")
    private ArrayList<CartModel> Cart;
    @SerializedName("EncryptSellerId")
    private String EncryptSellerId;
    @SerializedName("SellerId")
    private int SellerId;
    @SerializedName("CreatedBy")
    private int CreatedBy;
    @SerializedName("CreatedDate")
    private String CreatedDate;
    @SerializedName("IsDelete")
    private boolean IsDelete;
    @SerializedName("TotalQuantity")
    private double TotalQuantity;
    @SerializedName("TotalItems")
    private int TotalItems;
    @SerializedName("TotalDiscount")
    private double TotalDiscount;
    @SerializedName("TotalSavingAmount")
    private double TotalSavingAmount;
    @SerializedName("TotalAmount")
    private double TotalAmount;
    @SerializedName("TotalMrp")
    private double TotalMrp;
    @SerializedName("Id")
    private String Id;

    public ArrayList<CartModel> getCart() {
        return Cart;
    }

    public void setCart(ArrayList<CartModel> cart) {
        Cart = cart;
    }

    public String getEncryptSellerId() {
        return EncryptSellerId;
    }

    public void setEncryptSellerId(String encryptSellerId) {
        EncryptSellerId = encryptSellerId;
    }

    public int getSellerId() {
        return SellerId;
    }

    public void setSellerId(int sellerId) {
        SellerId = sellerId;
    }

    public int getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(int createdBy) {
        CreatedBy = createdBy;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public boolean isDelete() {
        return IsDelete;
    }

    public void setDelete(boolean delete) {
        IsDelete = delete;
    }

    public double getTotalQuantity() {
        return TotalQuantity;
    }

    public void setTotalQuantity(double totalQuantity) {
        TotalQuantity = totalQuantity;
    }

    public int getTotalItems() {
        return TotalItems;
    }

    public void setTotalItems(int totalItems) {
        TotalItems = totalItems;
    }

    public double getTotalDiscount() {
        return TotalDiscount;
    }

    public void setTotalDiscount(double totalDiscount) {
        TotalDiscount = totalDiscount;
    }

    public double getTotalSavingAmount() {
        return TotalSavingAmount;
    }

    public void setTotalSavingAmount(double totalSavingAmount) {
        TotalSavingAmount = totalSavingAmount;
    }

    public double getTotalAmount() {
        return TotalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        TotalAmount = totalAmount;
    }

    public double getTotalMrp() {
        return TotalMrp;
    }

    public void setTotalMrp(double totalMrp) {
        TotalMrp = totalMrp;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public static class CartModel implements Serializable {
        @SerializedName("ShoppingCartVariantAtrribute")
        private ArrayList<String> ShoppingCartVariantAtrribute;

        @SerializedName("TotalMrp")
        private double TotalMrp;

        @SerializedName("ShopName")
        private String ShopName;

        @SerializedName("IsActive")
        private boolean IsActive;

        @SerializedName("IsStockRequired")
        private boolean IsStockRequired;

        @SerializedName("Stock")
        private int Stock;

        @SerializedName("Measurement")
        private int Measurement;

        @SerializedName("Uom")
        private String Uom;

        @SerializedName("ImagePath")
        private String ImagePath;

        @SerializedName("TotalDiscountAmount")
        private int TotalDiscountAmount;

        @SerializedName("ProductName")
        private String ProductName;

        @SerializedName("TotalDiscount")
        private int TotalDiscount;

        @SerializedName("TotalSaving")
        private double TotalSaving;

        @SerializedName("IsDelete")
        private boolean IsDelete;

        @SerializedName("OffPercentage")
        private double OffPercentage;

        @SerializedName("TotalPrice")
        private double TotalPrice;

        @SerializedName("price")
        private double price;

        @SerializedName("Quantity")
        private int Quantity;

        @SerializedName("CreatedBy")
        private int CreatedBy;

        @SerializedName("CreatedDate")
        private String CreatedDate;

        @SerializedName("SellerId")
        private int SellerId;

        @SerializedName("BuyerId")
        private int BuyerId;

        @SerializedName("ProductMasterId")
        private int ProductMasterId;

        @SerializedName("Margin")
        private double Margin;

        @SerializedName("Mrp")
        private double Mrp;

        @SerializedName("MOQ")
        private int MOQ;

        @SerializedName("Id")
        private int Id;

        public CartModel(ArrayList<String> shoppingCartVariantAtrribute, double totalMrp, String shopName, boolean isActive, boolean isStockRequired, int stock, int measurement, String uom, String imagePath, int totalDiscountAmount, String productName, int totalDiscount, int totalSaving, boolean isDelete, double offPercentage, int totalPrice, int price, int quantity, int createdBy, String createdDate, int sellerId, int buyerId, int productMasterId, double margin, double mrp, int MOQ, int id) {
            ShoppingCartVariantAtrribute = shoppingCartVariantAtrribute;
            TotalMrp = totalMrp;
            ShopName = shopName;
            IsActive = isActive;
            IsStockRequired = isStockRequired;
            Stock = stock;
            Measurement = measurement;
            Uom = uom;
            ImagePath = imagePath;
            TotalDiscountAmount = totalDiscountAmount;
            ProductName = productName;
            TotalDiscount = totalDiscount;
            TotalSaving = totalSaving;
            IsDelete = isDelete;
            OffPercentage = offPercentage;
            TotalPrice = totalPrice;
            this.price = price;
            Quantity = quantity;
            CreatedBy = createdBy;
            CreatedDate = createdDate;
            SellerId = sellerId;
            BuyerId = buyerId;
            ProductMasterId = productMasterId;
            Margin = margin;
            Mrp = mrp;
            this.MOQ = MOQ;
            Id = id;
        }



        public ArrayList<String> getShoppingCartVariantAtrribute() {
            return ShoppingCartVariantAtrribute;
        }

        public void setShoppingCartVariantAtrribute(ArrayList<String> shoppingCartVariantAtrribute) {
            ShoppingCartVariantAtrribute = shoppingCartVariantAtrribute;
        }

        public double getTotalMrp() {
            return TotalMrp;
        }

        public void setTotalMrp(int totalMrp) {
            TotalMrp = totalMrp;
        }

        public String getShopName() {
            return ShopName;
        }

        public void setShopName(String shopName) {
            ShopName = shopName;
        }

        public boolean isActive() {
            return IsActive;
        }

        public void setActive(boolean active) {
            IsActive = active;
        }

        public boolean isStockRequired() {
            return IsStockRequired;
        }

        public void setStockRequired(boolean stockRequired) {
            IsStockRequired = stockRequired;
        }

        public int getStock() {
            return Stock;
        }

        public void setStock(int stock) {
            Stock = stock;
        }

        public int getMeasurement() {
            return Measurement;
        }

        public void setMeasurement(int measurement) {
            Measurement = measurement;
        }

        public String getUom() {
            return Uom;
        }

        public void setUom(String uom) {
            Uom = uom;
        }

        public String getImagePath() {
            return ImagePath;
        }

        public void setImagePath(String imagePath) {
            ImagePath = imagePath;
        }

        public int getTotalDiscountAmount() {
            return TotalDiscountAmount;
        }

        public void setTotalDiscountAmount(int totalDiscountAmount) {
            TotalDiscountAmount = totalDiscountAmount;
        }

        public String getProductName() {
            return ProductName;
        }

        public void setProductName(String productName) {
            ProductName = productName;
        }

        public int getTotalDiscount() {
            return TotalDiscount;
        }

        public void setTotalDiscount(int totalDiscount) {
            TotalDiscount = totalDiscount;
        }

        public double getTotalSaving() {
            return TotalSaving;
        }

        public void setTotalSaving(double totalSaving) {
            TotalSaving = totalSaving;
        }

        public boolean isDelete() {
            return IsDelete;
        }

        public void setDelete(boolean delete) {
            IsDelete = delete;
        }

        public double getOffPercentage() {
            return OffPercentage;
        }

        public void setOffPercentage(double offPercentage) {
            OffPercentage = offPercentage;
        }

        public double getTotalPrice() {
            return TotalPrice;
        }

        public void setTotalPrice(double totalPrice) {
            TotalPrice = totalPrice;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public int getQuantity() {
            return Quantity;
        }

        public void setQuantity(int quantity) {
            Quantity = quantity;
        }

        public int getCreatedBy() {
            return CreatedBy;
        }

        public void setCreatedBy(int createdBy) {
            CreatedBy = createdBy;
        }

        public String getCreatedDate() {
            return CreatedDate;
        }

        public void setCreatedDate(String createdDate) {
            CreatedDate = createdDate;
        }

        public int getSellerId() {
            return SellerId;
        }

        public void setSellerId(int sellerId) {
            SellerId = sellerId;
        }

        public int getBuyerId() {
            return BuyerId;
        }

        public void setBuyerId(int buyerId) {
            BuyerId = buyerId;
        }

        public int getProductMasterId() {
            return ProductMasterId;
        }

        public void setProductMasterId(int productMasterId) {
            ProductMasterId = productMasterId;
        }

        public double getMargin() {
            return Margin;
        }

        public void setMargin(int margin) {
            Margin = margin;
        }

        public double getMrp() {
            return Mrp;
        }

        public void setMrp(int mrp) {
            Mrp = mrp;
        }

        public int getMOQ() {
            return MOQ;
        }

        public void setMOQ(int MOQ) {
            this.MOQ = MOQ;
        }

        public int getId() {
            return Id;
        }

        public void setId(int id) {
            Id = id;
        }
    }
}
