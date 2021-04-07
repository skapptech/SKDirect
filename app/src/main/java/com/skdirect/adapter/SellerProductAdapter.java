package com.skdirect.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.skdirect.BuildConfig;
import com.skdirect.R;
import com.skdirect.activity.ProductDetailsActivity;
import com.skdirect.databinding.ItemSellerProductListBinding;
import com.skdirect.interfacee.AddItemInterface;
import com.skdirect.model.SellerProductList;
import com.skdirect.utils.MyApplication;
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
        SellerProductList model = sellerProductModels.get(position);
        holder.mBinding.tvSallerName.setText(model.getProductName());
        holder.mBinding.tvMrp.setText("₹ " + model.getMrp());
        holder.mBinding.tvMrp.setPaintFlags(holder.mBinding.tvMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.mBinding.tvSellingPrice.setText("₹ " + model.getSellingPrice());
        holder.mBinding.tvQuantity.setText("Quantity " + model.getMeasurement() + model.getUom());

        if (model.getMrp() ==model.getSellingPrice()){
            holder.mBinding.llSellingPrice.setVisibility(View.GONE);
            holder.mBinding.tvMrp.setText("₹ " + model.getMrp());
        }else {
            holder.mBinding.tvMrp.setText("₹ " + model.getMrp());
            holder.mBinding.tvMrp.setPaintFlags(holder.mBinding.tvMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mBinding.tvSellingPrice.setText("₹ " + model.getSellingPrice());
        }

        if (model.getOffPercentage()!=0.0) {
            holder.mBinding.tvMagrginOff.setVisibility(View.VISIBLE);
            holder.mBinding.tvMagrginOff.setText("" + model.getOffPercentage() + "%\n OFF");
        } else {
            holder.mBinding.tvMagrginOff.setVisibility(View.GONE);
        }

        if (model.getNoofView() > 0) {
            holder.mBinding.llNoOfView.setVisibility(View.VISIBLE);
            holder.mBinding.tvItemView.setText(String.valueOf(model.getNoofView()));
        } else {
            holder.mBinding.llNoOfView.setVisibility(View.INVISIBLE);
        }

        if (MyApplication.getInstance().cartRepository.isItemInCart(model.getId())) {
            holder.mBinding.LLPlusMinus.setVisibility(View.VISIBLE);
            holder.mBinding.tvAdd.setVisibility(View.GONE);
            holder.mBinding.tvSelectedQty.setText("" + MyApplication.getInstance().cartRepository.getItemQty(model.getId()));
        } else {
            holder.mBinding.LLPlusMinus.setVisibility(View.GONE);
            holder.mBinding.tvAdd.setVisibility(View.VISIBLE);
            holder.mBinding.tvSelectedQty.setText("0");
        }

        if (model.getImagePath() != null && !model.getImagePath().contains("http")) {
            Picasso.get().load(BuildConfig.apiEndpoint + model.getImagePath()).into(holder.mBinding.imItemImage);
        } else {
            Picasso.get().load(model.getImagePath()).into(holder.mBinding.imItemImage);
        }


        holder.mBinding.tvQtyPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemInterface.plusButtonOnClick(model, holder.mBinding.tvSelectedQty);
            }
        });

        holder.mBinding.tvQtyMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemInterface.minusButtonOnClick(model, holder.mBinding.tvSelectedQty, holder.mBinding.tvAdd, holder.mBinding.LLPlusMinus);
            }
        });

        holder.mBinding.tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemInterface.addButtonOnClick(model, holder.mBinding.tvSelectedQty, holder.mBinding.tvAdd, holder.mBinding.LLPlusMinus);
            }
        });

        holder.mBinding.LLMainCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ProductDetailsActivity.class).putExtra("ID", model.getSellerProductId()));
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