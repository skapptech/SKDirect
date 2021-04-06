package com.skdirect.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.skdirect.R;
import com.skdirect.adapter.CartListAdapter;
import com.skdirect.databinding.ActivityCartBinding;
import com.skdirect.interfacee.CartItemInterface;
import com.skdirect.model.AddCartItemModel;
import com.skdirect.model.CartItemModel;
import com.skdirect.model.ItemAddModel;
import com.skdirect.model.RemoveItemRequestModel;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.Utils;
import com.skdirect.viewmodel.CartItemViewMode;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity implements View.OnClickListener, CartItemInterface {
    private ActivityCartBinding mBinding;
    private CartItemViewMode cartItemViewMode;
    private final ArrayList<CartItemModel.CartModel> cartItemList = new ArrayList<>();
    private CartItemModel cartItemDataModel;
    private int skipCount = 0;
    private final int takeCount = 10;
    private int pastVisiblesItems = 0;
    private int visibleItemCount = 0;
    private int totalItemCount = 0;
    private boolean loading = true;
    private CartListAdapter cartListAdapter;
    private double totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_cart);
        cartItemViewMode = ViewModelProviders.of(this).get(CartItemViewMode.class);
        initView();
        callCartList();

    }

    private void initView() {
        mBinding.toolbarTittle.ivBackPress.setOnClickListener(this);
        mBinding.toolbarTittle.tvTittle.setText("Shopping Bag");
        mBinding.tvKeepShopping.setOnClickListener(this);
        mBinding.rlCheckOut.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
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
    }

    private void callCartList() {
        if (Utils.isNetworkAvailable(getApplicationContext())) {
            Utils.showProgressDialog(CartActivity.this);
            cartItemsAPI();
        } else {
            Utils.setToast(getApplicationContext(), "No Internet Connection Please connect.");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_press:
                onBackPressed();
                break;
            case R.id.rl_check_out:
                startActivity(new Intent(this, PaymentActivity.class).putExtra("cartItemSize",cartItemDataModel).putExtra("totalAmount",totalAmount));
                break;
            case R.id.tv_keep_shopping:
                startActivity(new Intent(CartActivity.this, MainActivity.class));
                finish();
                break;
        }
    }

    private void cartItemsAPI() {
        String cartID = SharePrefs.getInstance(CartActivity.this).getString(SharePrefs.CART_ITEM_ID);
        cartItemViewMode.getCartItemModelVMRequest(cartID, mBinding.rvCartItem, mBinding.blankBasket);
        cartItemViewMode.getCartItemModelVM().observe(this, new Observer<CartItemModel>() {
            @Override
            public void onChanged(CartItemModel cartItemModel) {
                Utils.hideProgressDialog();
                if (cartItemModel != null) {
                    cartItemDataModel =cartItemModel;
                    if (cartItemModel.getCart().size() > 0) {
                        mBinding.rlCheckOut.setVisibility(View.VISIBLE);
                        mBinding.rvCartItem.post(new Runnable() {
                            public void run() {
                                for (int i = 0; i < cartItemModel.getCart().size(); i++) {
                                    totalAmount += totalAmount = cartItemModel.getCart().get(i).getQuantity() * cartItemModel.getCart().get(i).getPrice();
                                    mBinding.tvTotalAmount.setText("₹ " + totalAmount);
                                }
                                cartItemList.addAll(cartItemModel.getCart());
                                cartListAdapter.notifyDataSetChanged();

                                loading = true;
                            }
                        });

                    } else {
                        loading = false;
                        mBinding.rvCartItem.setVisibility(View.GONE);
                        mBinding.blankBasket.setVisibility(View.VISIBLE);
                        mBinding.rlCheckOut.setVisibility(View.GONE);

                    }
                }
            }
        });

    }

    @Override
    public void plusButtonOnClick(CartItemModel.CartModel cartModel, TextView tvSelectedQty) {
        int increaseCount = Integer.parseInt(tvSelectedQty.getText().toString().trim());
        increaseCount++;
        tvSelectedQty.setText("" + increaseCount);
        addItemInCart(increaseCount, cartModel);
        totalAmount = totalAmount + cartModel.getPrice();
        mBinding.tvTotalAmount.setText("₹ " + totalAmount);
    }

    @Override
    public void minusButtonOnClick(CartItemModel.CartModel cartModel, TextView selectedQty, LinearLayout LLPlusMinus) {
        int decreaseCount = Integer.parseInt(selectedQty.getText().toString().trim());
        decreaseCount--;
        if (decreaseCount >= 1) {
            selectedQty.setText("" + decreaseCount);
            addItemInCart(decreaseCount, cartModel);
            totalAmount = totalAmount - cartModel.getPrice();
            mBinding.tvTotalAmount.setText("₹ " + totalAmount);
        } else {
            cartItemList.remove(cartModel);
            cartListAdapter.notifyDataSetChanged();
            MainActivity.cartItemModel = null;
            addItemInCart(decreaseCount, cartModel);
            totalAmount = totalAmount - cartModel.getPrice();
            mBinding.tvTotalAmount.setText("₹ " + totalAmount);

            if (totalAmount == 0) {
                mBinding.rlCheckOut.setVisibility(View.GONE);
                mBinding.blankBasket.setVisibility(View.VISIBLE);

            }
        }
    }

    @Override
    public void removeButtonOnClick(CartItemModel.CartModel cartModel, int position) {
        removeItemAlertDialog(cartModel, position);

    }

    private void addItemInCart(int QTY, CartItemModel.CartModel sellerProductModel) {
        ItemAddModel paginationModel = new ItemAddModel(QTY, "123", sellerProductModel.getId(), 0, 0);
        cartItemViewMode.getAddItemsInCardVMRequest(paginationModel);
        cartItemViewMode.getAddItemsInCardVM().observe(this, new Observer<AddCartItemModel>() {
            @Override
            public void onChanged(AddCartItemModel sellerProdList) {
                Utils.hideProgressDialog();
                if (sellerProdList != null) {

                }
            }
        });
    }

    public void removeItemAlertDialog(CartItemModel.CartModel cartModel, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Are you sure that you want to delete this items?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeItemFromCart(cartModel, position);
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

    private void removeItemFromCart(CartItemModel.CartModel cartModel, int position) {
        RemoveItemRequestModel itemRequestModel = new RemoveItemRequestModel("123", cartModel.getId());
        cartItemViewMode.getRemoveItemFromCartVMRequest(itemRequestModel);
        cartItemViewMode.getRemoveItemFromCartVM().observe(this, new Observer<JsonObject>() {
            @Override
            public void onChanged(JsonObject jsonObject) {
                Utils.hideProgressDialog();
                try {
                    if (jsonObject != null) {
                        cartItemList.remove(position);
                        cartListAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    cartItemList.remove(position);
                    cartListAdapter.notifyDataSetChanged();
                    MainActivity.cartItemModel = null;
                }

            }
        });


    }
}
