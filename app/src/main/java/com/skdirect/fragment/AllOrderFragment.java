package com.skdirect.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.skdirect.R;
import com.skdirect.activity.MyOrderActivity;
import com.skdirect.activity.PaymentActivity;
import com.skdirect.adapter.MyOrderAdapter;
import com.skdirect.databinding.FragmentAllOrderBinding;
import com.skdirect.model.MyOrderModel;
import com.skdirect.model.MyOrderRequestModel;
import com.skdirect.model.NearBySallerModel;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.MyOrderViewMode;
import com.skdirect.viewmodel.PaymentViewMode;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AllOrderFragment extends Fragment {
    private MyOrderActivity activity;
    private FragmentAllOrderBinding mBinding;
    private MyOrderViewMode myOrderViewMode;
    private final ArrayList<MyOrderModel> orderModelArrayList = new ArrayList<>();
    private int type;
    private int skipCount = 0;
    private int takeCount = 10;
    private int pastVisiblesItems = 0;
    private int visibleItemCount = 0;
    private int totalItemCount = 0;
    private boolean loading = true;
    private MyOrderAdapter myOrderAdapter;

    public AllOrderFragment(int type) {
        this.type = type;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = ((MyOrderActivity) context);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_all_order, container, false);
        myOrderViewMode = ViewModelProviders.of(this).get(MyOrderViewMode.class);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialization();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (myOrderAdapter != null) {
            myOrderAdapter = new MyOrderAdapter(activity, orderModelArrayList);
            mBinding.rMyOrder.setAdapter(myOrderAdapter);
            myOrderAdapter.notifyDataSetChanged();
        }
    }

    private void initialization() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity, RecyclerView.VERTICAL,false);
        mBinding.rMyOrder.setLayoutManager(layoutManager);
        myOrderAdapter = new MyOrderAdapter(activity, orderModelArrayList);
        mBinding.rMyOrder.setAdapter(myOrderAdapter);
        mBinding.shimmerViewContainer.startShimmer();

        mBinding.rMyOrder.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            callMyOrder();
                        }
                    }
                }
            }
        });
        orderModelArrayList.clear();
        callMyOrder();
    }

    private void callMyOrder() {
        if (Utils.isNetworkAvailable(activity)) {
           myOrderAPI();
        } else {
            Utils.setToast(activity, "No Internet Connection Please connect.");
        }
    }

    private void myOrderAPI() {
        MyOrderRequestModel myOrderRequestModel = new MyOrderRequestModel("",0,takeCount,skipCount,type,0);
        myOrderViewMode.getCategoriesViewModelRequest(myOrderRequestModel);
        myOrderViewMode.getCategoriesViewModel().observe(this, new Observer<ArrayList<MyOrderModel>>() {
            @Override
            public void onChanged(ArrayList<MyOrderModel> myOrderModels) {
                mBinding.shimmerViewContainer.stopShimmer();
                mBinding.shimmerViewContainer.setVisibility(View.GONE);
                if (myOrderModels!=null && myOrderModels.size()>0){
                    mBinding.rMyOrder.post(new Runnable() {
                        public void run() {
                            orderModelArrayList.addAll(myOrderModels);
                            myOrderAdapter.notifyDataSetChanged();
                            loading = true;
                        }
                    });

                }else {
                    loading = false;
                }
            }
        });
    }
}
