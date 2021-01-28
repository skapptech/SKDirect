package com.skdirect.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.skdirect.R;
import com.skdirect.adapter.NearProductListAdapter;
import com.skdirect.adapter.TopNearByItemAdapter;
import com.skdirect.databinding.ActivityProductListBinding;
import com.skdirect.model.NearProductListModel;
import com.skdirect.model.PaginationModel;
import com.skdirect.model.TopNearByItemModel;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.HomeViewModel;
import com.skdirect.viewmodel.LoginViewModel;
import com.skdirect.viewmodel.NearProductListViewMode;

import java.util.ArrayList;

public class NearByItemProductListActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityProductListBinding mBinding;
    private NearProductListViewMode nearProductListViewMode;
    private  ArrayList<NearProductListModel> nearProductList = new ArrayList<>();
    private int skipCount = 0;
    private int takeCount = 10;
    private int pastVisiblesItems = 0;
    private int visibleItemCount = 0;
    private int totalItemCount = 0;
    private boolean loading = true;
    private NearProductListAdapter nearProductListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_product_list);
        nearProductListViewMode = ViewModelProviders.of(this).get(NearProductListViewMode.class);
        initView();
        callProductList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nearProductListAdapter!=null){
            nearProductListAdapter = new NearProductListAdapter(getApplicationContext(),nearProductList);
            mBinding.rvNearByProduct.setAdapter(nearProductListAdapter);
            nearProductListAdapter.notifyDataSetChanged();
        }
    }
    private void initView() {
        mBinding.toolbarTittle.tvTittle.setText("Product List");
        mBinding.toolbarTittle.ivBackPress.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL,false);
        mBinding.rvNearByProduct.setLayoutManager(layoutManager);
        nearProductListAdapter = new NearProductListAdapter(getApplicationContext(),nearProductList);
        mBinding.rvNearByProduct.setAdapter(nearProductListAdapter);

        mBinding.rvNearByProduct.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                    if (loading) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            loading = false;
                            skipCount=skipCount+10;
                           // mBinding.progressBar.setVisibility(View.VISIBLE);
                            callProductList();
                        }
                    }
                }
            }
        });
        nearProductList.clear();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_press:
                onBackPressed();
                break;
        }
    }

    private void callProductList() {
        if (Utils.isNetworkAvailable(getApplicationContext())) {
            Utils.showProgressDialog(NearByItemProductListActivity.this);
            getProductListAPI();
        } else {
            Utils.setToast(getApplicationContext(), "No Internet Connection Please connect.");
        }
    }


    private void getProductListAPI() {
        PaginationModel paginationModel = new PaginationModel(0,0,0,skipCount,takeCount,0.0,0.0,0,"");
        nearProductListViewMode.getNearProductListRequest(paginationModel);
        nearProductListViewMode.getNearProductList().observe(this, new Observer<ArrayList<NearProductListModel>>() {
            @Override
            public void onChanged(ArrayList<NearProductListModel> nearProductListList) {
                Utils.hideProgressDialog();
                if (nearProductListList.size()>0){
                    mBinding.rvNearByProduct.post(new Runnable() {
                        public void run() {
                            nearProductList.addAll(nearProductListList);
                            nearProductListAdapter.notifyDataSetChanged();
                            loading = true;
                        }
                    });

                }else
                {
                    loading = false;
                }
            }
        });


    }
}
