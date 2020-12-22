package com.skdirect.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.skdirect.R;
import com.skdirect.databinding.FragmentHomeBinding;
import com.skdirect.utils.Utils;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        initViews();

        return mBinding.getRoot();
    }

    private void initViews() {

    }


}
