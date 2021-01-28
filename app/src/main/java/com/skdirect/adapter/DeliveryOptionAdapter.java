package com.skdirect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.skdirect.R;
import com.skdirect.databinding.ItemAllCategoriesBinding;
import com.skdirect.databinding.ItemDeliveryOptionBinding;
import com.skdirect.model.AllCategoriesModel;
import com.skdirect.model.DeliveryOptionModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DeliveryOptionAdapter extends RecyclerView.Adapter<DeliveryOptionAdapter.ViewHolder> {

    private Context context;
    private ArrayList<DeliveryOptionModel>deliveryOptionList;

    public DeliveryOptionAdapter(Context context, ArrayList<DeliveryOptionModel>deliveryOptionList) {
        this.context = context;
        this.deliveryOptionList = deliveryOptionList;
    }

    @NonNull
    @Override
    public DeliveryOptionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_delivery_option, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryOptionAdapter.ViewHolder holder, int position) {
        DeliveryOptionModel deliveryOptionModel = deliveryOptionList.get(position);
        holder.mBinding.tvDeliveryOption.setText(deliveryOptionModel.getDelivery());
    }

    @Override
    public int getItemCount() {
        return deliveryOptionList == null ? 0 : deliveryOptionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemDeliveryOptionBinding mBinding;

        public ViewHolder(ItemDeliveryOptionBinding Binding) {
            super(Binding.getRoot());
            this.mBinding = Binding;
        }
    }
}
