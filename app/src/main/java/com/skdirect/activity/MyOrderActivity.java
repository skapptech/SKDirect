package com.skdirect.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.skdirect.R;
import com.skdirect.adapter.DynamicTabAdapter;
import com.skdirect.api.CommonClassForAPI;
import com.skdirect.databinding.ActivityMyOrderdBinding;
import com.skdirect.fragment.AllOrderFragment;
import com.skdirect.fragment.HomeFragment;
import com.skdirect.model.OrderStatusDetails;
import com.skdirect.model.OrderStatusMainModel;
import com.skdirect.utils.DBHelper;
import com.skdirect.utils.MyApplication;
import com.skdirect.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import io.reactivex.observers.DisposableObserver;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class MyOrderActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityMyOrderdBinding mBinding;
    public DBHelper dbHelper;
    private CommonClassForAPI commonClassForAPI;
    private String invoiceNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_orderd);
        commonClassForAPI = CommonClassForAPI.getInstance(this);
        getIntentData();
        initView();
        openFragment(new AllOrderFragment(invoiceNumber));
    }

    private void getIntentData() {
        invoiceNumber = getIntent().getStringExtra("InvoiceNumber");


    }
    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fregment_my_order, fragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }

    private void initView() {
        dbHelper = MyApplication.getInstance().dbHelper;
        mBinding.toolbarTittle.tvTittle.setText(dbHelper.getString(R.string.my_order));
        mBinding.toolbarTittle.ivBackPress.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_back_press) {
            onBackPressed();
        }
    }

}
