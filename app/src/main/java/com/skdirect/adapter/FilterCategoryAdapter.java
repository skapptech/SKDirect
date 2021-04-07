package com.skdirect.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.skdirect.R;
import com.skdirect.databinding.ItemsFilterCategoryBinding;
import com.skdirect.databinding.ItemsFilterTypeBinding;
import com.skdirect.interfacee.FilterCategoryInterface;
import com.skdirect.interfacee.FilterTypeInterface;

import java.util.ArrayList;

public class FilterCategoryAdapter extends RecyclerView.Adapter<FilterCategoryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String>filterTypelist;
    private int lastSelectedPosition = 0;
    private final FilterCategoryInterface filterCategoryInterface;
    public FilterCategoryAdapter(Context context, ArrayList<String> filterTypelist,FilterCategoryInterface filterCategoryInterface) {
        this.context = context;
        this.filterTypelist = filterTypelist;
        this.filterCategoryInterface = filterCategoryInterface;
    }

    @NonNull
    @Override
    public FilterCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.items_filter_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FilterCategoryAdapter.ViewHolder holder, int position) {
        holder.mBinding.tvFilterCategoryName.setText(filterTypelist.get(position));
        holder.mBinding.itemSelect.setChecked(lastSelectedPosition == position);
        if(position == lastSelectedPosition){
            holder.mBinding.itemSelect.setChecked(true);
            filterCategoryInterface.clickFilterCategoryInterface(filterTypelist.get(position),position);
        }
        else {
            holder.mBinding.itemSelect.setChecked(false);
        }

        holder.mBinding.itemSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastSelectedPosition = position;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return filterTypelist == null ? 0 : filterTypelist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemsFilterCategoryBinding mBinding;
        public ViewHolder(ItemsFilterCategoryBinding Binding) {
            super(Binding.getRoot());
            this.mBinding = Binding;
        }
    }
}
