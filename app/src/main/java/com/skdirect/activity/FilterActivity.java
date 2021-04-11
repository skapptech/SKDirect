package com.skdirect.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.innovattic.rangeseekbar.RangeSeekBar;
import com.skdirect.R;
import com.skdirect.adapter.FilterCategoryAdapter;
import com.skdirect.adapter.FilterTypeAdapter;
import com.skdirect.api.CommonClassForAPI;
import com.skdirect.databinding.ActivityFilterBinding;
import com.skdirect.interfacee.FilterCategoryInterface;
import com.skdirect.interfacee.FilterTypeInterface;
import com.skdirect.model.FilterCategoryDetails;
import com.skdirect.model.MallMainPriceModel;
import com.skdirect.model.PostBrandModel;
import com.skdirect.utils.Constant;
import com.skdirect.utils.DBHelper;
import com.skdirect.utils.MyApplication;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.observers.DisposableObserver;

public class FilterActivity extends AppCompatActivity implements FilterTypeInterface, FilterCategoryInterface {
    ActivityFilterBinding mBinding;
    ArrayList<String> otherList = new ArrayList<>();
    ArrayList<FilterCategoryDetails> filterCateDataList = new ArrayList<>();
    FilterCategoryAdapter filterCategoryAdapter;
    private int maxprice = 10000, minprice = 1;
    private int skipCount = 0;
    private int takeCount = 15;
    private int pastVisiblesItems = 0;
    private int visibleItemCount = 0;
    private int totalItemCount = 0;
    private boolean loading = true;
    public DBHelper dbHelper;
    int categoryId = 0;
    String brand = "";
    ArrayList<String> selectedBrandList = new ArrayList<>();
    ArrayList<Integer> selectedPriceList = new ArrayList<>();
    String discount = "";
    boolean sideTabClick = false;
    private CommonClassForAPI commonClassForAPI;
    LinearLayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_filter);
        dbHelper = MyApplication.getInstance().dbHelper;

        initView();
        setString();
    }

    private void setString() {
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

        FilterTypeAdapter filterTypeAdapter = new FilterTypeAdapter(this, list, this);
        mBinding.rvFilterType.setAdapter(filterTypeAdapter);

        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mBinding.rvFilterData.setLayoutManager(layoutManager);
        filterCategoryAdapter = new FilterCategoryAdapter(this, filterCateDataList, this);
        mBinding.rvFilterData.setAdapter(filterCategoryAdapter);

        mBinding.tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPriceList.add(minprice);
                selectedPriceList.add(maxprice);
                Intent intent = new Intent();
                intent.putExtra(Constant.Category, categoryId);
                intent.putIntegerArrayListExtra(Constant.Price, selectedPriceList);
                intent.putStringArrayListExtra(Constant.Brands, selectedBrandList);
                intent.putExtra(Constant.Discount, discount);
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
        mBinding.tvClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryId = 0;
                brand = "";
                discount = "";
                maxprice = 0;
                minprice = 0;
            }
        });
        // if(sideTabClick) {
        mBinding.rvFilterData.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                    if (loading) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            loading = false;
                            skipCount = skipCount + 10;
                            callBrandList();
                        }
                    }
                }
            }
        });
        //  }
    }

    private void callBrandList() {
        //LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        // mBinding.rvFilterData.setLayoutManager(layoutManager);

        if (Utils.isNetworkAvailable(getApplicationContext())) {
            PostBrandModel postBrandModel = new PostBrandModel("", skipCount, takeCount, categoryId);
            commonClassForAPI.getFilterBrandsListResult(new DisposableObserver<JsonObject>() {
                @Override
                public void onNext(@NotNull JsonObject jsonObject) {
                    if (jsonObject.get("IsSuccess").getAsBoolean()) {
                        ArrayList<FilterCategoryDetails> detailsList = new Gson().fromJson(jsonObject.get("ResultItem").getAsJsonArray().toString(), new TypeToken<List<FilterCategoryDetails>>() {
                        }.getType());
                        if (detailsList.size() > 0) {
                            viewVisibility(1);
                            filterCateDataList.addAll(detailsList);
                            filterCategoryAdapter.setDataAdapter(filterCateDataList, "Brands");
                            // filterCategoryAdapter.notifyDataSetChanged();
                            loading = true;
                        } else {
                            loading = false;
                            viewVisibility(2);
                            mBinding.tvNoData.setText(dbHelper.getString(R.string.no_filter_brands));
                        }
                    } else {
                        loading = false;
                        //viewVisibility(2);
                       // mBinding.tvNoData.setText(dbHelper.getString(R.string.no_filter_brands));
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
            }, postBrandModel);
        } else {
            Utils.setToast(getApplicationContext(), dbHelper.getString(R.string.no_connection));
        }
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
        skipCount = 0;
        switch (position) {
            case 0:
                sideTabClick = false;
                String savedData = SharePrefs.getInstance(this).getString(SharePrefs.FILTER_CATEGORY_LIST);
                if (!savedData.isEmpty()) {
                    try {
                        JSONObject jsonObject = new JSONObject(savedData);
                        ArrayList<FilterCategoryDetails> detailsList = new Gson().fromJson(jsonObject.get("ResultItem").toString(), new TypeToken<List<FilterCategoryDetails>>() {
                        }.getType());
                        if (detailsList.size() > 0) {
                            viewVisibility(1);
                            filterCategoryAdapter.setDataAdapter(detailsList, "Category");
                            //  filterCateDataList.addAll(detailsList);
                            //filterCategoryAdapter.notifyDataSetChanged();
                            SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.FILTER_CATEGORY_LIST, jsonObject.toString());
                        } else {
                            viewVisibility(2);
                            mBinding.tvNoData.setText(dbHelper.getString(R.string.no_filter_categories));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (Utils.isNetworkAvailable(getApplicationContext())) {
                    commonClassForAPI.getFilterCategoryResult(new DisposableObserver<JsonObject>() {
                        @Override
                        public void onNext(@NotNull JsonObject jsonObject) {
                            if (jsonObject.get("IsSuccess").getAsBoolean()) {
                                ArrayList<FilterCategoryDetails> detailsList = new Gson().fromJson(jsonObject.get("ResultItem").getAsJsonArray().toString(), new TypeToken<List<FilterCategoryDetails>>() {
                                }.getType());
                                if (detailsList.size() > 0) {
                                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.FILTER_CATEGORY_LIST, jsonObject.toString());
                                    viewVisibility(1);
                                    // filterCateDataList.addAll(detailsList);
                                    //  filterCategoryAdapter.notifyDataSetChanged();
                                    filterCategoryAdapter.setDataAdapter(detailsList, "Category");
                                } else {
                                    viewVisibility(2);
                                    mBinding.tvNoData.setText(dbHelper.getString(R.string.no_filter_categories));
                                }
                            } else {
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
                sideTabClick = false;
                if (Utils.isNetworkAvailable(getApplicationContext())) {
                    commonClassForAPI.getFilterPriceRangeResult(new DisposableObserver<MallMainPriceModel>() {
                        @Override
                        public void onNext(@NotNull MallMainPriceModel result) {
                            if (result.isSuccess()) {
                                viewVisibility(3);
                                setRangeSeekbar(result.getResultItem().getMin(), result.getResultItem().getMax());
                            } else {
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
                    }, categoryId);
                } else {
                    Utils.setToast(getApplicationContext(), dbHelper.getString(R.string.no_connection));
                }
                break;
            case 2:
                sideTabClick = true;
                callBrandList();

                break;
            case 3:
                sideTabClick = false;
                viewVisibility(1);
                ArrayList<FilterCategoryDetails> discountList = new ArrayList<>();
                discountList.add(new FilterCategoryDetails(0, "Any"));
                discountList.add(new FilterCategoryDetails(0, "10% and above"));
                discountList.add(new FilterCategoryDetails(0, "20% and above"));
                discountList.add(new FilterCategoryDetails(0, "30% and above"));
                discountList.add(new FilterCategoryDetails(0, "40% and above"));
                discountList.add(new FilterCategoryDetails(0, "50% and above"));
                discountList.add(new FilterCategoryDetails(0, "60% and above"));
                discountList.add(new FilterCategoryDetails(0, "70% and above"));
                filterCategoryAdapter.setDataAdapter(discountList, "Discount");
                // filterCategoryAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    private void viewVisibility(int type) {
        if (type == 1) {
            mBinding.rvFilterData.setVisibility(View.VISIBLE);
            mBinding.priceFilter.setVisibility(View.GONE);
            mBinding.tvNoData.setVisibility(View.GONE);
        } else if (type == 2) {
            mBinding.rvFilterData.setVisibility(View.GONE);
            mBinding.priceFilter.setVisibility(View.GONE);
            mBinding.tvNoData.setVisibility(View.VISIBLE);
        } else if (type == 3) {
            mBinding.rvFilterData.setVisibility(View.GONE);
            mBinding.priceFilter.setVisibility(View.VISIBLE);
            mBinding.tvNoData.setVisibility(View.GONE);
        }
    }

    private void setRangeSeekbar(int min, int max) {
        mBinding.tvMinMaxRange.setText("₹" + min + "- ₹" + max);
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
                int min = mBinding.rangeSeekbar.getMinRange() + minValue;
                mBinding.tvMinMaxRange.setText("₹" + min + "- ₹" + maxValue);
                maxprice = (int) maxValue;
                minprice = (int) mBinding.rangeSeekbar.getMinRange() + minValue;
            }
        });
    }

    @Override
    public void clickFilterCategoryInterface(int value, String label, String filterType) {
        categoryId = value;
        System.out.println("category:::" + categoryId);
     /*   if (filterType.equalsIgnoreCase("Brands")) {
            brand = label;
        } else if (filterType.equalsIgnoreCase("Discount")) {
            discount = label;
        } else {

        }*/
    }

    @Override
    public void clickFilterBrandyInterface(int cateId, String label, String position, boolean remove) {
       // brand = label;
        if(remove)
        {
            selectedBrandList.remove(label);
        }else
        {
            selectedBrandList.add(label);
        }


        System.out.println("selectedBrandList::"+selectedBrandList.toString());
    }

    @Override
    public void clickFilterDiscountInterface(int cateId, String label, String position) {
        discount = label;
    }

}