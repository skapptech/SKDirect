package com.skdirect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.skdirect.R;
import com.skdirect.databinding.ItemTopNearByBinding;
import com.skdirect.databinding.ItemTopSellerBinding;
import com.skdirect.model.TopNearByItemModel;
import com.skdirect.model.TopSellerModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TopSellerAdapter extends RecyclerView.Adapter<TopSellerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<TopSellerModel>topSellerList;

    public TopSellerAdapter(Context context, ArrayList<TopSellerModel> topSellerList) {
        this.context = context;
        this.topSellerList = topSellerList;
    }

    @NonNull
    @Override
    public TopSellerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_top_seller, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TopSellerAdapter.ViewHolder holder, int position) {
        TopSellerModel topSellerModel = topSellerList.get(position);
        holder.mBinding.tvSellerName.setText(topSellerModel.getFirstName());

    }

    @Override
    public int getItemCount() {
        return topSellerList == null ? 0 : topSellerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemTopSellerBinding mBinding;

        public ViewHolder(ItemTopSellerBinding Binding) {
            super(Binding.getRoot());
            this.mBinding = Binding;
        }
    }
}
