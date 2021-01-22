package com.skdirect.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.skdirect.model.AppVersionModel;
import com.skdirect.model.ContactUploadModel;
import com.skdirect.model.UpdateTokenModel;

import java.util.ArrayList;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIServices {

    @GET("api/App/GetAppInfo")
    Observable<AppVersionModel> getAppversion();

    @POST("api/Notification/UpdateFcmId")
    Observable<JsonObject> getUpdateToken(@Body UpdateTokenModel updateTokenModel, @Header("authorization") String token);

    @POST("api/LoginUser/PostContactList")
    Observable<JsonElement> uploadContacts(@Body ArrayList<ContactUploadModel> contacts, @Header("authorization") String token);

    @Multipart
    @POST("Image/PostImage")
    Observable<String> imageUpload(@Part MultipartBody.Part body);
}