package com.skdirect.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skdirect.R;
import com.skdirect.adapter.SellerProductAdapter;
import com.skdirect.databinding.ActivitySellerProfileBinding;
import com.skdirect.interfacee.AddItemInterface;
import com.skdirect.model.AddCartItemModel;
import com.skdirect.model.AddViewModel;
import com.skdirect.model.CartItemModel;
import com.skdirect.model.ItemAddModel;
import com.skdirect.model.SellerDetailsModel;
import com.skdirect.model.SellerProductList;
import com.skdirect.model.SellerProductModel;
import com.skdirect.model.SellerProfileDataModel;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.SellerProfileViewMode;

import java.util.ArrayList;


public class SellerProfileActivity extends AppCompatActivity implements View.OnClickListener, AddItemInterface {
    private ActivitySellerProfileBinding mBinding;
    private SellerProfileViewMode sellerProfileViewMode;
    private String sellerID;
    private int skipCount = 0;
    private final int takeCount = 5;
    private int pastVisiblesItems = 0;
    private int visibleItemCount = 0;
    private int totalItemCount = 0;
    private boolean loading = true;
    private final ArrayList<SellerProductList> sellerProductModels = new ArrayList<>();
    private SellerProductAdapter sellerShopListAdapter;
    private String searchSellerName, EncryptSellerId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_seller_profile);
        sellerProfileViewMode = ViewModelProviders.of(this).get(SellerProfileViewMode.class);
        getIntentData();
        initView();
        callSellerDetails();
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
        sellerID = getIntent().getStringExtra("ID");
    }

    private void initView() {
        mBinding.toolbarTittle.tvTittle.setText("Seller Items");
        mBinding.toolbarTittle.ivBackPress.setOnClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mBinding.rvCategories.setLayoutManager(layoutManager);

        sellerShopListAdapter = new SellerProductAdapter(SellerProfileActivity.this, sellerProductModels, this);
        mBinding.rvCategories.setAdapter(sellerShopListAdapter);


        mBinding.etSearchSeller.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchSellerName = mBinding.etSearchSeller.getText().toString().trim();
                    sellerProductModels.clear();
                    sellerShopListAdapter.notifyDataSetChanged();
                    getSellerProductsApi(searchSellerName);
                    handled = true;
                }
                return handled;
            }
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_press:
                onBackPressed();
                break;
        }
    }


    private void getSellerDetailsAPI() {
        sellerProfileViewMode.getSellerDetailsRequest(sellerID);
        sellerProfileViewMode.getSellerDetailsVM().observe(this, new Observer<SellerDetailsModel>() {
            @Override
            public void onChanged(SellerDetailsModel sellerDetailsModel) {
                Utils.hideProgressDialog();
                mBinding.tvSellerName.setText(sellerDetailsModel.getShopName());
                mBinding.tvMobileNumber.setText(sellerDetailsModel.getSellerMobileNumber());
            }
        });
    }

    private void getSellerProductsApi(String searchSellerName) {
        SellerProfileDataModel paginationModel = new SellerProfileDataModel(sellerID, 0, 0, "", skipCount, takeCount, 0, searchSellerName);
        sellerProfileViewMode.getSellerProductRequest(paginationModel);
        sellerProfileViewMode.getSellerProductVM().observe(this, new Observer<SellerProductModel>() {
            @Override
            public void onChanged(SellerProductModel sellerProdList) {
                Utils.hideProgressDialog();
                if (sellerProdList != null) {
                    mBinding.rvCategories.post(new Runnable() {
                        public void run() {
                            sellerProductModels.addAll(sellerProdList.getSellerProductLists());
                            if (MainActivity.cartItemModel != null && MainActivity.cartItemModel.getCart().size() > 0) {
                                for (int i = 0; i < sellerProductModels.size(); i++) {
                                    for (int j = 0; j < MainActivity.cartItemModel.getCart().size(); j++) {
                                        if (sellerProductModels.get(i).getId() == MainActivity.cartItemModel.getCart().get(j).getId()) {
                                            sellerProductModels.get(i).setQty(MainActivity.cartItemModel.getCart().get(j).getQuantity());
                                        }
                                    }
                                }
                                sellerShopListAdapter.notifyDataSetChanged();
                            }
                            sellerShopListAdapter.notifyDataSetChanged();
                            loading = true;
                        }
                    });
                } else {
                    loading = false;
                }
            }
        });
    }

    private void addProduct() {
        sellerProfileViewMode.getAddProductVMRequest(new AddViewModel(sellerID));
        sellerProfileViewMode.getAddProductVM().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                 Utils.hideProgressDialog();
            }
        });
    }

    @Override
    public void plusButtonOnClick(SellerProductList sellerProductModel, TextView tvSelectedQty) {
        int increaseCount = Integer.parseInt(tvSelectedQty.getText().toString().trim());
        increaseCount++;
        tvSelectedQty.setText("" + increaseCount);
        SharePrefs.getInstance(SellerProfileActivity.this).putInt(SharePrefs.COUNT, increaseCount);
        addItemInCart(increaseCount, sellerProductModel);
        if (MainActivity.cartItemModel != null) {
            for (int i = 0; i < MainActivity.cartItemModel.getCart().size(); i++) {
                if (sellerProductModel.getId() == MainActivity.cartItemModel.getCart().get(i).getId()) {
                    MainActivity.cartItemModel.getCart().get(i).setQuantity(increaseCount);
                }
            }
        }
    }

    @Override
    public void minusButtonOnClick(SellerProductList sellerProductModel, TextView selectedQty, TextView btAddToCart, LinearLayout LLPlusMinus) {
        int decreaseCount = Integer.parseInt(selectedQty.getText().toString().trim());
        decreaseCount--;
        if (decreaseCount >= 0) {
            selectedQty.setText("" + decreaseCount);
            SharePrefs.getInstance(SellerProfileActivity.this).putInt(SharePrefs.COUNT, decreaseCount);
            addItemInCart(decreaseCount, sellerProductModel);
        } else {
            btAddToCart.setVisibility(View.VISIBLE);
            LLPlusMinus.setVisibility(View.GONE);
        }

        if (MainActivity.cartItemModel != null) {
            for (int i = 0; i < MainActivity.cartItemModel.getCart().size(); i++) {
                if (sellerProductModel.getId() == MainActivity.cartItemModel.getCart().get(i).getId()) {
                    MainActivity.cartItemModel.getCart().get(i).setQuantity(decreaseCount);
                }
            }
        }
    }

    @Override
    public void addButtonOnClick(SellerProductList sellerProductModel, TextView selectedQty, TextView btAddToCart, LinearLayout LLPlusMinus) {
        if (MainActivity.cartItemModel != null && MainActivity.cartItemModel.getEncryptSellerId() != null) {
            if (MainActivity.cartItemModel.getEncryptSellerId().equals(sellerID)) {
                btAddToCart.setVisibility(View.GONE);
                LLPlusMinus.setVisibility(View.VISIBLE);
                CartItemModel.CartModel cartModel = new CartItemModel.CartModel(null,0,null,false,sellerProductModel.isStockRequired(),sellerProductModel.getStock(),sellerProductModel.getMeasurement(),sellerProductModel.getUom(),sellerProductModel.getImagePath(),0,sellerProductModel.getProductName(),0,0,false,0,0,0,sellerProductModel.getQty(),sellerProductModel.getCreatedBy(),null,sellerProductModel.getSellerId(),0,0, sellerProductModel.getMargin(),sellerProductModel.getMrp(),sellerProductModel.getMOQ(),sellerProductModel.getId());
                MainActivity.cartItemModel.getCart().add(cartModel);
                SellerProfileActivity.this.plusButtonOnClick(sellerProductModel, selectedQty);
            } else {
                checkCustomerAlertDialog(MainActivity.cartItemModel.getId(), sellerProductModel,btAddToCart,LLPlusMinus);
            }
        } else {
            btAddToCart.setVisibility(View.GONE);
            LLPlusMinus.setVisibility(View.VISIBLE);
            SellerProfileActivity.this.plusButtonOnClick(sellerProductModel, selectedQty);
        }

    }

    private void addItemInCart(int QTY, SellerProductList sellerProductModel) {
        ItemAddModel paginationModel = new ItemAddModel(QTY, "123", sellerProductModel.getId(), 0, 0);
        sellerProfileViewMode.getAddItemsInCardVMRequest(paginationModel);
        sellerProfileViewMode.getAddItemsInCardVM().observe(this, new Observer<AddCartItemModel>() {
            @Override
            public void onChanged(AddCartItemModel sellerProdList) {
                Utils.hideProgressDialog();
                if (sellerProdList != null) {
                   // sellerShopListAdapter.notifyDataSetChanged();
                }
            }
        });
    }


    public void checkCustomerAlertDialog(String id, SellerProductList sellerProductModel, TextView btAddToCart, LinearLayout LLPlusMinus) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Your Cart has existing items from Another Seller.Do You Want to clear it and add items from this Seller?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearCartItem(id);
                CartItemModel cartModel = new CartItemModel();
                cartModel.setEncryptSellerId(sellerID);
                CartItemModel.CartModel cartItemModel = new CartItemModel.CartModel(null,0,null,false,sellerProductModel.isStockRequired(),sellerProductModel.getStock(),sellerProductModel.getMeasurement(),sellerProductModel.getUom(),sellerProductModel.getImagePath(),0,sellerProductModel.getProductName(),0,0,false,0,0,0,sellerProductModel.getQty(),sellerProductModel.getCreatedBy(),null,sellerProductModel.getSellerId(),0,0, sellerProductModel.getMargin(),sellerProductModel.getMrp(),sellerProductModel.getMOQ(),sellerProductModel.getId());
                cartModel.setCart(new ArrayList<>());
                cartModel.getCart().add(cartItemModel);
                MainActivity.cartItemModel = cartModel;
                sellerShopListAdapter.notifyDataSetChanged();
                btAddToCart.setVisibility(View.GONE);
                LLPlusMinus.setVisibility(View.VISIBLE);
                addItemInCart(1, sellerProductModel);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }

        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void clearCartItem(String id) {
        sellerProfileViewMode.getClearCartItemVMRequest(id);
        sellerProfileViewMode.getClearCartItemVM().observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object object) {
                Utils.hideProgressDialog();
//                if (object != null) {
//                    if (object.equals(true)) {
//                        MainActivity.cartItemModel.getCart().clear();
//                    }
//                }
            }
        });
    }
}
