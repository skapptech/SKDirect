package com.skdirect.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.skdirect.BuildConfig;
import com.skdirect.R;
import com.skdirect.activity.ProductDetailsActivity;
import com.skdirect.activity.SellerProfileActivity;
import com.skdirect.model.BannerItemListModel;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HomeBannerAdapter extends PagerAdapter {
    private final Context context;
    private final ArrayList<BannerItemListModel> bannerItemListModel;
    private final LayoutInflater inflater;

    public HomeBannerAdapter(Context context, ArrayList<BannerItemListModel> bannerListModel) {
        this.context = context;
        this.bannerItemListModel = bannerListModel;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NotNull Object object) {
        container.removeView((View) object);
    }

    @NotNull
    @Override
    public Object instantiateItem(@NotNull ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.home_banner_item, view, false);

        assert imageLayout != null;
        final ImageView imageView = imageLayout.findViewById(R.id.cat_img);
        if (bannerItemListModel.get(position).getImagepath() != null && !bannerItemListModel.get(position).getImagepath().contains("http")) {
            Picasso.get().load(BuildConfig.apiEndpoint + bannerItemListModel.get(position).getImagepath()).into(imageView);
        } else {
            Picasso.get().load(bannerItemListModel.get(position).getImagepath()).into(imageView);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bannerItemListModel.get(position).getType() != null) {
                    if (bannerItemListModel.get(position).getType().equals("SELLER")) {
                        context.startActivity(new Intent(context, SellerProfileActivity.class).putExtra("ID", bannerItemListModel.get(position).getGivenId()));
                    } else {
                        context.startActivity(new Intent(context, ProductDetailsActivity.class).putExtra("ID", bannerItemListModel.get(position).getGivenId()));
                    }
                }
            }
        });
        view.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public int getCount() {
        return bannerItemListModel.size();

    }

    @Override
    public boolean isViewFromObject(View view, @NotNull Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
