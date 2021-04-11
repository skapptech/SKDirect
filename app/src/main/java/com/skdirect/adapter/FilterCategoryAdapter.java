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
import com.skdirect.databinding.ItemsFilterBrandBinding;
import com.skdirect.databinding.ItemsFilterCategoryBinding;
import com.skdirect.databinding.ItemsFilterDiscountBinding;
import com.skdirect.databinding.ItemsFilterTypeBinding;
import com.skdirect.interfacee.FilterCategoryInterface;
import com.skdirect.interfacee.FilterTypeInterface;
import com.skdirect.model.FilterCategoryDetails;

import java.util.ArrayList;

public class FilterCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public final int CATEGORY_TYPE = 0;
    public final int BRAND_TYPE = 1;
    public final int DISCOUNT_TYPE = 4;
    private Context context;
    private ArrayList<FilterCategoryDetails>filterTypelist;
    private int lastSelectedPosition = 0;
    private final FilterCategoryInterface filterCategoryInterface;
    String filterType = "";
    public FilterCategoryAdapter(Context context, ArrayList<FilterCategoryDetails> filterTypelist,FilterCategoryInterface filterCategoryInterface) {
        this.context = context;
        this.filterTypelist = filterTypelist;
        this.filterCategoryInterface = filterCategoryInterface;
    }

    public void setDataAdapter(ArrayList<FilterCategoryDetails> filterTypelist,String filterType)
    {
        this.filterType = filterType;
        lastSelectedPosition = 0;
        this.filterTypelist = filterTypelist;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case CATEGORY_TYPE:
                return new ViewHolderCategory(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.items_filter_category, parent, false));
            case BRAND_TYPE:
                return new ViewHolderBrand(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.items_filter_brand, parent, false));
            case DISCOUNT_TYPE:
                return new ViewHolderDiscount(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.items_filter_discount, parent, false));
            default:
                return new ViewHolderCategory(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.items_filter_category, parent, false));
         }

       /* return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.items_filter_category, parent, false));*/
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        switch (vh.getItemViewType()) {
            case CATEGORY_TYPE:
                ((ViewHolderCategory) vh).mBinding.tvFilterCategoryName.setText(filterTypelist.get(position).getLabel());
                ((ViewHolderCategory) vh).mBinding.itemSelect.setChecked(lastSelectedPosition == position);
                if(position == lastSelectedPosition){
                    ((ViewHolderCategory) vh).mBinding.itemSelect.setChecked(true);
                    filterCategoryInterface.clickFilterCategoryInterface(filterTypelist.get(position).getValue(),filterTypelist.get(position).getLabel(),filterType);
                }
                else {
                    ((ViewHolderCategory) vh).mBinding.itemSelect.setChecked(false);
                }

                ((ViewHolderCategory) vh).mBinding.itemSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lastSelectedPosition = position;
                        notifyDataSetChanged();
                    }
                });
                break;
            case BRAND_TYPE:
                ((ViewHolderBrand) vh).mBinding.tvFilterCategoryName.setText(filterTypelist.get(position).getLabel());
               // ((ViewHolderBrand) vh).mBinding.rbBrandItemSelect.setChecked(lastSelectedPosition == position);
               /* if(position == lastSelectedPosition){
                    ((ViewHolderBrand) vh).mBinding.itemSelect.setChecked(true);
                    filterCategoryInterface.clickFilterBrandyInterface(filterTypelist.get(position).getValue(),filterTypelist.get(position).getLabel(),filterType);
                }
                else {
                    ((ViewHolderBrand) vh).mBinding.itemSelect.setChecked(false);
                }*/
              /*  if (filterTypelist.get(position).isSelected()) {
                    filterTypelist.get(position).setSelected(true);
                }else
                {
                    filterTypelist.get(position).setSelected(false);
                }*/
                ((ViewHolderBrand) vh).mBinding.rbBrandItemSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (filterTypelist.get(position).isSelected()) {
                            filterTypelist.get(position).setSelected(false);
                            ((ViewHolderBrand) vh).mBinding.rbBrandItemSelect.setChecked(false);
                            filterCategoryInterface.clickFilterBrandyInterface(filterTypelist.get(position).getValue(),filterTypelist.get(position).getLabel(),filterType,true);

                        } else {
                            filterTypelist.get(position).setSelected(true);
                            ((ViewHolderBrand) vh).mBinding.rbBrandItemSelect.setChecked(true);
                            filterCategoryInterface.clickFilterBrandyInterface(filterTypelist.get(position).getValue(),filterTypelist.get(position).getLabel(),filterType,false);

                        }
                       /* if(((ViewHolderBrand) vh).mBinding.itemSelect.isChecked())
                        {
                            ((ViewHolderBrand) vh).mBinding.itemSelect.setChecked(false);
                            filterCategoryInterface.clickFilterBrandyInterface(filterTypelist.get(position).getValue(),filterTypelist.get(position).getLabel(),filterType,true);

                        }else {
                            ((ViewHolderBrand) vh).mBinding.itemSelect.setChecked(true);
                            filterCategoryInterface.clickFilterBrandyInterface(filterTypelist.get(position).getValue(),filterTypelist.get(position).getLabel(),filterType,false);
                        }*/
                       // ((ViewHolderBrand) vh).mBinding.itemSelect.setChecked(true);
                      //  lastSelectedPosition = position;
                      //  notifyDataSetChanged();
                    }
                });
                break;
            case DISCOUNT_TYPE:
                ((ViewHolderDiscount) vh).mBinding.tvFilterCategoryName.setText(filterTypelist.get(position).getLabel());
               // ((ViewHolderDiscount) vh).mBinding.rbDiscountItemSelect.setChecked(lastSelectedPosition == position);
                if(position == lastSelectedPosition){
                    ((ViewHolderDiscount) vh).mBinding.rbDiscountItemSelect.setChecked(true);
                    filterCategoryInterface.clickFilterDiscountInterface(filterTypelist.get(position).getValue(),filterTypelist.get(position).getLabel(),filterType);
                }
                else {
                    ((ViewHolderDiscount) vh).mBinding.rbDiscountItemSelect.setChecked(false);
                }

                ((ViewHolderDiscount) vh).mBinding.rbDiscountItemSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lastSelectedPosition = position;
                        notifyDataSetChanged();
                    }
                });
                break;
        }


    }

    @Override
    public int getItemCount() {
        return filterTypelist == null ? 0 : filterTypelist.size();
    }

    public class ViewHolderCategory extends RecyclerView.ViewHolder {
        ItemsFilterCategoryBinding mBinding;
        public ViewHolderCategory(ItemsFilterCategoryBinding Binding) {
            super(Binding.getRoot());
            this.mBinding = Binding;
        }
    }

    public class ViewHolderBrand extends RecyclerView.ViewHolder {
        ItemsFilterBrandBinding mBinding;
        public ViewHolderBrand(ItemsFilterBrandBinding Binding) {
            super(Binding.getRoot());
            this.mBinding = Binding;
        }
    }

    public class ViewHolderDiscount extends RecyclerView.ViewHolder {
        ItemsFilterDiscountBinding mBinding;
        public ViewHolderDiscount(ItemsFilterDiscountBinding Binding) {
            super(Binding.getRoot());
            this.mBinding = Binding;
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (filterType) {
            case "Category":
                return CATEGORY_TYPE;
            case "Brands":
                return BRAND_TYPE;
            case "Discount":
                return DISCOUNT_TYPE;
            default:
                return CATEGORY_TYPE;
        }
    }
}
