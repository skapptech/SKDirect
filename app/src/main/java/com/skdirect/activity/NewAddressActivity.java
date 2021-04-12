package com.skdirect.activity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.skdirect.R;
import com.skdirect.databinding.ActivityNewAddreshBinding;
import com.skdirect.utils.DBHelper;
import com.skdirect.utils.GPSTracker;
import com.skdirect.utils.MyApplication;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.TextUtils;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.NewAddressViewMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class NewAddressActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityNewAddreshBinding mBinding;
    private NewAddressViewMode newAddressViewMode;
    private GPSTracker gpsTracker;
    private DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_addresh);
        newAddressViewMode = ViewModelProviders.of(this).get(NewAddressViewMode.class);
        dbHelper = MyApplication.getInstance().dbHelper;
        initView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_press:
                onBackPressed();
                break;
            case R.id.tv_using_location:
                callLocationAPI();
                break;

            case R.id.bt_save_addresh:
                addLocation();
                break;
        }
    }


    private void initView() {

        mBinding.tilFullName.setHint(dbHelper.getString(R.string.full_name));
        mBinding.tilStreetAdd.setHint(dbHelper.getString(R.string.street_address));
        mBinding.tilLandMark.setHint(dbHelper.getString(R.string.landmark_optional));
        mBinding.tilPincode.setHint(dbHelper.getString(R.string.pincode));
        mBinding.tilCity.setHint(dbHelper.getString(R.string.city));
        mBinding.tilState.setHint(dbHelper.getString(R.string.state));
        mBinding.btSaveAddresh.setText(dbHelper.getString(R.string.save));
        mBinding.toolbarTittle.tvUsingLocation.setText(dbHelper.getString(R.string.use_current_location));

        mBinding.toolbarTittle.tvUsingLocation.setVisibility(View.VISIBLE);
        mBinding.toolbarTittle.ivBackPress.setOnClickListener(this);
        mBinding.toolbarTittle.tvUsingLocation.setOnClickListener(this);
        mBinding.btSaveAddresh.setOnClickListener(this);

        /*mBinding.etFullName.setText(SharePrefs.getInstance(NewAddressActivity.this).getString(SharePrefs.FIRST_NAME));
        mBinding.tilStreetAdd.setText(SharePrefs.getInstance(NewAddressActivity.this).getString(SharePrefs.));
        mBinding.tilLandMark.setText(SharePrefs.getInstance(NewAddressActivity.this).getString(SharePrefs.FIRST_NAME));
        mBinding.tilPincode.setText(SharePrefs.getInstance(NewAddressActivity.this).getString(SharePrefs.PIN_CODE));
        mBinding.tilCity.setText(SharePrefs.getInstance(NewAddressActivity.this).getString(SharePrefs.CITY_NAME));
        mBinding.tilState.setText(SharePrefs.getInstance(NewAddressActivity.this).getString(SharePrefs.STATE));*/
    }

    private void addLocation() {
        if (TextUtils.isNullOrEmpty(mBinding.etFullName.getText().toString().trim())) {
            Utils.setToast(getApplicationContext(), "Please enter full name.");
        } else if (TextUtils.isNullOrEmpty(mBinding.etStreetAddresh.getText().toString().trim())) {
            Utils.setToast(getApplicationContext(), "Please enter street address.");
        } else if (TextUtils.isNullOrEmpty(mBinding.etPinCode.getText().toString().trim())) {
            Utils.setToast(getApplicationContext(), "Please enter pin code.");
        } else if (TextUtils.isNullOrEmpty(mBinding.etPinCity.getText().toString().trim())) {
            Utils.setToast(getApplicationContext(), "Please enter city name.");
        } else {
            if (Utils.isNetworkAvailable(getApplicationContext())) {
                Utils.showProgressDialog(NewAddressActivity.this);
                setLocationAPI();
            } else {
                Utils.setToast(getApplicationContext(), "No Internet Connection Please connect.");
            }
        }


    }

    private void setLocationAPI() {
        newAddressViewMode.getAddLocationVMRequest(setUserValue());
        newAddressViewMode.getAddLocationVM().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean status) {
                Utils.hideProgressDialog();
                if (status) {
                    onBackPressed();
                }
            }

        });

    }


    private JsonArray setUserValue() {
        gpsTracker = new GPSTracker(getApplicationContext());
        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("AddressOne", mBinding.etFullName.getText().toString());
            jsonObject.addProperty("AddressThree", mBinding.etStreetAddresh.getText().toString());
            jsonObject.addProperty("AddressTwo", mBinding.etLandmark.getText().toString());
            jsonObject.addProperty("Id", "");
            jsonObject.addProperty("IsActive", SharePrefs.getInstance(NewAddressActivity.this).getBoolean(SharePrefs.IS_ACTIVE));
            jsonObject.addProperty("IsDelete", SharePrefs.getInstance(NewAddressActivity.this).getBoolean(SharePrefs.IS_DELETE));
            jsonObject.addProperty("IsPrimaryAddress", "");
            jsonObject.addProperty("Latitiute", gpsTracker.getLatitude());
            jsonObject.addProperty("Longitude", gpsTracker.getLongitude());
            jsonObject.addProperty("LocationType", "");
            jsonObject.addProperty("Pincode", mBinding.etPinCode.getText().toString());
            jsonObject.addProperty("UserDetailId", "");
            jsonObject.addProperty("City", mBinding.etPinCity.getText().toString());
            jsonObject.addProperty("State", mBinding.etPinState.getText().toString());

        } catch (Exception e) {

            e.printStackTrace();
        }
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(jsonObject);

        return jsonArray;
    }

    private void callLocationAPI() {

        gpsTracker = new GPSTracker(getApplicationContext());

        if (Utils.isNetworkAvailable(getApplicationContext())) {
            if (gpsTracker != null) {
                Utils.showProgressDialog(this);
                callLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude());
            }

        } else {
            Utils.setToast(getApplicationContext(), dbHelper.getString(R.string.no_connection));
        }
    }


    private void callLocation(double latitude, double longitude) {
        newAddressViewMode.getMapViewModelRequest(latitude, longitude);
        newAddressViewMode.getMapViewModel().observe(this, new Observer<JsonObject>() {
            @Override
            public void onChanged(JsonObject data) {
                Utils.hideProgressDialog();
                JSONObject jsonResponse = null;
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
                    String address = addresses.get(0).getAddressLine(0);
                    String postalCode = addresses.get(0).getPostalCode();
                    String Premises = addresses.get(0).getAdminArea();


                    mBinding.etPinCode.setText(postalCode);
                    mBinding.etPinCity.setText(cityName);
                    mBinding.etStreetAddresh.setText(address);
                    mBinding.etPinState.setText(Premises);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }


}
