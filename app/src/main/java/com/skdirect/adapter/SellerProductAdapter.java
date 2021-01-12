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
import com.skdirect.databinding.ItemSellerProductListBinding;
import com.skdirect.interfacee.AddItemInterface;
import com.skdirect.model.SellerProductList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SellerProductAdapter extends RecyclerView.Adapter<SellerProductAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<SellerProductList> sellerProductModels;
    private final AddItemInterface addItemInterface;

    public SellerProductAdapter(Context context, ArrayList<SellerProductList> SallerShopList, AddItemInterface addItemInter) {
        this.context = context;
        this.sellerProductModels = SallerShopList;
        addItemInterface = addItemInter;
    }

    @NonNull
    @Override
    public SellerProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_seller_product_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SellerProductList sellerProductModel = sellerProductModels.get(position);
        holder.mBinding.tvSallerName.setText(sellerProductModel.getProductName());
        holder.mBinding.tvMrp.setText("₹ " + sellerProductModel.getMrp());
        holder.mBinding.tvTax.setText("Inclusive of all taxes");


        if (sellerProductModel.getQty()>0){
            holder.mBinding.LLPlusMinus.setVisibility(View.VISIBLE);
            holder.mBinding.tvAdd.setVisibility(View.GONE);
            holder.mBinding.tvSelectedQty.setText(String.valueOf(sellerProductModel.getQty()));
        }else {
            holder.mBinding.LLPlusMinus.setVisibility(View.GONE);
            holder.mBinding.tvAdd.setVisibility(View.VISIBLE);

        }

        if (sellerProductModel.getImagePath() != null && !sellerProductModel.getImagePath().contains("http")) {
            Picasso.get().load(BuildConfig.apiEndpoint + sellerProductModel.getImagePath()).into(holder.mBinding.imItemImage);
        } else {
            Picasso.get().load(sellerProductModel.getImagePath()).into(holder.mBinding.imItemImage);
        }

        holder.mBinding.tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.mBinding.tvAdd.setVisibility(View.GONE);
                holder.mBinding.LLPlusMinus.setVisibility(View.VISIBLE);
            }
        });
        holder.mBinding.tvQtyPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemInterface.plusButtonOnClick(sellerProductModel,holder.mBinding.tvSelectedQty);
            }
        });

        holder.mBinding.tvQtyMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemInterface.minusButtonOnClick(sellerProductModel,holder.mBinding.tvSelectedQty,holder.mBinding.tvAdd,holder.mBinding.LLPlusMinus);
            }
        });

        holder.mBinding.tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemInterface.addButtonOnClick(sellerProductModel,holder.mBinding.tvSelectedQty,holder.mBinding.tvAdd,holder.mBinding.LLPlusMinus);
            }
        });


    }

    @Override
    public int getItemCount() {
        return sellerProductModels == null ? 0 : sellerProductModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemSellerProductListBinding mBinding;

        public ViewHolder(ItemSellerProductListBinding Binding) {
            super(Binding.getRoot());
            this.mBinding = Binding;
        }
    }
}