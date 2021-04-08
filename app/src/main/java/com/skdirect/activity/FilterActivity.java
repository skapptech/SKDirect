package com.skdirect.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.innovattic.rangeseekbar.RangeSeekBar;
import com.skdirect.R;
import com.skdirect.adapter.FilterCategoryAdapter;
import com.skdirect.adapter.FilterTypeAdapter;
import com.skdirect.databinding.ActivityFilterBinding;
import com.skdirect.interfacee.FilterCategoryInterface;
import com.skdirect.interfacee.FilterTypeInterface;
import com.skdirect.utils.Constant;

import java.util.ArrayList;

public class FilterActivity extends AppCompatActivity implements FilterTypeInterface, FilterCategoryInterface {
     ActivityFilterBinding mBinding;
    ArrayList<String> filterCateDataList = new ArrayList<>();
    FilterCategoryAdapter filterCategoryAdapter;
    private int maxprice = 10000, minprice = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_filter);
       initView();
    }

    private void initView() {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        mBinding.rvFilterType.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.rvFilterType.addItemDecoration(dividerItemDecoration);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.devider));

        ArrayList<String> list = new ArrayList<>();
        list.add("Categories");
        list.add("Price");
        list.add("Brands");
        list.add("Discount");

        FilterTypeAdapter filterTypeAdapter = new FilterTypeAdapter(this,list,this);
        mBinding.rvFilterType.setAdapter(filterTypeAdapter);

        mBinding.rvFilterData.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        filterCategoryAdapter = new FilterCategoryAdapter(this,filterCateDataList,this);
        mBinding.rvFilterData.setAdapter(filterCategoryAdapter);

        mBinding.tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(Constant.Category,"A");
                intent.putExtra(Constant.Price,"100");
                intent.putExtra(Constant.Brands,"Local");
                intent.putExtra(Constant.Discount,"10");
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        mBinding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_CANCELED, intent);
                finish();
            }
        });
        setRangeSeekbar();
    }
    private void setRangeSeekbar() {
        mBinding.tvMinMaxRange.setText("₹" + 1+"- ₹" + 10000);
        mBinding.rangeSeekbar.setMax(10000);
        mBinding.rangeSeekbar.setMinRange(1);
        mBinding.rangeSeekbar.setSeekBarChangeListener(new RangeSeekBar.SeekBarChangeListener() {
            @Override
            public void onStartedSeeking() {
            }
            @Override
            public void onStoppedSeeking() {
            }
            @Override
            public void onValueChanged(int minValue, int maxValue) {
                mBinding.tvMinMaxRange.setText("₹" + minValue+"- ₹" + maxValue);
                maxprice = (int) maxValue;
                minprice = (int) minValue;
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    @Override
    public void clickFilterTypeInterface(String name, int position) {
        filterCateDataList.clear();
        switch(position) {
            case 0:
                mBinding.rvFilterData.setVisibility(View.VISIBLE);
                mBinding.priceFilter.setVisibility(View.GONE);
                filterCateDataList.add("Apparels");
                filterCateDataList.add("Auto parts");
                filterCateDataList.add("Baby product");
                filterCateDataList.add("Books Store");
                filterCategoryAdapter.notifyDataSetChanged();
                break;
            case 1:
                mBinding.rvFilterData.setVisibility(View.GONE);
                mBinding.priceFilter.setVisibility(View.VISIBLE);
                break;
            case 2:
                mBinding.rvFilterData.setVisibility(View.VISIBLE);
                mBinding.priceFilter.setVisibility(View.GONE);
                filterCateDataList.add("Nirvika");
                filterCateDataList.add("No brand");
                break;
            case 3:
                mBinding.rvFilterData.setVisibility(View.VISIBLE);
                mBinding.priceFilter.setVisibility(View.GONE);
                filterCateDataList.add("Any");
                filterCateDataList.add("10%");
                filterCateDataList.add("20%");
                filterCateDataList.add("30%");
                filterCateDataList.add("40%");
                filterCategoryAdapter.notifyDataSetChanged();
                break;
            default:

                break;
        }
    }

    @Override
    public void clickFilterCategoryInterface(String name, int position) {
        System.out.println("Filter Category Data>>>."+position);
    }
}