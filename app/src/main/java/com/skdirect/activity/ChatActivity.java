package com.skdirect.activity;

import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.skdirect.R;
import com.skdirect.databinding.ActivityChatBinding;
import com.skdirect.utils.DBHelper;
import com.skdirect.utils.MyApplication;
import com.skdirect.viewmodel.NewAddressViewMode;

import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding mBinding;
    public DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        dbHelper = MyApplication.getInstance().dbHelper;
        initView();
    }

    private void initView() {

        dbHelper = MyApplication.getInstance().dbHelper;
        mBinding.toolbarTittle.tvTittle.setText(dbHelper.getString(R.string.chat));
        mBinding.toolbarTittle.ivBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
