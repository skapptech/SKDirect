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
import com.skdirect.activity.ShowImageActivity;
import com.skdirect.model.ImageListModel;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HomeBannerAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater inflater;

    public HomeBannerAdapter(Context context) {
        this.context = context;
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
        imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.b3));

       /* if (imageListModels.get(position).getImagePath() != null && !imageListModels.get(position).getImagePath().contains("http")) {
            Picasso.get().load(BuildConfig.apiEndpoint+imageListModels.get(position).getImagePath()).into(imageView);
        }else {
            Picasso.get().load(imageListModels.get(position).getImagePath()).into(imageView);
        }*/


        view.addView(imageLayout, 0);

        return imageLayout;
    }

    @Override
    public int getCount() {
        return 10;
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