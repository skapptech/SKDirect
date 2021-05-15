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
import com.skdirect.activity.SellerProfileActivity;
import com.skdirect.databinding.ItemNearSallerListBinding;
import com.skdirect.databinding.ItemShowCartInHomeBinding;
import com.skdirect.model.CartModel;
import com.skdirect.model.NearBySallerModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ShowCartInHomeAdapter extends RecyclerView.Adapter<ShowCartInHomeAdapter.ViewHolder> {

    private Context context;
    private List<CartModel> homeCartItemList;

    public ShowCartInHomeAdapter(Context context, List<CartModel> cartModelList) {
        this.context = context;
        this.homeCartItemList = cartModelList;
    }

    @NonNull
    @Override
    public ShowCartInHomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_show_cart_in_home, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ShowCartInHomeAdapter.ViewHolder holder, int position) {
        CartModel  homeCartModel = homeCartItemList.get(position);
        holder.mBinding.tvSellerName.setText(homeCartModel.getProductName());
        holder.mBinding.tvMrp.setText("₹ " + homeCartModel.getPrice());

        /*if (homeCartModel.getDiscountAmount() > 0.0) {
            double DiscountAmount = homeCartModel.getPrice() - homeCartModel.getDiscountAmount();
            holder.mBinding.tvSellingPrice.setVisibility(View.VISIBLE);
            ///holder.mBinding.tvSellingPrice.setText("₹ " + DiscountAmount);
            holder.mBinding.tvMrp.setText("₹ " + DiscountAmount);
            //holder.mBinding.tvMrp.setPaintFlags(holder.mBinding.tvMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }*/

        if (homeCartModel.getImagePath()!=null && !homeCartModel.getImagePath().contains("http")) {
            Picasso.get().load(BuildConfig.apiEndpoint+homeCartModel.getImagePath()).error(R.drawable.ic_top_seller).into(holder.mBinding.ivImage);
        }else {
            Picasso.get().load(homeCartModel.getImagePath()).placeholder(R.drawable.ic_top_seller).into(holder.mBinding.ivImage);
        }

    }

    @Override
    public int getItemCount() {
        return homeCartItemList == null ? 0 : homeCartItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemShowCartInHomeBinding mBinding;

        public ViewHolder(ItemShowCartInHomeBinding Binding) {
            super(Binding.getRoot());
            this.mBinding = Binding;
        }
    }
}
