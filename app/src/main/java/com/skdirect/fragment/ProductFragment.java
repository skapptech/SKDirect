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
import com.skdirect.adapter.CategoriesAdapter;
import com.skdirect.adapter.SearchDataAdapter;
import com.skdirect.databinding.FragmentProductBinding;
import com.skdirect.databinding.FragmentShopBinding;
import com.skdirect.model.SearchDataModel;
import com.skdirect.model.SearchRequestModel;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.SearchViewMode;

import java.util.ArrayList;

public class ProductFragment extends Fragment {
     private FragmentProductBinding mBinding;
    private ArrayList<SearchDataModel.TableOneTwo> list = new ArrayList<>();
    private SearchViewMode searchViewMode;
    private int skipCount = 0;
    private int takeCount = 7;
    private int pastVisiblesItems = 0;
    private int visibleItemCount = 0;
    private int totalItemCount = 0;
    private boolean loading = true;
    private SearchDataAdapter searchDataAdapter;
    private String searchSellerName;


    @SuppressLint("ValidFragment")
    public ProductFragment(String searchSellName) {
        searchSellerName = searchSellName;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_product, container, false);
        searchViewMode = ViewModelProviders.of(this).get(SearchViewMode.class);
        initViews();
        callSearchAPi(searchSellerName);

        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
            if (searchDataAdapter!=null){
                searchDataAdapter = new SearchDataAdapter(getActivity(),list);
                mBinding.rvSearch.setAdapter(searchDataAdapter);
                searchDataAdapter.notifyDataSetChanged();
            }

    }

    private void initViews() {
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
                            skipCount=skipCount+7;
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
            getSearchData(searchSellerName);
        } else {
            Utils.setToast(getActivity(), "No Internet Connection Please connect.");
        }
    }

    private void getSearchData(String searchSellerNam) {
        SearchRequestModel searchRequestModel = new SearchRequestModel(0, 0, 0, skipCount, takeCount, 0.0, 0.0, "", "", "", 0, searchSellerNam);
        searchViewMode.getSearchRequest(searchRequestModel);
        searchViewMode.getSearchViewModel().observe(this, new Observer<SearchDataModel>() {
            @Override
            public void onChanged(SearchDataModel searchDataList) {
                Utils.hideProgressDialog();
                if (searchDataList != null) {
                    if (searchDataList.getTableOneModels().size() > 0) {
                        ArrayList<SearchDataModel.TableOneTwo> itemList = searchDataList.getTableTwoModels();
                        for (int i = 0; i < itemList.size(); i++) {
                            list.add(itemList.get(i));
                            for (int j = 0; j < searchDataList.getTableOneModels().size(); j++) {
                                if (list.get(i).getId() == searchDataList.getTableOneModels().get(j).getSellerId()) {
                                    if (list.get(i).getProductList() == null) {
                                        list.get(i).setProductList(new ArrayList<>());
                                    }
                                    list.get(i).getProductList().add(searchDataList.getTableOneModels().get(j));
                                }
                            }
                        }
                        loading = true;
                        searchDataAdapter.notifyDataSetChanged();

                        /*mBinding.rvSearch.post(new Runnable() {
                            public void run() {

                            }
                        });*/
                    }
                }
            }
        });
    }

}
