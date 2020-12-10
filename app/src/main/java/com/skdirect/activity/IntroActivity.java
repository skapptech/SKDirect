package com.skdirect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.skdirect.R;
import com.skdirect.adapter.IntroAdapter;
import com.skdirect.databinding.ActivityIntroBinding;
import com.skdirect.utils.IntroPageTransformer;
import com.skdirect.utils.SharePrefs;


public class IntroActivity extends AppCompatActivity {
    ActivityIntroBinding mBinding;
    IntroActivity activity;

    private int dotsCount=5;
    private ImageView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_intro);
        activity = this;
        initViews();

    }

    private void initViews() {
        mBinding.viewpager.setAdapter(new IntroAdapter(getSupportFragmentManager()));
        mBinding.viewpager.setPageTransformer(false, new IntroPageTransformer());
        drawPageSelectionIndicators(0);
        mBinding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                drawPageSelectionIndicators(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        mBinding.tvBuyer.setOnClickListener(view -> {
            SharePrefs.getInstance(activity).putBoolean(SharePrefs.IS_SELLER, false);
            SharePrefs.getInstance(activity).putBoolean(SharePrefs.IS_SHOW_INTRO, true);
            Intent i = new Intent(activity, MainActivity.class);
            startActivity(i);
        });
        mBinding.tvSeller.setOnClickListener(view -> {
            SharePrefs.getInstance(activity).putBoolean(SharePrefs.IS_SELLER, true);
            SharePrefs.getInstance(activity).putBoolean(SharePrefs.IS_SHOW_INTRO, true);
            Intent i = new Intent(activity, MainActivity.class);
            startActivity(i);
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private void drawPageSelectionIndicators(int mPosition){
        if(mBinding.viewPagerCountDots!=null) {
            mBinding.viewPagerCountDots.removeAllViews();
        }
        dots = new ImageView[dotsCount];
        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(activity);
            if(i==mPosition)
                dots[i].setImageDrawable(getResources().getDrawable(R.drawable.tab_indicator_selected));
            else
                dots[i].setImageDrawable(getResources().getDrawable(R.drawable.tab_indicator_default));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);
            mBinding.viewPagerCountDots.addView(dots[i], params);
        }
    }
}
