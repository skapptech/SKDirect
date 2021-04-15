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
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skdirect.R;
import com.skdirect.activity.SearchActivity;
import com.skdirect.adapter.SearchDataAdapter;
import com.skdirect.databinding.FragmentProductBinding;
import com.skdirect.interfacee.SearchInterface;
import com.skdirect.model.SearchDataModel;
import com.skdirect.model.SearchRequestModel;
import com.skdirect.utils.MyApplication;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.SearchViewMode;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ProductFragment extends Fragment implements SearchInterface {
    private FragmentProductBinding mBinding;
    private SearchActivity activity;

    private final ArrayList<SearchDataModel.TableOneTwo> list = new ArrayList<>();
    private SearchViewMode searchViewMode;
    private int skipCount = 0;
    private final int takeCount = 7;
    private int pastVisiblesItems = 0;
    private int visibleItemCount = 0;
    private int totalItemCount = 0;
    private boolean loading = true;
    private SearchDataAdapter searchDataAdapter;
    private String searchSellerName;
    private int cateogryId = 0;


    @SuppressLint("ValidFragment")
    public ProductFragment(String searchSellName, int allCategoriesID) {
        searchSellerName = searchSellName;
        cateogryId = allCategoriesID;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (SearchActivity) context;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_product, container, false);
        searchViewMode = ViewModelProviders.of(this).get(SearchViewMode.class);
        initViews();
        callSearchAPi(searchSellerName);
        activity.searchInterface = this;
        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (searchDataAdapter != null) {
            searchDataAdapter = new SearchDataAdapter(getActivity(), list);
            mBinding.rvSearch.setAdapter(searchDataAdapter);
            searchDataAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onSearchClick(String query, int categoryId) {
        searchSellerName = query;
        cateogryId = categoryId;
        callSearchAPi(query);
    }


    private void initViews() {
        mBinding.tvNotDataFound.setText(MyApplication.getInstance().dbHelper.getString(R.string.no_data_found));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mBinding.rvSearch.setLayoutManager(layoutManager);
        searchDataAdapter = new SearchDataAdapter(getActivity(), list);
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
                            skipCount = skipCount + 7;
                            // mBinding.progressBar.setVisibility(View.VISIBLE);
                            // getSearchData();
                        }
                    }
                }
            }
        });
        list.clear();
    }


    private void callSearchAPi(String searchSellerName) {
        if (Utils.isNetworkAvailable(getActivity())) {
            Utils.showProgressDialog(getActivity());
            list.clear();
            getSearchData(searchSellerName);
        } else {
            Utils.setToast(getActivity(), MyApplication.getInstance().dbHelper.getString(R.string.no_internet_connection));
        }
    }

    private void getSearchData(String searchSellerNam) {
        SearchRequestModel searchRequestModel = new SearchRequestModel((String.valueOf(cateogryId).equals("0")) ? null : String.valueOf(cateogryId),
                skipCount, takeCount, searchSellerNam, SharePrefs.getInstance(getActivity()).getString(SharePrefs.PIN_CODE));
        searchViewMode.getSearchRequest(searchRequestModel);
        searchViewMode.getSearchViewModel().observe(this, searchDataList -> {
            Utils.hideProgressDialog();
            if (searchDataList != null && searchDataList.isSuccess()) {
                if (searchDataList.getResultItem().getTableOneModels() != null && searchDataList.getResultItem().getTableTwoModels() != null) {
                    if (searchDataList.getResultItem().getTableOneModels().size() > 0) {
                        ArrayList<SearchDataModel.TableOneTwo> itemList = searchDataList.getResultItem().getTableTwoModels();
                        for (int i = 0; i < itemList.size(); i++) {
                            list.add(itemList.get(i));
                            for (int j = 0; j < searchDataList.getResultItem().getTableOneModels().size(); j++) {
                                if (list.get(i).getId() == searchDataList.getResultItem().getTableOneModels().get(j).getSellerId()) {
                                    if (list.get(i).getProductList() == null) {
                                        list.get(i).setProductList(new ArrayList<>());
                                    }
                                    list.get(i).getProductList().add(searchDataList.getResultItem().getTableOneModels().get(j));
                                }
                            }
                        }
                        loading = true;
                        searchDataAdapter.notifyDataSetChanged();
                        mBinding.tvNotDataFound.setVisibility(View.GONE);
                        mBinding.rvSearch.setVisibility(View.VISIBLE);
                    } else {
                        mBinding.tvNotDataFound.setVisibility(View.VISIBLE);
                        mBinding.rvSearch.setVisibility(View.GONE);
                    }
                } else {
                    mBinding.tvNotDataFound.setVisibility(View.VISIBLE);
                    mBinding.rvSearch.setVisibility(View.GONE);
                }
            }
        });
    }
}
