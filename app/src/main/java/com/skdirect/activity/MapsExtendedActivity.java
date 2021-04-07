package com.skdirect.activity;

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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

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
import com.google.gson.JsonObject;
import com.skdirect.R;
import com.skdirect.api.CommonClassForAPI;
import com.skdirect.databinding.ActivityMapsExtendedBinding;
import com.skdirect.utils.GpsUtils;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.MapViewViewMode;


import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class MapsExtendedActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private ActivityMapsExtendedBinding mBinding;
    Utils utils;
    CommonClassForAPI commonClassForAPI;
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
    private MapViewViewMode mapViewViewMode;



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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_maps_extended);
        initializeViews();
        checkPlayServices();
    }

    private void initializeViews() {
        utils = new Utils(MapsExtendedActivity.this);
        mapViewViewMode = ViewModelProviders.of(this).get(MapViewViewMode.class);
        commonClassForAPI = CommonClassForAPI.getInstance(MapsExtendedActivity.this);

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
            if (Utils.isNetworkAvailable(getApplicationContext())) {
                Utils.showProgressDialog(MapsExtendedActivity.this);
                callLocationAPI(latitude, longitude);
            } else {
                Utils.setToast(getApplicationContext(), "No Internet Connection Please connect.");
            }

        });

        new GpsUtils(this).turnGPSOn(isGPSEnable -> {
            // turn on GPS
            isGPS = isGPSEnable;
        });

        mBinding.tvChangeMe.setOnClickListener(V -> startActivityForResult(new Intent(this, PlacesActivity.class), LAUNCH_PLACES_ACTIVITY));


    }
    private void callLocationAPI(double latitude, double longitude) {
        mapViewViewMode.getMapViewModelRequest(latitude, longitude);
        mapViewViewMode.getMapViewModel().observe(this, new Observer<JsonObject>() {
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
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.CITYNAME,cityName);
                    SharePrefs.setStringSharedPreference(getApplicationContext(),SharePrefs.LAT,String.valueOf(lat));
                    SharePrefs.setStringSharedPreference(getApplicationContext(),SharePrefs.LON, String.valueOf(lng));
                    Log.e("cityName", "cityName  "+cityName);
                    startActivity(new Intent(MapsExtendedActivity.this,MainActivity.class));
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });


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
            Toast.makeText(this,"Location", Toast.LENGTH_SHORT).show();
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
            }
        }
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
                    "Sorry", Toast.LENGTH_SHORT)
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGpsSwitchStateReceiver);
    }
}