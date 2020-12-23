package com.skdirect.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.skdirect.R;
import com.skdirect.activity.GenerateOTPActivity;
import com.skdirect.activity.LoginActivity;
import com.skdirect.activity.MainActivity;
import com.skdirect.activity.SplashActivity;
import com.skdirect.adapter.TopNearByItemAdapter;
import com.skdirect.databinding.FragmentHomeBinding;
import com.skdirect.model.CustomerDataModel;
import com.skdirect.model.LoginResponseModel;
import com.skdirect.model.TopNearByItemModel;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.HomeViewModel;
import com.skdirect.viewmodel.LoginViewModel;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding mBinding;
    private MainActivity activity;
    private HomeViewModel homeViewModel;



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        initViews();
        topNearByItem();


        return mBinding.getRoot();
    }

    private void topNearByItem() {
        if (Utils.isNetworkAvailable(activity)) {
            Utils.showProgressDialog(activity);
            getNearByItem();
        } else {
            Utils.setToast(activity, "No Internet Connection Please connect.");
        }
    }

    private void getNearByItem() {
        homeViewModel.GetTopNearByItem().observe(this, new Observer<ArrayList<TopNearByItemModel>>() {
            @Override
            public void onChanged(ArrayList<TopNearByItemModel> topNearByItemList) {
                Utils.hideProgressDialog();
                if (topNearByItemList.size()>0){
                    TopNearByItemAdapter topNearByItemAdapter = new TopNearByItemAdapter(getContext(),topNearByItemList);
                    mBinding.rvNearByItem.setAdapter(topNearByItemAdapter);

                }
            }
        });
    }

    private void initViews() {
        mBinding.rvNearByItem.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        activity.appBarLayout.setVisibility(View.VISIBLE);
        if (Utils.isNetworkAvailable(activity)) {
            Utils.showProgressDialog(activity);
            getLoginData();
        } else {
            Utils.setToast(activity, "No Internet Connection Please connect.");
        }

    }

    private void getLoginData() {
        homeViewModel.GetUserDetail().observe(this, new Observer<CustomerDataModel>() {
            @Override
            public void onChanged(CustomerDataModel customerDataModel) {
                Utils.hideProgressDialog();
                if (customerDataModel!=null){
                    activity.userNameTV.setText(customerDataModel.getFirstName());
                    activity.mobileNumberTV.setText(customerDataModel.getMobileNo());
                    SharePrefs.getInstance(activity).putString(SharePrefs.FIRST_NAME, customerDataModel.getFirstName());
                    SharePrefs.getInstance(activity).putString(SharePrefs.MOBILE_NUMBER, customerDataModel.getMobileNo());
                    SharePrefs.getInstance(activity).putString(SharePrefs.SHOP_NAME, customerDataModel.getShopName());
                    SharePrefs.getInstance(activity).putString(SharePrefs.EMAIL_ID, customerDataModel.getEmail());
                    SharePrefs.getInstance(activity).putString(SharePrefs.STATE, customerDataModel.getState());
                    SharePrefs.getInstance(activity).putString(SharePrefs.CITY, customerDataModel.getCity());
                    SharePrefs.getInstance(activity).putBoolean(SharePrefs.IS_REGISTRACTION, customerDataModel.isRegistrationComplete());
                }

            }
        });

    }


}
