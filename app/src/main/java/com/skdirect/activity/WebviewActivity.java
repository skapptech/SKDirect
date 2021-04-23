package com.skdirect.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.JsonObject;
import com.skdirect.R;
import com.skdirect.api.CommonClassForAPI;
import com.skdirect.databinding.ActivityWebviewBinding;
import com.skdirect.utils.MyApplication;
import com.skdirect.utils.Utils;

import org.jetbrains.annotations.NotNull;

import io.reactivex.observers.DisposableObserver;

public class WebviewActivity extends AppCompatActivity {
    private ActivityWebviewBinding mBinding;

    private String name = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_webview);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent().getExtras() != null) {
            name = getIntent().getStringExtra("FunctionName");
            if (name.equalsIgnoreCase("PrivacyPolicy")) {
//                String privacy = SharePrefs.getInstance(this).getString(SharePrefs.PRIVACY_POLICY);
//                if (!TextUtils.isNullOrEmpty(privacy)) {
//                    mBinding.webView.loadUrl(privacy);
//                }
                setTitle(MyApplication.getInstance().dbHelper.getString(R.string.webview_title_name_privacy));
                callAPI();
            } else if (name.equalsIgnoreCase("TermsCondition")) {
//                String terms = SharePrefs.getInstance(this).getString(SharePrefs.TERMS_CONDITION);
//                if (!TextUtils.isNullOrEmpty(terms)) {
//                    mBinding.webView.loadUrl(terms);
//                }
                setTitle(MyApplication.getInstance().dbHelper.getString(R.string.terms_and_condition));
                callAPI();
            } else {
//                String aboutApp = SharePrefs.getInstance(this).getString(SharePrefs.ABOUT_APP);
//                if (!TextUtils.isNullOrEmpty(aboutApp)) {
//                    mBinding.webView.loadUrl(aboutApp);
//                }
                setTitle(MyApplication.getInstance().dbHelper.getString(R.string.webview_title_name_about_direct));
                callAPI();
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

    private void callAPI() {
        Utils.showProgressDialog(this);
        CommonClassForAPI.getInstance(this).getOtherInfo(observer);
    }


    private final DisposableObserver<JsonObject> observer = new DisposableObserver<JsonObject>() {
        @Override
        public void onNext(@NotNull JsonObject jsonObject) {
            Utils.hideProgressDialog();
            try {
                String data = "";
                if (name.equalsIgnoreCase("PrivacyPolicy")) {
                    data = jsonObject.get("PrivacyPolicy").getAsString();
                } else if (name.equalsIgnoreCase("AboutApp")) {
                    data = jsonObject.get("About").getAsString();
                } else if (name.equalsIgnoreCase("TermsAndCondition")) {
                    data = jsonObject.get("TermsCondition").getAsString();
                } else {
                    data = jsonObject.get("extra").getAsString();
                }
                mBinding.webView.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
            Utils.hideProgressDialog();
        }

        @Override
        public void onComplete() {
        }
    };
}