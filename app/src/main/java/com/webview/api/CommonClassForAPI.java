package com.webview.api;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.webview.model.AppVersionModel;

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


    public void getAppVersionApi(final DisposableObserver observer) {
        RestClient.getInstance(mActivity).getService().getAppversion()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AppVersionModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(AppVersionModel o) {
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