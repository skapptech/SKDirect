package com.skdirect.activity;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.bumptech.glide.Glide;
import com.skdirect.R;
import com.skdirect.databinding.ActivityFullNotificationBinding;

public class FullScreenNotificationActivity extends AppCompatActivity {
    private ActivityFullNotificationBinding mBinding;
    String intentImageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_full_notification);
        initViews();

    }

    private void initViews() {
        intentImageUrl = getIntent().getStringExtra("image");
        Glide.with(getApplicationContext()).load(intentImageUrl)
                .into(mBinding.imNotificationImage);
    }
}