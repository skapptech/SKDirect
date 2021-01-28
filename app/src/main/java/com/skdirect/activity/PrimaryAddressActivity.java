package com.skdirect.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skdirect.R;
import com.skdirect.adapter.UserLocationAdapter;
import com.skdirect.databinding.ActivityPrimaryAddressBinding;
import com.skdirect.model.UserLocationModel;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.PrimaryAddressViewMode;

import java.util.ArrayList;

public class PrimaryAddressActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityPrimaryAddressBinding mBinding;
    private PrimaryAddressViewMode primaryAddressViewMode;
    private ArrayList<UserLocationModel> locationModelArrayList = new ArrayList<>();
    private UserLocationAdapter userLocationAdapter;
    private int userLocationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_primary_address);
        primaryAddressViewMode = ViewModelProviders.of(this).get(PrimaryAddressViewMode.class);
        getSharedData();
        initView();
        callUserLocation();

    }

    @Override
    protected void onResume() {
        super.onResume();
        callUserLocation();
    }

    private void getSharedData() {
        userLocationId = getIntent().getIntExtra("UserLocationId", 0);
    }

    private void initView() {
        mBinding.toolbarTittle.ivBackPress.setOnClickListener(this);
        mBinding.btSelectedProcess.setOnClickListener(this);
        mBinding.btAddNewAddresh.setOnClickListener(this);
        mBinding.toolbarTittle.tvTittle.setText("Address");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mBinding.rvUserLocation.setLayoutManager(layoutManager);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_press:
                onBackPressed();
                break;

            case R.id.bt_selected_process:
                Intent intent = new Intent();
                intent.putExtra("address", userLocationAdapter.getSelectedData());
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;

            case R.id.bt_add_new_addresh:
              startActivity(new Intent(PrimaryAddressActivity.this,NewAddressActivity.class));
                break;
        }
    }

    private void callUserLocation() {
        if (Utils.isNetworkAvailable(getApplicationContext())) {
            Utils.showProgressDialog(PrimaryAddressActivity.this);
            userLocationAPI();
        } else {
            Utils.setToast(getApplicationContext(), "No Internet Connection Please connect.");
        }
    }

    private void userLocationAPI() {
        primaryAddressViewMode.getUserLocationVMRequest();
        primaryAddressViewMode.getUserLocationVM().observe(this, new Observer<ArrayList<UserLocationModel>>() {
            @Override
            public void onChanged(ArrayList<UserLocationModel> locationModel) {
                Utils.hideProgressDialog();
                if (locationModel != null) {
                    if (locationModel.size() > 0) {
                        locationModelArrayList.addAll(locationModel);
                        int position = 0;
                        for (int i = 0; i < locationModelArrayList.size(); i++) {
                            if (locationModelArrayList.get(i).getId() == userLocationId) {
                                position = i;
                                break;
                            }
                        }
                        userLocationAdapter = new UserLocationAdapter(PrimaryAddressActivity.this, locationModelArrayList, position);
                        mBinding.rvUserLocation.setAdapter(userLocationAdapter);
                    }

                }
            }
        });
    }
}
