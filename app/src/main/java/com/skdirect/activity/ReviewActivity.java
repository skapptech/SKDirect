package com.skdirect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.skdirect.R;
import com.skdirect.databinding.ActivityReviewBinding;
import com.skdirect.model.AddReviewModel;
import com.skdirect.model.ReviewMainModel;
import com.skdirect.utils.DBHelper;
import com.skdirect.utils.MyApplication;
import com.skdirect.utils.TextUtils;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.ReViewViewMode;

public class ReviewActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityReviewBinding mBinding;
    private int orderID;
    private ReViewViewMode reViewViewMode;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_review);
        reViewViewMode = ViewModelProviders.of(this).get(ReViewViewMode.class);
        dbHelper = MyApplication.getInstance().dbHelper;
        getIntentData();
        initView();
    }

    private void getIntentData() {
        orderID = getIntent().getIntExtra("OrderID", 0);

    }

    private void initView() {
        mBinding.tvRateExperience.setText(dbHelper.getString(R.string.rate_your_experience));
        mBinding.tvHowWouldRate.setText(dbHelper.getString(R.string.how_would_you_rate_the_seller));
        mBinding.tvComments.setText(dbHelper.getString(R.string.comments));
        mBinding.etEnterComment.setHint(dbHelper.getString(R.string.enter_comments));
        mBinding.btSaveRatting.setText(dbHelper.getString(R.string.save));

        mBinding.toolbarTittle.tvTittle.setText(dbHelper.getString(R.string.review));
        mBinding.toolbarTittle.tvUsingLocation.setVisibility(View.VISIBLE);
        mBinding.toolbarTittle.tvUsingLocation.setText(dbHelper.getString(R.string.order_id) + " " + orderID);
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
        if (mBinding.ratingBar.getRating() == 0.0) {
            Utils.setToast(this, dbHelper.getString(R.string.please_add_rating));
        } else if (TextUtils.isNullOrEmpty(mBinding.etEnterComment.getText().toString().trim())) {
            Utils.setToast(this, dbHelper.getString(R.string.please_enter_comments));
        } else {
            if (Utils.isNetworkAvailable(getApplicationContext())) {
                Utils.showProgressDialog(ReviewActivity.this);
                addReViewAPI();


            } else {
                Utils.setToast(getApplicationContext(), dbHelper.getString(R.string.no_internet_connection));
            }
        }

    }

    private void addReViewAPI() {
        reViewViewMode.getReviewModelRequest(new AddReviewModel(orderID, mBinding.etEnterComment.getText().toString(), Math.round(mBinding.ratingBar.getRating())));
        reViewViewMode.getReviewModel().observe(this, new Observer<ReviewMainModel>() {
            @Override
            public void onChanged(ReviewMainModel customerDataModel) {
                Utils.hideProgressDialog();
                if (customerDataModel.isSuccess()) {
                    startActivity(new Intent(ReviewActivity.this, MyOrderActivity.class));
                    finish();
                }
            }
        });

    }
}
