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
import com.skdirect.activity.ProductDetailsActivity;
import com.skdirect.activity.SellerProfileActivity;
import com.skdirect.databinding.ItemAllCategoriesBinding;
import com.skdirect.databinding.ItemNearProductListBinding;
import com.skdirect.model.AllCategoriesModel;
import com.skdirect.model.NearProductListModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NearProductListAdapter extends RecyclerView.Adapter<NearProductListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<NearProductListModel> nearProductListList;

    public NearProductListAdapter(Context context, ArrayList<NearProductListModel> nearProductListList) {
        this.context = context;
        this.nearProductListList = nearProductListList;
    }

    @NonNull
    @Override
    public NearProductListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_near_product_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NearProductListAdapter.ViewHolder holder, int position) {
        NearProductListModel nearProductListModel = nearProductListList.get(position);
        holder.mBinding.tvItemName.setText(nearProductListModel.getProductName());
        holder.mBinding.tvShopName.setText(nearProductListModel.getShopName());
        holder.mBinding.tvMpr.setText("₹ "+String.valueOf(nearProductListModel.getMrp()));
        holder.mBinding.tvTax.setText("Inclusive of all taxes");

        if (nearProductListModel.getImagePath()!=null && !nearProductListModel.getImagePath().contains("http")) {
            Picasso.get().load(BuildConfig.apiEndpoint+nearProductListModel.getImagePath()).error(R.drawable.ic_top_seller).into(holder.mBinding.imItemImage);
        }else {
            Picasso.get().load(nearProductListModel.getImagePath()).error(R.drawable.ic_top_seller).into(holder.mBinding.imItemImage);
        }

        holder.mBinding.tvNearByViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent menuIntent = new Intent(context, ProductDetailsActivity.class);
                menuIntent.putExtra("ID",nearProductListModel.getId());
                menuIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(menuIntent);

            }
        });




    }

    @Override
    public int getItemCount() {
        return nearProductListList == null ? 0 : nearProductListList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemNearProductListBinding mBinding;

        public ViewHolder(ItemNearProductListBinding Binding) {
            super(Binding.getRoot());
            this.mBinding = Binding;
        }
    }
}
