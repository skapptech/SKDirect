package com.skdirect.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.skdirect.R;
import com.skdirect.adapter.ViewPagerAdapter;
import com.skdirect.databinding.ActivitySearchBinding;
import com.skdirect.fragment.ProductFragment;
import com.skdirect.fragment.ShopFragment;
import com.skdirect.utils.Utils;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    ActivitySearchBinding mBinding;
    private String searchSellerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        getIntentData();
        initView();
        setupViewPager(mBinding.viewpager);
    }


    public void getIntentData() {
        searchSellerName = getIntent().getStringExtra("searchSellerName");
        mBinding.etSearchSeller.setText(searchSellerName);
        mBinding.tabs.setupWithViewPager(mBinding.viewpager);
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new ProductFragment(searchSellerName), "Product");
        adapter.addFragment(new ShopFragment(searchSellerName), "Shop");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);

    }




    private void initView() {
        mBinding.toolbarTittle.tvTittle.setText("Product List");
        mBinding.toolbarTittle.ivBackPress.setOnClickListener(this);



        /*sellerShopListAdapter = new SellerShopListAdapter(getApplicationContext(),sallerShopList);
        mBinding.rvSearch.setAdapter(sellerShopListAdapter);*/




       /* mBinding.etSearchSeller.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchSellerName = mBinding.etSearchSeller.getText().toString().trim();
                        loadingData();
                    handled = true;
                }
                return handled;
            }
        });*/


                                               }

   /* private void loadingData() {
        list.clear();
        searchDataAdapter.notifyDataSetChanged();
        getSearchData();
    }


    private void shopItemData() {
        list.clear();
        searchDataAdapter.notifyDataSetChanged();
        callShopAPi();
    }*/

        @Override
        public void onClick (View view){
            switch (view.getId()) {
                case R.id.iv_back_press:
                    onBackPressed();
                    break;
            }
        }

   /* private void callShopAPi() {
        if (Utils.isNetworkAvailable(this)) {
            Utils.showProgressDialog(this);
            getShopData();
        } else {
            Utils.setToast(this, "No Internet Connection Please connect.");
        }
    }*/


  /*  private void getShopData() {
        searchViewMode.getShopDataViewModelRequest(skipCount,takeCount,searchSellerName);
        searchViewMode.getShopDataViewModel().observe(this, new Observer<ArrayList<TopSellerModel>>() {
            @Override
            public void onChanged(ArrayList<TopSellerModel> topSellerModels) {
                Utils.hideProgressDialog();
                if (topSellerModels.size()>0){
                    mBinding.rvSearch.post(new Runnable() {
                        public void run() {
                            sallerShopList.addAll(topSellerModels);
                            sellerShopListAdapter.notifyDataSetChanged();
                            loading = true;
                        }
                    });

                }else
                {
                    loading = false;
                }
            }
        });
    }*/

    }