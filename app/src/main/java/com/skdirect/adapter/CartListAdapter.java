package com.skdirect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.skdirect.BuildConfig;
import com.skdirect.R;
import com.skdirect.databinding.ItemCartListBinding;
import com.skdirect.databinding.ItemSellerProductListBinding;
import com.skdirect.interfacee.AddItemInterface;
import com.skdirect.interfacee.CartItemInterface;
import com.skdirect.model.CartItemModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.ViewHolder> {
    private Context context;
    ArrayList<CartItemModel.CartModel> cartItemList;
    private CartItemInterface cartItemInterface;

    public CartListAdapter(Context context, ArrayList<CartItemModel.CartModel> cartItemList,CartItemInterface cartItemInter) {
        this.context = context;
        this.cartItemList = cartItemList;
        cartItemInterface = cartItemInter;
    }

    @NonNull
    @Override
    public CartListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartListAdapter.ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_cart_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartListAdapter.ViewHolder holder, int position) {
        CartItemModel.CartModel cartModel = cartItemList.get(position);
        holder.mBinding.tvProductName.setText(cartModel.getProductName());
        holder.mBinding.tvSellerName.setText("Seller: "+cartModel.getShopName());
        holder.mBinding.tvMrp.setText("₹ "+String.valueOf(cartModel.getPrice()));
        holder.mBinding.tvSelectedQty.setText(String.valueOf(cartModel.getQuantity()));

        if (cartModel.getImagePath() != null && !cartModel.getImagePath().contains("http")) {
            Picasso.get().load(BuildConfig.apiEndpoint + cartModel.getImagePath()).into(holder.mBinding.imItemImage);
        } else {
            Picasso.get().load(cartModel.getImagePath()).into(holder.mBinding.imItemImage);
        }

        holder.mBinding.tvQtyPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartItemInterface.plusButtonOnClick(cartModel,holder.mBinding.tvSelectedQty);
            }
        });

        holder.mBinding.tvQtyMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartItemInterface.minusButtonOnClick(cartModel,holder.mBinding.tvSelectedQty,holder.mBinding.LLPlusMinus);
            }
        });
        holder.mBinding.tvRomove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartItemInterface.removeButtonOnClick(cartModel,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartItemList == null ? 0 : cartItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemCartListBinding mBinding;


        public ViewHolder(ItemCartListBinding Binding) {
            super(Binding.getRoot());
            this.mBinding = Binding;
        }
    }
}
