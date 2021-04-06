package com.skdirect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skdirect.BuildConfig;
import com.skdirect.R;
import com.skdirect.adapter.SellerProductAdapter;
import com.skdirect.databinding.ActivitySellerProfileBinding;
import com.skdirect.interfacee.AddItemInterface;
import com.skdirect.model.AddViewModel;
import com.skdirect.model.CartModel;
import com.skdirect.model.ItemAddModel;
import com.skdirect.model.SellerDeliveryModel;
import com.skdirect.model.SellerProductList;
import com.skdirect.model.SellerProfileDataModel;
import com.skdirect.model.UserDetailModel;
import com.skdirect.utils.MyApplication;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.SellerProfileViewMode;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SellerProfileActivity extends AppCompatActivity implements View.OnClickListener, AddItemInterface {
    private ActivitySellerProfileBinding mBinding;
    private SellerProfileViewMode sellerProfileViewMode;
    private int sellerID;
    private int skipCount = 0;
    private final int takeCount = 5;
    private int pastVisiblesItems = 0;
    private int visibleItemCount = 0;
    private int totalItemCount = 0;
    private boolean loading = true;
    private final ArrayList<SellerProductList> sellerProductModels = new ArrayList<>();
    private SellerProductAdapter sellerShopListAdapter;
    private String searchSellerName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_seller_profile);
        sellerProfileViewMode = ViewModelProviders.of(this).get(SellerProfileViewMode.class);
        getIntentData();
        initView();
        callSellerDetails();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        MyApplication.getInstance().cartRepository.getCartCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mBinding.toolbarTittle.cartBadge.setText("" + integer);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sellerShopListAdapter != null) {
            sellerShopListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_press:
                onBackPressed();
                break;
            case R.id.notifiction_count:
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
                break;
        }
    }

    private void callSellerDetails() {
        if (Utils.isNetworkAvailable(getApplicationContext())) {
            Utils.showProgressDialog(SellerProfileActivity.this);
            getSellerDetailsAPI();
            getSellerProductsApi(searchSellerName);
            addProduct();
        } else {
            Utils.setToast(getApplicationContext(), "No Internet Connection Please connect.");
        }
    }

    private void getIntentData() {
        sellerID = getIntent().getIntExtra("ID", 0);
    }

    private void initView() {
        mBinding.toolbarTittle.notifictionCount.setVisibility(View.VISIBLE);
        mBinding.toolbarTittle.tvTittle.setText("Seller Items");
        mBinding.toolbarTittle.ivBackPress.setOnClickListener(this);
        mBinding.toolbarTittle.notifictionCount.setOnClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mBinding.rvCategories.setLayoutManager(layoutManager);

        sellerShopListAdapter = new SellerProductAdapter(SellerProfileActivity.this, sellerProductModels, this);
        mBinding.rvCategories.setAdapter(sellerShopListAdapter);

        mBinding.etSearchSeller.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchSellerName = mBinding.etSearchSeller.getText().toString().trim();
                sellerProductModels.clear();
                sellerShopListAdapter.notifyDataSetChanged();
                getSellerProductsApi(searchSellerName);
                handled = true;
            }
            return handled;
        });


        mBinding.rvCategories.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            skipCount = skipCount + 5;
                            // mBinding.progressBar.setVisibility(View.VISIBLE);
                            getSellerProductsApi(searchSellerName);
                        }
                    }
                }
            }
        });
        sellerProductModels.clear();
    }


    private void getSellerDetailsAPI() {
        sellerProfileViewMode.getSellerDetailsRequest(sellerID);
        sellerProfileViewMode.getSellerDetailsVM().observe(this, sellerDetailsModel -> {
            Utils.hideProgressDialog();
            if (sellerDetailsModel.isSuccess()) {
                if (sellerDetailsModel.getSellerInfoModel().getRating() > 0.0) {
                    Double deg = sellerDetailsModel.getSellerInfoModel().getRating();
                    float rating = deg.floatValue();
                    mBinding.ratingbarSeller.setRating(rating);
                } else {
                    mBinding.ratingbarSeller.setVisibility(View.GONE);
                }

                mBinding.tvSellerName.setText(sellerDetailsModel.getSellerInfoModel().getShopName());
                if (sellerDetailsModel.getSellerInfoModel().getMinOrderValue() != 0.0 && sellerDetailsModel.getSellerInfoModel().getRadialDistance() != 0.0) {
                    mBinding.tvMinimumOrderAmt.setText("₹ " + Math.round(sellerDetailsModel.getSellerInfoModel().getMinOrderValue()));
                    mBinding.tvDiliverDistance.setText("" + Math.round(sellerDetailsModel.getSellerInfoModel().getRadialDistance()));
                } else {
                    mBinding.llMiniOrder.setVisibility(View.GONE);
                    mBinding.llDelivert.setVisibility(View.GONE);
                }

                if (sellerDetailsModel.getSellerInfoModel().getImagePath() != null && !sellerDetailsModel.getSellerInfoModel().getImagePath().contains("http")) {
                    Picasso.get().load(BuildConfig.apiEndpoint + sellerDetailsModel.getSellerInfoModel().getImagePath()).error(R.drawable.ic_top_seller).into(mBinding.ivSShopImage);
                } else {
                    Picasso.get().load(sellerDetailsModel.getSellerInfoModel().getImagePath()).placeholder(R.drawable.ic_top_seller).into(mBinding.ivSShopImage);
                }
            }
        });
    }

    private void getSellerProductsApi(String searchSellerName) {
        SellerProfileDataModel paginationModel = new SellerProfileDataModel(sellerID, 0, 0, "", skipCount, takeCount, 0, searchSellerName, Double.parseDouble(SharePrefs.getInstance(this).getString(SharePrefs.LAT)), Double.parseDouble(SharePrefs.getInstance(this).getString(SharePrefs.LON)));
        sellerProfileViewMode.getSellerProductRequest(paginationModel);
        sellerProfileViewMode.getSellerProductVM().observe(this, sellerProdList -> {
            Utils.hideProgressDialog();
            if (sellerProdList != null) {
                updateUserDetails(sellerProdList.getSellerProductModel().getUserDetailModel(), sellerProdList.getSellerProductModel().getSellerDeliveryModel());
                if (sellerProdList.getSellerProductModel().getSellerProductLists().size() > 0) {
                    mBinding.rvCategories.post(() -> {
                        sellerProductModels.addAll(sellerProdList.getSellerProductModel().getSellerProductLists());
                        sellerShopListAdapter.notifyDataSetChanged();
                        loading = true;
                    });
                } else {
                    if (sellerProductModels.size() == 0) {
                        mBinding.tvItemNotFound.setVisibility(View.VISIBLE);
                        mBinding.rvCategories.setVisibility(View.GONE);
                    }
                }
            } else {
                loading = false;
            }
        });
    }

    private void updateUserDetails(UserDetailModel userDetailModel, ArrayList<SellerDeliveryModel> sellerDeliveryModel) {
        String deliveryOption = "";

        if (userDetailModel != null) {
            if (userDetailModel.getStoreView() != 0) {
                mBinding.toolbarTittle.tvStoreView.setVisibility(View.VISIBLE);
                mBinding.toolbarTittle.tvStoreView.setText(String.valueOf(userDetailModel.getStoreView()));
            } else {
                mBinding.toolbarTittle.tvStoreView.setVisibility(View.GONE);
            }
            mBinding.tvAddreshOne.setText(userDetailModel.getAddressOne() + " " + userDetailModel.getAddressTwo() + " " + userDetailModel.getCity() + " - " + userDetailModel.getPincode() + " (" + userDetailModel.getState() + ")");
            mBinding.tvDeliveryOption.setText(userDetailModel.getAddressOne() + " " + userDetailModel.getAddressTwo());
            mBinding.tvSellerDistance.setText("" + String.format("%.2f", userDetailModel.getDistance()) + " KM");

        } else {
            mBinding.tvSellerDistance.setVisibility(View.GONE);
        }

        if (sellerDeliveryModel.size() > 0) {
            for (int i = 0; i < sellerDeliveryModel.size(); i++) {
                deliveryOption += sellerDeliveryModel.get(i).getDelivery() + ", ";
            }
            mBinding.tvDeliveryOption.setText(deliveryOption);
        } else {
            mBinding.llDeliverOption.setVisibility(View.GONE);
        }
    }

    private void addProduct() {
        sellerProfileViewMode.getAddProductVMRequest(new AddViewModel(sellerID));
        sellerProfileViewMode.getAddProductVM().observe(this, aBoolean -> {
            Utils.hideProgressDialog();
            if (aBoolean.isSuccess()) {

            }
        });
    }

    @Override
    public void plusButtonOnClick(SellerProductList sellerProductModel, TextView tvSelectedQty) {
        int qty = Integer.parseInt(tvSelectedQty.getText().toString().trim());
        qty++;
        mBinding.toolbarTittle.cartBadge.setVisibility(View.VISIBLE);
        tvSelectedQty.setText("" + qty);
        // add item  to cart
        CartModel cartModel = new CartModel(null, 0, null, false, sellerProductModel.isStockRequired(), sellerProductModel.getStock(), sellerProductModel.getMeasurement(), sellerProductModel.getUom(), sellerProductModel.getImagePath(), 0, sellerProductModel.getProductName(), 0, 0, false, 0, 0, 0, qty, sellerProductModel.getCreatedBy(), null, sellerProductModel.getSellerId(), 0, 0, sellerProductModel.getMargin(), sellerProductModel.getMrp(), sellerProductModel.getMOQ(), sellerProductModel.getId());
        MyApplication.getInstance().cartRepository.addToCart(cartModel);
        addItemInCart(qty, sellerProductModel);
    }

    @Override
    public void minusButtonOnClick(SellerProductList sellerProductModel, TextView selectedQty, TextView btAddToCart, LinearLayout LLPlusMinus) {
        int qty = Integer.parseInt(selectedQty.getText().toString().trim());
        qty--;
        if (qty >= 0) {
            selectedQty.setText("" + qty);
            addItemInCart(qty, sellerProductModel);
        } else {
            btAddToCart.setVisibility(View.VISIBLE);
            LLPlusMinus.setVisibility(View.GONE);
            mBinding.toolbarTittle.cartBadge.setVisibility(View.GONE);
        }
        // add item  to cart
        CartModel cartModel = new CartModel(null, 0, null, false, sellerProductModel.isStockRequired(), sellerProductModel.getStock(), sellerProductModel.getMeasurement(), sellerProductModel.getUom(), sellerProductModel.getImagePath(), 0, sellerProductModel.getProductName(), 0, 0, false, 0, 0, 0, 1, sellerProductModel.getCreatedBy(), null, sellerProductModel.getSellerId(), 0, 0, sellerProductModel.getMargin(), sellerProductModel.getMrp(), sellerProductModel.getMOQ(), sellerProductModel.getId());
        MyApplication.getInstance().cartRepository.addToCart(cartModel);
    }

    @Override
    public void addButtonOnClick(SellerProductList sellerProductModel, TextView tvSelectedQty, TextView btAddToCart, LinearLayout LLPlusMinus) {
        mBinding.toolbarTittle.notifictionCount.setVisibility(View.VISIBLE);
        Integer cartSellerId = MyApplication.getInstance().cartRepository.getCartSellerId();
        if (cartSellerId != null && cartSellerId != 0) {
            if (cartSellerId == sellerID) {
                btAddToCart.setVisibility(View.GONE);
                LLPlusMinus.setVisibility(View.VISIBLE);
                CartModel cartModel = new CartModel(null, 0, null, false, sellerProductModel.isStockRequired(), sellerProductModel.getStock(), sellerProductModel.getMeasurement(), sellerProductModel.getUom(), sellerProductModel.getImagePath(), 0, sellerProductModel.getProductName(), 0, 0, false, 0, 0, 0, sellerProductModel.getQty(), sellerProductModel.getCreatedBy(), null, sellerProductModel.getSellerId(), 0, 0, sellerProductModel.getMargin(), sellerProductModel.getMrp(), sellerProductModel.getMOQ(), sellerProductModel.getId());
                MyApplication.getInstance().cartRepository.addToCart(cartModel);
                SellerProfileActivity.this.plusButtonOnClick(sellerProductModel, tvSelectedQty);
            } else {
                checkCustomerAlertDialog(cartSellerId, sellerProductModel, btAddToCart, LLPlusMinus);
            }
        } else {
            btAddToCart.setVisibility(View.GONE);
            LLPlusMinus.setVisibility(View.VISIBLE);
            tvSelectedQty.setText("1");
            // add item  to cart
            CartModel cartModel = new CartModel(null, 0, null, false, sellerProductModel.isStockRequired(), sellerProductModel.getStock(), sellerProductModel.getMeasurement(), sellerProductModel.getUom(), sellerProductModel.getImagePath(), 0, sellerProductModel.getProductName(), 0, 0, false, 0, 0, 0, 1, sellerProductModel.getCreatedBy(), null, sellerProductModel.getSellerId(), 0, 0, sellerProductModel.getMargin(), sellerProductModel.getMrp(), sellerProductModel.getMOQ(), sellerProductModel.getId());
            MyApplication.getInstance().cartRepository.addToCart(cartModel);
        }
    }

    private void addItemInCart(int QTY, SellerProductList sellerProductModel) {
        ItemAddModel paginationModel = new ItemAddModel(QTY, "123", sellerProductModel.getId(), 0, 0);
        sellerProfileViewMode.getAddItemsInCardVMRequest(paginationModel);
        sellerProfileViewMode.getAddItemsInCardVM().observe(this, sellerProdList -> {
            Utils.hideProgressDialog();
            if (sellerProdList != null) {
                // sellerShopListAdapter.notifyDataSetChanged();
            }
        });
    }


    public void checkCustomerAlertDialog(int id, SellerProductList sellerProductModel, TextView btAddToCart, LinearLayout LLPlusMinus) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Your Cart has existing items from Another Seller.Do You Want to clear it and add items from this Seller?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            clearCartItem(id);
            MyApplication.getInstance().cartRepository.truncateCart();
            // cartModel.setSellerId(sellerID);
            CartModel cartItemModel = new CartModel(null, 0, null, false,
                    sellerProductModel.isStockRequired(), sellerProductModel.getStock(), sellerProductModel.getMeasurement(),
                    sellerProductModel.getUom(), sellerProductModel.getImagePath(), 0, sellerProductModel.getProductName(),
                    0, 0, false, 0, 0, 0, sellerProductModel.getQty(),
                    sellerProductModel.getCreatedBy(), null, sellerProductModel.getSellerId(), 0, 0,
                    sellerProductModel.getMargin(), sellerProductModel.getMrp(), sellerProductModel.getMOQ(), sellerProductModel.getId());
            MyApplication.getInstance().cartRepository.addToCart(cartItemModel);
            sellerShopListAdapter.notifyDataSetChanged();
            btAddToCart.setVisibility(View.GONE);
            LLPlusMinus.setVisibility(View.VISIBLE);
            addItemInCart(1, sellerProductModel);
        });

        builder.setNegativeButton("No", (dialog, which) -> {

        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void clearCartItem(int id) {
        sellerProfileViewMode.getClearCartItemVMRequest(id);
        sellerProfileViewMode.getClearCartItemVM().observe(this, object -> {
            Utils.hideProgressDialog();
//                if (object != null) {
//                    if (object.equals(true)) {
//                        MainActivity.cartItemModel.getCart().clear();
//                    }
//                }
        });
    }
}
