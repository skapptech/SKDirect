package com.skdirect.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.skdirect.R;
import com.skdirect.activity.CategoriesListActivity;
import com.skdirect.activity.GenerateOTPActivity;
import com.skdirect.activity.LoginActivity;
import com.skdirect.activity.MainActivity;
import com.skdirect.activity.NearByItemProductListActivity;
import com.skdirect.activity.SearchActivity;
import com.skdirect.activity.SellerProductListActivity;
import com.skdirect.activity.SplashActivity;
import com.skdirect.adapter.AllCategoriesAdapter;
import com.skdirect.adapter.TopNearByItemAdapter;
import com.skdirect.adapter.TopSellerAdapter;
import com.skdirect.databinding.FragmentHomeBinding;
import com.skdirect.model.AllCategoriesModel;
import com.skdirect.model.CartItemModel;
import com.skdirect.model.CustomerDataModel;
import com.skdirect.model.LoginResponseModel;
import com.skdirect.model.TopNearByItemModel;
import com.skdirect.model.TopSellerModel;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        initViews();
        topNearByItem();
        getSellerAPi();
        getAllCategoriesAPi();


        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        mBinding.etSearchSeller.setText("");
    }

    private void getAllCategoriesAPi() {
        if (Utils.isNetworkAvailable(activity)) {
            Utils.showProgressDialog(activity);
            getTopSeller();
        } else {
            Utils.setToast(activity, "No Internet Connection Please connect.");
        }
    }

    private void getSellerAPi() {
        if (Utils.isNetworkAvailable(activity)) {
            Utils.showProgressDialog(activity);
            getAllCategories();
        } else {
            Utils.setToast(activity, "No Internet Connection Please connect.");
        }
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
                    TopNearByItemAdapter topNearByItemAdapter = new TopNearByItemAdapter(getActivity(),topNearByItemList);
                    mBinding.rvNearByItem.setAdapter(topNearByItemAdapter);

                }else {
                    mBinding.llNearByNotFound.setVisibility(View.VISIBLE);
                    mBinding.rvNearByItem.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getTopSeller() {
        homeViewModel.GetTopSellerLiveData().observe(this, new Observer<ArrayList<TopSellerModel>>() {
            @Override
            public void onChanged(ArrayList<TopSellerModel> topSellerList) {
                Utils.hideProgressDialog();
                if (topSellerList.size()>0){
                    TopSellerAdapter topSellerAdapter = new TopSellerAdapter(getActivity(),topSellerList);
                    mBinding.rvTopSeller.setAdapter(topSellerAdapter);

                }else {
                    mBinding.llTopSellerNotFound.setVisibility(View.VISIBLE);
                    mBinding.rvTopSeller.setVisibility(View.GONE);
                }

            }
        });
    }




    private void getAllCategories() {
        homeViewModel.getAllCategoriesLiveData().observe(this, new Observer<ArrayList<AllCategoriesModel>>() {
            @Override
            public void onChanged(ArrayList<AllCategoriesModel> allCategoriesList) {

                if (allCategoriesList.size()>0){
                    AllCategoriesAdapter allCategoriesAdapter = new AllCategoriesAdapter(getActivity(),allCategoriesList);
                    mBinding.rvAllCetegory.setAdapter(allCategoriesAdapter);
                }else {
                    mBinding.llAllCetegoryNotFound.setVisibility(View.VISIBLE);
                    mBinding.rvAllCetegory.setVisibility(View.GONE);
                }


            }
        });

    }

    private void initViews() {
        mBinding.rvNearByItem.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        mBinding.rvTopSeller.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        mBinding.rvAllCetegory.setLayoutManager(new GridLayoutManager(activity,3));
        activity.appBarLayout.setVisibility(View.VISIBLE);
        if (Utils.isNetworkAvailable(activity)) {
            Utils.showProgressDialog(activity);
            getLoginData();
        } else {
            Utils.setToast(activity, "No Internet Connection Please connect.");
        }

        mBinding.tvNearByViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), NearByItemProductListActivity.class));
            }
        });

        mBinding.tvNearBySellar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SellerProductListActivity.class));
            }
        });

        mBinding.tvCetegory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CategoriesListActivity.class));
            }
        });

        mBinding.etSearchSeller.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchData();
                    handled = true;
                }
                return handled;
            }
        });

    }

    private void searchData() {
        String searchSellerName = mBinding.etSearchSeller.getText().toString().trim();
        startActivity(new Intent(getActivity(), SearchActivity.class).putExtra("searchSellerName",searchSellerName));


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
                    SharePrefs.getInstance(activity).putInt(SharePrefs.ID, customerDataModel.getId());
                    SharePrefs.getInstance(activity).putString(SharePrefs.MOBILE_NUMBER, customerDataModel.getMobileNo());
                    SharePrefs.getInstance(activity).putString(SharePrefs.SHOP_NAME, customerDataModel.getShopName());
                    SharePrefs.getInstance(activity).putString(SharePrefs.EMAIL_ID, customerDataModel.getEmail());
                    SharePrefs.getInstance(activity).putString(SharePrefs.STATE, customerDataModel.getState());
                    SharePrefs.getInstance(activity).putString(SharePrefs.CITYNAME, customerDataModel.getCity());
                    SharePrefs.getInstance(activity).putString(SharePrefs.PIN_CODE, customerDataModel.getPincode());
                    SharePrefs.getInstance(activity).putBoolean(SharePrefs.IS_ACTIVE, customerDataModel.isActive());
                    SharePrefs.getInstance(activity).putBoolean(SharePrefs.IS_DELETE, customerDataModel.isDelete());
                    SharePrefs.getInstance(activity).putBoolean(SharePrefs.IS_REGISTRATIONCOMPLETE, customerDataModel.isRegistrationComplete());
                }

            }
        });

    }


}
