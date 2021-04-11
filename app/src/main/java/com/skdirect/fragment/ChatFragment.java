package com.skdirect.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.skdirect.R;
import com.skdirect.activity.MainActivity;
import com.skdirect.databinding.FragmentChatBinding;
import com.skdirect.databinding.FragmentHomeBinding;
import com.skdirect.utils.DBHelper;
import com.skdirect.utils.MyApplication;
import com.skdirect.viewmodel.HomeViewModel;

import org.jetbrains.annotations.NotNull;

public class ChatFragment extends Fragment {
    private FragmentChatBinding mBinding;
    private MainActivity activity;
    public DBHelper dbHelper;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false);
        initViews();
        return mBinding.getRoot();
    }
    private void initViews() {
        dbHelper = MyApplication.getInstance().dbHelper;
        mBinding.toolbarTittle.tvTittle.setText(dbHelper.getString(R.string.chat));
        mBinding.toolbarTittle.ivBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });
        mBinding.tvCmngSoon.setText(dbHelper.getString(R.string.coming_soon));

    }
}
