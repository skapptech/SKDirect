package com.skdirect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.skdirect.BuildConfig;
import com.skdirect.R;
import com.skdirect.databinding.ItemInvoiceBinding;
import com.skdirect.databinding.ItemShowCartInHomeBinding;
import com.skdirect.model.CartModel;
import com.skdirect.model.InvoiceModel;
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
        holder.mBinding.tvNetPaybleAmount.setText("Net payable Amount   "+String.format("%.2f", invoiceModel.getTotalprice()));;
        holder.mBinding.tvPaymentMode.setText("Payment Mode "+invoiceModel.getPaymentmode());
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
