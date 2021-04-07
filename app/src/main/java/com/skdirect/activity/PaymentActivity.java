package com.skdirect.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.skdirect.R;
import com.skdirect.adapter.DeliveryOptionAdapter;
import com.skdirect.databinding.ActivityPaymentBinding;
import com.skdirect.model.CartItemModel;
import com.skdirect.model.DeliveryOptionModel;
import com.skdirect.model.OrderPlaceMainModel;
import com.skdirect.model.OrderPlaceRequestModel;
import com.skdirect.model.UserLocationModel;
import com.skdirect.utils.GPSTracker;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.PaymentViewMode;

import java.util.ArrayList;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityPaymentBinding mBinding;
    private int itemSize;
    private PaymentViewMode paymentViewMode;
    private CartItemModel cartItemModel;
    private final ArrayList<DeliveryOptionModel> deliveryOptionList = new ArrayList<>();
    private DeliveryOptionAdapter deliveryOptionAdapter;
    private int deliveryOption, UserLocationId;
    private double totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_payment);
        paymentViewMode = ViewModelProviders.of(this).get(PaymentViewMode.class);
        getSharedData();
        initView();
        callUserLocation();
        callRunTimePermissions();
    }

    private void getSharedData() {
        cartItemModel = (CartItemModel) getIntent().getSerializableExtra("cartItemSize");
        totalAmount = getIntent().getDoubleExtra("totalAmount", 0);

        Log.e("Total Amount ","##### "+cartItemModel.getTotalAmount());

        mBinding.tvItemPrice.setText("Price Details ( " + (itemSize = cartItemModel.getCart().size()) + " items)");
        mBinding.tvOrderValue.setText("₹ " + totalAmount);
        mBinding.tvTotalAmount.setText("₹ " + totalAmount);
        mBinding.tvAmountTotal.setText("₹ " + totalAmount);


    }

    private void initView() {
        mBinding.toolbarTittle.ivBackPress.setOnClickListener(this);
        mBinding.tvPlaceOrder.setOnClickListener(this);
        mBinding.toolbarTittle.tvTittle.setText("Payment ");
        mBinding.rlChnage.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mBinding.rvDeliveryOption.setLayoutManager(layoutManager);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_press:
                onBackPressed();
                break;

            case R.id.tv_place_order:
                if (SharePrefs.getSharedPreferences(getApplicationContext(), SharePrefs.IS_REGISTRATIONCOMPLETE)&& SharePrefs.getSharedPreferences(getApplicationContext(), SharePrefs.IS_LOGIN)) {
                    OrderPlaceAlertDialog();
                } else {
                    startActivity(new Intent(this,LoginActivity.class));
                }


                break;
            case R.id.rl_chnage:
                startActivityForResult(new Intent(PaymentActivity.this,
                        PrimaryAddressActivity.class).putExtra("UserLocationId", UserLocationId), 2);
                break;
        }
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

    public void OrderPlaceAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to place order?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Utils.isNetworkAvailable(getApplicationContext())) {
                    Utils.showProgressDialog(PaymentActivity.this);
                    orderPlaceAPI();
                } else {
                    Utils.setToast(getApplicationContext(), "No Internet Connection Please connect.");
                }


            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void orderPlaceAPI() {
        OrderPlaceRequestModel orderPlaceRequestModel = new OrderPlaceRequestModel("CASE", deliveryOption, cartItemModel.getId(), UserLocationId);
        paymentViewMode.getOrderPlaceVMRequest(orderPlaceRequestModel);
        paymentViewMode.getOrderPlaceVM().observe(this, new Observer<OrderPlaceMainModel>() {
            @Override
            public void onChanged(OrderPlaceMainModel response) {
                Utils.hideProgressDialog();
                if (response.isSuccess()) {
                    orderPlaceDialog();
                    clearCartItem(cartItemModel.getSellerId());
                }else
                {
                  Toast.makeText(PaymentActivity.this, response.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void orderPlaceDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Congratulation");
        dialog.setMessage("Order Place Successfully");
        dialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(PaymentActivity.this, MainActivity.class));
                        finish();

                    }
                });
        dialog.show();
    }


    private void userLocationAPI() {
        paymentViewMode.getUserLocationVMRequest();
        paymentViewMode.getUserLocationVM().observe(this, new Observer<ArrayList<UserLocationModel>>() {
            @Override
            public void onChanged(ArrayList<UserLocationModel> locationModel) {
                Utils.hideProgressDialog();
                if (locationModel != null) {
                    if (locationModel.size() > 0) {
                        for (int i = 0; i < locationModel.size(); i++) {
                            if (locationModel.get(i).isPrimaryAddress()) {
                                UserLocationId = locationModel.get(i).getId();
                                mBinding.tvShopName.setText(locationModel.get(i).getAddressOne());
                                mBinding.tvAddresh.setText(locationModel.get(i).getAddressTwo());
                                mBinding.tvAddreshTree.setText(locationModel.get(i).getAddressThree());
                                mBinding.tvCityName.setText(locationModel.get(i).getCity() + " - " + locationModel.get(i).getPincode() + " (" + locationModel.get(i).getState() + ")");

                            }


                        }

                    }

                }
            }
        });
    }

    private void deliveryOptionAPI() {
        paymentViewMode.getDeliveryOptionVMRequest(cartItemModel.getEncryptSellerId());
        paymentViewMode.getDeliveryOptionVM().observe(this, new Observer<ArrayList<DeliveryOptionModel>>() {
            @Override
            public void onChanged(ArrayList<DeliveryOptionModel> locationModel) {
                Utils.hideProgressDialog();
                if (locationModel.size() > 0) {
                    for (int i = 0; i < locationModel.size(); i++) {
                        deliveryOption = locationModel.get(i).getId();
                    }
                    deliveryOptionList.addAll(locationModel);
                }
                deliveryOptionAdapter = new DeliveryOptionAdapter(getApplicationContext(), deliveryOptionList);
                mBinding.rvDeliveryOption.setAdapter(deliveryOptionAdapter);
            }
        });

    }

    private void checkOutItemApi() {
        paymentViewMode.getCheckOutItemVMRequest("123");
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
               /* JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(data.toString());
                    JSONObject components = jsonResponse.getJSONObject("geometry");
                    JSONObject location = components.getJSONObject("location");
                    double lat = location.getDouble("lat");
                    double lng = location.getDouble("lng");
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(lat, lng, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String cityName = addresses.get(0).getLocality();
                    Log.e("cityName", "cityName  "+cityName);
                    // setLocationTV.setText(cityName);

                } catch (JSONException e) {
                    e.printStackTrace();
                }*/


            }
        });
    }


    private void clearCartItem(int id) {
        paymentViewMode.getClearCartDataRequest(id);
        paymentViewMode.getClearCartData().observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object object) {
                Utils.hideProgressDialog();
//                if (object != null) {
//                    if (object.equals(true)) {
//                        MainActivity.cartItemModel.getCart().clear();
//                    }
//                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 2 && resultCode == RESULT_OK) {
            UserLocationModel userLocationModel = (UserLocationModel) data.getSerializableExtra("address");
            UserLocationId = userLocationModel.getId();
            mBinding.tvShopName.setText(userLocationModel.getAddressOne());
            mBinding.tvAddresh.setText(userLocationModel.getAddressTwo());
            mBinding.tvAddreshTree.setText(userLocationModel.getAddressThree());
            mBinding.tvCityName.setText(userLocationModel.getCity() + " - " + userLocationModel.getPincode() + " (" + userLocationModel.getState() + ")");
        }
    }
}
