package com.skdirect.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.skdirect.BuildConfig;
import com.skdirect.R;
import com.skdirect.adapter.BottomListAdapter;
import com.skdirect.adapter.ShowImagesAdapter;
import com.skdirect.adapter.TopSellerAdapter;
import com.skdirect.adapter.TopSimilarSellerAdapter;
import com.skdirect.api.CommonClassForAPI;
import com.skdirect.databinding.ActivityProductDetailsBinding;
import com.skdirect.interfacee.BottomBarInterface;
import com.skdirect.model.AddCartItemModel;
import com.skdirect.model.AddReviewModel;
import com.skdirect.model.AddViewMainModel;
import com.skdirect.model.CartItemModel;
import com.skdirect.model.CartModel;
import com.skdirect.model.ImageListModel;
import com.skdirect.model.ItemAddModel;
import com.skdirect.model.MainSimilarTopSellerModel;
import com.skdirect.model.MainTopSimilarSellerModel;
import com.skdirect.model.ProductDataModel;
import com.skdirect.model.ProductResultModel;
import com.skdirect.model.ProductVariantAttributeDCModel;
import com.skdirect.model.VariationListModel;
import com.skdirect.utils.Constant;
import com.skdirect.utils.DBHelper;
import com.skdirect.utils.MyApplication;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.ProductDetailsViewMode;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Objects;

import io.reactivex.observers.DisposableObserver;

public class ProductDetailsActivity extends AppCompatActivity implements View.OnClickListener, BottomBarInterface {
    private ActivityProductDetailsBinding mBinding;
    private int productID;
    private ArrayList<ImageListModel> imageListModels = new ArrayList<>();
    private ArrayList<VariationListModel> variationList = new ArrayList<>();
    private ArrayList<ProductVariantAttributeDCModel> variantAttributeDCModels = new ArrayList<>();
    private BottomSheetDialog bottomSheetDialog;
    private int SellerItemID;
    private ProductResultModel resultModel = new ProductResultModel();
    private String shopName;
    private DBHelper dbHelper;
    private String productName;
    private Bitmap bitmapx;
    private CommonClassForAPI commonClassForAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_product_details);
        dbHelper = MyApplication.getInstance().dbHelper;
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
                if (resultModel.getMaxOrderQuantity() != null && Integer.parseInt(resultModel.getMaxOrderQuantity()) > 0 && resultModel.getQty() >= Integer.parseInt(resultModel.getMaxOrderQuantity())) {
                    Utils.setToast(getApplicationContext(), dbHelper.getString(R.string.order_quantity));
                } else {
                    resultModel.setQty(resultModel.getQty() + 1);
                    mBinding.tvSelectedQty.setText("" + resultModel.getQty());
                    updateCart();
                }
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
                startActivity(new Intent(getApplicationContext(), SellerProfileActivity.class)
                        .putExtra("ID", resultModel.getSellerId()));
                break;
            case R.id.imShare:
                callRunTimePermissions();
                break;
        }
    }

    public void callRunTimePermissions() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                Log.e("onDenied", "onGranted");
                shareProductDetails();
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
                Log.e("onDenied", "onDenied" + deniedPermissions);

            }
        });
    }

    public void shareProductDetails() {
            Picasso.get().load(resultModel.getImagePath()).into(mBinding.ivDemo);
            BitmapDrawable drawable = (BitmapDrawable) mBinding.ivDemo.getDrawable();
            bitmapx = drawable.getBitmap();
            Utils.shareProduct(ProductDetailsActivity.this,
                    dbHelper.getString(R.string.hello_check_product) +
                            " " + productName + " " +
                            dbHelper.getString(R.string.social_mall_home) + "\n" +
                            SharePrefs.getInstance(this).getString(SharePrefs.BUYER_URL) + "/product/" + productID, bitmapx);

    }


    private void apiCalling() {
        callProductData();
        GetCartItems();
        if (SharePrefs.getInstance(this).getBoolean(SharePrefs.IS_Mall)) {
            mBinding.llSellarsOtherProducs.setVisibility(View.VISIBLE);
            GetSellarOtherProducts();
        } else {
            mBinding.llOtherSellar.setVisibility(View.GONE);
            mBinding.llSimilarProduct.setVisibility(View.GONE);
            /*GetTopSellar();
            GetSellarOtherProducts();
            GetTopSimilarProduct();*/
        }
    }


    private void addProductAPI() {
        if (Utils.isNetworkAvailable(this)) {
            Utils.showProgressDialog(this);
            addProduct();
        } else {
            Utils.setToast(getApplicationContext(), dbHelper.getString(R.string.no_internet_connection));
        }
    }

    private void GetCartItems() {
        if (Utils.isNetworkAvailable(this)) {
            Utils.showProgressDialog(this);
            cartItemsAPI();
        } else {
            Utils.setToast(getApplicationContext(), dbHelper.getString(R.string.no_internet_connection));
        }
    }

    private void GetSellarOtherProducts() {
        if (Utils.isNetworkAvailable(this)) {
            Utils.showProgressDialog(this);
            SellarOtherProductsAPI();
        } else {
            Utils.setToast(getApplicationContext(), dbHelper.getString(R.string.no_internet_connection));
        }
    }

    private void getIntentData() {
        if (getIntent().getData() != null) {
            String sharedUrl = getIntent().getData().toString();
            sharedUrl = sharedUrl.substring(sharedUrl.lastIndexOf("/") + 1);
            productID = Integer.parseInt(sharedUrl);
        } else {
            productID = getIntent().getIntExtra("ID", 0);
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() != null) {
            productID = intent.getIntExtra("ID", 0);
            apiCalling();
        }
    }

    private void callProductData() {
        if (Utils.isNetworkAvailable(this)) {
            Utils.showProgressDialog(this);
            getProductListAPI();
        } else {
            Utils.setToast(getApplicationContext(), dbHelper.getString(R.string.no_internet_connection));
        }
    }

    private void initView() {
        commonClassForAPI = CommonClassForAPI.getInstance(this);
        mBinding.tvMrpTitle.setText(dbHelper.getString(R.string.txt_mrp));
        mBinding.tvTax.setText(dbHelper.getString(R.string.txt_Inclusive));
        mBinding.tvQuantity.setText(dbHelper.getString(R.string.txt_Inclusive));
        mBinding.tvSellingPriceTitle.setText(dbHelper.getString(R.string.txt_selling_price));
        mBinding.tvSellerDeatil.setText(dbHelper.getString(R.string.txt_seller_details));
        mBinding.tvShopNameTitle.setText(dbHelper.getString(R.string.txt_shop_name));
        mBinding.tvSimilaProducts.setText(dbHelper.getString(R.string.similar_products));
        mBinding.tvOtherSellers.setText(dbHelper.getString(R.string.other_sellers));
        mBinding.tvSellersOtherProduct.setText(dbHelper.getString(R.string.seller_s_others_product));
        mBinding.btAddToCart.setText(dbHelper.getString(R.string.add_to_cart));
        mBinding.tvVarientButton.setText(dbHelper.getString(R.string.variants));
        mBinding.tvDeliveryOptionsTitle.setText(dbHelper.getString(R.string.txt_delivery));

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
        mBinding.toolbarTittle.imShare.setOnClickListener(this);
        mBinding.toolbarTittle.imShare.setVisibility(View.VISIBLE);
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
            MyApplication.getInstance().cartRepository.updateCartItem(cartModel);
        }
        if (qty == 0) {
            resultModel.setQty(0);
            mBinding.btAddToCart.setVisibility(View.VISIBLE);
            mBinding.LLPlusMinus.setVisibility(View.GONE);
            MyApplication.getInstance().cartRepository.deleteCartItem(cartModel);
        }
        addItemInCart(qty, SellerItemID);
    }

    private void addToCart() {
        mBinding.toolbarTittle.notifictionCount.setVisibility(View.VISIBLE);
        Integer cartSellerId = MyApplication.getInstance().cartRepository.getCartSellerId();
        if (cartSellerId != null && cartSellerId != 0) {
            if (cartSellerId == resultModel.getSellerId()) {
                mBinding.btAddToCart.setVisibility(View.GONE);
                mBinding.LLPlusMinus.setVisibility(View.VISIBLE);
                // add item in cart
                CartModel cartModel = new CartModel(null, 0, null,
                        resultModel.IsActive, resultModel.IsStockRequired, resultModel.getStock(),
                        resultModel.getMeasurement(), resultModel.getUom(), "", 0,
                        resultModel.getProductName(), 0, 0, resultModel.IsDelete,
                        resultModel.getOffPercentage(), 0, 0, 1, 0,
                        null, resultModel.getSellerId(), 0, 0,
                        0, resultModel.getMrp(), 0, resultModel.getId());
                MyApplication.getInstance().cartRepository.addToCart(cartModel);
                addItemInCart(1, SellerItemID);
            } else {
                checkCustomerAlertDialog(cartSellerId);
            }
        } else {
            mBinding.btAddToCart.setVisibility(View.GONE);
            mBinding.LLPlusMinus.setVisibility(View.VISIBLE);
            // clear cart
            MyApplication.getInstance().cartRepository.truncateCart();
            // add item in cart
            CartModel cartModel = new CartModel(null, 0, null,
                    resultModel.IsActive, resultModel.IsStockRequired, resultModel.getStock(),
                    resultModel.getMeasurement(), resultModel.getUom(), "", 0,
                    resultModel.getProductName(), 0, 0, resultModel.IsDelete,
                    resultModel.getOffPercentage(), 0, 0, 1, 0, null,
                    resultModel.getSellerId(), 0, 0, 0,
                    resultModel.getMrp(), 0, resultModel.getId());
            MyApplication.getInstance().cartRepository.addToCart(cartModel);
            addItemInCart(1, SellerItemID);
        }
        Utils.logAppsFlayerEventApp(this, "AddToCartProductDetails", "ProductName - " + resultModel.getProductName() + ", ProductId - " + resultModel.getId());
    }

    private void addProduct() {
        commonClassForAPI .getAddProductVMRequest(AddProductObserver,productID);
    }

    private final DisposableObserver<AddViewMainModel> AddProductObserver = new DisposableObserver<AddViewMainModel>() {
        @Override
        public void onNext(@NotNull AddViewMainModel addViewMainModel) {
            Utils.hideProgressDialog();

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

    private void SellarOtherProductsAPI() {

        commonClassForAPI.getSellarOtherVMRequest(sellarOtherObserver,productID);
    }

    private final DisposableObserver<MainTopSimilarSellerModel> sellarOtherObserver = new DisposableObserver<MainTopSimilarSellerModel>() {
        @Override
        public void onNext(@NotNull MainTopSimilarSellerModel model) {
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
                mBinding.llSellarsOtherProducs.setVisibility(View.GONE);
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

    private void cartItemsAPI() {
        commonClassForAPI.getCartItemsVMRequest(cartItemsObserver);
    }

    private final DisposableObserver<CartItemModel> cartItemsObserver = new DisposableObserver<CartItemModel>() {
        @Override
        public void onNext(@NotNull CartItemModel model) {
            Utils.hideProgressDialog();
            if (model != null && model.getCart() != null) {
                MyApplication.getInstance().cartRepository.addToCart(model.getCart());
                SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.CART_ITEM_ID, model.getId());
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

    private void getProductListAPI() {
        commonClassForAPI.getCategoriesViewModelRequest(observer,productID);
    }

    private final DisposableObserver<ProductDataModel> observer = new DisposableObserver<ProductDataModel>() {
        @Override
        public void onNext(@NotNull ProductDataModel productDataModel) {
            Utils.hideProgressDialog();
            if (productDataModel.getSuccess()) {
                if (productDataModel.getResultItem() != null) {
                    resultModel = productDataModel.getResultItem();
                    SellerItemID = productDataModel.getResultItem().getId();
                    shopName = productDataModel.getResultItem().getShopName();
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
                        mBinding.tvDeliveryOption.setText("Home Delivery");
                    }
                    if (productDataModel.getResultItem().getDiscountAmount() > 0.0) {
                        double DiscountAmount = productDataModel.getResultItem().getSellingPrice() - productDataModel.getResultItem().getDiscountAmount();
                        mBinding.llDescountAmount.setVisibility(View.VISIBLE);
                        mBinding.tvDiscount.setText("₹ " + DiscountAmount);
                        mBinding.tvItemMrp.setText("₹ " + productDataModel.getResultItem().getMrp());
                        mBinding.tvItemMrp.setPaintFlags(mBinding.tvItemMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        mBinding.tvSellingPrice.setText(String.valueOf(productDataModel.getResultItem().getSellingPrice()));
                        mBinding.tvSellingPrice.setPaintFlags(mBinding.tvSellingPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

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
                    if (productDataModel.getResultItem().getImageList() != null && productDataModel.getResultItem().getImageList().size() > 0) {
                        imageListModels = productDataModel.getResultItem().getImageList();
                    }
                    mBinding.tvItemName.setText(productDataModel.getResultItem().getProductName());
                    productName = productDataModel.getResultItem().getProductName();
                    mBinding.pager.setAdapter(new ShowImagesAdapter(ProductDetailsActivity.this, imageListModels));
                    mBinding.indicator.setViewPager(mBinding.pager);
                    checkAddButtonValidaction();

                    if (productDataModel.getResultItem().getVariationModelList() != null && productDataModel.getResultItem().getVariationModelList().size() > 0) {
                        setVariation(productDataModel.getResultItem().getVariationModelList(), 0);
                    } else {
                        mBinding.tvVarientButton.setVisibility(View.GONE);
                    }
                }
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

    @Override
    public void onOnClick(ArrayList<VariationListModel> variationList, int position) {
        if (bottomSheetDialog != null) {
            bottomSheetDialog.dismiss();
        }
        productID = variationList.get(position).getId();
        setVariation(variationList, position);
    }

    public void setVariation(ArrayList<VariationListModel> variationListMain, int position) {
        variationList = variationListMain;
        VariationListModel variationListModel = variationList.get(position);
        mBinding.tvVarientButton.setVisibility(View.VISIBLE);
        if (variationListModel.getImageList() != null && variationListModel.getImageList().size() > 0) {
            imageListModels = variationListModel.getImageList();
        }

        if (variationListModel.getDiscountAmount() > 0.0) {
            mBinding.llDescountAmount.setVisibility(View.VISIBLE);
            double DiscountAmount = variationListModel.getSellingPrice() - variationListModel.getDiscountAmount();
            mBinding.tvDiscount.setText("₹ " + DiscountAmount);
            //mBinding.tvItemMrpOff.setPaintFlags(mBinding.tvItemMrpOff.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        if (variationListModel.getOffPercentage() != 0.0) {
            mBinding.tvMagrginOff.setVisibility(View.VISIBLE);
            mBinding.tvMagrginOff.setText(variationListModel.getOffPercentage() + "%\n OFF");
        } else {
            mBinding.tvMagrginOff.setVisibility(View.GONE);
        }

        mBinding.tvItemName.setText(variationListModel.getProductName());
        if (variationListModel.getMrp() == variationListModel.getSellingPrice()) {
            mBinding.llSellingPrice.setVisibility(View.GONE);
            mBinding.tvItemMrp.setText("₹ " + variationListModel.getMrp());
        } else {
            mBinding.tvItemMrp.setText("₹ " + variationListModel.getMrp());
            mBinding.tvItemMrp.setPaintFlags(mBinding.tvItemMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            mBinding.tvSellingPrice.setText(String.valueOf(variationListModel.getSellingPrice()));
        }

        mBinding.tvQuantity.setText("Quantity " + variationListModel.getMeasurement() + variationListModel.getUomValue());
        mBinding.tvShopName.setText(shopName);
        String varientName = "";
        if (variationListModel.getProductVariantAttributeDC() != null && variationListModel.getProductVariantAttributeDC().size() > 0) {
            for (int j = 0; j < variationListModel.getProductVariantAttributeDC().size(); j++) {
                variantAttributeDCModels = variationListModel.getProductVariantAttributeDC();
                varientName = varientName + variantAttributeDCModels.get(j).getAttributeName() + " :" + variantAttributeDCModels.get(j).getAttributeValue() + " ";
            }
        }
        mBinding.tvItemColor.setText(varientName);

        if (variationList.get(0).getProductVariantSpecification() != null && variationList.get(0).getProductVariantSpecification().size() > 0) {
                               /* mBinding.tvVarient.setText(productDataModel.getResultItem().getVariationModelList().get(0).getProductVariantSpecification().get(0).getAttributeName());
                                mBinding.tvDiscripction.setText(productDataModel.getResultItem().getVariationModelList().get(0).getProductVariantSpecification().get(0).getAttributeValue());*/
        }
        mBinding.pager.setAdapter(new ShowImagesAdapter(ProductDetailsActivity.this, imageListModels));
        mBinding.indicator.setViewPager(mBinding.pager);
    }

    private void addItemInCart(int QTY, int sellerItemID) {
        ItemAddModel paginationModel = new ItemAddModel(QTY, "123", sellerItemID, 0, 0, SharePrefs.getInstance(this).getString(SharePrefs.MALL_ID));
        commonClassForAPI.getAddItemsInCardVMRequest(AddItemsInCardObserver,paginationModel);
    }

    private final DisposableObserver<AddCartItemModel> AddItemsInCardObserver = new DisposableObserver<AddCartItemModel>() {
        @Override
        public void onNext(@NotNull AddCartItemModel addCartItemModel) {
            Utils.hideProgressDialog();
            if (addCartItemModel.isSuccess()) {
                if (addCartItemModel != null && addCartItemModel.getResultItem() != null) {
                    MyApplication.getInstance().cartRepository.updateCartId(addCartItemModel.getResultItem().getId());
                }
            } else {
                Utils.setToast(getApplicationContext(), addCartItemModel.getErrorMessage());
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

    public void checkCustomerAlertDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(dbHelper.getString(R.string.alert));
        builder.setMessage(dbHelper.getString(R.string.existing_clear_cart));
        builder.setPositiveButton(dbHelper.getString(R.string.yes), (dialog, which) -> {
            clearCartItem();
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

    private void clearCartItem() {
        String cartId = MyApplication.getInstance().cartRepository.getCartId();
        MyApplication.getInstance().cartRepository.truncateCart();

        commonClassForAPI.getClearCartItemVMRequest(clearCartItemObserver, cartId);

    }

    private final DisposableObserver<Object> clearCartItemObserver = new DisposableObserver<Object>() {
        @Override
        public void onNext(@NotNull Object addCartItemModel) {
            Utils.hideProgressDialog();

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