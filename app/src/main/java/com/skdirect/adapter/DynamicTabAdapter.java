package com.skdirect.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.skdirect.fragment.AllOrderFragment;

import java.util.ArrayList;

public class DynamicTabAdapter extends FragmentPagerAdapter {
    private ArrayList<String> statusList;
    private String invoiceNumber;
    public DynamicTabAdapter(FragmentManager fm, int behavior, ArrayList<String> statusList,String invoice) {
        super(fm, behavior);
        this.statusList = statusList;
        this.invoiceNumber = invoice;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return new AllOrderFragment(/*statusList.get(position),*/invoiceNumber);
    }
    @Override
    public int getCount() {
        return statusList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return statusList.get(position);
    }
}
