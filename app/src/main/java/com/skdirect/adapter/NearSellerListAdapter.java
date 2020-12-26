package com.skdirect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.skdirect.R;
import com.skdirect.databinding.ItemNearProductListBinding;
import com.skdirect.databinding.ItemNearSallerListBinding;
import com.skdirect.model.NearBySallerModel;
import com.skdirect.model.NearProductListModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NearSellerListAdapter extends RecyclerView.Adapter<NearSellerListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<NearBySallerModel> nearBySallerList;

    public NearSellerListAdapter(Context context, ArrayList<NearBySallerModel> nearBySallerList) {
        this.context = context;
        this.nearBySallerList = nearBySallerList;
    }

    @NonNull
    @Override
    public NearSellerListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_near_saller_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NearSellerListAdapter.ViewHolder holder, int position) {
        NearBySallerModel  nearBySallerModel = nearBySallerList.get(position);
        holder.mBinding.tvSallerName.setText(nearBySallerModel.getFirstName());
        holder.mBinding.tvSallerCity.setText(nearBySallerModel.getCity());
        holder.mBinding.tvSallerState.setText(nearBySallerModel.getState());

        if (nearBySallerModel.getImagePath()!=null){
            Picasso.get().load(nearBySallerModel.getImagePath()).placeholder(R.drawable.ic_top_seller).into(holder.mBinding.imItemImage);
        }else {
            Picasso.get()
                    .load(R.drawable.ic_top_seller)
                    .placeholder(R.drawable.ic_top_seller)
                    .error(R.drawable.ic_top_seller)
                    .into(holder.mBinding.imItemImage);
        }
    }

    @Override
    public int getItemCount() {
        return nearBySallerList == null ? 0 : nearBySallerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemNearSallerListBinding mBinding;

        public ViewHolder(ItemNearSallerListBinding Binding) {
            super(Binding.getRoot());
            this.mBinding = Binding;
        }
    }
}
