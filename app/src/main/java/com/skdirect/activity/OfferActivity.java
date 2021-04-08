package com.skdirect.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.skdirect.R;
import com.skdirect.api.CommonClassForAPI;
import com.skdirect.databinding.ActivityOfferBinding;
import com.skdirect.model.CommonResponseModel;
import com.skdirect.utils.MyApplication;
import com.skdirect.utils.Utils;

import org.jetbrains.annotations.NotNull;

import io.reactivex.observers.DisposableObserver;

public class OfferActivity extends AppCompatActivity {
    private ActivityOfferBinding mBinding;
    private CommonClassForAPI commonClassForAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_offer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.apply_coupon));

        initViews();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {


        commonClassForAPI = CommonClassForAPI.getInstance(this);
        commonClassForAPI.getCouponList(couponObserver, MyApplication.getInstance().cartRepository.getCartSellerId());
    }


    private final DisposableObserver<CommonResponseModel> couponObserver = new DisposableObserver<CommonResponseModel>() {
        @Override
        public void onNext(@NotNull CommonResponseModel model) {
            mBinding.progressOffer.setVisibility(View.GONE);
            try {
                if (model != null) {
                    if (model.isSuccess()) {

                    } else {
                        Utils.setToast(getApplicationContext(), model.getErrorMessage());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Throwable e) {
            mBinding.progressOffer.setVisibility(View.INVISIBLE);
            e.printStackTrace();
        }

        @Override
        public void onComplete() {
            Utils.hideProgressDialog();
        }
    };
}