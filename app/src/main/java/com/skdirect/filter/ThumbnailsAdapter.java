package com.skdirect.filter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.skdirect.R;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;

public class ThumbnailsAdapter extends RecyclerView.Adapter<ThumbnailsAdapter.MyViewHolder> {

    private List<ThumbnailItem> thumbnailItemList;
    private ThumbnailsAdapterListener listener;
    private Context mContext;
    private int selectedIndex = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView filterName;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.thumbnail);
            filterName = view.findViewById(R.id.filter_name);
        }
    }


    public ThumbnailsAdapter(Context context, List<ThumbnailItem> thumbnailItemList, ThumbnailsAdapterListener listener) {
        mContext = context;
        this.thumbnailItemList = thumbnailItemList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_thumbnail_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ThumbnailItem thumbnailItem = thumbnailItemList.get(position);

        holder.thumbnail.setImageBitmap(thumbnailItem.image);

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFilterSelected(thumbnailItem.filter);
                selectedIndex = position;
                notifyDataSetChanged();
            }
        });

        holder.filterName.setText(thumbnailItem.filterName);

        if (selectedIndex == position) {
            holder.filterName.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        } else {
            holder.filterName.setTextColor(ContextCompat.getColor(mContext, R.color.grey));
        }
    }

    @Override
    public int getItemCount() {
        return thumbnailItemList.size();
    }

    public interface ThumbnailsAdapterListener {
        void onFilterSelected(Filter filter);
    }
}