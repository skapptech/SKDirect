package com.skdirect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.skdirect.R;
import com.skdirect.databinding.ItemCouponListBinding;
import com.skdirect.model.response.CouponResponse;
import com.skdirect.utils.MyApplication;

import java.util.ArrayList;

public class CouponListAdapter extends RecyclerView.Adapter<CouponListAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<CouponResponse.Coupon> list;


    public CouponListAdapter(Context context, ArrayList<CouponResponse.Coupon> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CouponListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CouponListAdapter.ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_coupon_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CouponListAdapter.ViewHolder holder, int position) {
        CouponResponse.Coupon model = list.get(position);
        holder.mBinding.btnApply.setText(MyApplication.getInstance().dbHelper.getString(R.string.apply));
        holder.mBinding.tvName.setText(model.getCouponName());
        holder.mBinding.tvDes.setText(MyApplication.getInstance().dbHelper.getString(R.string.inr) +" "+ model.getAmount() + " "+ MyApplication.getInstance().dbHelper.getString(R.string.off_upto_inr) +" "+ model.getMaxAmount());
        holder.mBinding.tvTerms.setText(MyApplication.getInstance().dbHelper.getString(R.string.inr) +" "+ model.getAmount() + " "+ MyApplication.getInstance().dbHelper.getString(R.string.off_upto_inr) +" "+  model.getMaxAmount() + " "+ MyApplication.getInstance().dbHelper.getString(R.string.on_order_of) + model.getMaxAmount() + " "+MyApplication.getInstance().dbHelper.getString(R.string.or_above));
        holder.mBinding.tvCouponCode.setText("" + model.getCouponCode());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCouponListBinding mBinding;

        public ViewHolder(ItemCouponListBinding Binding) {
            super(Binding.getRoot());
            this.mBinding = Binding;
        }
    }
}
