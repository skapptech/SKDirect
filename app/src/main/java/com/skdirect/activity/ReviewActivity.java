package com.skdirect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.skdirect.R;
import com.skdirect.databinding.ActivityProfileBinding;
import com.skdirect.databinding.ActivityReviewBinding;
import com.skdirect.model.AddReviewModel;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.TextUtils;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.ReViewViewMode;
import com.skdirect.viewmodel.UpdateProfileViewMode;

public class ReviewActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityReviewBinding mBinding;
    private int orderID;
    private ReViewViewMode reViewViewMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_review);
        reViewViewMode = ViewModelProviders.of(this).get(ReViewViewMode.class);
        getIntentData();
        initView();
    }
    private void getIntentData() {
        orderID = getIntent().getIntExtra("OrderID",0);

    }

    private void initView() {
        mBinding.toolbarTittle.tvTittle.setText("Review");
        mBinding.toolbarTittle.tvUsingLocation.setVisibility(View.VISIBLE);
        mBinding.toolbarTittle.tvUsingLocation.setText("Order ID :"+orderID);
        mBinding.toolbarTittle.ivBackPress.setOnClickListener(this);
        mBinding.btSaveRatting.setOnClickListener(this);
        mBinding.ratingBar.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_press:
                onBackPressed();
                break;
            case R.id.bt_save_ratting:
               checkRatting();
                break;

            case R.id.ratingBar:
                addRating();
                break;
        }
    }

    private void addRating() {


    }

    private void checkRatting() {
        if (mBinding.ratingBar.getRating() == 0.0){
            Utils.setToast(this,"Please Add rating");
        }else if (TextUtils.isNullOrEmpty(mBinding.etEnterComment.getText().toString().trim())){
            Utils.setToast(this,"Please enter comments");
        }else {
            if (Utils.isNetworkAvailable(getApplicationContext())) {
                Utils.showProgressDialog(ReviewActivity.this);
                addReViewAPI();


            } else {
                Utils.setToast(getApplicationContext(), "No Internet Connection Please connect.");
            }
        }

    }

    private void addReViewAPI() {
        reViewViewMode.getReviewModelRequest(new AddReviewModel(orderID,mBinding.etEnterComment.getText().toString(),  Math.round(mBinding.ratingBar.getRating())));
        reViewViewMode.getReviewModel().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean customerDataModel) {
                Utils.hideProgressDialog();
                if (customerDataModel != null) {
                    if (customerDataModel) {
                        startActivity(new Intent(ReviewActivity.this,MyOrderActivity.class));
                        finish();
                    }
                }

            }

        });

    }
}
