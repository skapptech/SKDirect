package com.skdirect.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class BannerModel implements Serializable {
    @Expose
    @SerializedName("BannerItemList")
    private ArrayList<BannerItemListModel> bannerItemListModel;

    public ArrayList<BannerItemListModel> getBannerItemListModel() {
        return bannerItemListModel;
    }

    public void setBannerItemListModel(ArrayList<BannerItemListModel> bannerItemListModel) {
        this.bannerItemListModel = bannerItemListModel;
    }
}
