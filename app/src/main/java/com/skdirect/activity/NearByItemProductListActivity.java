package com.skdirect.activity;

import android.content.Intent;
import android.nfc.Tag;
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
import com.skdirect.model.NearProductListMainModel;
import com.skdirect.model.NearProductListModel;
import com.skdirect.model.PaginationModel;
import com.skdirect.model.TopNearByItemModel;
import com.skdirect.utils.Constant;
import com.skdirect.utils.DBHelper;
import com.skdirect.utils.MyApplication;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.HomeViewModel;
import com.skdirect.viewmodel.LoginViewModel;
import com.skdirect.viewmodel.NearProductListViewMode;

import org.json.JSONException;
import org.json.JSONObject;

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
    private final int  REQUEST = 100;
    public DBHelper dbHelper;
    public String searchKeyBord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_product_list);
        nearProductListViewMode = ViewModelProviders.of(this).get(NearProductListViewMode.class);
        dbHelper = MyApplication.getInstance().dbHelper;
        initView();
        callProductList();
        setString();
    }
    private void setString() {
        mBinding.tvSort.setText(dbHelper.getString(R.string.sort));
        mBinding.tvFilter.setText(dbHelper.getString(R.string.filter));
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
        mBinding.toolbarTittle.tvTittle.setText(dbHelper.getString(R.string.product_list));
        mBinding.toolbarTittle.ivBackPress.setOnClickListener(this);
        mBinding.shimmerViewContainer.startShimmer();

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

        mBinding.llFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FilterActivity.class);
                startActivityForResult(intent, REQUEST);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST) {
            if (data != null && resultCode == RESULT_OK) {
                int category = data.getExtras().getInt(Constant.Category);
                int priceMin = data.getExtras().getInt(Constant.PriceMin);
                int priceMax = data.getExtras().getInt(Constant.PriceMax);
                String brand = data.getExtras().getString(Constant.Brands);
                String discount = data.getExtras().getString(Constant.Discount);
                Log.e("ProductList","Cate>>"+category+"\n Price Min>>"+priceMin+
                        "\n Price Max>>"+priceMax+"\n Brand>>"+brand+"\n Discount>>"+discount);
            }else
            {
                System.out.println("Canceld by user");
            }

        }
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
            Utils.setToast(getApplicationContext(), dbHelper.getString(R.string.no_connection));
        }
    }


    private void getProductListAPI() {
        PaginationModel paginationModel = new PaginationModel(skipCount,takeCount,searchKeyBord);
        nearProductListViewMode.getNearProductListRequest(paginationModel);
        nearProductListViewMode.getNearProductList().observe(this, new Observer<NearProductListMainModel>() {
            @Override
            public void onChanged(NearProductListMainModel nearProductListList) {
                if (nearProductListList.isSuccess()) {
                    mBinding.shimmerViewContainer.stopShimmer();
                    mBinding.shimmerViewContainer.setVisibility(View.GONE);
                    if (nearProductListList.getResultItem().size() > 0) {
                        mBinding.rvNearByProduct.post(new Runnable() {
                            public void run() {
                                nearProductList.addAll(nearProductListList.getResultItem());
                                nearProductListAdapter.notifyDataSetChanged();
                                loading = true;
                            }
                        });

                    } else {
                        loading = false;
                    }
                }
            }
        });
    }
}
