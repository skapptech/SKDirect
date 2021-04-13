package com.skdirect.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.MenuItem;

import com.skdirect.R;
import com.skdirect.databinding.ActivityWebviewBinding;
import com.skdirect.utils.MyApplication;

public class WebviewActivity extends AppCompatActivity {
    ActivityWebviewBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_webview);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (getIntent().getExtras() != null) {
            if(getIntent().getStringExtra("FunctionName").equalsIgnoreCase("PrivacyPolicy"))
            {
               // mBinding.webView.loadUrl("https://www.google.com/");
                setTitle(MyApplication.getInstance().dbHelper.getString(R.string.webview_title_name_privacy));
            } else
            {
              //  mBinding.webView.loadUrl("https://www.google.com/");
                setTitle(MyApplication.getInstance().dbHelper.getString(R.string.webview_title_name_about_direct));
            }
        }

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}