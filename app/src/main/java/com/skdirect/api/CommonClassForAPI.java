package com.skdirect.api;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.ObjectValue;
import com.google.gson.JsonObject;
import com.skdirect.model.AppVersionModel;
import com.skdirect.model.LoginWithPasswordModel;
import com.skdirect.model.OtpResponceModel;
import com.skdirect.model.OtpVerificationModel;
import com.skdirect.model.TokenModel;
import com.skdirect.model.UpdateTokenModel;

import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class CommonClassForAPI {
    private Activity mActivity;
    private static CommonClassForAPI CommonClassForAPI;


    public static CommonClassForAPI getInstance(Activity activity) {
        if (CommonClassForAPI == null) {
            CommonClassForAPI = new CommonClassForAPI(activity);
        }
        return CommonClassForAPI;
    }

    public CommonClassForAPI(Activity activity) {
        mActivity = activity;
    }



    public void getToken(final DisposableObserver observer,String password, String mobileNumber, String passwordString, boolean ISOTP, boolean ISBUYER, String buyerapp,boolean isDevice,String deviceID,double lat,double log,String pincode) {
        RestClient.getInstance().getService().getToken(password, mobileNumber, passwordString, ISOTP, ISBUYER, buyerapp,isDevice,deviceID,lat,log ,pincode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TokenModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(TokenModel o) {
                        observer.onNext(o);
                    }

                    @Override
                    public void onError(Throwable e) {
                        observer.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        observer.onComplete();
                    }
                });
    }

    public void getUpdateToken(final DisposableObserver observer,UpdateTokenModel updateTokenModel) {
        RestClient.getInstance().getService().getUpdateToken(updateTokenModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(JsonObject o) {
                        observer.onNext(o);
                    }

                    @Override
                    public void onError(Throwable e) {
                        observer.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        observer.onComplete();
                    }
                });
    }

    public void getLogin(final DisposableObserver observer, OtpVerificationModel otpVerificationModel) {
        RestClient.getInstance().getService().getVerfiyOtp(otpVerificationModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OtpResponceModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(OtpResponceModel o) {
                        observer.onNext(o);
                    }

                    @Override
                    public void onError(Throwable e) {
                        observer.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        observer.onComplete();
                    }
                });
    }

    public void getLayout(final DisposableObserver observer) {
        RestClient.getInstance1().getService1().getLayoutJsonData("https://firebasestorage.googleapis.com/v0/b/zaruriapp-d6994.appspot.com/o/layouts.json?alt=media&token=7ddd3621-c00f-4e1f-b1e8-71b4436a8e88")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Map<String, Layout>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Map<String, Layout> o) {
                        observer.onNext(o);
                    }

                    @Override
                    public void onError(Throwable e) {
                        observer.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        observer.onComplete();
                    }
                });
    }


}