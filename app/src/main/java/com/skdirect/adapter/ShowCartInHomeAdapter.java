package com.skdirect.adapter;

import android.content.Context;
import android.content.Intent;
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
        holder.mBinding.tvSallerName.setText(homeCartModel.getProductName());

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
