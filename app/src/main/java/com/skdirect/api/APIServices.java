package com.skdirect.api;

import com.google.gson.JsonObject;
import com.skdirect.model.AppVersionModel;
import com.skdirect.model.LoginResponseModel;
import com.skdirect.model.LoginWithPasswordModel;
import com.skdirect.model.OtpResponceModel;
import com.skdirect.model.OtpVerificationModel;
import com.skdirect.model.UpdateTokenModel;

import org.json.JSONObject;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIServices {

    @GET("api/App/GetAppInfo")
    Call<AppVersionModel> getAppversion();

    @POST("api/Notification/UpdateFcmId")
    Call<JsonObject> getUpdateToken(@Body UpdateTokenModel updateTokenModel, @Header("authorization") String token);

    @GET("api/Buyer/Registration/GenerateOtp/{GenerateOtp}")
    Call<LoginResponseModel> GenerateOtp(@Path("GenerateOtp") String GenerateOtp);

    @POST("api/Buyer/Registration/VerfiyOtp")
    Call<OtpResponceModel> getVerfiyOtp(@Body OtpVerificationModel OtpVerificationModel);

    @FormUrlEncoded
    @POST("/token")
    Observable<LoginWithPasswordModel> getToken(@Field("grant_type") String grant_type, @Field("username") String username, @Field("password") String password, @Field("ISOTP") boolean isOTp, @Field("ISBUYER") boolean isBuyer, @Field("LOGINTYPE") String LOGINTYPE);

}