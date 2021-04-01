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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.flipkart.android.proteus.Proteus;
import com.flipkart.android.proteus.ProteusBuilder;
import com.flipkart.android.proteus.ProteusContext;
import com.flipkart.android.proteus.ProteusLayoutInflater;
import com.flipkart.android.proteus.ProteusView;
import com.flipkart.android.proteus.exceptions.ProteusInflateException;
import com.flipkart.android.proteus.gson.ProteusTypeAdapterFactory;
import com.flipkart.android.proteus.support.design.DesignModule;
import com.flipkart.android.proteus.support.v4.SupportV4Module;
import com.flipkart.android.proteus.support.v7.CardViewModule;
import com.flipkart.android.proteus.support.v7.RecyclerViewModule;
import com.flipkart.android.proteus.value.DrawableValue;
import com.flipkart.android.proteus.value.Layout;
import com.flipkart.android.proteus.value.ObjectValue;
import com.flipkart.android.proteus.value.Value;
import com.google.gson.stream.JsonReader;
import com.skdirect.R;
import com.skdirect.activity.CategoriesListActivity;
import com.skdirect.activity.MainActivity;
import com.skdirect.activity.NearByItemProductListActivity;
import com.skdirect.activity.ProteusActivity;
import com.skdirect.activity.SearchActivity;
import com.skdirect.activity.SellerProductListActivity;
import com.skdirect.adapter.AllCategoriesAdapter;
import com.skdirect.adapter.MallCategorieBannerAdapter;
import com.skdirect.adapter.TopNearByItemAdapter;
import com.skdirect.adapter.TopSellerAdapter;
import com.skdirect.api.ImageLoaderTarget;
import com.skdirect.databinding.FragmentHomeBinding;
import com.skdirect.model.AllCategoriesModel;
import com.skdirect.model.CustomerDataModel;
import com.skdirect.model.MallMainModel;
import com.skdirect.model.TopNearByItemModel;
import com.skdirect.model.TopSellerModel;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.HomeViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import static com.skdirect.utils.JsonData.DATA;
import static com.skdirect.utils.JsonData.LAYOUT;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private FragmentHomeBinding mBinding;
    private MainActivity activity;
    private HomeViewModel homeViewModel;
    Proteus proteus;
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
        if (mBinding.llMainAppHome.getVisibility() == View.VISIBLE) {
            topNearByItem();
            getSellerAPi();
            getAllCategoriesAPi();
        } else {
           //mBinding.swiperefresh.setVisibility(View.GONE);
        }


    }

    private void getAllCategoriesAPi() {
        if (Utils.isNetworkAvailable(activity)) {
            Utils.showProgressDialog(activity);
            getTopSeller();
        } else {
            Utils.setToast(activity, "No Internet Connection Please connect.");
        }
    }

    private void getSellerAPi() {
        if (Utils.isNetworkAvailable(activity)) {
            Utils.showProgressDialog(activity);
            getAllCategories();
        } else {
            Utils.setToast(activity, "No Internet Connection Please connect.");
        }
    }

    private void topNearByItem() {
        if (Utils.isNetworkAvailable(activity)) {
            Utils.showProgressDialog(activity);
            getNearByItem();
        } else {
            Utils.setToast(activity, "No Internet Connection Please connect.");
        }
    }

    private void getMall() {
        if (Utils.isNetworkAvailable(activity)) {
            Utils.showProgressDialog(activity);
            getMallApiData();
        } else {
            Utils.setToast(activity, "No Internet Connection Please connect.");
        }
    }

    private void getMallApiData() {
        homeViewModel.getMallData().observe(this, new Observer<MallMainModel>() {
            @Override
            public void onChanged(MallMainModel mallMainModel) {
                Utils.hideProgressDialog();
                if (mallMainModel.getResultItem() != null) {
                    MallCategorieBannerAdapter mallCategorieBannerAdapter = new MallCategorieBannerAdapter(getActivity(),mallMainModel.getResultItem().getStoreCategoryList());
                    mBinding.rvStoreCategoryList.setAdapter(mallCategorieBannerAdapter);
                    mBinding.llMainAppHome.setVisibility(View.GONE);
                    mBinding.llMallHome.setVisibility(View.VISIBLE);
                } else {
                    mBinding.llMainAppHome.setVisibility(View.VISIBLE);
                    mBinding.llMallHome.setVisibility(View.GONE);
                    topNearByItem();
                    getSellerAPi();
                    getAllCategoriesAPi();
                }
            }
        });
    }

    private void getNearByItem() {
        homeViewModel.GetTopNearByItem().observe(this, new Observer<ArrayList<TopNearByItemModel>>() {
            @Override
            public void onChanged(ArrayList<TopNearByItemModel> topNearByItemList) {
                Utils.hideProgressDialog();
                //mBinding.swiperefresh.setRefreshing(false);
                if (topNearByItemList.size() > 0) {
                    TopNearByItemAdapter topNearByItemAdapter = new TopNearByItemAdapter(getActivity(), topNearByItemList);
                    mBinding.rvNearByItem.setAdapter(topNearByItemAdapter);

                } else {
                    mBinding.llNearByNotFound.setVisibility(View.VISIBLE);
                    mBinding.rvNearByItem.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getTopSeller() {
        homeViewModel.GetTopSellerLiveData().observe(this, new Observer<ArrayList<TopSellerModel>>() {
            @Override
            public void onChanged(ArrayList<TopSellerModel> topSellerList) {
                //mBinding.swiperefresh.setRefreshing(false);
                Utils.hideProgressDialog();
                if (topSellerList.size() > 0) {
                    TopSellerAdapter topSellerAdapter = new TopSellerAdapter(getActivity(), topSellerList);
                    mBinding.rvTopSeller.setAdapter(topSellerAdapter);

                } else {
                    mBinding.llTopSellerNotFound.setVisibility(View.VISIBLE);
                    mBinding.rvTopSeller.setVisibility(View.GONE);
                }

            }
        });
    }


    private void getAllCategories() {
        homeViewModel.getAllCategoriesLiveData().observe(this, new Observer<ArrayList<AllCategoriesModel>>() {
            @Override
            public void onChanged(ArrayList<AllCategoriesModel> allCategoriesList) {
               // mBinding.swiperefresh.setRefreshing(false);
                if (allCategoriesList.size() > 0) {
                    AllCategoriesAdapter allCategoriesAdapter = new AllCategoriesAdapter(getActivity(), allCategoriesList);
                    mBinding.rvAllCetegory.setAdapter(allCategoriesAdapter);
                } else {
                    mBinding.llAllCetegoryNotFound.setVisibility(View.VISIBLE);
                    mBinding.rvAllCetegory.setVisibility(View.GONE);
                }


            }
        });

    }

    private void initViews() {

        proteus = new ProteusBuilder()
                .register(SupportV4Module.create())
                .register(RecyclerViewModule.create())
                .register(CardViewModule.create())
                .register(DesignModule.create())
                .build();
        ProteusTypeAdapterFactory adapter = new ProteusTypeAdapterFactory(getActivity());
        ProteusTypeAdapterFactory.PROTEUS_INSTANCE_HOLDER.setProteus(proteus);

        Layout layout;
        ObjectValue data;
        try {
            layout = adapter.LAYOUT_TYPE_ADAPTER.read(new JsonReader(new StringReader(LAYOUT)));
            data = adapter.OBJECT_TYPE_ADAPTER.read(new JsonReader(new StringReader(DATA)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ProteusContext context = proteus.createContextBuilder(getActivity()).setCallback(callback).setImageLoader(loader).build();
        ProteusLayoutInflater inflater = context.getInflater();
        ProteusView view = inflater.inflate(layout, data, mBinding.contentMain, 0);
        mBinding.contentMain.addView(view.getAsView());



        //mBinding.swiperefresh.setOnRefreshListener(this);
        mBinding.rvNearByItem.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        mBinding.rvTopSeller.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        mBinding.rvAllCetegory.setLayoutManager(new GridLayoutManager(activity, 3));
        activity.appBarLayout.setVisibility(View.VISIBLE);
        if (Utils.isNetworkAvailable(activity)) {
            Utils.showProgressDialog(activity);
            getLoginData();
        } else {
            Utils.setToast(activity, "No Internet Connection Please connect.");
        }

        mBinding.tvNearByViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), NearByItemProductListActivity.class));
            }
        });

        mBinding.tvNearBySellar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SellerProductListActivity.class));
            }
        });

        mBinding.tvCetegory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CategoriesListActivity.class));
            }
        });

        mBinding.etSearchSeller.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchData();
                    handled = true;
                }
                return handled;
            }
        });

    }

    private void searchData() {
        String searchSellerName = mBinding.etSearchSeller.getText().toString().trim();
        startActivity(new Intent(getActivity(), SearchActivity.class).putExtra("searchSellerName", searchSellerName));


    }

    private void getLoginData() {
        homeViewModel.GetUserDetail().observe(this, new Observer<CustomerDataModel>() {
            @Override
            public void onChanged(CustomerDataModel customerDataModel) {
                Utils.hideProgressDialog();
                if (customerDataModel != null) {
                    activity.userNameTV.setText(customerDataModel.getFirstName());
                    activity.mobileNumberTV.setText(customerDataModel.getMobileNo());

                    SharePrefs.getInstance(activity).putString(SharePrefs.FIRST_NAME, customerDataModel.getFirstName());
                    SharePrefs.getInstance(activity).putString(SharePrefs.MIDDLE_NAME, customerDataModel.getMiddleName());
                    SharePrefs.getInstance(activity).putString(SharePrefs.USER_NAME, customerDataModel.getUserName());
                    SharePrefs.getInstance(activity).putString(SharePrefs.LAST_NAME, customerDataModel.getLastName());
                    SharePrefs.getInstance(activity).putString(SharePrefs.USER_ID, customerDataModel.getUserId());
                    SharePrefs.getInstance(activity).putInt(SharePrefs.ID, customerDataModel.getId());
                    SharePrefs.getInstance(activity).putString(SharePrefs.ENCRIPTED_ID, customerDataModel.getEncryptedId());
                    SharePrefs.getInstance(activity).putString(SharePrefs.MOBILE_NUMBER, customerDataModel.getMobileNo());
                    SharePrefs.getInstance(activity).putString(SharePrefs.SHOP_NAME, customerDataModel.getShopName());
                    SharePrefs.getInstance(activity).putString(SharePrefs.EMAIL_ID, customerDataModel.getEmail());
                    SharePrefs.getInstance(activity).putString(SharePrefs.STATE, customerDataModel.getState());
                    SharePrefs.getInstance(activity).putString(SharePrefs.CITYNAME, customerDataModel.getCity());
                    SharePrefs.getInstance(activity).putString(SharePrefs.PIN_CODE, customerDataModel.getPincode());
                    SharePrefs.getInstance(activity).putString(SharePrefs.IMAGE_PATH, customerDataModel.getImagePath());
                    SharePrefs.getInstance(activity).putInt(SharePrefs.PIN_CODE_master, customerDataModel.getPinCodeMasterId());
                    SharePrefs.getInstance(activity).putBoolean(SharePrefs.IS_ACTIVE, customerDataModel.isActive());
                    SharePrefs.getInstance(activity).putBoolean(SharePrefs.IS_DELETE, customerDataModel.isDelete());
                    SharePrefs.getInstance(activity).putBoolean(SharePrefs.IS_REGISTRATIONCOMPLETE, customerDataModel.isRegistrationComplete());

                    if (customerDataModel.getUserDeliveryDC() != null && customerDataModel.getUserDeliveryDC().size() > 0) {
                        for (int i = 0; i < customerDataModel.getUserDeliveryDC().size(); i++) {
                            SharePrefs.getInstance(activity).putBoolean(SharePrefs.USER_IS_DELETE, customerDataModel.getUserDeliveryDC().get(i).isDelete());
                            SharePrefs.getInstance(activity).putBoolean(SharePrefs.USER_IS_ACTIVE, customerDataModel.getUserDeliveryDC().get(i).isActive());
                            SharePrefs.getInstance(activity).putInt(SharePrefs.USER_DC_ID, customerDataModel.getUserDeliveryDC().get(i).getId());
                            SharePrefs.getInstance(activity).putInt(SharePrefs.USER_DC_USER_ID, customerDataModel.getUserDeliveryDC().get(i).getUserId());
                            SharePrefs.getInstance(activity).putString(SharePrefs.DELIVERY, customerDataModel.getUserDeliveryDC().get(i).getDelivery());

                        }
                    }
                }
            }
        });

    }


    private ProteusLayoutInflater.ImageLoader loader = new ProteusLayoutInflater.ImageLoader() {
        @Override
        public void getBitmap(ProteusView view, String url, final DrawableValue.AsyncCallback callback) {
            Glide.with(getActivity())
                    .load(url)
                    .placeholder(R.drawable.ic_direct_active)
                    .error(R.drawable.ic_direct_active)
                    .into(new ImageLoaderTarget(callback));
        }
    };
    private ProteusLayoutInflater.Callback callback = new ProteusLayoutInflater.Callback() {

        @NonNull
        @Override
        public ProteusView onUnknownViewType(ProteusContext context, String type, Layout layout, ObjectValue data, int index) {
            // TODO: instead return some implementation of an unknown view
            throw new ProteusInflateException("Unknown view type '" + type + "' cannot be inflated");
        }

        @Override
        public void onEvent(String event, Value value, ProteusView view) {
            ObjectValue objectValue = value.getAsObject();
            Log.i("ProteusEvent", event+" - "+objectValue.get("type").getAsString());
            String type = objectValue.get("type").getAsString();
            if (event.equalsIgnoreCase("onClick")){
                if (type.equalsIgnoreCase("showToast")){
                    showToast();
                }
            }
        }
    };
    public void showToast(){
        Utils.setToast(activity,"Showing Toast");
    }




}
