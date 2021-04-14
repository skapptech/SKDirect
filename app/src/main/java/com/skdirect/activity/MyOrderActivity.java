package com.skdirect.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.skdirect.R;
import com.skdirect.adapter.DynamicTabAdapter;
import com.skdirect.api.CommonClassForAPI;
import com.skdirect.databinding.ActivityMyOrderBinding;
import com.skdirect.utils.DBHelper;
import com.skdirect.utils.MyApplication;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;

import io.reactivex.observers.DisposableObserver;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class MyOrderActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityMyOrderBinding mBinding;
    public DBHelper dbHelper;
    private CommonClassForAPI commonClassForAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_order);
        commonClassForAPI = CommonClassForAPI.getInstance(this);
        initView();

    }

    private void initView() {
        dbHelper = MyApplication.getInstance().dbHelper;
        mBinding.toolbarTittle.tvTittle.setText(dbHelper.getString(R.string.my_order));
        mBinding.toolbarTittle.ivBackPress.setOnClickListener(this);
        //mBinding.tabLayout.setupWithViewPager(mBinding.viewPager);
        if (commonClassForAPI!=null) {
            commonClassForAPI.getAllOrderStatusList(observer);
        }
    }

    private final DisposableObserver<JsonObject> observer = new DisposableObserver<JsonObject>() {
        @Override
        public void onNext(@NotNull JsonObject jsonObject) {
            try {
                if (jsonObject != null) {
                    if (jsonObject.get("IsSuccess").getAsBoolean()) {
                        JsonArray jsonArray = jsonObject.get("ResultItem").getAsJsonArray();
                        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
                        ArrayList<String> list = new Gson().fromJson(jsonArray, listType);
                        setupViewPager(mBinding.viewPager,list);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
        }

        @Override
        public void onComplete() {

        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_press:
                onBackPressed();
                break;
        }

    }

    private void setupViewPager(ViewPager viewPager, ArrayList<String> statusList) {
        DynamicTabAdapter adapter = new DynamicTabAdapter(MyOrderActivity.this.getSupportFragmentManager(),
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, statusList);
        viewPager.setAdapter(adapter);
        mBinding.tabLayout.setupWithViewPager(viewPager);
    }

   /* private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AllOrderFragment(0), dbHelper.getString(R.string.order_tab_all));
        adapter.addFragment(new AllOrderFragment(1), dbHelper.getString(R.string.order_tab_pending));
        adapter.addFragment(new AllOrderFragment(2), dbHelper.getString(R.string.order_tab_accept));
        adapter.addFragment(new AllOrderFragment(3), dbHelper.getString(R.string.order_tab_shiped));
        adapter.addFragment(new AllOrderFragment(4), dbHelper.getString(R.string.order_tab_delivered));
        adapter.addFragment(new AllOrderFragment(6), dbHelper.getString(R.string.order_tab_cancelled));
        viewPager.setAdapter(adapter);
    }*/
}
