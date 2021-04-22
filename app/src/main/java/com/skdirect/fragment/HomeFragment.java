package com.skdirect.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kenilt.loopingviewpager.scroller.AutoScroller;
import com.skdirect.R;
import com.skdirect.activity.CategoriesListActivity;
import com.skdirect.activity.MainActivity;
import com.skdirect.activity.MostVisitedSellerActivity;
import com.skdirect.activity.NearByItemProductListActivity;
import com.skdirect.activity.NearSellerActivity;
import com.skdirect.activity.NewSellerActivity;
import com.skdirect.activity.SearchActivity;
import com.skdirect.activity.SellerProductListActivity;
import com.skdirect.adapter.AllCategoriesAdapter;
import com.skdirect.adapter.HomeBannerAdapter;
import com.skdirect.adapter.MallCategorieBannerAdapter;
import com.skdirect.adapter.TopNearByItemAdapter;
import com.skdirect.adapter.TopSellerAdapter;
import com.skdirect.databinding.FragmentHomeBinding;
import com.skdirect.model.AllCategoresMainModel;
import com.skdirect.model.MallMainModel;
import com.skdirect.model.NearByMainModel;
import com.skdirect.model.TopSellerMainModel;
import com.skdirect.utils.DBHelper;
import com.skdirect.utils.MyApplication;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.HomeViewModel;

import org.jetbrains.annotations.NotNull;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private FragmentHomeBinding mBinding;
    private MainActivity activity;
    private HomeViewModel homeViewModel;
    public  DBHelper dbHelper;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        initViews();
        getMall();
        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        mBinding.etSearchSeller.setText("");
    }

    @Override
    public void onRefresh() {



    }

    private void getMall() {
        if (Utils.isNetworkAvailable(activity)) {
            Utils.showProgressDialog(activity);
            getMallApiData();
        } else {
            Utils.setToast(activity, activity.dbHelper.getString(R.string.no_connection));
        }
    }

    private void getMallApiData() {
        homeViewModel.getMallData().observe(this, new Observer<MallMainModel>() {
            @Override
            public void onChanged(MallMainModel mallMainModel) {
                Utils.hideProgressDialog();
                if (mallMainModel.isSuccess()) {
                    if (mallMainModel.getResultItem() != null) {
                        mBinding.rlHomeSearch.setVisibility(View.VISIBLE);
                        SharePrefs.getInstance(activity).putBoolean(SharePrefs.IS_Mall, true);
                        SharePrefs.getInstance(getActivity()).putString(SharePrefs.MALL_ID, mallMainModel.getResultItem().getId());
                        MallCategorieBannerAdapter mallCategorieBannerAdapter = new MallCategorieBannerAdapter(getActivity(), mallMainModel.getResultItem().getStoreCategoryList());
                        mBinding.rvStoreCategoryList.setAdapter(mallCategorieBannerAdapter);
                        mBinding.llMallHome.setVisibility(View.VISIBLE);
                        mBinding.rlHomeBanner.setVisibility(View.VISIBLE);
                        mBinding.llMainAppHome.setVisibility(View.GONE);
                    } else {
                        mBinding.llMallHome.setVisibility(View.GONE);
                        mBinding.rlHomeBanner.setVisibility(View.GONE);
                        mBinding.llMainAppHome.setVisibility(View.VISIBLE);
                    }
                } else {
                    mBinding.llMallHome.setVisibility(View.GONE);
                    mBinding.llMainAppHome.setVisibility(View.VISIBLE);
                }
            }
        });
    }



    private void initViews() {
        HomeBannerAdapter homeBannerAdapter  = new HomeBannerAdapter(getActivity());
        mBinding.pager.setAdapter(homeBannerAdapter);

        AutoScroller autoScroller=new AutoScroller(mBinding.pager,activity.getLifecycle(),5000);
        autoScroller.setAutoScroll(true);

        if (SharePrefs.getInstance(getActivity()).getBoolean(SharePrefs.IS_Mall)) {
          mBinding.rlHomeSearch.setVisibility(View.VISIBLE);
        } else {
            mBinding.rlHomeSearch.setVisibility(View.GONE);
        }

        dbHelper = MyApplication.getInstance().dbHelper;
        mBinding.etSearchSeller.setHint(dbHelper.getString(R.string.search_seller));
        activity.appBarLayout.setVisibility(View.VISIBLE);

        mBinding.etSearchSeller.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                    searchData();
                    return true;
                }
                return false;
            }
        });
    }



    private void searchData() {
        String searchSellerName = mBinding.etSearchSeller.getText().toString().trim();
        startActivity(new Intent(getActivity(), SearchActivity.class).putExtra("searchSellerName", searchSellerName));
    }

}
