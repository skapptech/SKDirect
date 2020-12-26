package com.skdirect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.skdirect.R;
import com.skdirect.databinding.ItemAllCategoriesBinding;
import com.skdirect.databinding.ItemCategoriesBinding;
import com.skdirect.model.AllCategoriesModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<AllCategoriesModel>allCategoriesList;

    public CategoriesAdapter(Context context, ArrayList<AllCategoriesModel>allCategories) {
        this.context = context;
        this.allCategoriesList = allCategories;
    }

    @NonNull
    @Override
    public CategoriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_categories, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesAdapter.ViewHolder holder, int position) {
        AllCategoriesModel allCategoriesModel = allCategoriesList.get(position);
        holder.mBinding.tvCetegoryName.setText(allCategoriesModel.getName());
        if (allCategoriesModel.getImagePath()!=null){
                Picasso.get().load(allCategoriesModel.getImagePath()).error(R.drawable.no_image).into(holder.mBinding.imItemImage);
        }else {
            Picasso.get()
                    .load(R.drawable.ic_lost_items)
                    .placeholder(R.drawable.ic_lost_items)
                    .error(R.drawable.ic_lost_items)
                    .into(holder.mBinding.imItemImage);
        }
    }

    @Override
    public int getItemCount() {
        return allCategoriesList == null ? 0 : allCategoriesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemCategoriesBinding mBinding;

        public ViewHolder(ItemCategoriesBinding Binding) {
            super(Binding.getRoot());
            this.mBinding = Binding;
        }
    }
}
