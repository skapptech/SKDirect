package com.skdirect.api;

import com.google.gson.JsonObject;
import com.skdirect.model.AppVersionModel;
import com.skdirect.model.UpdateTokenModel;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface APIServices {

    @GET("api/App/GetAppInfo")
    Observable<AppVersionModel> getAppversion();

    @POST("api/Notification/UpdateFcmId")
    Observable<JsonObject> getUpdateToken(@Body UpdateTokenModel updateTokenModel, @Header("authorization") String token);

}