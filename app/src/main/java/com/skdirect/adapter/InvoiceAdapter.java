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
import com.skdirect.activity.MyOrderActivity;
import com.skdirect.activity.SellerProfileActivity;
import com.skdirect.databinding.ItemInvoiceBinding;
import com.skdirect.databinding.ItemShowCartInHomeBinding;
import com.skdirect.model.CartModel;
import com.skdirect.model.InvoiceModel;
import com.skdirect.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.ViewHolder> {

    private Context context;
    private ArrayList<InvoiceModel> invoiceList;

    public InvoiceAdapter(Context context, ArrayList<InvoiceModel> invoiceModels) {
        this.context = context;
        this.invoiceList = invoiceModels;
    }

    @NonNull
    @Override
    public InvoiceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_invoice, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceAdapter.ViewHolder holder, int position) {
        InvoiceModel invoiceModel = invoiceList.get(position);
        holder.mBinding.tvShopName.setText(invoiceModel.getShopname());;
        holder.mBinding.tvInvoiceNumber.setText(invoiceModel.getInvoicenumber());;
        holder.mBinding.tvNetPaybleAmount.setText(String.format("%.2f", invoiceModel.getTotalPayableAmount()));
        holder.mBinding.tvPaymentMode.setText(invoiceModel.getPaymentmode());
        holder.mBinding.tvOrderDate.setText(Utils.getDateFormate(invoiceModel.getInvoiceDate()));
        holder.mBinding.tvTotalAmount.setText(String.format("%.2f", invoiceModel.getTotalitemamount()));

        if (invoiceModel.getTotaldeliverycharges()>0.0){
            holder.mBinding.rlDeliveryCharge.setVisibility(View.VISIBLE);
            holder.mBinding.tvTotalDeliveryCharge.setText(String.format("%.2f", invoiceModel.getTotaldeliverycharges()));
        }

        if (invoiceModel.getTotaldiscountamount()>0){
            holder.mBinding.rlDicount.setVisibility(View.VISIBLE);
            holder.mBinding.tvTotalDiscount.setText(String.format("%.2f", invoiceModel.getTotaldiscountamount()));
        }


        holder.mBinding.LLMainCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent menuIntent = new Intent(context, MyOrderActivity.class);
                menuIntent.putExtra("InvoiceNumber",invoiceModel.getInvoicenumber());
                context.startActivity(menuIntent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return invoiceList == null ? 0 : invoiceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemInvoiceBinding mBinding;

        public ViewHolder(ItemInvoiceBinding Binding) {
            super(Binding.getRoot());
            this.mBinding = Binding;
        }
    }
}
