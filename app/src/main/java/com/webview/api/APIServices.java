package com.webview.api;

import com.google.gson.JsonObject;
import com.webview.model.AppVersionModel;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface APIServices {

    @GET("api/App/GetAppInfo")
    Observable<AppVersionModel> getAppversion();

}