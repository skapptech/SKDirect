package com.skdirect.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skdirect.R;
import com.skdirect.activity.MainActivity;
import com.skdirect.activity.SearchActivity;
import com.skdirect.adapter.SearchDataAdapter;
import com.skdirect.adapter.SellerShopListAdapter;
import com.skdirect.databinding.FragmentProductBinding;
import com.skdirect.databinding.FragmentShopBinding;
import com.skdirect.model.ShopMainModel;
import com.skdirect.model.TopSellerModel;
import com.skdirect.utils.MyApplication;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.SearchViewMode;

import java.util.ArrayList;

public class ShopFragment extends Fragment {
    private FragmentShopBinding mBinding;
    private int skipCount = 0;
    private int takeCount = 7;
    private int pastVisiblesItems = 0;
    private int visibleItemCount = 0;
    private int totalItemCount = 0;
    private boolean loading = true;
    private SearchViewMode searchViewMode;
    private String searchSellerName;
    private SellerShopListAdapter sellerShopListAdapter;
    private final ArrayList<TopSellerModel> sallerShopList = new ArrayList<>();
    private int cateogryId;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);


    }
    @SuppressLint("ValidFragment")
    public ShopFragment(String searchSellName, int allCategoriesID) {
        searchSellerName = searchSellName;
        cateogryId = allCategoriesID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_shop, container, false);
        searchViewMode = ViewModelProviders.of(this).get(SearchViewMode.class);
        initViews();
        callShopAPi();

        return mBinding.getRoot();
    }



    private void initViews() {
        mBinding.tvNotDataFound.setText(MyApplication.getInstance().dbHelper.getString(R.string.no_data_found));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mBinding.rvSearch.setLayoutManager(layoutManager);
        sellerShopListAdapter = new SellerShopListAdapter(getActivity(), sallerShopList);
        mBinding.rvSearch.setAdapter(sellerShopListAdapter);




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
                            callShopAPi();
                        }
                    }
                }
            }
        });
        sallerShopList.clear();

    }


    private void callShopAPi() {
        if (Utils.isNetworkAvailable(getActivity())) {
            Utils.showProgressDialog(getActivity());
            getShopData();
        } else {
            Utils.setToast(getActivity(), MyApplication.getInstance().dbHelper.getString(R.string.no_internet_connection));
        }

    }

    private void getShopData() {
        searchViewMode.getShopDataViewModelRequest(skipCount, takeCount, searchSellerName,(String.valueOf(cateogryId).equals("0")) ? null : String.valueOf(cateogryId));
        searchViewMode.getShopDataViewModel().observe(this, new Observer<ShopMainModel>() {
            @Override
            public void onChanged(ShopMainModel shopMainModel) {
                Utils.hideProgressDialog();
                if (shopMainModel.isSuccess()) {
                    if (shopMainModel != null && shopMainModel.getResultItem().size() > 0) {
                        mBinding.rvSearch.post(new Runnable() {
                            public void run() {
                                sallerShopList.addAll(shopMainModel.getResultItem());
                                sellerShopListAdapter.notifyDataSetChanged();
                                loading = true;
                            }
                        });
                    } else {
                        if (sallerShopList.size() == 0) {
                            mBinding.rvSearch.setVisibility(View.GONE);
                            mBinding.tvNotDataFound.setVisibility(View.VISIBLE);
                        }
                    }
                }else {
                    if (sallerShopList.size() == 0) {
                        mBinding.rvSearch.setVisibility(View.GONE);
                        mBinding.tvNotDataFound.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
}
