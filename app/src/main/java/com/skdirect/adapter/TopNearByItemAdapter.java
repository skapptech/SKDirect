package com.skdirect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.skdirect.R;
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
        holder.mBinding.tvItemPrice.setText(String.valueOf(topNearByItemModel.getMrp()));
        if (topNearByItemModel.getImagePath()!=null) {
            Picasso.get().load(topNearByItemModel.getImagePath())/*.error(R.drawable.trade_splace)*/.into(holder.mBinding.ivImage);
        }
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
