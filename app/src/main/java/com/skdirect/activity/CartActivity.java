package com.skdirect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skdirect.R;
import com.skdirect.adapter.CartListAdapter;
import com.skdirect.databinding.ActivityCartBinding;
import com.skdirect.interfacee.CartItemInterface;
import com.skdirect.model.CartItemModel;
import com.skdirect.model.CartModel;
import com.skdirect.model.ItemAddModel;
import com.skdirect.model.RemoveItemRequestModel;
import com.skdirect.utils.MyApplication;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.CartItemViewMode;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity implements View.OnClickListener, CartItemInterface {
    private ActivityCartBinding mBinding;
    private CartItemViewMode cartItemViewMode;
    private final ArrayList<CartModel> cartItemList = new ArrayList<>();
    private CartListAdapter cartListAdapter;
    private CartItemModel cartItemDataModel;
    private int skipCount = 0;
    private int pastVisiblesItems = 0;
    private int visibleItemCount = 0;
    private int totalItemCount = 0;
    private boolean loading = true;
    private double totalAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_cart);
        cartItemViewMode = ViewModelProviders.of(this).get(CartItemViewMode.class);
        initView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_press:
                onBackPressed();
                break;
            case R.id.tv_add:
                startActivity(new Intent(getApplicationContext(), PaymentActivity.class).putExtra("cartItemSize", cartItemDataModel).putExtra("totalAmount", totalAmount));
                break;
            case R.id.tv_keep_shopping:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                break;
        }
    }

    @Override
    public void plusButtonOnClick(CartModel cartModel) {
        int qty = cartModel.getQuantity();
        totalAmount = totalAmount + cartModel.getPrice();
        mBinding.tvTotalAmount.setText("₹ " + totalAmount);
        MyApplication.getInstance().cartRepository.updateCartItem(cartModel);
        addItemInCart(qty, cartModel);
    }

    @Override
    public void minusButtonOnClick(CartModel cartModel, LinearLayout LLPlusMinus) {
        int qty = cartModel.getQuantity();
        if (qty >= 1) {
            totalAmount = totalAmount - cartModel.getPrice();
            mBinding.tvTotalAmount.setText("₹ " + totalAmount);
            MyApplication.getInstance().cartRepository.updateCartItem(cartModel);
            addItemInCart(qty, cartModel);
        } else {
            cartItemList.remove(cartModel);
            totalAmount = totalAmount - cartModel.getPrice();
            mBinding.tvTotalAmount.setText("₹ " + totalAmount);
            cartListAdapter.notifyDataSetChanged();
            addItemInCart(qty, cartModel);
            MyApplication.getInstance().cartRepository.updateCartItem(cartModel);
        }
    }

    @Override
    public void removeButtonOnClick(CartModel cartModel, int position) {
        removeItemAlertDialog(cartModel, position);
    }


    private void initView() {
        mBinding.toolbarTittle.ivBackPress.setOnClickListener(this);
        mBinding.toolbarTittle.tvTittle.setText("Shopping Bag");
        mBinding.tvKeepShopping.setOnClickListener(this);
        mBinding.tvAdd.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mBinding.rvCartItem.setLayoutManager(layoutManager);

        cartListAdapter = new CartListAdapter(getApplicationContext(), cartItemList, this);
        mBinding.rvCartItem.setAdapter(cartListAdapter);

        mBinding.rvCartItem.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            skipCount = skipCount + 10;
                            // mBinding.progressBar.setVisibility(View.VISIBLE);
                            //callCartList();
                        }
                    }
                }
            }
        });
        cartItemList.clear();

        callCartList();
    }

    private void callCartList() {
        if (Utils.isNetworkAvailable(getApplicationContext())) {
            Utils.showProgressDialog(this);
            cartItemsAPI();
        } else {
            Utils.setToast(getApplicationContext(), "No Internet Connection Please connect.");
        }
    }

    private void cartItemsAPI() {
        Integer cartID = MyApplication.getInstance().cartRepository.getCartSellerId();
        cartItemViewMode.getCartItemModelVMRequest(cartID, mBinding.rvCartItem, mBinding.blankBasket);
        cartItemViewMode.getCartItemModelVM().observe(this, cartItemModel -> {
            Utils.hideProgressDialog();
            if (cartItemModel != null) {
                cartItemDataModel = cartItemModel;
                if (cartItemModel.getCart().size() > 0) {
                    mBinding.rlCheckOut.setVisibility(View.VISIBLE);
                    mBinding.rvCartItem.post(() -> {
                        for (int i = 0; i < cartItemModel.getCart().size(); i++) {
                            totalAmount += totalAmount = cartItemModel.getCart().get(i).getQuantity() * cartItemModel.getCart().get(i).getPrice();
                            mBinding.tvTotalAmount.setText("₹ " + totalAmount);
                        }
                        cartItemList.addAll(cartItemModel.getCart());
                        cartListAdapter.notifyDataSetChanged();

                        loading = true;
                    });
                } else {
                    loading = false;
                    mBinding.rvCartItem.setVisibility(View.GONE);
                    mBinding.blankBasket.setVisibility(View.VISIBLE);
                    mBinding.rlCheckOut.setVisibility(View.GONE);
                    MyApplication.getInstance().cartRepository.truncateCart();
                }
            }
        });
    }

    private void addItemInCart(int QTY, CartModel sellerProductModel) {
        MyApplication.getInstance().cartRepository.updateCartItem(sellerProductModel);
        ItemAddModel paginationModel = new ItemAddModel(QTY, "123", sellerProductModel.getId(), 0, 0);
        cartItemViewMode.getAddItemsInCardVMRequest(paginationModel);
        cartItemViewMode.getAddItemsInCardVM().observe(this, sellerProdList -> {
            Utils.hideProgressDialog();
            if (sellerProdList != null) {

            }
        });
    }

    public void removeItemAlertDialog(CartModel cartModel, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Are you sure that you want to delete this items?");
        builder.setPositiveButton("Yes", (dialog, which) -> removeItemFromCart(cartModel, position));
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void removeItemFromCart(CartModel cartModel, int position) {
        RemoveItemRequestModel itemRequestModel = new RemoveItemRequestModel("123", cartModel.getId());
        cartItemViewMode.getRemoveItemFromCartVMRequest(itemRequestModel);
        cartItemViewMode.getRemoveItemFromCartVM().observe(this, jsonElement -> {
            Utils.hideProgressDialog();
            try {
                if (jsonElement != null || cartItemList.size() == 1) {
                    MyApplication.getInstance().cartRepository.deleteCartItem(cartModel);
                    cartItemList.remove(position);
                    cartListAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
                cartItemList.remove(position);
                cartListAdapter.notifyDataSetChanged();
            }
            if (cartItemList.size() == 0) {
                mBinding.rlCheckOut.setVisibility(View.GONE);
                mBinding.blankBasket.setVisibility(View.VISIBLE);
            }
        });
    }
}