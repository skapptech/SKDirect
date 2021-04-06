package com.moreyeahs.hindmud.supplier.activity.signup;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.moreyeahs.hindmud.supplier.BuildConfig;
import com.moreyeahs.hindmud.supplier.R;
import com.moreyeahs.hindmud.supplier.api.CommonClassForApi;
import com.moreyeahs.hindmud.supplier.databinding.ActivityMapsExtendedBinding;
import com.moreyeahs.hindmud.supplier.utils.GpsUtils;
import com.moreyeahs.hindmud.supplier.utils.IntentKeys;
import com.moreyeahs.hindmud.supplier.utils.LocaleHelper;
import com.moreyeahs.hindmud.supplier.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
* Created by Pritesh on 17/07/2020.
**/

public class MapsExtendedActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private ActivityMapsExtendedBinding mBinding;
    Utils utils;
    CommonClassForApi commonClassForAPI;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static String TAG = "MAPS EXTENDED ACTIVITY";
    public static final int PERMISSIONS_REQUESTED_ACCESS_FINE_LOCATION = 99;
    public int LAUNCH_PLACES_ACTIVITY = 2000;
    boolean selectedLocation = false;
    public boolean isPermissionAllowed = false;
    View mapView;

    String zipCode;
    String premises;
    String country;
    String customerState;
    String city;
    String subLocality;
    String fullAddress;
    String displayAddress;
    Double latitude = 0.0;
    Double longitude = 0.0;
    LatLng mCenterLatLong;
    Boolean isGPS = false;
    String mobileNumber;
    String password;
    String businessName;
    String businessEmail;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        if (overrideConfiguration != null) {
            int uiMode = overrideConfiguration.uiMode;
            overrideConfiguration.setTo(getBaseContext().getResources().getConfiguration());
            overrideConfiguration.uiMode = uiMode;
        }
        super.applyOverrideConfiguration(overrideConfiguration);
    }

    private BroadcastReceiver mGpsSwitchStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.requireNonNull(intent.getAction()).matches("android.location.PROVIDERS_CHANGED")) {
                checkPlayServices();
            }
        }
    };

//    DisposableObserver<ZipCodeResponse> getZipCode = new DisposableObserver<ZipCodeResponse>() {
//        @Override
//        public void onNext(ZipCodeResponse zipCodeResponse) {
//            Utils.hideProgressDialog(MapsExtendedActivity.this);
//            if (zipCodeResponse != null) {
//                if (zipCodeResponse.getCityId() > 0) {
//                    mBinding.tvSorry.setVisibility(View.GONE);
//                    mBinding.tvSubmit.setEnabled(true);
//                    cityId = zipCodeResponse.getCityId();
//                } else {
//                    mBinding.tvSorry.setVisibility(View.VISIBLE);
//                    mBinding.tvSubmit.setEnabled(false);
//                }
//            }
//        }
//
//        @Override
//        public void onError(Throwable e) {
//            Utils.hideProgressDialog(MapsExtendedActivity.this);
//            e.printStackTrace();
//        }
//
//        @Override
//        public void onComplete() {
//
//        }
//    };
//
//    DisposableObserver<UpdateAddressResponse> postAddressDess = new DisposableObserver<UpdateAddressResponse>() {
//        @Override
//        public void onNext(UpdateAddressResponse addressResponse) {
//            Utils.hideProgressDialog(MapsExtendedActivity.this);
//            if (addressResponse.getMessage().equalsIgnoreCase("Successfully")) {
//                Toast.makeText(MapsExtendedActivity.this, getString(R.string.LocationUpdatedSuccessfully), Toast.LENGTH_SHORT).show();
//                saveToMyPref();
//                SharePrefs.getInstance(MapsExtendedActivity.this).putInt(SharePrefs.VENDOR_CITYID, addressResponse.getCityid());
//                startActivity(new Intent(MapsExtendedActivity.this, IndustriesListActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//                finish();
//            }
//            else
//            {
//                Toast.makeText(MapsExtendedActivity.this, getString(R.string.someee), Toast.LENGTH_SHORT).show();
//
//            }
//        }
//
//        @Override
//        public void onError(Throwable e) {
//            e.printStackTrace();
//            postAddressDess.dispose();
//            Toast.makeText(MapsExtendedActivity.this, getString(R.string.someee), Toast.LENGTH_SHORT).show();
//
//            Utils.hideProgressDialog(MapsExtendedActivity.this);
//        }
//
//        @Override
//        public void onComplete() {
//
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_maps_extended);

        getExtrasFromIntent();
        initializeViews();
        checkPlayServices();
    }

    private void initializeViews() {
        utils = new Utils(MapsExtendedActivity.this);
        commonClassForAPI = CommonClassForApi.getInstance(MapsExtendedActivity.this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        View locationBtn = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).
                getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationBtn.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layoutParams.setMargins(0, 0, 30, 95);

        mBinding.tvSubmit.setOnClickListener(v -> {
            if (zipCode != null && zipCode.length() > 0) {
                if (mBinding.etShopNumber.getText().toString().trim().isEmpty()) {
                    Utils.setToast(getApplicationContext(), getString(R.string.Pleas));
                } else {
                    confirmAddress(mBinding.etShopNumber.getText().toString().trim(),
                            mBinding.etLandmark.getText().toString().trim());
                }
            } else {
                Toast.makeText(this, getString(R.string.unable), Toast.LENGTH_SHORT).show();
            }
        });

        new GpsUtils(this).turnGPSOn(isGPSEnable -> {
            // turn on GPS
            isGPS = isGPSEnable;
        });

        mBinding.tvChangeMe.setOnClickListener(V -> startActivityForResult(new Intent(this, PlacesActivity.class), LAUNCH_PLACES_ACTIVITY));

        mBinding.etShopNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!mBinding.etShopNumber.getText().toString().trim().isEmpty()) {
                    mBinding.tvSubmit.setTextColor(getResources().getColor(R.color.White));
                    mBinding.tvSubmit.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.black_bg));
                } else {
                    mBinding.tvSubmit.setTextColor(getResources().getColor(R.color.Darkgrey));
                    mBinding.tvSubmit.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.global_btn_grey));

                }
            }
        });
    }

    private void getExtrasFromIntent() {
        mobileNumber = getIntent().getStringExtra(IntentKeys.MOBILE_NUMBER);
        password = getIntent().getStringExtra(IntentKeys.PASSWORD);
        businessName = getIntent().getStringExtra(IntentKeys.BUSINESS_NAME);
        businessEmail = getIntent().getStringExtra(IntentKeys.BUSINESS_EMAIL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 150) {
                isGPS = true; // flag maintain before get location
            }
        }
        if (requestCode == LAUNCH_PLACES_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                double rLatitude = data.getDoubleExtra("latitude", 0.0);
                double rLongitude = data.getDoubleExtra("longitude", 0.0);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(rLatitude, rLongitude)).zoom(16f).build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                selectedLocation = true;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isPermissionAllowed) {
            startActivity(new Intent(MapsExtendedActivity.this, MapsExtendedActivity.class));
            registerReceiver(mGpsSwitchStateReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        } else {
            registerReceiver(mGpsSwitchStateReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        }
    }

    private void checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            Toast.makeText(this, getString(R.string.loca), Toast.LENGTH_SHORT).show();
        } else if (isGPS) {
            buildGoogleApiClient();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String permissions[], @NotNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUESTED_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MapsExtendedActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUESTED_ACCESS_FINE_LOCATION);
                }
                Intent intent = new Intent();
                intent.setClass(MapsExtendedActivity.this, MapsExtendedActivity.class);
                startActivity(intent);
                finish();
            } else {
                closeActivityPopup();
            }
        }
    }

    private void closeActivityPopup() {
        final Dialog dialog = Utils.showCustomDialog(this, R.layout.permission_dialog);
        TextView ok = dialog.findViewById(R.id.btn_ok);
        ok.setOnClickListener(v -> {
            isPermissionAllowed = true;
            startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
            dialog.dismiss();
        });
        TextView cancel = dialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "OnMapReady");
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUESTED_ACCESS_FINE_LOCATION);
            return;
        }

        if (mMap != null) {
            mMap.getUiSettings().setAllGesturesEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.setOnCameraMoveStartedListener(i -> {
                mBinding.tvSorry.setVisibility(View.GONE);
                mBinding.cvAddress.setVisibility(View.GONE);
            });
            mMap.setOnCameraIdleListener(() -> {
                mCenterLatLong = mMap.getCameraPosition().target;
                try {
                    Geocoder geocoder = new Geocoder(MapsExtendedActivity.this);
                    List<Address> addresses = geocoder.getFromLocation(mCenterLatLong.latitude, mCenterLatLong.longitude, 1);

                    if (addresses != null) {
                        fullAddress = "";
                        displayAddress = "";
                        subLocality = "";
                        city = "";

                        if (addresses.get(0).getPostalCode() != null) {
                            zipCode = addresses.get(0).getPostalCode();
                        }
                        if (addresses.get(0).getLatitude() != 0.0) {
                            latitude = addresses.get(0).getLatitude();
                        }
                        if (addresses.get(0).getLongitude() != 0.0) {
                            longitude = addresses.get(0).getLongitude();
                        }

                        if (mBinding.etShopNumber.getText().toString().length() > 0) {
                            fullAddress = fullAddress + mBinding.etShopNumber.getText().toString();
                        }
                        if (addresses.get(0).getPremises() != null) {
                            premises = addresses.get(0).getPremises();
                            if (fullAddress.length() > 0) {
                                fullAddress = fullAddress + ", " + addresses.get(0).getPremises();
                            } else {
                                fullAddress = fullAddress + addresses.get(0).getPremises();
                            }
                            displayAddress = displayAddress + addresses.get(0).getPremises();
                        }
                        if (addresses.get(0).getSubLocality() != null) {
                            subLocality = addresses.get(0).getSubLocality();
                            if (fullAddress.length() > 0) {
                                fullAddress = fullAddress + ", " + addresses.get(0).getSubLocality();
                            } else {
                                fullAddress = fullAddress + addresses.get(0).getSubLocality();
                            }

                            if (displayAddress.length() > 0) {
                                displayAddress = displayAddress + ", " + addresses.get(0).getSubLocality();
                            } else {
                                displayAddress = displayAddress + addresses.get(0).getSubLocality();
                            }
                        }
                        if (addresses.get(0).getLocality() != null) {
                            city = addresses.get(0).getLocality();
                            if (fullAddress.length() > 0) {
                                fullAddress = fullAddress + ", " + addresses.get(0).getLocality();
                            } else {
                                fullAddress = fullAddress + addresses.get(0).getLocality();
                            }
                            if (displayAddress.length() > 0) {
                                displayAddress = displayAddress + ", " + addresses.get(0).getLocality();
                            } else {
                                displayAddress = displayAddress + addresses.get(0).getLocality();
                            }
                        }
                        if (addresses.get(0).getAdminArea() != null) {
                            customerState = addresses.get(0).getAdminArea();
                            if (fullAddress.length() > 0) {
                                fullAddress = fullAddress + ", " + addresses.get(0).getAdminArea();
                            } else {
                                fullAddress = fullAddress + addresses.get(0).getAdminArea();
                            }
                            if (displayAddress.length() > 0) {
                                displayAddress = displayAddress + ", " + addresses.get(0).getAdminArea();
                            } else {
                                displayAddress = displayAddress + addresses.get(0).getAdminArea();
                            }
                        }
                        if (addresses.get(0).getCountryName() != null) {
                            country = addresses.get(0).getCountryName();
                            fullAddress = fullAddress + ", " + addresses.get(0).getCountryName();
                            displayAddress = displayAddress + ", " + addresses.get(0).getCountryName();
                        }

                        if (subLocality == null || subLocality.isEmpty()) {
                            subLocality = "";
                        } else if (city == null) {
                            city = "";
                        } else if (country == null) {
                            country = "";
                        } else if (premises == null) {
                            premises = "";
                        }
                        if (displayAddress.length() > 0) {
//                            mBinding.cvAddress.setVisibility(View.VISIBLE);
                            mBinding.tvCurrentAddress.setText(displayAddress);
                            mBinding.tvCustAddress.setText(displayAddress + "," + zipCode);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.soory), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            if (!selectedLocation) {
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(16f).build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, this);
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            mGoogleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void exitApp(Context activity) {
        final Dialog dialog = Utils.showCustomDialog(activity, R.layout.exit_dialog);
        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        TextView ok = (TextView) dialog.findViewById(R.id.btn_ok);
        ok.setOnClickListener(v -> {
            dialog.dismiss();
            finishAffinity();
        });
        TextView cancel = (TextView) dialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

//    void getCityIdApiCall(String zip) {
//        if (utils.isNetworkAvailable()) {
//            if (commonClassForAPI != null) {
//                commonClassForAPI.fetchCityId(getZipCode, Integer.parseInt(zip));
//            }
//        } else {
//            Utils.setToast(this, getString(R.string.internet_connection));
//        }
//    }

    void confirmAddress(String shopNo, String landmark) {
        startActivity(new Intent(this, PersonalDetailsActivity.class)
                .putExtra(IntentKeys.MOBILE_NUMBER, mobileNumber)
                .putExtra(IntentKeys.PASSWORD, password)
                .putExtra(IntentKeys.BUSINESS_NAME, businessName)
                .putExtra(IntentKeys.BUSINESS_EMAIL, businessEmail)
                .putExtra(IntentKeys.SHOP_NUMBER, shopNo)
                .putExtra(IntentKeys.LANDMARK, landmark)
                .putExtra(IntentKeys.SUB_LOCALITY, subLocality)
                .putExtra(IntentKeys.CITY, city)
                .putExtra(IntentKeys.STATE, customerState)
                .putExtra(IntentKeys.COUNTRY, country)
                .putExtra(IntentKeys.ZIPCODE, Integer.parseInt(zipCode))
                .putExtra(IntentKeys.LATITUDE, latitude.toString())
                .putExtra(IntentKeys.LONGITUDE, longitude.toString())
                .putExtra(IntentKeys.FULL_ADDRESS, fullAddress));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGpsSwitchStateReceiver);
    }
}