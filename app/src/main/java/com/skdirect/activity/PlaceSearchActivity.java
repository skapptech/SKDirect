package com.skdirect.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.skdirect.R;
import com.skdirect.databinding.ActivityPlacesSearchBinding;
import com.skdirect.utils.GPSTracker;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.TextUtils;
import com.skdirect.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlaceSearchActivity extends AppCompatActivity implements View.OnClickListener {
    private final int REQUEST_FOR_ADDRESS = 1001;
    private ActivityPlacesSearchBinding mBinding;
    private Place place;
    private Geocoder mGeocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_places_search);
        initView();

    }

    private void initView() {
        mGeocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        mBinding.etLocation.setOnClickListener(this);
        mBinding.btSave.setOnClickListener(this);
        mBinding.etPinCode.setFocusable(false);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_location:
                callRunTimePermissions();
                break;

            case R.id.bt_save:
                saveLocationData();
                break;
        }
    }

    private void saveLocationData() {
         if (TextUtils.isNullOrEmpty(mBinding.etLocation.getText().toString().trim())){
            Utils.setToast(this,"Enter your Address");
        }else if (TextUtils.isNullOrEmpty(mBinding.etPinCode.getText().toString().trim())){
            Utils.setToast(this,"Enter your PinCode");
        }else {
             SharePrefs.getInstance(this).putBoolean(SharePrefs.IS_LOGIN, true);
            startActivity(new Intent(this,MainActivity.class));
        }
    }

    private void findLocation() {
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        }
        startActivityForResult(new Intent(getApplicationContext(), UserLocationActvity.class)
                .putExtra("cityname", "")
                .putExtra("searchCity", false), REQUEST_FOR_ADDRESS);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FOR_ADDRESS & resultCode == RESULT_OK) {
            //place = Autocomplete.getPlaceFromIntent(data);
            place = data.getParcelableExtra("PlaceResult");
                mBinding.etLocation.setText(place.getAddress());
                try {
                    LatLng latLng = place.getLatLng();
                    List<Address> addresses = mGeocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (addresses.get(0).getLocality() != null) {
                        String pin = addresses.get(0).getPostalCode();
                        String tempCity = addresses.get(0).getLocality();
                        mBinding.etPinCode.setText(pin);
                        if (TextUtils.isNullOrEmpty(mBinding.etPinCode.getText().toString())) {
                            mBinding.etPinCode.setFocusable(true);
                            mBinding.etPinCode.setFocusableInTouchMode(true);
                        } else {
                            mBinding.etPinCode.setFocusable(false);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
        }else if (resultCode==3){
            try {
                LatLng latLng = data.getParcelableExtra("PlaceResult");
                List<Address> addresses = mGeocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                mBinding.etLocation.setText(addresses.get(0).getAddressLine(0));
                if (addresses.get(0).getLocality() != null) {
                    String pin = addresses.get(0).getPostalCode();
                    String tempCity = addresses.get(0).getLocality();
                    mBinding.etPinCode.setText(pin);
                    if (TextUtils.isNullOrEmpty(mBinding.etPinCode.getText().toString())) {
                        mBinding.etPinCode.setFocusable(true);
                        mBinding.etPinCode.setFocusableInTouchMode(true);
                    } else {
                        mBinding.etPinCode.setFocusable(false);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void callRunTimePermissions() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                Log.e("onDenied", "onGranted");
                findLocation();
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
                Log.e("onDenied", "onDenied" + deniedPermissions);

            }
        });
    }

}
