package com.skdirect.stepform;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.skdirect.model.OrderDetailsModel;
import com.skdirect.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class MainStepperAdapter extends VerticalStepperAdapter {
    private List<OrderDetailsModel.OrderStatusDC> OrderStatusDC;


    public MainStepperAdapter(Context context, List<OrderDetailsModel.OrderStatusDC> list) {
        super(context);
        this.OrderStatusDC = list;
    }

    @NonNull
    @Override
    public CharSequence getTitle(int position) {
        return OrderStatusDC.get(position).getStatus();
    }

    @Nullable
    @Override
    public CharSequence getSummary(int position) {
//        return list.get(position).getMessage();
        return Utils.getDateFormate(OrderStatusDC.get(position).getCreatedDate());
    }

    @Nullable
    @Override
    public CharSequence getDate(int position) {
//        return Utils.getDateTimeFormate(list.get(position).getDate());
        return "";
    }

    @Override
    public int getCount() {
        return OrderStatusDC == null ? 0 : OrderStatusDC.size();
    }

    @Override
    public Void getItem(int position) {
        return null;
    }

    @NonNull
    @Override
    public View onCreateContentView(Context context, int position) {
        return new MainItemView(context);
    }
}