package com.skdirect.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.skdirect.R;
import com.skdirect.adapter.PlacesAutoCompleteAdapter;
import com.skdirect.databinding.ActivityUserLocationBinding;
import com.skdirect.utils.MyApplication;
import com.skdirect.utils.TextUtils;
import com.skdirect.utils.Utils;

public class UserLocationActvity extends AppCompatActivity implements PlacesAutoCompleteAdapter.ClickListener {
    private ActivityUserLocationBinding mBinding;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private String cityName = "";
    private boolean searchCity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_location);
        mBinding.toolbarTittle.ivBackPress.setOnClickListener(view -> onBackPressed());


        if (getIntent() != null) {
            cityName = getIntent().getStringExtra("cityname");
            searchCity = getIntent().getBooleanExtra("searchCity", false);
        }


        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));

        //mBinding.etSearchPlace.addTextChangedListener(filterTextWatcher);
        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this, searchCity);
        mAutoCompleteAdapter.setClickListener(this);
        mBinding.placesRecyclerView.setAdapter(mAutoCompleteAdapter);
        mAutoCompleteAdapter.notifyDataSetChanged();
        mBinding.etSearchPlace.requestFocus();

        mBinding.imSearchPlace.setVisibility(View.VISIBLE);
        mBinding.progressSearch.setVisibility(View.INVISIBLE);

        mBinding.etSearchPlace.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchPlace();
                return true;
            }
            return false;
        });
        mBinding.imSearchPlace.setOnClickListener(view -> searchPlace());
    }

    @Override
    public void click(Place place) {
        Intent intent = new Intent();
        intent.putExtra("PlaceResult", place);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void searchPlace() {
        String s = mBinding.etSearchPlace.getText().toString();
        if (TextUtils.isNullOrEmpty(s)) {
            if (searchCity) {
                Utils.setToast(getApplicationContext(), "Please enter city name");
            } else {
                Utils.setToast(getApplicationContext(), "Please enter address");
            }
        } else {
            mBinding.imSearchPlace.setVisibility(View.INVISIBLE);
            mBinding.progressSearch.setVisibility(View.VISIBLE);
            if (searchCity) {
                mAutoCompleteAdapter.getFilter().filter(s);
            } else {
                mAutoCompleteAdapter.getFilter().filter(cityName + ", " + s);
            }
        }
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            mBinding.imSearchPlace.setVisibility(View.VISIBLE);
            mBinding.progressSearch.setVisibility(View.INVISIBLE);
        }, 2000);
    }
}
