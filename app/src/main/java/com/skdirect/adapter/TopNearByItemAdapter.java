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
import com.skdirect.databinding.ItemTopNearByBinding;
import com.skdirect.model.TopNearByItemModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TopNearByItemAdapter extends RecyclerView.Adapter<TopNearByItemAdapter.ViewHolder> {

    private Context context;
    private ArrayList<TopNearByItemModel>topNearByItemList;

    public TopNearByItemAdapter(Context context, ArrayList<TopNearByItemModel> topNearByItemList) {
        this.context = context;
        this.topNearByItemList = topNearByItemList;
    }

    @NonNull
    @Override
    public TopNearByItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_top_near_by, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TopNearByItemAdapter.ViewHolder holder, int position) {
        TopNearByItemModel topNearByItemModel = topNearByItemList.get(position);
        holder.mBinding.tvItemName.setText(topNearByItemModel.getProductName());
        holder.mBinding.tvItemPrice.setText("MRP "+String.valueOf(topNearByItemModel.getMrp()));

        if(topNearByItemModel.getImagePath()!=null && !topNearByItemModel.getImagePath().contains("http")) {
            Picasso.get().load(BuildConfig.apiEndpoint+topNearByItemModel.getImagePath()).error(R.drawable.ic_top_seller).into(holder.mBinding.ivImage);
        }else {
            Picasso.get().load(topNearByItemModel.getImagePath()).error(R.drawable.ic_top_seller).into(holder.mBinding.ivImage);
        }

        holder.mBinding.llTopNearBySeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent menuIntent = new Intent(context, ProductDetailsActivity.class);
                menuIntent.putExtra("ID",topNearByItemModel.getId());
                context.startActivity(menuIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return topNearByItemList == null ? 0 : topNearByItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemTopNearByBinding mBinding;

        public ViewHolder(ItemTopNearByBinding Binding) {
            super(Binding.getRoot());
            this.mBinding = Binding;
        }
    }
}
