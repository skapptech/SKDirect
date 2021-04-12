package com.skdirect.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.skdirect.R;
import com.skdirect.databinding.ActivityWebviewBinding;

public class WebviewActivity extends AppCompatActivity {
    ActivityWebviewBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_webview);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mBinding.webView.loadUrl("s");
    }
}