package com.skdirect.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.JsonObject;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.skdirect.R;
import com.skdirect.adapter.DeliveryOptionAdapter;
import com.skdirect.databinding.ActivityPaymentBinding;
import com.skdirect.interfacee.DeliveryOptionInterface;
import com.skdirect.model.CartItemModel;
import com.skdirect.model.DeliveryMainModel;
import com.skdirect.model.DeliveryOptionModel;
import com.skdirect.model.OrderPlaceRequestModel;
import com.skdirect.model.UserLocationModel;
import com.skdirect.model.response.OfferResponse;
import com.skdirect.utils.GPSTracker;
import com.skdirect.utils.MyApplication;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.PaymentViewMode;

import java.util.ArrayList;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener, DeliveryOptionInterface {
    private ActivityPaymentBinding mBinding;
    private int itemSize;
    private PaymentViewMode paymentViewMode;
    private CartItemModel cartItemModel;
    private final ArrayList<DeliveryOptionModel> deliveryOptionList = new ArrayList<>();
    private DeliveryOptionAdapter deliveryOptionAdapter;

    private OfferResponse.Coupon coupon;
    private int deliveryOption, userLocationId;
    private double cartTotal, totalAmount, discount = 0;
    private boolean isSelfPickup = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_payment);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(MyApplication.getInstance().dbHelper.getString(R.string.title_activity_payment));

        paymentViewMode = ViewModelProviders.of(this).get(PaymentViewMode.class);
        getSharedData();
        initView();
        callUserLocation();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRemove:
                updateViews(false, coupon);
                break;
            case R.id.btnOffer:
                startActivityForResult(new Intent(getApplicationContext(), OfferActivity.class), 9);
                break;
            case R.id.btnPlaceOrder:
                if (SharePrefs.getSharedPreferences(getApplicationContext(), SharePrefs.IS_REGISTRATIONCOMPLETE) && SharePrefs.getInstance(getApplicationContext()).getBoolean(SharePrefs.IS_LOGIN)) {
                    if (userLocationId != 0 || isSelfPickup) {
                        orderPlaceAlertDialog();
                    } else {
                        startActivityForResult(new Intent(getApplicationContext(),
                                PrimaryAddressActivity.class).putExtra("UserLocationId", userLocationId), 2);
                        //Utils.setToast(getApplicationContext(), MyApplication.getInstance().dbHelper.getString(R.string.please_select_address));
                    }
                } else {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
                break;
            case R.id.btnAdd:
                startActivityForResult(new Intent(getApplicationContext(),
                        PrimaryAddressActivity.class).putExtra("UserLocationId", userLocationId), 2);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 2 && resultCode == RESULT_OK) {
            UserLocationModel userLocationModel = (UserLocationModel) data.getSerializableExtra("address");
            userLocationId = userLocationModel.getId();
            mBinding.tvShopName.setText(userLocationModel.getAddressOne());
            mBinding.tvAddresh.setText(userLocationModel.getAddressTwo());
            mBinding.tvAddreshTree.setText(userLocationModel.getAddressThree());
            mBinding.tvCityName.setText(userLocationModel.getCity() + " - " + userLocationModel.getPincode() + " (" + userLocationModel.getState() + ")");
            mBinding.liAddress.setVisibility(View.VISIBLE);
            mBinding.btnAdd.setText(MyApplication.getInstance().dbHelper.getString(R.string.change));
        } else if (requestCode == 9 && resultCode == Activity.RESULT_OK) {
            mBinding.liCoupon.setVisibility(View.VISIBLE);
            coupon = data.getParcelableExtra("list");
            if (cartTotal > coupon.getMinOrderValue()) {
                discount = coupon.getAmount();
                totalAmount = cartTotal - discount;
                updateViews(true, coupon);
            }
        }
    }


    private void initView() {
        mBinding.tvOrderValueH.setText(MyApplication.getInstance().dbHelper.getString(R.string.total_order_value));
        mBinding.tvAmountH.setText(MyApplication.getInstance().dbHelper.getString(R.string.total_amount));
        mBinding.tvItemPrice.setText(MyApplication.getInstance().dbHelper.getString(R.string.price_details));
        mBinding.tvAddress.setText(MyApplication.getInstance().dbHelper.getString(R.string.address));
        mBinding.tvCodTitle.setText(MyApplication.getInstance().dbHelper.getString(R.string.cash_on_delivery));
        mBinding.tvDOption.setText(MyApplication.getInstance().dbHelper.getString(R.string.delivery_option));
        mBinding.btnPlaceOrder.setText(MyApplication.getInstance().dbHelper.getString(R.string.place_order));
        mBinding.tvSelectPromoTitle.setText(MyApplication.getInstance().dbHelper.getString(R.string.select_a_promo_code));
        mBinding.btnOffer.setText(MyApplication.getInstance().dbHelper.getString(R.string.view_offer));
        mBinding.tvPaymentTitle.setText(MyApplication.getInstance().dbHelper.getString(R.string.title_activity_payment));
        mBinding.btnAdd.setText(MyApplication.getInstance().dbHelper.getString(R.string.change));
        mBinding.btnOffer.setOnClickListener(this);
        mBinding.btnRemove.setOnClickListener(this);
        mBinding.btnPlaceOrder.setOnClickListener(this);
        mBinding.btnAdd.setOnClickListener(this);
    }

    private void getSharedData() {
        cartItemModel = (CartItemModel) getIntent().getSerializableExtra("cartItemSize");
        cartTotal = getIntent().getDoubleExtra("totalAmount", 0);
        totalAmount = cartTotal;

        Log.e("Total Amount ", "##### " + cartItemModel.getTotalAmount());

        mBinding.tvItemPrice.setText("Price Details ( " + (itemSize = cartItemModel.getCart().size()) + " items)");
        mBinding.tvOrderValue.setText("₹ " + cartTotal);
        mBinding.tvTotalAmount.setText("₹ " + totalAmount);
        mBinding.tvTotal.setText("₹ " + totalAmount);
    }

    private void callUserLocation() {
        if (Utils.isNetworkAvailable(getApplicationContext())) {
            Utils.showProgressDialog(PaymentActivity.this);
            userLocationAPI();
            deliveryOptionAPI();
            checkOutItemApi();
        } else {
            Utils.setToast(getApplicationContext(), "No Internet Connection Please connect.");
        }
    }

    public void orderPlaceAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to place order?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            if (Utils.isNetworkAvailable(getApplicationContext())) {
                Utils.showProgressDialog(this);
                orderPlaceAPI();
            } else {
                Utils.setToast(getApplicationContext(), "No Internet Connection Please connect.");
            }
        });

        builder.setNegativeButton("No", (dialog, which) -> {

        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void orderPlaceAPI() {
        if (isSelfPickup){
            userLocationId=0;
        }
        OrderPlaceRequestModel orderPlaceRequestModel = new OrderPlaceRequestModel("CASH", deliveryOption, cartItemModel.getId(), userLocationId);
        paymentViewMode.getOrderPlaceVMRequest(orderPlaceRequestModel);
        paymentViewMode.getOrderPlaceVM().observe(this, response -> {
            Utils.hideProgressDialog();
            if (response.isSuccess()) {
                orderPlaceDialog();
            } else {
                Toast.makeText(PaymentActivity.this, response.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void orderPlaceDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Congratulation");
        dialog.setMessage("Order Place Successfully");
        dialog.setPositiveButton("OK",
                (dialog1, which) -> {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                });
        dialog.show();
        // clear cart
        MyApplication.getInstance().cartRepository.truncateCart();
    }

    private void updateViews(boolean isApplied, OfferResponse.Coupon coupon) {
        if (isApplied) {
            //mBinding.rlApplyOffer.setVisibility(View.GONE);
            mBinding.liOffer.setVisibility(View.VISIBLE);
            mBinding.liCoupon.setVisibility(View.VISIBLE);
            mBinding.tvOffer.setText("Code " + coupon.getCouponCode() + " Applied");
            mBinding.tvDes.setText(coupon.getAmount() + " OFF up to ₹" + coupon.getMaxAmount());
            mBinding.tvOfferAmount.setText("-₹ " + discount);
            mBinding.tvOfferTotal.setText("-₹ " + discount);
        } else {
            cartTotal = totalAmount;
           // mBinding.rlApplyOffer.setVisibility(View.VISIBLE);
            mBinding.liOffer.setVisibility(View.GONE);
            mBinding.liCoupon.setVisibility(View.GONE);
        }
        mBinding.tvTotalAmount.setText("₹ " + totalAmount);
        mBinding.tvTotal.setText("₹ " + totalAmount);
    }

    private void userLocationAPI() {
        paymentViewMode.getUserLocationVMRequest();
        paymentViewMode.getUserLocationVM().observe(this, locationModel -> {
            Utils.hideProgressDialog();
            if (locationModel.isSuccess() && locationModel.getResultItem().size() > 0) {
                for (int i = 0; i < locationModel.getResultItem().size(); i++) {
                    if (locationModel.getResultItem().get(i).isPrimaryAddress()) {
                        userLocationId = locationModel.getResultItem().get(i).getId();
                        mBinding.tvShopName.setText(locationModel.getResultItem().get(i).getAddressOne());
                        mBinding.tvAddresh.setText(locationModel.getResultItem().get(i).getAddressTwo());
                        mBinding.tvAddreshTree.setText(locationModel.getResultItem().get(i).getAddressThree());
                        mBinding.tvCityName.setText(locationModel.getResultItem().get(i).getCity() + " - " + locationModel.getResultItem().get(i).getPincode() + " (" + locationModel.getResultItem().get(i).getState() + ")");
                    }
                }
            } else {
                mBinding.liAddress.setVisibility(View.GONE);
                mBinding.btnAdd.setText(MyApplication.getInstance().dbHelper.getString(R.string.add_new_address));
            }
        });
    }

    private void deliveryOptionAPI() {
        paymentViewMode.getDeliveryOptionVMRequest(cartItemModel.getSellerId());
        paymentViewMode.getDeliveryOptionVM().observe(this, new Observer<DeliveryMainModel>() {
            @Override
            public void onChanged(DeliveryMainModel deliveryMainModel) {
                Utils.hideProgressDialog();
                if (deliveryMainModel.isSuccess())
                    if (deliveryMainModel.getResultItem().get(0).getDelivery().equals("Self Pickup")) {
                        mBinding.liAddressV.setVisibility(View.GONE);
                        isSelfPickup = true;
                    }

                if (deliveryMainModel.getResultItem().size() > 0) {
                    for (int i = 0; i < deliveryMainModel.getResultItem().size(); i++) {
                        deliveryOption = deliveryMainModel.getResultItem().get(i).getId();
                    }
                    deliveryOptionList.addAll(deliveryMainModel.getResultItem());

                }
                deliveryOptionAdapter = new DeliveryOptionAdapter(getApplicationContext(), deliveryOptionList, PaymentActivity.this);
                mBinding.rvDeliveryOption.setAdapter(deliveryOptionAdapter);
            }
        });

    }

    private void checkOutItemApi() {
        paymentViewMode.getCheckOutItemVMRequest();
        paymentViewMode.getCheckOutItemVM().observe(this, new Observer<JsonObject>() {
            @Override
            public void onChanged(JsonObject aBoolean) {
                Utils.hideProgressDialog();

            }
        });
    }

    public void callRunTimePermissions() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                Log.e("onDenied", "onGranted");
                GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                if (gpsTracker != null) {
                    callLocationAPI(gpsTracker.getLatitude(), gpsTracker.getLongitude());
                }
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
                Log.e("onDenied", "onDenied" + deniedPermissions);

            }
        });
    }

    private void callLocationAPI(double latitude, double longitude) {
        paymentViewMode.getMapViewModelRequest(latitude, longitude);
        paymentViewMode.getMapViewModel().observe(this, new Observer<JsonObject>() {
            @Override
            public void onChanged(JsonObject data) {
                Utils.hideProgressDialog();

            }
        });
    }

    @Override
    public void onOnClick(DeliveryOptionModel deliveryOptionModel, int position) {
        deliveryOption = deliveryOptionModel.getId();
        if (deliveryOptionModel.getDelivery().equals("Self Pickup")){
            mBinding.liAddressV.setVisibility(View.GONE);
            isSelfPickup = true;
        }else {
            mBinding.liAddressV.setVisibility(View.VISIBLE);
            isSelfPickup = false;
        }
    }
}