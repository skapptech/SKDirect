package com.skdirect.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.innovattic.rangeseekbar.RangeSeekBar;
import com.skdirect.R;
import com.skdirect.adapter.FilterCategoryAdapter;
import com.skdirect.adapter.FilterTypeAdapter;
import com.skdirect.api.CommonClassForAPI;
import com.skdirect.databinding.ActivityFilterBinding;
import com.skdirect.interfacee.FilterCategoryInterface;
import com.skdirect.interfacee.FilterTypeInterface;
import com.skdirect.model.FilterCategoryDetails;
import com.skdirect.model.MallMainModelListResult;
import com.skdirect.model.MallMainPriceModel;
import com.skdirect.utils.DBHelper;
import com.skdirect.utils.MyApplication;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import io.reactivex.observers.DisposableObserver;

public class FilterActivity extends AppCompatActivity implements FilterTypeInterface, FilterCategoryInterface {
     ActivityFilterBinding mBinding;
    ArrayList<String> otherList = new ArrayList<>();
    ArrayList<FilterCategoryDetails> filterCateDataList = new ArrayList<>();
    FilterCategoryAdapter filterCategoryAdapter;
    private int maxprice = 10000, minprice = 1;

    public DBHelper dbHelper;
    int categoryId = 0;
    private CommonClassForAPI commonClassForAPI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_filter);
        initView();
        setString();
    }

    private void setString() {
        dbHelper = MyApplication.getInstance().dbHelper;
        mBinding.tvFilter.setText(dbHelper.getString(R.string.filter));
        mBinding.tvClearAll.setText(dbHelper.getString(R.string.clear_all));
        mBinding.tvFilterType.setText(dbHelper.getString(R.string.select_price_range));
        mBinding.tvCancel.setText(dbHelper.getString(R.string.cancel));
        mBinding.tvApply.setText(dbHelper.getString(R.string.apply));
        mBinding.tvNoData.setText(dbHelper.getString(R.string.no_data_found));
    }

    private void initView() {
        commonClassForAPI = CommonClassForAPI.getInstance(this);
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
                System.out.println("MIn>>>"+minprice+"<<Max>>>"+maxprice);
              /*  Intent intent = new Intent();
                intent.putExtra(Constant.Category,"A");
                intent.putExtra(Constant.Price,"100");
                intent.putExtra(Constant.Brands,"Local");
                intent.putExtra(Constant.Discount,"10");
                setResult(Activity.RESULT_OK, intent);
                finish();*/
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
                String dataSaved = SharePrefs.getInstance(this).getString(SharePrefs.FILTER_CATEGORY_LIST);
                if (Utils.isNetworkAvailable(getApplicationContext())) {
                    commonClassForAPI.getFilterCategoryResult(new DisposableObserver<MallMainModelListResult>() {
                        @Override
                        public void onNext(@NotNull MallMainModelListResult result) {
                            if (result.isSuccess()){
                                if(result.getResultItem().size()>0){
                                    viewVisibility(1);
                                    filterCateDataList.addAll(result.getResultItem());
                                    filterCategoryAdapter.notifyDataSetChanged();
                                }else
                                {
                                    viewVisibility(2);
                                    mBinding.tvNoData.setText(dbHelper.getString(R.string.no_filter_categories));

                                }
                            }else
                            {
                                viewVisibility(2);
                                mBinding.tvNoData.setText(dbHelper.getString(R.string.no_filter_categories));
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            viewVisibility(2);
                        }

                        @Override
                        public void onComplete() {
                            Utils.hideProgressDialog();
                        }
                    });

                } else {
                    Utils.setToast(getApplicationContext(), dbHelper.getString(R.string.no_connection));
                }
                break;
            case 1:
                if (Utils.isNetworkAvailable(getApplicationContext())) {

                    commonClassForAPI.getFilterPriceRangeResult(new DisposableObserver<MallMainPriceModel>() {
                        @Override
                        public void onNext(@NotNull MallMainPriceModel result) {
                            if (result.isSuccess()){
                                viewVisibility(3);
                                setRangeSeekbar(result.getResultItem().getMin(),result.getResultItem().getMax());
                            }else
                            {
                                viewVisibility(2);
                                mBinding.tvNoData.setText(dbHelper.getString(R.string.no_filter_price));
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            viewVisibility(2);
                        }

                        @Override
                        public void onComplete() {
                            Utils.hideProgressDialog();
                        }
                    },categoryId);
                } else {
                    Utils.setToast(getApplicationContext(), dbHelper.getString(R.string.no_connection));
                }
                break;
            case 2:
                viewVisibility(1);
                otherList.add("Nirvika");
                otherList.add("No brand");
                break;
            case 3:
                viewVisibility(1);
                otherList.add("Any");
                otherList.add("10%");
                otherList.add("20%");
                otherList.add("30%");
                otherList.add("40%");
                filterCategoryAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    private void viewVisibility(int type) {
        if(type==1)
        {
            mBinding.rvFilterData.setVisibility(View.VISIBLE);
            mBinding.priceFilter.setVisibility(View.GONE);
            mBinding.tvNoData.setVisibility(View.GONE);
        }else if(type==2)
        {
            mBinding.rvFilterData.setVisibility(View.GONE);
            mBinding.priceFilter.setVisibility(View.GONE);
            mBinding.tvNoData.setVisibility(View.VISIBLE);
        }else if(type==3)
        {
            mBinding.rvFilterData.setVisibility(View.GONE);
            mBinding.priceFilter.setVisibility(View.VISIBLE);
            mBinding.tvNoData.setVisibility(View.GONE);
        }
    }
    private void setRangeSeekbar(int min,int max) {
        mBinding.tvMinMaxRange.setText("₹" + min+"- ₹" + max);
        mBinding.rangeSeekbar.setMinRange(min);
        mBinding.rangeSeekbar.setMax(max);
        minprice = min;
        maxprice = max;
        mBinding.rangeSeekbar.setSeekBarChangeListener(new RangeSeekBar.SeekBarChangeListener() {
            @Override
            public void onStartedSeeking() {
            }
            @Override
            public void onStoppedSeeking() {
            }
            @Override
            public void onValueChanged(int minValue, int maxValue) {
                int min = mBinding.rangeSeekbar.getMinRange()+minValue;
                mBinding.tvMinMaxRange.setText("₹" +min +"- ₹" + maxValue);
                maxprice = (int) maxValue;
                minprice = (int) mBinding.rangeSeekbar.getMinRange()+minValue;
            }
        });
    }
    @Override
    public void clickFilterCategoryInterface(int cateId, int position) {
        categoryId = cateId;
        System.out.println("Filter Category Data>>>"+categoryId);
    }

}