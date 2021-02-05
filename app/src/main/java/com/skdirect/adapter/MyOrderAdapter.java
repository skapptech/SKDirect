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
import com.skdirect.databinding.ItemMyOrderBinding;
import com.skdirect.model.MyOrderModel;
import com.skdirect.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<MyOrderModel> orderModelArrayList;


    public MyOrderAdapter(Context context, ArrayList<MyOrderModel> deliveryOptionList) {
        this.context = context;
        this.orderModelArrayList = deliveryOptionList;
    }

    @NonNull
    @Override
    public MyOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_my_order, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrderAdapter.ViewHolder holder, int position) {
        MyOrderModel myOrderModel = orderModelArrayList.get(position);
        holder.mBinding.tvOrderStatus.setText(myOrderModel.getOrderStatus() + " on " + Utils.getDateFormate(myOrderModel.getCreatedDate()));
        holder.mBinding.tvProductName.setText(myOrderModel.getProductName());
        holder.mBinding.tvOrderNumber.setText("Order No#: " + myOrderModel.getId());
        holder.mBinding.tvSaller.setText(myOrderModel.getShopName());
        holder.mBinding.ratingbar.setVisibility(View.GONE);

        if (myOrderModel.getImagePath() != null && !myOrderModel.getImagePath().contains("http")) {
            Picasso.get().load(BuildConfig.apiEndpoint + myOrderModel.getImagePath()).error(R.drawable.ic_top_seller).into(holder.mBinding.imItemImage);
        } else {
            Picasso.get().load(myOrderModel.getImagePath()).error(R.drawable.ic_top_seller).into(holder.mBinding.imItemImage);
        }

        if (myOrderModel.getOrderStatus().equals("Delivered")&&myOrderModel.getRating()==0) {
            holder.mBinding.tvAddReview.setVisibility(View.VISIBLE);
        } else {
            holder.mBinding.ratingbar.setVisibility(View.VISIBLE);
            holder.mBinding.ratingbar.setRating(myOrderModel.getRating());
            holder.mBinding.tvAddReview.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return orderModelArrayList == null ? 0 : orderModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemMyOrderBinding mBinding;

        public ViewHolder(ItemMyOrderBinding Binding) {
            super(Binding.getRoot());
            this.mBinding = Binding;

        }
    }
}
