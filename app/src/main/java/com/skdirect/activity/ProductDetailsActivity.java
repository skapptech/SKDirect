package com.skdirect.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.skdirect.R;
import com.skdirect.adapter.BottomListAdapter;
import com.skdirect.adapter.ShowImagesAdapter;
import com.skdirect.adapter.TopNearByItemAdapter;
import com.skdirect.adapter.TopSellerAdapter;
import com.skdirect.databinding.ActivityProductDetailsBinding;
import com.skdirect.interfacee.BottomBarInterface;
import com.skdirect.model.ImageListModel;
import com.skdirect.model.PaginationModel;
import com.skdirect.model.ProductDataModel;
import com.skdirect.model.ProductVariantAttributeDCModel;
import com.skdirect.model.TopNearByItemModel;
import com.skdirect.model.TopSellerModel;
import com.skdirect.model.VariationListModel;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.ProductDetailsViewMode;

import java.util.ArrayList;

public class ProductDetailsActivity extends AppCompatActivity implements View.OnClickListener, BottomBarInterface {
    ActivityProductDetailsBinding mBinding;
    private ProductDetailsViewMode productDetailsViewMode;
    private int productID;
    private ArrayList<ImageListModel> imageListModels = new ArrayList<>();
    private ArrayList<VariationListModel> variationList = new ArrayList<>();
    private ArrayList<ProductVariantAttributeDCModel> variantAttributeDCModels = new ArrayList<>();
    private BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_product_details);
        productDetailsViewMode = ViewModelProviders.of(this).get(ProductDetailsViewMode.class);
        getIntentData();
        initView();
        ClickListener();
        apiCalling();
    }
    private void apiCalling() {
        callProductData();
        GetTopSimilarProduct();
        GetTopSellar();
        GetSellarOtherProducts();
        GetCartItems();
        addProductAPI();
    }

    private void addProductAPI() {
        if (Utils.isNetworkAvailable(this)) {
            Utils.showProgressDialog(this);
            addProduct();
        } else {
            Utils.setToast(this, "No Internet Connection Please connect.");
        }
    }



    private void GetCartItems() {
        if (Utils.isNetworkAvailable(this)) {
            Utils.showProgressDialog(this);
            cartItemsAPI();
        } else {
            Utils.setToast(this, "No Internet Connection Please connect.");
        }
    }

    private void GetTopSimilarProduct() {
        if (Utils.isNetworkAvailable(this)) {
            Utils.showProgressDialog(this);
            topSimilarProductAPI();
        } else {
            Utils.setToast(this, "No Internet Connection Please connect.");
        }
    }
    private void GetSellarOtherProducts() {
        if (Utils.isNetworkAvailable(this)) {
            Utils.showProgressDialog(this);
            SellarOtherProductsAPI();
        } else {
            Utils.setToast(this, "No Internet Connection Please connect.");
        }
    }
    private void GetTopSellar() {
        if (Utils.isNetworkAvailable(this)) {
            Utils.showProgressDialog(this);
            getTopSeller();
        } else {
            Utils.setToast(this, "No Internet Connection Please connect.");
        }
    }
    private void getIntentData() {
        productID = getIntent().getIntExtra("ID", 0);
    }
    private void callProductData() {
        if (Utils.isNetworkAvailable(this)) {
            Utils.showProgressDialog(this);
            getProductListAPI();
        } else {
            Utils.setToast(this, "No Internet Connection Please connect.");
        }
    }
    private void initView() {
        mBinding.rvNearByItem.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        mBinding.rvOtherSellars.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        mBinding.rvSellarsOthersItems.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        mBinding.toolbarTittle.tvTittle.setText("");
        float density = getResources().getDisplayMetrics().density;
        mBinding.indicator.setRadius(3 * density);
        mBinding.indicator.setFillColor(getResources().getColor(R.color.colorAccent));
        mBinding.pager.startAutoScroll(900);
        mBinding.pager.setInterval(900);
        mBinding.pager.setCycle(true);
        mBinding.pager.setBorderAnimation(false);
        mBinding.pager.setStopScrollWhenTouch(true);
        mBinding.pager.setAutoScrollDurationFactor(20);
        // mBinding.indicator.setViewPager(mBinding.pager);


    }

    private void ClickListener() {
        mBinding.toolbarTittle.ivBackPress.setOnClickListener(this);
        mBinding.btAddToCart.setOnClickListener(this);
        mBinding.tvQtyPlus.setOnClickListener(this);
        mBinding.tvQtyMinus.setOnClickListener(this);
        mBinding.tvVarientButton.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_press:
                onBackPressed();
                break;

            case R.id.bt_add_to_cart:
                addToCart();
                break;

            case R.id.tvQtyPlus:
                increaseQTY();
                break;

            case R.id.tvQtyMinus:
                decreaseQTY();
                break;

            case R.id.tv_varient_button:
                openBottomSheetDialog();
                break;
        }
    }
    private void openBottomSheetDialog() {
        bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_lay, null);
        RecyclerView recyclerView = view.findViewById(R.id.rv_bottom_sheet);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        BottomListAdapter bottomListAdapter = new BottomListAdapter(getApplicationContext(), variationList, this);
        recyclerView.setAdapter(bottomListAdapter);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

    }
    private void increaseQTY() {
        int increaseCount = Integer.parseInt(mBinding.tvSelectedQty.getText().toString().trim());
        increaseCount++;
        mBinding.tvSelectedQty.setText("" + increaseCount);


    }
    private void decreaseQTY() {
        int decreaseCount = Integer.parseInt(mBinding.tvSelectedQty.getText().toString().trim());
        decreaseCount--;

        if (decreaseCount >= 0) {
            mBinding.tvSelectedQty.setText("" + decreaseCount);
        } else {
            mBinding.btAddToCart.setVisibility(View.VISIBLE);
            mBinding.LLPlusMinus.setVisibility(View.GONE);
        }
    }
    private void addToCart() {
        mBinding.btAddToCart.setVisibility(View.GONE);
        mBinding.LLPlusMinus.setVisibility(View.VISIBLE);

    }
    private void addProduct() {
        productDetailsViewMode.getAddProductVMRequest(new PaginationModel(productID));
        productDetailsViewMode.getAddProductVM().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Utils.hideProgressDialog();

            }

        });
    }

    private void SellarOtherProductsAPI() {
        productDetailsViewMode.getSellarOtherVMRequest(productID);
        productDetailsViewMode.getSallerOtherProducsVM().observe(this, new Observer<ArrayList<TopNearByItemModel>>() {
            @Override
            public void onChanged(ArrayList<TopNearByItemModel> topNearSimilarProduct) {
                Utils.hideProgressDialog();
                if (topNearSimilarProduct.size()>0){
                    mBinding.llSellarsOtherProducs.setVisibility(View.VISIBLE);
                    TopNearByItemAdapter topNearByItemAdapter = new TopNearByItemAdapter(ProductDetailsActivity.this,topNearSimilarProduct);
                    mBinding.rvSellarsOthersItems.setAdapter(topNearByItemAdapter);

                }else {
                    mBinding.llSellarsOtherProducs.setVisibility(View.GONE);
                }

            }

        });
    }
    private void topSimilarProductAPI() {
        productDetailsViewMode.getSimilarProductVMRequest(productID);
        productDetailsViewMode.getSimilarProductVM().observe(this, new Observer<ArrayList<TopNearByItemModel>>() {
            @Override
            public void onChanged(ArrayList<TopNearByItemModel> topNearSimilarProduct) {
                Utils.hideProgressDialog();
                if (topNearSimilarProduct.size()>0){
                    mBinding.llSimilarProduct.setVisibility(View.VISIBLE);
                    TopNearByItemAdapter topNearByItemAdapter = new TopNearByItemAdapter(ProductDetailsActivity.this,topNearSimilarProduct);
                    mBinding.rvNearByItem.setAdapter(topNearByItemAdapter);

                }else {

                    mBinding.llSimilarProduct.setVisibility(View.GONE);
                }

            }

        });

    }
    private void getTopSeller() {
        productDetailsViewMode.GetTopSellerLiveRequest(productID);
        productDetailsViewMode.GetTopSellerLiveData().observe(this, new Observer<ArrayList<TopSellerModel>>() {
            @Override
            public void onChanged(ArrayList<TopSellerModel> topSellerList) {
                Utils.hideProgressDialog();
                if (topSellerList.size()>0){
                    mBinding.llOtherSellar.setVisibility(View.VISIBLE);
                    TopSellerAdapter topSellerAdapter = new TopSellerAdapter(ProductDetailsActivity.this,topSellerList);
                    mBinding.rvOtherSellars.setAdapter(topSellerAdapter);

                }else {
                    mBinding.llOtherSellar.setVisibility(View.GONE);
                }
            }
        });
    }
    private void cartItemsAPI() {
        productDetailsViewMode.getCartItemsVMRequest("123");
        productDetailsViewMode.getCartItemsVM().observe(this, new Observer<JsonObject>() {
            @Override
            public void onChanged(JsonObject topSellerList) {
                Utils.hideProgressDialog();


            }
        });

    }
    private void getProductListAPI() {
        productDetailsViewMode.getCategoriesViewModelRequest(productID);
        productDetailsViewMode.getProductDetailsVM().observe(this, new Observer<ProductDataModel>() {
            @Override
            public void onChanged(ProductDataModel productDataModel) {
                Utils.hideProgressDialog();
                if (productDataModel.getResultItem() != null) {
                    if (productDataModel.getResultItem().getVariationModelList() != null && productDataModel.getResultItem().getVariationModelList().size() > 0) {
                        variationList = productDataModel.getResultItem().getVariationModelList();
                        for (int i = 0; i < productDataModel.getResultItem().getVariationModelList().size(); i++) {
                            mBinding.tvVarientButton.setVisibility(View.VISIBLE);
                            if (productDataModel.getResultItem().getVariationModelList().get(i).getImageList() != null && productDataModel.getResultItem().getVariationModelList().get(i).getImageList().size() > 0) {
                                imageListModels = productDataModel.getResultItem().getVariationModelList().get(i).getImageList();
                            }
                            mBinding.tvItemName.setText(productDataModel.getResultItem().getVariationModelList().get(i).getProductName());
                            mBinding.tvItemMrp.setText("₹ " + productDataModel.getResultItem().getVariationModelList().get(i).getSellingPrice());
                            mBinding.tvItemMrpOff.setText(String.valueOf(productDataModel.getResultItem().getVariationModelList().get(i).getMrp()));
                            mBinding.tvItemMrpPercent.setText(productDataModel.getResultItem().getVariationModelList().get(i).getOffPercentage() + "%  Off");
                            mBinding.tvTax.setText("Inclusive of all taxes");
                            mBinding.tvDeatils.setText("Features & details");


                            String varientName = "";
                            if (productDataModel.getResultItem().getVariationModelList().get(i).getProductVariantAttributeDC() != null && productDataModel.getResultItem().getVariationModelList().get(i).getProductVariantAttributeDC().size() > 0) {
                                for (int j = 0; j < productDataModel.getResultItem().getVariationModelList().get(i).getProductVariantAttributeDC().size(); j++) {
                                    variantAttributeDCModels = productDataModel.getResultItem().getVariationModelList().get(j).getProductVariantAttributeDC();
                                    varientName = varientName + variantAttributeDCModels.get(j).getAttributeName() + " :" + variantAttributeDCModels.get(j).getAttributeValue() + " ";
                                }
                            }
                            mBinding.tvItemColor.setText(varientName);

                            if (productDataModel.getResultItem().getVariationModelList().get(0).getProductVariantSpecification() != null && productDataModel.getResultItem().getVariationModelList().get(0).getProductVariantSpecification().size() > 0) {
                                mBinding.tvVarient.setText(productDataModel.getResultItem().getVariationModelList().get(0).getProductVariantSpecification().get(0).getAttributeName());
                                mBinding.tvDiscripction.setText(productDataModel.getResultItem().getVariationModelList().get(0).getProductVariantSpecification().get(0).getAttributeValue());
                            }
                        }
                        mBinding.pager.setAdapter(new ShowImagesAdapter(ProductDetailsActivity.this, imageListModels));
                        mBinding.indicator.setViewPager(mBinding.pager);

                    } else {
                        mBinding.tvVarientButton.setVisibility(View.GONE);
                        if (productDataModel.getResultItem().getImageList() != null && productDataModel.getResultItem().getImageList().size() > 0) {
                            imageListModels = productDataModel.getResultItem().getImageList();
                        }
                        mBinding.tvItemName.setText(productDataModel.getResultItem().getProductName());
                        mBinding.tvItemMrp.setText("₹ " + productDataModel.getResultItem().getSellingPrice());
                        mBinding.tvItemMrpOff.setText(String.valueOf(productDataModel.getResultItem().getMrp()));
                        if (productDataModel.getResultItem().getOffPercentage() != 0.0) {
                            mBinding.tvItemMrpPercent.setText("( " + productDataModel.getResultItem().getOffPercentage() + ")" + "%  Off");
                        } else {
                            mBinding.tvItemMrpPercent.setVisibility(View.GONE);
                        }
                        mBinding.tvTax.setText("Inclusive of all taxes");
                        mBinding.tvDeatils.setText("Features & details");

                        if (productDataModel.getResultItem().getProductVariantSpecification() != null && productDataModel.getResultItem().getProductVariantSpecification().size() > 0) {
                            for (int i = 0; i < productDataModel.getResultItem().getProductVariantSpecification().size(); i++) {
                                mBinding.tvVarient.setText(productDataModel.getResultItem().getProductVariantSpecification().get(i).getAttributeName());
                                mBinding.tvDiscripction.setText(productDataModel.getResultItem().getProductVariantSpecification().get(i).getAttributeValue());
                            }
                        }
                        mBinding.pager.setAdapter(new ShowImagesAdapter(ProductDetailsActivity.this, imageListModels));
                        mBinding.indicator.setViewPager(mBinding.pager);

                    }

                }
            }
        });
    }

    @Override
    public void onOnClick(VariationListModel variationListModel) {
        if (bottomSheetDialog != null) {
            bottomSheetDialog.dismiss();
        }
        productID = variationListModel.getId();
        callProductData();

    }
}
