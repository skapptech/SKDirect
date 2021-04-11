package com.skdirect.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skdirect.R;
import com.skdirect.adapter.NearSellerListAdapter;
import com.skdirect.databinding.ActivityMostVisitedSalleBinding;
import com.skdirect.databinding.ActivityNearSalleBinding;
import com.skdirect.databinding.ActivityNewSalleBinding;
import com.skdirect.model.NearBySallerModel;
import com.skdirect.model.NearBySellerMainModel;
import com.skdirect.model.PaginationModel;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.MostVisitedViewMode;
import com.skdirect.viewmodel.NewSellerViewMode;

import java.util.ArrayList;

public class NewSellerActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityNewSalleBinding mBinding;
    private NewSellerViewMode newSellerViewMode;
    private final ArrayList<NearBySallerModel> nearBySallerList = new ArrayList<>();
    private int skipCount = 0;
    private int takeCount = 10;
    private int pastVisiblesItems = 0;
    private int visibleItemCount = 0;
    private int totalItemCount = 0;
    private boolean loading = true;
    private String SearchKeybard;
    private NearSellerListAdapter nearSellerListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_salle);
        newSellerViewMode = ViewModelProviders.of(this).get(NewSellerViewMode.class);
        initView();
        callProductList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nearSellerListAdapter!=null){
            nearSellerListAdapter = new NearSellerListAdapter(this,nearBySallerList);
            mBinding.rvSellerProduct.setAdapter(nearSellerListAdapter);
            nearSellerListAdapter.notifyDataSetChanged();
        }
    }
    private void initView() {
        mBinding.shimmerViewContainer.startShimmer();
        mBinding.toolbarTittle.tvTittle.setText("Seller List");
        mBinding.toolbarTittle.ivBackPress.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL,false);
        mBinding.rvSellerProduct.setLayoutManager(layoutManager);
        nearSellerListAdapter = new NearSellerListAdapter(getApplicationContext(),nearBySallerList);
        mBinding.rvSellerProduct.setAdapter(nearSellerListAdapter);

        mBinding.rvSellerProduct.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        nearBySallerList.clear();

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
            getProductListAPI();
        } else {
            Utils.setToast(getApplicationContext(), "No Internet Connection Please connect.");
        }
    }


    private void getProductListAPI() {
        newSellerViewMode.getSellerProductListRequest(new PaginationModel(skipCount,takeCount,SearchKeybard));
        newSellerViewMode.getSellerProductListVM().observe(this, new Observer<NearBySellerMainModel>() {
            @Override
            public void onChanged(NearBySellerMainModel nearBySaller) {
                if (nearBySaller.isSuccess()){
                    mBinding.shimmerViewContainer.stopShimmer();
                    mBinding.shimmerViewContainer.setVisibility(View.GONE);
                    if (nearBySaller.getResultItem().size()>0){
                        mBinding.rvSellerProduct.post(new Runnable() {
                            public void run() {
                                nearBySallerList.addAll(nearBySaller.getResultItem());
                                nearSellerListAdapter.notifyDataSetChanged();
                                loading = true;
                            }
                        });
                    }else
                    {
                        loading = false;
                    }
                }

            }
        });
    }
}