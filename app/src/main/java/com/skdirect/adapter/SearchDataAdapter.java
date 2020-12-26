package com.skdirect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.skdirect.R;
import com.skdirect.databinding.ItemCategoriesBinding;
import com.skdirect.databinding.ItemSearchBinding;
import com.skdirect.model.AllCategoriesModel;
import com.skdirect.model.SearchDataModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchDataAdapter extends RecyclerView.Adapter<SearchDataAdapter.ViewHolder> {

    private Context context;
    private ArrayList<SearchDataModel.TableOneModel> searchDataListOne;
    private ArrayList<SearchDataModel.TableOneTwo> searchDataListTwo;

    public SearchDataAdapter(Context context, ArrayList<SearchDataModel.TableOneModel> searchDataListOne, ArrayList<SearchDataModel.TableOneTwo> searchDataListTwo) {
        this.context = context;
        this.searchDataListOne = searchDataListOne;
        this.searchDataListTwo = searchDataListTwo;
    }

    @NonNull
    @Override
    public SearchDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_search, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchDataAdapter.ViewHolder holder, int position) {
        SearchDataModel.TableOneModel tableOneModel = searchDataListOne.get(position);
        SearchDataModel.TableOneTwo tableOneTwo = searchDataListTwo.get(position);
        holder.mBinding.tvSaller.setText(tableOneTwo.getShopName());
        holder.mBinding.tvCityName.setText(tableOneTwo.getCityName()+"-"+ tableOneTwo.getPinCode());
        holder.mBinding.tvItemName.setText(tableOneModel.getProductName());
        holder.mBinding.tvPrice.setText("₹ "+String.valueOf(tableOneModel.getMrp()));
        holder.mBinding.tvTax.setText("Inclusive of all taxes");
        holder.mBinding.tvQty.setText("Qty "+String.valueOf(tableOneModel.getMeasurement())+ " PC");

        if (tableOneModel.getImagePath()!=null){
            Picasso.get().load(tableOneModel.getImagePath()).error(R.drawable.no_image).into(holder.mBinding.ivItemImage);
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
        return searchDataListOne == null ? 0 : searchDataListOne.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemSearchBinding mBinding;

        public ViewHolder(ItemSearchBinding Binding) {
            super(Binding.getRoot());
            this.mBinding = Binding;
        }
    }
}
