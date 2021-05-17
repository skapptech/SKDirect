package com.skdirect.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kenilt.loopingviewpager.scroller.AutoScroller;
import com.skdirect.R;
import com.skdirect.activity.CartActivity;
import com.skdirect.activity.ChangeLanguageActivity;
import com.skdirect.activity.LoginActivity;
import com.skdirect.activity.MainActivity;
import com.skdirect.activity.PaymentActivity;
import com.skdirect.activity.SearchActivity;
import com.skdirect.adapter.HomeBannerAdapter;
import com.skdirect.adapter.MallCategorieBannerAdapter;
import com.skdirect.adapter.ShowCartInHomeAdapter;
import com.skdirect.api.CommonClassForAPI;
import com.skdirect.databinding.FragmentHomedBinding;
import com.skdirect.model.BannerModel;
import com.skdirect.model.CartItemModel;
import com.skdirect.model.CartMainModel;
import com.skdirect.model.CartModel;
import com.skdirect.model.MallMainModel;
import com.skdirect.utils.DBHelper;
import com.skdirect.utils.MyApplication;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.Utils;

import org.jetbrains.annotations.NotNull;

import io.reactivex.observers.DisposableObserver;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private FragmentHomedBinding mBinding;
    private MainActivity activity;
    public DBHelper dbHelper;
    private CommonClassForAPI commonClassForAPI;
    private CartItemModel cartItemDataModel;
    private double totalAmount;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_homed, container, false);
        initViews();
        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        getMall();
        showCartItemInHomePage();
        mBinding.etSearchSeller.setText("");

    }

    @Override
    public void onRefresh() {
        getMall();
        showCartItemInHomePage();

    }

    private void getMall() {
        if (Utils.isNetworkAvailable(activity)) {
            Utils.showProgressDialog(activity);
            commonClassForAPI.getMallData(mallObserve);
        } else {
            Utils.setToast(activity, activity.dbHelper.getString(R.string.no_connection));
        }
    }


    private final DisposableObserver<MallMainModel> mallObserve = new DisposableObserver<MallMainModel>() {
        @Override
        public void onNext(@NotNull MallMainModel mallMainModel) {
            Utils.hideProgressDialog();
            if (mallMainModel.isSuccess()) {
                mBinding.swiperefresh.setRefreshing(false);
                mBinding.rlHomeSearch.setVisibility(View.VISIBLE);
                if (mallMainModel.getResultItem().getBannerModel() != null) {
                    if (mallMainModel.getResultItem().getBannerModel().getBannerItemListModel().size() > 0) {
                        mBinding.rlHomeBanner.setVisibility(View.VISIBLE);
                        bannerList(mallMainModel.getResultItem().getBannerModel());
                    } else {
                        mBinding.rlHomeBanner.setVisibility(View.GONE);
                    }
                } else {
                    mBinding.rlHomeBanner.setVisibility(View.GONE);
                }
                ///  changeToHindiDialog();
                SharePrefs.getInstance(activity).putBoolean(SharePrefs.IS_Mall, true);
                SharePrefs.getInstance(getActivity()).putString(SharePrefs.MALL_ID, mallMainModel.getResultItem().getId());
                MallCategorieBannerAdapter mallCategorieBannerAdapter = new MallCategorieBannerAdapter(getActivity(), mallMainModel.getResultItem().getStoreCategoryList());
                mBinding.rvStoreCategoryList.setAdapter(mallCategorieBannerAdapter);
                mBinding.llMallHome.setVisibility(View.VISIBLE);
                mBinding.llMainAppHome.setVisibility(View.GONE);
            } else {
                mBinding.llMallHome.setVisibility(View.GONE);
                mBinding.llMainAppHome.setVisibility(View.VISIBLE);
                mBinding.rlHomeBanner.setVisibility(View.GONE);
            }
        }

        @Override
        public void onError(Throwable e) {
            Utils.hideProgressDialog();
            e.printStackTrace();
        }

        @Override
        public void onComplete() {
        }
    };
    private void initViews() {
        commonClassForAPI = CommonClassForAPI.getInstance(getActivity());
        mBinding.swiperefresh.setOnRefreshListener(this);
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
    private void bannerList(BannerModel bannerModel) {
        HomeBannerAdapter homeBannerAdapter = new HomeBannerAdapter(activity, bannerModel.getBannerItemListModel());
        mBinding.pager.setAdapter(homeBannerAdapter);

        AutoScroller autoScroller = new AutoScroller(mBinding.pager, activity.getLifecycle(), 5000);
        autoScroller.setAutoScroll(true);
    }
    private void changeToHindiDialog() {
        if (!SharePrefs.getInstance(getActivity()).getBoolean(SharePrefs.IS_DIALOG_SHOW)) {
            mBinding.rlChnageToHindi.setVisibility(View.VISIBLE);
            mBinding.rlChnageToHindi.postDelayed(new Runnable() {
                public void run() {
                    SharePrefs.getInstance(getActivity()).putBoolean(SharePrefs.IS_DIALOG_SHOW, true);
                    mBinding.rlChnageToHindi.setVisibility(View.GONE);

                }
            }, 6000);
        } else {
            mBinding.rlChnageToHindi.setVisibility(View.GONE);
        }


        mBinding.layoutChnageLag.tvChangeToHindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChangeLanguageActivity.class));
            }
        });

        mBinding.layoutChnageLag.ivCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.rlChnageToHindi.setVisibility(View.GONE);
            }
        });
    }

    private void showCartItemInHomePage() {

        cartItemsAPI();
        mBinding.layoutShowingCartInHome.btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharePrefs.setSharedPreference(getActivity(), SharePrefs.CAME_FROM_CART, false);
                if (SharePrefs.getSharedPreferences(getActivity(), SharePrefs.IS_REGISTRATIONCOMPLETE) && SharePrefs.getInstance(getActivity()).getBoolean(SharePrefs.IS_LOGIN)) {
                    startActivity(new Intent(getActivity(), PaymentActivity.class)
                            .putExtra("cartItemSize", cartItemDataModel)
                            .putExtra("totalAmount", totalAmount));
                } else {
                    SharePrefs.setSharedPreference(getActivity(), SharePrefs.CAME_FROM_CART, true);
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            }
        });

    }

    private void cartItemsAPI() {
        commonClassForAPI.getCartItemModelVMRequest(getCartItem);
    }

    private final DisposableObserver<CartMainModel> getCartItem = new DisposableObserver<CartMainModel>() {
        @Override
        public void onNext(@NotNull CartMainModel cartMainModel) {
            Utils.hideProgressDialog();
            if (cartMainModel.isSuccess()) {
                if (cartMainModel.getResultItem() != null) {
                    cartItemDataModel = cartMainModel.getResultItem();
                    if (cartMainModel.getResultItem().getCart().size()>0){
                        mBinding.llShowingCartInHome.setVisibility(View.VISIBLE);
                        totalAmount =0;
                        for (int i = 0; i < cartMainModel.getResultItem().getCart().size(); i++) {
                            totalAmount = totalAmount + cartMainModel.getResultItem().getCart().get(i).getQuantity() * cartMainModel.getResultItem().getCart().get(i).getPrice();

                        }

                        mBinding.layoutShowingCartInHome.tvTotalAmount.setText("₹ " + totalAmount);

                        mBinding.layoutShowingCartInHome.tvItemInCart.setText(cartMainModel.getResultItem().getCart().size()+" Item In Your Cart");
                        ShowCartInHomeAdapter showCartInHomeAdapter = new ShowCartInHomeAdapter(getActivity(),cartMainModel.getResultItem().getCart());
                        mBinding.layoutShowingCartInHome.rvCartHomePage.setAdapter(showCartInHomeAdapter);

                    }else {
                        mBinding.llShowingCartInHome.setVisibility(View.GONE);
                    }

                }
            } else {

            }

        }

        @Override
        public void onError(Throwable e) {
            Utils.hideProgressDialog();
            e.printStackTrace();
        }

        @Override
        public void onComplete() {
        }
    };


}
