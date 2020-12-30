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
import com.skdirect.R;
import com.skdirect.adapter.BottomListAdapter;
import com.skdirect.adapter.NearProductListAdapter;
import com.skdirect.adapter.ShowImagesAdapter;
import com.skdirect.databinding.ActivityProductDetailsBinding;
import com.skdirect.model.ImageListModel;
import com.skdirect.model.ProductDataModel;
import com.skdirect.model.ProductVariantAttributeDCModel;
import com.skdirect.model.VariationListModel;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.ProductDetailsViewMode;

import java.util.ArrayList;

public class ProductDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityProductDetailsBinding mBinding;
    private ProductDetailsViewMode productDetailsViewMode;
    private int productID;
    private ArrayList<ImageListModel> imageListModels = new ArrayList<>();
    private ArrayList<VariationListModel> variationList = new ArrayList<>();
    private ArrayList<ProductVariantAttributeDCModel> variantAttributeDCModels = new ArrayList<>();
    private TextView[] dot;
    private ShowImagesAdapter showImagesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_product_details);
        productDetailsViewMode = ViewModelProviders.of(this).get(ProductDetailsViewMode.class);
        getIntentData();
        initView();
        ClickListener();
        callProductData();


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
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_lay, null);
        RecyclerView recyclerView = view.findViewById(R.id.rv_bottom_sheet);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        BottomListAdapter bottomListAdapter = new BottomListAdapter(getApplicationContext(),variationList);
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
                        mBinding.pager.setAdapter(new ShowImagesAdapter(getApplicationContext(), imageListModels));
                        mBinding.indicator.setViewPager(mBinding.pager);

                    } else {
                        mBinding.tvVarientButton.setVisibility(View.GONE);
                        if (productDataModel.getResultItem().getImageList() != null && productDataModel.getResultItem().getImageList().size() > 0) {
                            imageListModels = productDataModel.getResultItem().getImageList();
                        }
                        mBinding.tvItemName.setText(productDataModel.getResultItem().getProductName());
                        mBinding.tvItemMrp.setText("₹ " + productDataModel.getResultItem().getSellingPrice());
                        mBinding.tvItemMrpOff.setText(String.valueOf(productDataModel.getResultItem().getMrp()));
                        mBinding.tvItemMrpPercent.setText("( " + productDataModel.getResultItem().getOffPercentage() + ")" + "%  Off");
                        mBinding.tvTax.setText("Inclusive of all taxes");
                        mBinding.tvDeatils.setText("Features & details");

                        if (productDataModel.getResultItem().getProductVariantSpecification() != null && productDataModel.getResultItem().getProductVariantSpecification().size() > 0) {
                            for (int i = 0; i < productDataModel.getResultItem().getProductVariantSpecification().size(); i++) {
                                mBinding.tvVarient.setText(productDataModel.getResultItem().getProductVariantSpecification().get(i).getAttributeName());
                                mBinding.tvDiscripction.setText(productDataModel.getResultItem().getProductVariantSpecification().get(i).getAttributeValue());
                            }
                        }

                        mBinding.pager.setAdapter(new ShowImagesAdapter(getApplicationContext(), imageListModels));
                        mBinding.indicator.setViewPager(mBinding.pager);

                    }

                }
            }
        });
    }
}
