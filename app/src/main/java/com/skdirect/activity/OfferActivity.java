package com.skdirect.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.skdirect.R;
import com.skdirect.adapter.CouponListAdapter;
import com.skdirect.api.CommonClassForAPI;
import com.skdirect.databinding.ActivityOfferBinding;
import com.skdirect.model.response.CouponResponse;
import com.skdirect.utils.MyApplication;
import com.skdirect.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import io.reactivex.observers.DisposableObserver;

public class OfferActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityOfferBinding mBinding;

    private ArrayList<CouponResponse.Coupon> list;
    private CouponListAdapter adapter;
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

    @Override
    public void onClick(View v) {

    }


    private void initViews() {
        mBinding.btnApply.setOnClickListener(this);

        list = new ArrayList<>();
        adapter = new CouponListAdapter(this, list);
        mBinding.rvOffer.setAdapter(adapter);

        commonClassForAPI = CommonClassForAPI.getInstance(this);
        commonClassForAPI.getCouponList(couponObserver, MyApplication.getInstance().cartRepository.getCartSellerId());
    }


    private final DisposableObserver<CouponResponse> couponObserver = new DisposableObserver<CouponResponse>() {
        @Override
        public void onNext(@NotNull CouponResponse model) {
            mBinding.progressOffer.setVisibility(View.GONE);
            mBinding.tvEmpty.setVisibility(View.GONE);
            try {
                if (model != null && model.isSuccess()) {
                    list.addAll(model.getResultItem());
                    adapter.notifyDataSetChanged();
                } else {
                    mBinding.tvEmpty.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Throwable e) {
            mBinding.progressOffer.setVisibility(View.INVISIBLE);
            mBinding.tvEmpty.setVisibility(View.VISIBLE);
            e.printStackTrace();
        }

        @Override
        public void onComplete() {
            Utils.hideProgressDialog();
        }
    };
}