package com.rixengine.topon.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.rixengine.AppConfig;
import com.rixengine.R;
import com.thinkup.core.api.TUSDK;


public class TopOnAdDemoListActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_on_ad_demo);
        initView();
        initSdk();
    }

    private void initView() {
        TextView tv_banner = findViewById(R.id.tv_banner);
        TextView tv_video_ad = findViewById(R.id.tv_video_ad);
        TextView tv_interstitial_ad = findViewById(R.id.tv_interstitial_ad);
        TextView tv_native = findViewById(R.id.tv_native);
       // TextView tv_splash = findViewById(R.id.tv_splash);
        tv_banner.setOnClickListener(this);
        tv_video_ad.setOnClickListener(this);
        tv_native.setOnClickListener(this);
        tv_interstitial_ad.setOnClickListener(this);
       // tv_splash.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_banner) {
            startActivity(new Intent(this, TopOnBannerDemoActivity.class));
        } else if (id == R.id.tv_video_ad) {
            startActivity(new Intent(this, TopOnVideoDemoActivity.class));
        } else if (id == R.id.tv_interstitial_ad) {
            startActivity(new Intent(this, TopOnInterstitialDemoActivity.class));
        } else if (id == R.id.tv_native) {
            startActivity(new Intent(this, TopOnNativeDemoActivity.class));
        }
//        else if (id == R.id.tv_splash) {
//            startActivity(new Intent(this, TopOnSplashDemoActivity.class));
//        }
    }
    public void initSdk (){
        TUSDK.init(getApplicationContext(), AppConfig.TOPON_APP_ID, AppConfig.TOPON_KEY);
        TUSDK.setNetworkLogDebug(true);
    }
}