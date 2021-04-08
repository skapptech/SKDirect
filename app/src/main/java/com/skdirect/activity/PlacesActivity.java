package com.skdirect.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.skdirect.R;
import com.skdirect.adapter.PlacesAutoCompleteMapsAdapter;
import com.skdirect.databinding.ActivityPlacesBinding;


/**
 * Created by Pritesh on 17/07/2020.
 **/

public class PlacesActivity extends AppCompatActivity implements PlacesAutoCompleteMapsAdapter.ClickListener {
    public ActivityPlacesBinding binding;
    private PlacesAutoCompleteMapsAdapter mAutoCompleteAdapter;
    private Boolean searchCity = false;




    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        if (overrideConfiguration != null) {
            int uiMode = overrideConfiguration.uiMode;
            overrideConfiguration.setTo(getBaseContext().getResources().getConfiguration());
            overrideConfiguration.uiMode = uiMode;
        }
        super.applyOverrideConfiguration(overrideConfiguration);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_places);
        binding.llGps.setOnClickListener(V -> {
            startActivityForResult(new Intent(PlacesActivity.this, MapsExtendedActivity.class),2000);
            finish();
        });
        binding.ivBack.setOnClickListener(V -> finish());
        initPlaces();
    }

    public void initPlaces() {
        Places.initialize(this, getResources().getString(R.string.google_maps_key));
        binding.etSearchPlace.addTextChangedListener(filterTextWatcher);
        mAutoCompleteAdapter = new PlacesAutoCompleteMapsAdapter(this, searchCity);
        binding.rvPlaces.setLayoutManager(new LinearLayoutManager(this));
        mAutoCompleteAdapter.setClickListener(this);
        binding.rvPlaces.setAdapter(mAutoCompleteAdapter);
        mAutoCompleteAdapter.notifyDataSetChanged();
        binding.etSearchPlace.requestFocus();
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            if (!s.toString().equals("")) {
                if (searchCity) {
                    mAutoCompleteAdapter.getFilter().filter(s.toString());
                } else {
                    mAutoCompleteAdapter.getFilter().filter(s.toString());
                }
                binding.placesProgress.setVisibility(View.VISIBLE);
                binding.ivCancel.setVisibility(View.GONE);
                binding.llPlaces.setVisibility(View.VISIBLE);
            } else {
                binding.placesProgress.setVisibility(View.GONE);
                binding.ivCancel.setVisibility(View.GONE);
                binding.llPlaces.setVisibility(View.GONE);
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    @Override
    public void click(Place place) {
        try {
            LatLng latLng = place.getLatLng();

            Intent returnIntent = new Intent();
            assert latLng != null;

            returnIntent.putExtra("latitude", latLng.latitude);
            returnIntent.putExtra("longitude", latLng.longitude);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}