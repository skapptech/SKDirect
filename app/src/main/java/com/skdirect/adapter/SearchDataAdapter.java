package com.skdirect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.skdirect.BuildConfig;
import com.skdirect.R;
import com.skdirect.databinding.ItemCategoriesBinding;
import com.skdirect.databinding.ItemSearchBinding;
import com.skdirect.model.AllCategoriesModel;
import com.skdirect.model.SearchDataModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchDataAdapter extends RecyclerView.Adapter<SearchDataAdapter.ViewHolder> {

    private Context context;
    private ArrayList<SearchDataModel.TableOneTwo> list;

    public SearchDataAdapter(Context context, ArrayList<SearchDataModel.TableOneTwo> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SearchDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_search, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchDataAdapter.ViewHolder holder, int position) {
        SearchDataModel.TableOneTwo model = list.get(position);
        holder.mBinding.tvSaller.setText(model.getShopName());
        holder.mBinding.tvCityName.setText(model.getCityName()+" - "+ model.getPinCode());

        if (model.getImagePath()!=null){
            Picasso.get().load(BuildConfig.apiEndpoint+model.getImagePath()).into(holder.mBinding.imItemImage);
        }else {
            Picasso.get()
                    .load(R.drawable.ic_top_seller)
                    .placeholder(R.drawable.ic_top_seller)
                    .error(R.drawable.ic_top_seller)
                    .into(holder.mBinding.imItemImage);
        }


        SearchItemAdapter searchDataAdapter = new SearchItemAdapter(context, model.getProductList());
        holder.mBinding.rvSearchItem.setAdapter(searchDataAdapter);

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemSearchBinding mBinding;

        public ViewHolder(ItemSearchBinding Binding) {
            super(Binding.getRoot());
            this.mBinding = Binding;
        }
    }
}
