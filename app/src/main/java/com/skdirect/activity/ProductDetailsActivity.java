package com.skdirect.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.skdirect.R;
import com.skdirect.adapter.BottomListAdapter;
import com.skdirect.adapter.ShowImagesAdapter;
import com.skdirect.adapter.TopSellerAdapter;
import com.skdirect.adapter.TopSimilarSellerAdapter;
import com.skdirect.databinding.ActivityProductDetailsBinding;
import com.skdirect.interfacee.BottomBarInterface;
import com.skdirect.model.CartModel;
import com.skdirect.model.ImageListModel;
import com.skdirect.model.ItemAddModel;
import com.skdirect.model.MainSimilarTopSellerModel;
import com.skdirect.model.MainTopSimilarSellerModel;
import com.skdirect.model.ProductDataModel;
import com.skdirect.model.ProductResultModel;
import com.skdirect.model.ProductVariantAttributeDCModel;
import com.skdirect.model.VariationListModel;
import com.skdirect.utils.MyApplication;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.ProductDetailsViewMode;

import java.util.ArrayList;

public class ProductDetailsActivity extends AppCompatActivity implements View.OnClickListener, BottomBarInterface {
    private ActivityProductDetailsBinding mBinding;
    private ProductDetailsViewMode productDetailsViewMode;
    private int productID;
    private ArrayList<ImageListModel> imageListModels = new ArrayList<>();
    private ArrayList<VariationListModel> variationList = new ArrayList<>();
    private ArrayList<ProductVariantAttributeDCModel> variantAttributeDCModels = new ArrayList<>();
    private BottomSheetDialog bottomSheetDialog;
    private int SellerItemID;
    private ProductResultModel resultModel = new ProductResultModel();


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

    @Override
    protected void onResume() {
        super.onResume();
        setupBadge();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_press:
                onBackPressed();
                break;
            case R.id.bt_add_to_cart:
                resultModel.setQty(1);
                mBinding.tvSelectedQty.setText("" + resultModel.getQty());
                addToCart();
                break;
            case R.id.tvQtyPlus:
                resultModel.setQty(resultModel.getQty() + 1);
                mBinding.tvSelectedQty.setText("" + resultModel.getQty());
                updateCart();
                break;
            case R.id.tvQtyMinus:
                resultModel.setQty(resultModel.getQty() - 1);
                mBinding.tvSelectedQty.setText("" + resultModel.getQty());
                updateCart();
                break;
            case R.id.tv_varient_button:
                openBottomSheetDialog();
                break;
            case R.id.notifiction_count:
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
                break;
            case R.id.tv_shop_name:
                startActivity(new Intent(getApplicationContext(), SellerProfileActivity.class).putExtra("ID", resultModel.getEncryptSellerId()));
                break;
        }
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
        mBinding.toolbarTittle.notifictionCount.setOnClickListener(this);
        mBinding.btAddToCart.setOnClickListener(this);
        mBinding.tvQtyPlus.setOnClickListener(this);
        mBinding.tvQtyMinus.setOnClickListener(this);
        mBinding.tvVarientButton.setOnClickListener(this);
        mBinding.tvShopName.setOnClickListener(this);
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

    private void updateCart() {
        int qty = resultModel.getQty();
        CartModel cartModel = new CartModel(null, 0, null,
                resultModel.IsActive, resultModel.IsStockRequired, resultModel.getStock(), resultModel.getMeasurement(),
                resultModel.getUom(), "", 0, resultModel.getProductName(), 0,
                0, resultModel.IsDelete, resultModel.getOffPercentage(), 0, 0, qty,
                0, null, resultModel.getSellerId(), 0, 0, 0,
                resultModel.getMrp(), 0, resultModel.getId());
        if (qty >= 0) {
            addItemInCart(qty, SellerItemID);
            MyApplication.getInstance().cartRepository.updateCartItem(cartModel);
        }
        if (qty == 0) {
            mBinding.btAddToCart.setVisibility(View.VISIBLE);
            mBinding.LLPlusMinus.setVisibility(View.GONE);
            MyApplication.getInstance().cartRepository.deleteCartItem(cartModel);
        }
    }

    private void addToCart() {
        mBinding.toolbarTittle.notifictionCount.setVisibility(View.VISIBLE);
        Integer cartSellerId = MyApplication.getInstance().cartRepository.getCartSellerId();
        if (cartSellerId != null && cartSellerId != null) {
            if (cartSellerId == resultModel.getSellerId()) {
                mBinding.btAddToCart.setVisibility(View.GONE);
                mBinding.LLPlusMinus.setVisibility(View.VISIBLE);
                CartModel cartModel = new CartModel(null, 0, null, resultModel.IsActive, resultModel.IsStockRequired, resultModel.getStock(), resultModel.getMeasurement(), resultModel.getUom(), "", 0, resultModel.getProductName(), 0, 0, resultModel.IsDelete, resultModel.getOffPercentage(), 0, 0, 1, 0, null, resultModel.getSellerId(), 0, 0, 0, resultModel.getMrp(), 0, resultModel.getId());
                addItemInCart(1, SellerItemID);
                MyApplication.getInstance().cartRepository.addToCart(cartModel);
            } else {
                checkCustomerAlertDialog(cartSellerId);
            }
        } else {
            mBinding.btAddToCart.setVisibility(View.GONE);
            mBinding.LLPlusMinus.setVisibility(View.VISIBLE);
            CartModel cartModel = new CartModel(null, 0, null, resultModel.IsActive, resultModel.IsStockRequired, resultModel.getStock(), resultModel.getMeasurement(), resultModel.getUom(), "", 0, resultModel.getProductName(), 0, 0, resultModel.IsDelete, resultModel.getOffPercentage(), 0, 0, 1, 0, null, resultModel.getSellerId(), 0, 0, 0, resultModel.getMrp(), 0, resultModel.getId());
            addItemInCart(1, SellerItemID);
        }
    }

    private void addProduct() {
        productDetailsViewMode.getAddProductVMRequest(productID);
        productDetailsViewMode.getAddProductVM().observe(this, aBoolean -> {
            Utils.hideProgressDialog();
        });
    }

    private void SellarOtherProductsAPI() {
        productDetailsViewMode.getSellarOtherVMRequest(productID);
        productDetailsViewMode.getSallerOtherProducsVM().observe(this, new Observer<MainTopSimilarSellerModel>() {
            @Override
            public void onChanged(MainTopSimilarSellerModel model) {
                Utils.hideProgressDialog();
                if (model.isSuccess()) {
                    if (model.getResultItem().size() > 0) {
                        mBinding.llSellarsOtherProducs.setVisibility(View.VISIBLE);
                        TopSimilarSellerAdapter topSimilarSellerAdapter = new TopSimilarSellerAdapter(ProductDetailsActivity.this, model.getResultItem());
                        mBinding.rvSellarsOthersItems.setAdapter(topSimilarSellerAdapter);
                    } else {
                        mBinding.llSellarsOtherProducs.setVisibility(View.GONE);
                    }
                } else {
                }
            }
        });
    }

    private void topSimilarProductAPI() {
        productDetailsViewMode.getSimilarProductVMRequest(productID);
        productDetailsViewMode.getSimilarProductVM().observe(this, new Observer<MainTopSimilarSellerModel>() {
            @Override
            public void onChanged(MainTopSimilarSellerModel topNearSimilarProduct) {
                Utils.hideProgressDialog();
                if (topNearSimilarProduct.isSuccess()) {
                    if (topNearSimilarProduct.getResultItem().size() > 0) {
                        mBinding.llSimilarProduct.setVisibility(View.VISIBLE);
                        TopSimilarSellerAdapter topSellerAdapter = new TopSimilarSellerAdapter(ProductDetailsActivity.this, topNearSimilarProduct.getResultItem());
                        mBinding.rvNearByItem.setAdapter(topSellerAdapter);

                    } else {

                        mBinding.llSimilarProduct.setVisibility(View.GONE);
                    }
                } else {
                    Utils.setToast(ProductDetailsActivity.this, topNearSimilarProduct.getErrorMessage());
                }


            }

        });

    }

    private void getTopSeller() {
        productDetailsViewMode.GetTopSellerLiveRequest(productID);
        productDetailsViewMode.GetTopSellerLiveData().observe(this, new Observer<MainSimilarTopSellerModel>() {
            @Override
            public void onChanged(MainSimilarTopSellerModel topSimilarSellerModel) {
                Utils.hideProgressDialog();
                if (topSimilarSellerModel.isSuccess()) {
                    if (topSimilarSellerModel.getResultItem().size() > 0) {
                        mBinding.llOtherSellar.setVisibility(View.VISIBLE);
                        TopSellerAdapter topSellerAdapter = new TopSellerAdapter(ProductDetailsActivity.this, topSimilarSellerModel.getResultItem());
                        mBinding.rvOtherSellars.setAdapter(topSellerAdapter);
                    } else {
                        mBinding.llOtherSellar.setVisibility(View.GONE);
                    }
                } else {
                    Utils.setToast(ProductDetailsActivity.this, topSimilarSellerModel.getErrorMessage());
                }
            }
        });
    }

    private void cartItemsAPI() {
        productDetailsViewMode.getCartItemsVMRequest("123");
        productDetailsViewMode.getCartItemsVM().observe(this, model -> {
            Utils.hideProgressDialog();
            Utils.hideProgressDialog();
            if (model != null && model.getCart() != null) {
                MyApplication.getInstance().cartRepository.addToCart(model.getCart());
                SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.CART_ITEM_ID, model.getId());
            }
        });
    }


    private void getProductListAPI() {
        productDetailsViewMode.getCategoriesViewModelRequest(productID);
        productDetailsViewMode.getProductDetailsVM().observe(this, new Observer<ProductDataModel>() {
            @Override
            public void onChanged(ProductDataModel productDataModel) {
                Utils.hideProgressDialog();
                if (productDataModel.getSuccess()) {
                    if (productDataModel.getResultItem() != null) {
                        resultModel = productDataModel.getResultItem();
                        SellerItemID = productDataModel.getResultItem().getId();
                        checkAddButtonValidaction();
                        if (productDataModel.getResultItem().getVariationModelList() != null && productDataModel.getResultItem().getVariationModelList().size() > 0) {
                            variationList = productDataModel.getResultItem().getVariationModelList();
                            for (int i = 0; i < productDataModel.getResultItem().getVariationModelList().size(); i++) {
                                mBinding.tvVarientButton.setVisibility(View.VISIBLE);
                                if (productDataModel.getResultItem().getVariationModelList().get(i).getImageList() != null && productDataModel.getResultItem().getVariationModelList().get(i).getImageList().size() > 0) {
                                    imageListModels = productDataModel.getResultItem().getVariationModelList().get(i).getImageList();
                                }

                                if (productDataModel.getResultItem().getVariationModelList().get(i).getDiscountAmount() == 0.0) {
                                    mBinding.tvDiscount.setVisibility(View.VISIBLE);
                                    double DiscountAmount = productDataModel.getResultItem().getVariationModelList().get(i).getSellingPrice() - productDataModel.getResultItem().getVariationModelList().get(i).getDiscountAmount();
                                    mBinding.tvDiscount.setText("" + DiscountAmount + "OFF");
                                    //mBinding.tvItemMrpOff.setPaintFlags(mBinding.tvItemMrpOff.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                }

                                if (productDataModel.getResultItem().getVariationModelList().get(i).getOffPercentage() != 0.0) {
                                    mBinding.tvMagrginOff.setVisibility(View.VISIBLE);
                                    mBinding.tvMagrginOff.setText(productDataModel.getResultItem().getVariationModelList().get(i).getOffPercentage() + "%\n OFF");
                                } else {
                                    mBinding.tvMagrginOff.setVisibility(View.GONE);
                                }

                                mBinding.tvItemName.setText(productDataModel.getResultItem().getVariationModelList().get(i).getProductName());
                                if (productDataModel.getResultItem().getVariationModelList().get(i).getMrp() == productDataModel.getResultItem().getVariationModelList().get(i).getSellingPrice()) {
                                    mBinding.llSellingPrice.setVisibility(View.GONE);
                                    mBinding.tvItemMrp.setText("₹ " + productDataModel.getResultItem().getVariationModelList().get(i).getMrp());
                                } else {
                                    mBinding.tvItemMrp.setText("₹ " + productDataModel.getResultItem().getVariationModelList().get(i).getMrp());
                                    mBinding.tvItemMrp.setPaintFlags(mBinding.tvItemMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                    mBinding.tvSellingPrice.setText(String.valueOf(productDataModel.getResultItem().getVariationModelList().get(i).getSellingPrice()));
                                }

                                mBinding.tvQuantity.setText("Quantity " + productDataModel.getResultItem().getVariationModelList().get(i).getMeasurement() + productDataModel.getResultItem().getVariationModelList().get(i).getUomValue());
                                mBinding.tvShopName.setText(productDataModel.getResultItem().getShopName());
                                String varientName = "";
                                if (productDataModel.getResultItem().getVariationModelList().get(i).getProductVariantAttributeDC() != null && productDataModel.getResultItem().getVariationModelList().get(i).getProductVariantAttributeDC().size() > 0) {
                                    for (int j = 0; j < productDataModel.getResultItem().getVariationModelList().get(i).getProductVariantAttributeDC().size(); j++) {
                                        variantAttributeDCModels = productDataModel.getResultItem().getVariationModelList().get(j).getProductVariantAttributeDC();
                                        varientName = varientName + variantAttributeDCModels.get(j).getAttributeName() + " :" + variantAttributeDCModels.get(j).getAttributeValue() + " ";
                                    }
                                }
                                mBinding.tvItemColor.setText(varientName);

                                if (productDataModel.getResultItem().getVariationModelList().get(0).getProductVariantSpecification() != null && productDataModel.getResultItem().getVariationModelList().get(0).getProductVariantSpecification().size() > 0) {
                               /* mBinding.tvVarient.setText(productDataModel.getResultItem().getVariationModelList().get(0).getProductVariantSpecification().get(0).getAttributeName());
                                mBinding.tvDiscripction.setText(productDataModel.getResultItem().getVariationModelList().get(0).getProductVariantSpecification().get(0).getAttributeValue());*/
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

                            if (productDataModel.getResultItem().getMrp() == productDataModel.getResultItem().getSellingPrice()) {
                                mBinding.llSellingPrice.setVisibility(View.GONE);
                                mBinding.tvItemMrp.setText("₹ " + productDataModel.getResultItem().getMrp());
                            } else {
                                mBinding.tvItemMrp.setText("₹ " + productDataModel.getResultItem().getMrp());
                                mBinding.tvItemMrp.setPaintFlags(mBinding.tvItemMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                mBinding.tvSellingPrice.setText(String.valueOf(productDataModel.getResultItem().getSellingPrice()));
                                mBinding.tvAddresh.setText(productDataModel.getResultItem().getAddressOne() + " " + productDataModel.getResultItem().getAddressTwo() + "\n- " + productDataModel.getResultItem().getPincode() + "(" + productDataModel.getResultItem().getState() + ")");
                            }


                            if (productDataModel.getResultItem().getDeliveryOptionDC().size() > 0) {
                                mBinding.llDeliverOption.setVisibility(View.VISIBLE);
                                String deliveryOption = "";
                                for (int j = 0; j < productDataModel.getResultItem().getDeliveryOptionDC().size(); j++) {
                                    deliveryOption = deliveryOption + " " + productDataModel.getResultItem().getDeliveryOptionDC().get(j).getDelivery();
                                }
                                mBinding.tvDeliveryOption.setText(deliveryOption);
                            }

                            if (productDataModel.getResultItem().getDiscountAmount() == 0.0) {
                                double DiscountAmount = productDataModel.getResultItem().getSellingPrice() - productDataModel.getResultItem().getDiscountAmount();
                                mBinding.tvDiscount.setText("" + DiscountAmount + "OFF");
                            }
                            if (productDataModel.getResultItem().getOffPercentage() != 0.0) {
                                mBinding.tvMagrginOff.setVisibility(View.VISIBLE);
                                mBinding.tvMagrginOff.setText(productDataModel.getResultItem().getOffPercentage() + "%\n OFF");
                            } else {
                                mBinding.tvMagrginOff.setVisibility(View.GONE);
                            }
                            mBinding.tvQuantity.setText("Quantity " + productDataModel.getResultItem().getMeasurement() + " " + productDataModel.getResultItem().getUomValue());
                            mBinding.tvShopName.setText(productDataModel.getResultItem().getShopName());
                            if (productDataModel.getResultItem().getProductVariantSpecification() != null && productDataModel.getResultItem().getProductVariantSpecification().size() > 0) {
                                for (int i = 0; i < productDataModel.getResultItem().getProductVariantSpecification().size(); i++) {

                                    TextView tv = new TextView(getApplicationContext());
                                    TextView textView = new TextView(getApplicationContext());

                                    LinearLayout linearLayout = new LinearLayout(getApplicationContext());
                                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                                    tv.setText(productDataModel.getResultItem().getProductVariantSpecification().get(i).getAttributeName());
                                    textView.setText(productDataModel.getResultItem().getProductVariantSpecification().get(i).getAttributeValue());
                                    tv.setTextColor(getResources().getColor(R.color.seller_button_color));
                                    textView.setTextColor(getResources().getColor(R.color.black));

                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    params.setMargins(10, 0, 10, 0);
                                    tv.setLayoutParams(params);

                                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    params1.setMargins(10, 0, 10, 0);
                                    textView.setLayoutParams(params);

                                    linearLayout.addView(tv);
                                    linearLayout.addView(textView);
                                    mBinding.llDiscr.addView(linearLayout);
                                }
                            }
                            mBinding.pager.setAdapter(new ShowImagesAdapter(ProductDetailsActivity.this, imageListModels));
                            mBinding.indicator.setViewPager(mBinding.pager);
                        }
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

    private void addItemInCart(int QTY, int sellerItemID) {
        ItemAddModel paginationModel = new ItemAddModel(QTY, "123", sellerItemID, 0, 0);
        productDetailsViewMode.getAddItemsInCardVMRequest(paginationModel);
        productDetailsViewMode.getAddItemsInCardVM().observe(this, sellerProdList -> {
            Utils.hideProgressDialog();
        });
    }

    public void checkCustomerAlertDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Your Cart has existing items from Another Seller.Do You Want to clear it and add items from this Seller?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            clearCartItem(id);
            CartModel cartModel = new CartModel(null, 0, null,
                    resultModel.IsActive, resultModel.IsStockRequired, resultModel.getStock(), resultModel.getMeasurement(),
                    resultModel.getUom(), "", 0, resultModel.getProductName(), 0,
                    0, resultModel.IsDelete, resultModel.getOffPercentage(), 0, 0, 1,
                    0, null, resultModel.getSellerId(), 0, 0, 0,
                    resultModel.getMrp(), 0, resultModel.getId());
            MyApplication.getInstance().cartRepository.addToCart(cartModel);
            addItemInCart(1, SellerItemID);
            mBinding.btAddToCart.setVisibility(View.GONE);
            mBinding.LLPlusMinus.setVisibility(View.VISIBLE);
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });

//        AlertDialog dialog = builder.create();
        builder.show();
    }

    private void clearCartItem(int id) {
        productDetailsViewMode.getClearCartItemVMRequest(id);
        productDetailsViewMode.getClearCartItemVM().observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object object) {
                Utils.hideProgressDialog();
            }
        });
    }

    private void checkAddButtonValidaction() {
        if (MyApplication.getInstance().cartRepository.isItemInCart(SellerItemID)) {
            mBinding.btAddToCart.setVisibility(View.GONE);
            mBinding.LLPlusMinus.setVisibility(View.VISIBLE);
            resultModel.setQty(MyApplication.getInstance().cartRepository.getItemQty(SellerItemID));
            mBinding.tvSelectedQty.setText(String.valueOf(resultModel.getQty()));
        } else {
            mBinding.btAddToCart.setVisibility(View.VISIBLE);
            mBinding.LLPlusMinus.setVisibility(View.GONE);
            resultModel.setQty(0);
        }
    }

    private void setupBadge() {
        MyApplication.getInstance().cartRepository.getCartCount().observe(this, count -> {
            if (count == 0) {
                mBinding.toolbarTittle.notifictionCount.setVisibility(View.GONE);
            } else {
                mBinding.toolbarTittle.notifictionCount.setVisibility(View.VISIBLE);
                mBinding.toolbarTittle.cartBadge.setText(String.valueOf(Math.min(count, 99)));
            }
        });
        checkAddButtonValidaction();
    }
}