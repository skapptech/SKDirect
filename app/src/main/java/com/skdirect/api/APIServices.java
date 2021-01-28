package com.skdirect.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.skdirect.model.AddViewModel;
import com.skdirect.model.AllCategoriesModel;
import com.skdirect.model.AppVersionModel;
import com.skdirect.model.AddCartItemModel;
import com.skdirect.model.CartItemModel;
import com.skdirect.model.CustomerDataModel;
import com.skdirect.model.DeliveryOptionModel;
import com.skdirect.model.ItemAddModel;
import com.skdirect.model.LoginResponseModel;
import com.skdirect.model.LoginWithPasswordModel;
import com.skdirect.model.NearBySallerModel;
import com.skdirect.model.NearProductListModel;
import com.skdirect.model.OrderPlaceModel;
import com.skdirect.model.OrderPlaceRequestModel;
import com.skdirect.model.OtpResponceModel;
import com.skdirect.model.OtpVerificationModel;
import com.skdirect.model.PaginationModel;
import com.skdirect.model.ProductDataModel;
import com.skdirect.model.RemoveItemRequestModel;
import com.skdirect.model.SearchDataModel;
import com.skdirect.model.SearchRequestModel;
import com.skdirect.model.SellerDetailsModel;
import com.skdirect.model.SellerProductModel;
import com.skdirect.model.SellerProfileDataModel;
import com.skdirect.model.TopNearByItemModel;
import com.skdirect.model.TopSellerModel;
import com.skdirect.model.UpdateTokenModel;
import com.skdirect.model.UserLocationModel;

import org.json.JSONArray;

import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIServices {

    @GET("api/App/GetAppInfo")
    Call<AppVersionModel> getAppversion();

    @POST("api/Notification/UpdateFcmId")
    Call<JsonObject> getUpdateToken(@Body UpdateTokenModel updateTokenModel, @Header("authorization") String token);

    @GET("api/Buyer/Registration/GenerateOtp/{GenerateOtp}")
    Call<LoginResponseModel> GenerateOtp(@Path("GenerateOtp") String GenerateOtp);

    @POST("api/Buyer/Registration/VerfiyOtp")
    Observable<OtpResponceModel> getVerfiyOtp(@Body OtpVerificationModel OtpVerificationModel);

    @FormUrlEncoded
    @POST("/token")
    Observable<LoginWithPasswordModel> getToken(@Field("grant_type") String grant_type, @Field("username") String username, @Field("password") String password, @Field("ISOTP") boolean isOTp, @Field("ISBUYER") boolean isBuyer, @Field("LOGINTYPE") String LOGINTYPE);

    @GET("api/buyer/Profile/GetUserDetail")
    Call<CustomerDataModel> GetUserDetail();

    @GET("api/buyer/SkAppHome/GetTopNearByItem")
    Call<ArrayList<TopNearByItemModel>> GetTopNearByItem();


    @GET("api/buyer/SkAppHome/GetTopSeller")
    Call<ArrayList<TopSellerModel>> GetTopSeller();

    @GET("api/buyer/SkAppHome/GetTopCategory")
    Call<ArrayList<AllCategoriesModel>> GetTopCategory();

    @POST("api/Buyer/Item/GetItem")
    Call<ArrayList<NearProductListModel>> getNearItem(@Body PaginationModel paginationModel);

    @GET("api/buyer/Seller/GetSellerListForBuyer")
    Call<ArrayList<NearBySallerModel>> GetSellerListForBuyer(@Query("Skip") int Skip, @Query("Take") int password, @Query("Keyword") String Keyword);


    @POST("api/Buyer/cateogry/GetCategorybyfilter")
    Call<ArrayList<AllCategoriesModel>> GetCategorybyfilter(@Body PaginationModel paginationModel);

    @POST("api/Buyer/Item/GetSellerListWithItem")
    Call<SearchDataModel> GetSellerListWithItem(@Body SearchRequestModel paginationModel);


    @GET("api/buyer/Seller/GetTopSeller")
    Call<ArrayList<TopSellerModel>> GetTopSellerItem(@Query("Skip") int Skip, @Query("Take") int password, @Query("Keyword") String Keyword);

    @GET("api/buyer/SellerProductDetail/GetSellerProductById/{GetSellerProductById}")
    Call<ProductDataModel> GetSellerProductById(@Path("GetSellerProductById") int GetSellerProductById);

    @GET("api/buyer/SellerProductDetail/GetTopSimilarproduct/{GetSellerProductById}")
    Call<ArrayList<TopNearByItemModel>> GetTopSimilarproduct(@Path("GetSellerProductById") int GetSellerProductById);


    @GET("api/buyer/SellerProductDetail/GetSimilarProductTopSeller/{GetSimilarProductTopSeller}")
    Call<ArrayList<TopSellerModel>> GetSimilarProductTopSeller(@Path("GetSimilarProductTopSeller") int GetSellerProductById);

    @GET("api/buyer/SellerProductDetail/GetMoreSimilarSellerProduct/{GetMoreSimilarSellerProduct}")
    Call<ArrayList<TopNearByItemModel>> GetMoreSimilarSellerProduct(@Path("GetMoreSimilarSellerProduct") int GetSellerProductById);

    @GET("api/Buyer/CartOverview/GetCartItems/{GetCartItems}")
    Call<JsonObject> GetCartItems(@Path("GetCartItems") String GetSellerProductById);

    @POST("api/Buyer/SellerProfile/AddProductView")
    Call<Boolean> AddProductView(@Body PaginationModel paginationModel);

    @GET("api/Buyer/SellerProfile/GetSellerDetail/{GetSellerDetail}")
    Call<SellerDetailsModel> GetSellerDetail(@Path("GetSellerDetail") String GetSellerDetail);


    @POST("api/Buyer/SellerProfile/GetSellerProduct")
    Call<SellerProductModel> GetSellerProduct(@Body SellerProfileDataModel paginationModel);

    @POST("api/Buyer/SellerProfile/AddStoreView")
    Call<Boolean> AddStoreView(@Body AddViewModel paginationModel);

    @POST("api/Buyer/SellerProfile/AddCart")
    Call<AddCartItemModel> AddCart(@Body ItemAddModel itemAddModel);

    @GET("api/Buyer/SellerProfile/GetCartItems")
    Call<CartItemModel> GetCartItem(@Query("CookieValue") String CookieValue);

    @POST("api/Buyer/SellerProfile/ClearCart")
    Call<Object> ClearCart(@Query("Id") String id);

    @GET("api/Buyer/CartOverview/GetCartItems/{GetCartItems}")
    Call<CartItemModel> CartItems(@Path("GetCartItems") String GetCartItems);

    @POST("api/Buyer/SellerProfile/DeleteCartItems")
    Call<JsonObject> deleteCartItems(@Body RemoveItemRequestModel itemRequestModel);

    @GET("api/buyer/Profile/GetLocation")
    Call<JsonObject> GetLocation(@Query("lat") double lat,@Query("lng") double log );

    @GET("api/Buyer/Order/GetUserLocation")
    Call<ArrayList<UserLocationModel>> GetUserLocation();

    @GET("api/Buyer/Order/GetDeliveryOption")
    Call<ArrayList<DeliveryOptionModel>> GetDeliveryOption(@Query("SellerId") String SellerId);

    @GET("api/Buyer/Order/GetCheckOutItem")
    Call<JsonObject> GetCheckOutItem(@Query("CookieValue") String CookieValue);

    @POST("api/Buyer/Order/PlaceOrder")
    Call<OrderPlaceModel> PlaceOrder(@Body OrderPlaceRequestModel placeRequestModel);

    @POST("api/buyer/Profile/AddLocation")
    Call<Boolean> AddLocation(@Body JsonArray jsonArray);

    @POST("api/Buyer/SellerProfile/AddCartItem")
    Call<AddCartItemModel> AddCartItem(@Body ItemAddModel itemAddModel);




}
