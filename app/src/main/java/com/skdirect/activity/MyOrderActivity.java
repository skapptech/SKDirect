package com.skdirect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.skdirect.R;
import com.skdirect.adapter.SectionsPagerAdapter;
import com.skdirect.databinding.ActivityMyOrderBinding;
import com.skdirect.fragment.AllOrderFragment;

public class MyOrderActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityMyOrderBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_order);

        initView();


    }

    private void initView() {
        mBinding.toolbarTittle.tvTittle.setText("My Order");
        mBinding.toolbarTittle.ivBackPress.setOnClickListener(this);
        setupViewPager(mBinding.viewPager);
        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_press:
                onBackPressed();
                break;
        }

    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AllOrderFragment(0), "All");
        adapter.addFragment(new AllOrderFragment(1), "Pending");
        adapter.addFragment(new AllOrderFragment(2), "Accepted");
        adapter.addFragment(new AllOrderFragment(3), "Shipped");
        adapter.addFragment(new AllOrderFragment(4), "Delivered");
        adapter.addFragment(new AllOrderFragment(6), "Cancelled");
        viewPager.setAdapter(adapter);
    }
}
