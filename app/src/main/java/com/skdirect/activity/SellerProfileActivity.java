package com.skdirect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skdirect.BuildConfig;
import com.skdirect.R;
import com.skdirect.adapter.SellerProductAdapter;
import com.skdirect.api.CommonClassForAPI;
import com.skdirect.databinding.ActivitySellerProfileBinding;
import com.skdirect.interfacee.AddItemInterface;
import com.skdirect.model.AddCartItemModel;
import com.skdirect.model.AddViewMainModel;
import com.skdirect.model.AddViewModel;
import com.skdirect.model.CartModel;
import com.skdirect.model.ItemAddModel;
import com.skdirect.model.SellerDeliveryModel;
import com.skdirect.model.SellerDetailsModel;
import com.skdirect.model.SellerProductList;
import com.skdirect.model.SellerProductMainModel;
import com.skdirect.model.SellerProfileDataModel;
import com.skdirect.model.UserDetailModel;
import com.skdirect.utils.DBHelper;
import com.skdirect.utils.MyApplication;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.Utils;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import io.reactivex.observers.DisposableObserver;

public class SellerProfileActivity extends AppCompatActivity implements View.OnClickListener, AddItemInterface {
    private ActivitySellerProfileBinding mBinding;
    private int sellerID;
    private int skipCount = 0, takeCount = 15;
    private int pastVisiblesItems = 0;
    private int visibleItemCount = 0;
    private int totalItemCount = 0;
    private boolean loading = true;
    private final ArrayList<SellerProductList> sellerProductModels = new ArrayList<>();
    private SellerProductAdapter sellerShopListAdapter;
    private String searchSellerName;
    private DBHelper dbHelper;
    private String sellerImagePath, sellerShopName;
    private CommonClassForAPI commonClassForAPI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_seller_profile);
        dbHelper = MyApplication.getInstance().dbHelper;
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
                mBinding.cartBadge.setText("" + integer);
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
            case R.id.RLShare:
                Utils.showShareWhatsappDialog(this,
                        dbHelper.getString(R.string.hello_check_seller)
                                + " " +
                                sellerShopName
                                + "'" +
                                dbHelper.getString(R.string.seller_catelogue) +
                                "\n" + SharePrefs.getInstance(this).getString(SharePrefs.BUYER_URL) + "/seller/" + sellerID, "");
                Utils.logAppsFlayerEventApp(this, "CatalogueShare", "SellerName - " + sellerShopName + ", SellerId - " + sellerID);
                break;
            case R.id.iv_s_shop_image:
                startActivity(new Intent(getApplicationContext(), SellerImageGalleryActivity.class).putExtra("ImageData", sellerImagePath).putExtra("ShopName", sellerShopName));
                break;
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() != null&&intent.hasExtra("ID")) {
            sellerID = intent.getIntExtra("ID", 0);
            sellerProductModels.clear();
            callSellerDetails();
        }else  if (intent.getData() != null) {
            String sharedUrl = intent.getData().toString();
            sharedUrl = sharedUrl.substring(sharedUrl.lastIndexOf("/") + 1);
            sellerID = Integer.parseInt(sharedUrl);
            sellerProductModels.clear();
            callSellerDetails();
        }
    }

    @Override
    public void plusButtonOnClick(SellerProductList model, TextView tvSelectedQty) {
        mBinding.cartBadge.setVisibility(View.VISIBLE);

        if (model.getMaxOrderQuantity() != null && Integer.parseInt(model.getMaxOrderQuantity()) > 0 && model.getQty() >= Integer.parseInt(model.getMaxOrderQuantity())) {
            Utils.setToast(getApplicationContext(), getString(R.string.order_quantity));
        } else {
            model.setQty(model.getQty() + 1);
            tvSelectedQty.setText("" + model.getQty());
            addItemInCart(model.getQty(), model);
        }

        // add item  to cart
        CartModel cartModel = new CartModel(null, 0, null,
                false, model.isStockRequired(), model.getStock(), model.getMeasurement(),
                model.getUom(), model.getImagePath(), 0, model.getProductName(),
                0, 0, false, 0, 0, 0,
                model.getQty(), model.getCreatedBy(), null, sellerID, 0,
                0, model.getMargin(), model.getMrp(), model.getMOQ(), model.getId());
        MyApplication.getInstance().cartRepository.updateCartItem(cartModel);
    }

    @Override
    public void minusButtonOnClick(SellerProductList model, TextView selectedQty, TextView btAddToCart, LinearLayout LLPlusMinus) {
        int qty = model.getQty();
        qty--;
        if (qty > 0) {
            selectedQty.setText("" + qty);
        } else {
            btAddToCart.setVisibility(View.VISIBLE);
            LLPlusMinus.setVisibility(View.GONE);
        }
        model.setQty(model.getQty() - 1);
        // add item  to cart
        CartModel cartModel = new CartModel(null, 0, null,
                false, model.isStockRequired(), model.getStock(),
                model.getMeasurement(), model.getUom(), model.getImagePath(),
                0, model.getProductName(), 0, 0,
                false, 0, 0, 0, model.getQty(),
                model.getCreatedBy(), null, sellerID, 0, 0,
                model.getMargin(), model.getMrp(), model.getMOQ(), model.getId());
        if (qty > 0) {
            MyApplication.getInstance().cartRepository.updateCartItem(cartModel);
        } else {
            MyApplication.getInstance().cartRepository.deleteCartItem(cartModel);
        }
        addItemInCart(qty, model);
    }

    @Override
    public void addButtonOnClick(SellerProductList sellerProductModel, TextView tvSelectedQty, TextView btAddToCart, LinearLayout LLPlusMinus) {
        mBinding.notifictionCount.setVisibility(View.VISIBLE);
        Integer cartSellerId = MyApplication.getInstance().cartRepository.getCartSellerId();
        if (cartSellerId != null && cartSellerId != 0) {
            if (cartSellerId == sellerID) {
                btAddToCart.setVisibility(View.GONE);
                LLPlusMinus.setVisibility(View.VISIBLE);
                CartModel cartModel = new CartModel(null, 0, null,
                        false, sellerProductModel.isStockRequired(), sellerProductModel.getStock(),
                        sellerProductModel.getMeasurement(), sellerProductModel.getUom(), sellerProductModel.getImagePath(),
                        0, sellerProductModel.getProductName(), 0, 0, false,
                        0, 0, 0, sellerProductModel.getQty(), sellerProductModel.getCreatedBy(),
                        null, sellerID, 0, 0, sellerProductModel.getMargin(),
                        sellerProductModel.getMrp(), sellerProductModel.getMOQ(), sellerProductModel.getId());
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
            CartModel cartModel = new CartModel(null, 0, null,
                    false, sellerProductModel.isStockRequired(), sellerProductModel.getStock(),
                    sellerProductModel.getMeasurement(), sellerProductModel.getUom(), sellerProductModel.getImagePath(),
                    0, sellerProductModel.getProductName(), 0, 0, false,
                    0, 0, 0, 1, sellerProductModel.getCreatedBy(), null,
                    sellerID, 0, 0, sellerProductModel.getMargin(),
                    sellerProductModel.getMrp(), sellerProductModel.getMOQ(), sellerProductModel.getId());
            MyApplication.getInstance().cartRepository.addToCart(cartModel);
            addItemInCart(1, sellerProductModel);
        }
        Utils.logAppsFlayerEventApp(this, "AddToCartSellerCatelogue",
                "ProductName - " + sellerProductModel.getProductName() + ", ProductId - " + sellerProductModel.getId());
    }


    private void initView() {
        commonClassForAPI = CommonClassForAPI.getInstance(this);
        mBinding.etSearchSeller.setHint(dbHelper.getString(R.string.search_seller_by_name_or_product));
        mBinding.btShare.setText(dbHelper.getString(R.string.share));
        mBinding.tvDeliveryTitle.setText(dbHelper.getString(R.string.delivery_options));
        mBinding.tvMinOrderTitle.setText(dbHelper.getString(R.string.minimum_order_amount));
        mBinding.tvDeliverDistanceTitle.setText(dbHelper.getString(R.string.delivered_till_distance));
        mBinding.tvItemNotFound.setText(dbHelper.getString(R.string.searched_item_is_not_available));

        mBinding.notifictionCount.setVisibility(View.VISIBLE);
        mBinding.tvTittle.setText(dbHelper.getString(R.string.seller_items));
        mBinding.ivBackPress.setOnClickListener(this);
        mBinding.ivSShopImage.setOnClickListener(this);
        mBinding.notifictionCount.setOnClickListener(this);
        mBinding.RLShare.setOnClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mBinding.rvCategories.setLayoutManager(layoutManager);

        sellerShopListAdapter = new SellerProductAdapter(this, sellerProductModels, this);
        mBinding.rvCategories.setAdapter(sellerShopListAdapter);

        mBinding.etSearchSeller.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                skipCount = 0;
                takeCount = 15;
                searchSellerName = mBinding.etSearchSeller.getText().toString().trim();
                sellerProductModels.clear();
                sellerShopListAdapter.notifyDataSetChanged();
                getSellerProductsApi(searchSellerName);
                return true;
            }
            return false;
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
                            skipCount = skipCount + 15;
                            // mBinding.progressBar.setVisibility(View.VISIBLE);
                            getSellerProductsApi(searchSellerName);
                        }
                    }
                }
            }
        });
        sellerProductModels.clear();
    }

    private void callSellerDetails() {
        if (Utils.isNetworkAvailable(getApplicationContext())) {
            Utils.showProgressDialog(this);
            getSellerDetailsAPI();
            addProduct();
            getSellerProductsApi(searchSellerName);
        } else {
            Utils.setToast(getApplicationContext(), dbHelper.getString(R.string.no_internet_connection));
        }
    }

    private void getIntentData() {
        if (getIntent().getData() != null) {
            String sharedUrl = getIntent().getData().toString();
            sharedUrl = sharedUrl.substring(sharedUrl.lastIndexOf("/") + 1);
            sellerID = Integer.parseInt(sharedUrl);
        } else {
            sellerID = getIntent().getIntExtra("ID", 0);
        }
    }

    private void getSellerDetailsAPI() {
        commonClassForAPI.getSellerDetails(observer, sellerID);
    }


    private void getSellerProductsApi(String searchSellerName) {
        SellerProfileDataModel paginationModel = new SellerProfileDataModel(sellerID, 0, 0,
                "", skipCount, takeCount, 0, searchSellerName,
                Double.parseDouble(SharePrefs.getStringSharedPreferences(this, SharePrefs.LAT)),
                Double.parseDouble(SharePrefs.getStringSharedPreferences(this, SharePrefs.LON)));

        commonClassForAPI.getSellerProductRequest(SellerProductObserver, paginationModel);

    }


    private void updateUserDetails(UserDetailModel userDetailModel, ArrayList<SellerDeliveryModel> sellerDeliveryModel) {
        String deliveryOption = "";
        if (userDetailModel != null) {
            if (userDetailModel.getStoreView() != 0) {
                mBinding.tvStoreView.setVisibility(View.VISIBLE);
                mBinding.tvStoreView.setText(String.valueOf(userDetailModel.getStoreView()));
            } else {
                mBinding.tvStoreView.setVisibility(View.GONE);
            }
            mBinding.tvAddreshOne.setText(userDetailModel.getAddressOne() + " " + userDetailModel.getAddressTwo() + " " + userDetailModel.getCity() + " - " + userDetailModel.getPincode() + " (" + userDetailModel.getState() + ")");
            //mBinding.tvDeliveryOption.setText(userDetailModel.getAddressOne() + " " + userDetailModel.getAddressTwo());
            mBinding.tvSellerDistance.setText("" + String.format("%.2f", userDetailModel.getDistance()) + " KM");

        } else {
            mBinding.tvSellerDistance.setVisibility(View.GONE);
        }

        if (sellerDeliveryModel.size() > 0) {
            for (int i = 0; i < sellerDeliveryModel.size(); i++) {
                deliveryOption += sellerDeliveryModel.get(i).getDelivery() + ", ";
            }
            /*remove condiction*/
            mBinding.tvDeliveryOption.setText("Home Delivery");
        } else {
            mBinding.llDeliverOption.setVisibility(View.VISIBLE);
        }
    }

    private void addProduct() {
        commonClassForAPI.getAddProductVMRequest(addProductObserver, new AddViewModel(sellerID));
    }

    public void checkCustomerAlertDialog(int id, SellerProductList sellerProductModel, TextView btAddToCart, LinearLayout LLPlusMinus) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Your Cart has existing items from Another Seller. Do You Want to clear it and add items from this Seller?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            clearCartItem();
            MyApplication.getInstance().cartRepository.truncateCart();
            // cartModel.setSellerId(sellerID);
            sellerProductModel.setQty(1);
            CartModel cartItemModel = new CartModel(null, 0, null, false,
                    sellerProductModel.isStockRequired(), sellerProductModel.getStock(), sellerProductModel.getMeasurement(),
                    sellerProductModel.getUom(), sellerProductModel.getImagePath(), 0, sellerProductModel.getProductName(),
                    0, 0, false, 0, 0, 0, sellerProductModel.getQty(),
                    sellerProductModel.getCreatedBy(), null, sellerID, 0, 0,
                    sellerProductModel.getMargin(), sellerProductModel.getMrp(), sellerProductModel.getMOQ(), sellerProductModel.getId());
            MyApplication.getInstance().cartRepository.addToCart(cartItemModel);
            sellerShopListAdapter.notifyDataSetChanged();
            btAddToCart.setVisibility(View.GONE);
            LLPlusMinus.setVisibility(View.VISIBLE);
            addItemInCart(1, sellerProductModel);
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            sellerProductModel.setQty(0);
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void clearCartItem() {
        String cartId = MyApplication.getInstance().cartRepository.getCartId();
        commonClassForAPI.getClearCartItemVMRequest(clearCartItemObserver, cartId);

    }

    private void addItemInCart(int QTY, SellerProductList sellerProductModel) {
        ItemAddModel paginationModel = new ItemAddModel(QTY, "123", sellerProductModel.getId(),
                0, 0, SharePrefs.getInstance(this).getString(SharePrefs.MALL_ID));

        commonClassForAPI.getAddItemsInCardVMRequest(AddItemsInCardObserver, paginationModel);

    }


    private final DisposableObserver<SellerDetailsModel> observer = new DisposableObserver<SellerDetailsModel>() {
        @Override
        public void onNext(@NotNull SellerDetailsModel sellerDetailsModel) {
            Utils.hideProgressDialog();
            if (sellerDetailsModel.isSuccess()) {
                if (sellerDetailsModel.getSellerInfoModel().getRating() != 0.0) {
                    Double deg = sellerDetailsModel.getSellerInfoModel().getRating();
                    float rating = deg.floatValue();
                    mBinding.ratingbarSeller.setRating(rating);
                } else {
                    mBinding.ratingbarSeller.setVisibility(View.GONE);
                }

                if (sellerDetailsModel.getSellerInfoModel().getImagePath()!=null){
                    sellerImagePath = sellerDetailsModel.getSellerInfoModel().getImagePath();
                }

                mBinding.tvSellerName.setText(sellerDetailsModel.getSellerInfoModel().getShopName());
                sellerShopName = sellerDetailsModel.getSellerInfoModel().getShopName();
                if (sellerDetailsModel.getSellerInfoModel().getMinOrderValue() != 0.0 && sellerDetailsModel.getSellerInfoModel().getRadialDistance() != 0.0) {
                    mBinding.tvMinimumOrderAmt.setText("₹ " + Math.round(sellerDetailsModel.getSellerInfoModel().getMinOrderValue()));
                    mBinding.tvDiliverDistance.setText("" + Math.round(sellerDetailsModel.getSellerInfoModel().getRadialDistance()) + " KM");
                } else {
                    mBinding.llMiniOrder.setVisibility(View.GONE);
                    mBinding.llDelivert.setVisibility(View.GONE);
                }

                if (sellerDetailsModel.getSellerInfoModel().getImagePath() != null && !sellerDetailsModel.getSellerInfoModel().getImagePath().contains("http")) {
                    Picasso.get().load(BuildConfig.apiEndpoint + sellerDetailsModel.getSellerInfoModel().getImagePath())
                            .error(R.drawable.ic_top_seller).into(mBinding.ivSShopImage);
                } else {
                    Picasso.get().load(sellerDetailsModel.getSellerInfoModel().getImagePath()).placeholder(R.drawable.ic_top_seller)
                            .into(mBinding.ivSShopImage);
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

    private final DisposableObserver<SellerProductMainModel> SellerProductObserver = new DisposableObserver<SellerProductMainModel>() {
        @Override
        public void onNext(@NotNull SellerProductMainModel sellerProdList) {
            Utils.hideProgressDialog();
            if (sellerProdList.isSuccess()) {
                updateUserDetails(sellerProdList.getSellerProductModel().getUserDetailModel(), sellerProdList.getSellerProductModel().getSellerDeliveryModel());
                if (sellerProdList.getSellerProductModel().getSellerProductLists().size() > 0) {
                    mBinding.tvItemNotFound.setVisibility(View.GONE);
                    mBinding.rvCategories.setVisibility(View.VISIBLE);
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

    private final DisposableObserver<AddCartItemModel> AddItemsInCardObserver = new DisposableObserver<AddCartItemModel>() {
        @Override
        public void onNext(@NotNull AddCartItemModel addCartItemModel) {
            Utils.hideProgressDialog();
            if (addCartItemModel.isSuccess()) {
                if (addCartItemModel != null && addCartItemModel.getResultItem() != null) {
                    MyApplication.getInstance().cartRepository.updateCartId(addCartItemModel.getResultItem().getId());
                }
            } else {
                Utils.setToast(SellerProfileActivity.this, addCartItemModel.getErrorMessage());
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

    private final DisposableObserver<AddViewMainModel> addProductObserver = new DisposableObserver<AddViewMainModel>() {
        @Override
        public void onNext(@NotNull AddViewMainModel addViewMainModel) {
            Utils.hideProgressDialog();
            if (addViewMainModel.isSuccess()) {

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
}