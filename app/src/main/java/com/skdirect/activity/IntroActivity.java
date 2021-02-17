package com.skdirect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AFInAppEventType;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.attribution.AppsFlyerRequestListener;
import com.onesignal.OneSignal;
import com.skdirect.R;
import com.skdirect.adapter.IntroAdapter;
import com.skdirect.databinding.ActivityIntroBinding;
import com.skdirect.utils.IntroPageTransformer;
import com.skdirect.utils.SharePrefs;
import com.skdirect.utils.Utils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static com.appsflyer.AppsFlyerLibCore.LOG_TAG;


public class IntroActivity extends AppCompatActivity {
    ActivityIntroBinding mBinding;
    IntroActivity activity;

    private int dotsCount=3;
    private ImageView[] dots;
    String url = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_intro);
        activity = this;
        initViews();

    }

    private void initViews() {

        mBinding.viewpager.setAdapter(new IntroAdapter(getSupportFragmentManager()));
       // mBinding.viewpager.setPageTransformer(false, new IntroPageTransformer());
        drawPageSelectionIndicators(0);

        Utils.logAppsFlayerEventApp(getApplicationContext(),"IntroScreen", "Introduction Screen 1");

        mBinding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                drawPageSelectionIndicators(position);
                if (position==2){
                    mBinding.RLBottomLayout.setVisibility(View.VISIBLE);
                    mBinding.RLBottomLayoutNext.setVisibility(View.GONE);
                }else {
                    mBinding.RLBottomLayout.setVisibility(View.GONE);
                    mBinding.RLBottomLayoutNext.setVisibility(View.VISIBLE);
                }
                Utils.logAppsFlayerEventApp(getApplicationContext(),"IntroScreen", "Introduction Screen "+(position+1));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        mBinding.tvBuyer.setOnClickListener(view -> {
            OneSignal.sendTag("user_type","Buyer");
            Utils.logAppsFlayerEventApp(getApplicationContext(),"IntroScreen", "Introduction Screen Buyer Button Click");
            SharePrefs.getInstance(activity).putBoolean(SharePrefs.IS_SELLER, false);
            SharePrefs.getInstance(activity).putBoolean(SharePrefs.IS_SHOW_INTRO, true);
            Intent i = new Intent(activity, MainActivity.class);
            startActivity(i);
            finish();
        });
        mBinding.tvSeller.setOnClickListener(view -> {
            OneSignal.sendTag("user_type","Seller");
            Utils.logAppsFlayerEventApp(getApplicationContext(),"IntroScreen", "Introduction Screen Seller Button Click");
            SharePrefs.getInstance(activity).putBoolean(SharePrefs.IS_SELLER, true);
            SharePrefs.getInstance(activity).putBoolean(SharePrefs.IS_SHOW_INTRO, true);
            Intent i = new Intent(activity, MainActivity.class);
            startActivity(i);
            finish();
        });
        mBinding.tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = mBinding.viewpager.getCurrentItem();
                if (pos==2){

                }else {
                    mBinding.viewpager.setCurrentItem(pos + 1);
                }
            }
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


    /*new Thread(new Runnable(){
        @Override
        public void run() {
            try {
                sendNotification();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }).start();
    public  void sendNotification(){
        try {
            String jsonResponse;

            URL url = new URL("https://onesignal.com/api/v1/notifications");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);

            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Authorization", "Basic MmQ4ODYzOTctM2JlNy00ODEzLTkyM2QtMjU1NmFkMjk5NzI3");
            con.setRequestMethod("POST");

           *//* String strJsonBody = "{"
                    +   "\"app_id\": \"eda68699-028d-403f-8ef8-cd7306db0a09\","
                    +   "\"included_segments\": [\"Subscribed Users\"],"
                    +   "\"data\": {\"foo\": \"bar\"},"
                    +   "\"contents\": {\"en\": \"English Message\"}"
                    + "}";*//*

            String strJsonBody = "{"
                    +   "\"app_id\": \"eda68699-028d-403f-8ef8-cd7306db0a09\","
                    +   "\"include_player_ids\": [\"b2704957-6577-4110-a06e-a3cd50e9a750\"],"
                    +   "\"data\": {\"foo\": \"bar\"},"
                    +   "\"contents\": {\"en\": \"English Message\"}"
                    + "}";

            System.out.println("strJsonBody:\n" + strJsonBody);

            byte[] sendBytes = strJsonBody.getBytes("UTF-8");
            con.setFixedLengthStreamingMode(sendBytes.length);

            OutputStream outputStream = con.getOutputStream();
            outputStream.write(sendBytes);

            int httpResponse = con.getResponseCode();
            System.out.println("httpResponse: " + httpResponse);

            if (  httpResponse >= HttpURLConnection.HTTP_OK
                    && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            }
            else {
                Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
            }
            System.out.println("jsonResponse:\n" + jsonResponse);

        } catch(Throwable t) {
            t.printStackTrace();
        }
    }*/
}
