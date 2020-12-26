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

import com.google.gson.JsonArray;
import com.skdirect.R;
import com.skdirect.adapter.CategoriesAdapter;
import com.skdirect.adapter.SearchDataAdapter;
import com.skdirect.databinding.ActivitySearchBinding;
import com.skdirect.model.AllCategoriesModel;
import com.skdirect.model.PaginationModel;
import com.skdirect.model.SearchDataModel;
import com.skdirect.model.SearchRequestModel;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.CategoriesViewMode;
import com.skdirect.viewmodel.SearchViewMode;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    ActivitySearchBinding mBinding;
    private String searchSellerName;
    private SearchViewMode searchViewMode;
    private ArrayList<SearchDataModel.TableOneModel> searchListOne = new ArrayList<>();
    private ArrayList<SearchDataModel.TableOneTwo> searchListTwo = new ArrayList<>();
    private int skipCount = 0;
    private int takeCount = 7;
    private int pastVisiblesItems = 0;
    private int visibleItemCount = 0;
    private int totalItemCount = 0;
    private boolean loading = true;
    private  SearchDataAdapter searchDataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        searchViewMode = ViewModelProviders.of(this).get(SearchViewMode.class);
        getIntentData();
        initView();
        callSearchAPi();
    }


    private void getIntentData() {
        searchSellerName = getIntent().getStringExtra("searchSellerName");
        mBinding.etSearchSeller.setText(searchSellerName);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (searchDataAdapter!=null){
            searchDataAdapter = new SearchDataAdapter(getApplicationContext(),searchListOne,searchListTwo);
            mBinding.rvSearch.setAdapter(searchDataAdapter);
            searchDataAdapter.notifyDataSetChanged();
        }
    }

    private void initView() {

        mBinding.toolbarTittle.tvTittle.setText("Product List");
        mBinding.toolbarTittle.ivBackPress.setOnClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL,false);
        mBinding.rvSearch.setLayoutManager(layoutManager);
        SearchDataAdapter searchDataAdapter = new SearchDataAdapter(getApplicationContext(),searchListOne,searchListTwo);
        mBinding.rvSearch.setAdapter(searchDataAdapter);

        mBinding.rvSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            skipCount=skipCount+7;
                            // mBinding.progressBar.setVisibility(View.VISIBLE);
                            getSearchData();
                        }
                    }
                }
            }
        });
        searchListOne.clear();
        searchListTwo.clear();
        getSearchData();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_press:
                onBackPressed();
                break;
        }
    }


    private void callSearchAPi() {
        if (Utils.isNetworkAvailable(this)) {
            Utils.showProgressDialog(this);
            getSearchData();
        } else {
            Utils.setToast(this, "No Internet Connection Please connect.");
        }
    }

    private void getSearchData() {
        SearchRequestModel searchRequestModel = new SearchRequestModel(0,0,0,0,takeCount,0.0,0.0,"","","",0,searchSellerName);
        searchViewMode.getSearchRequest(searchRequestModel);
        searchViewMode.getSearchViewModel().observe(this, new Observer<SearchDataModel>() {
            @Override
            public void onChanged(SearchDataModel searchDataList) {
                Utils.hideProgressDialog();
                if (searchDataList!=null){
                    if (searchDataList.getTableOneModels().size()>0){
                        mBinding.rvSearch.post(new Runnable() {
                            public void run() {
                                searchListOne.addAll(searchDataList.getTableOneModels());
                                searchListTwo.addAll(searchDataList.getTableTwoModels());
                               // searchDataAdapter.notifyDataSetChanged();
                                loading = true;
                            }
                        });
                    }

                }

            }
        });

    }
}
