package com.rixengine.tradplus.demo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.rixengine.AppConfig;
import com.rixengine.GroupActivity;
import com.rixengine.R;
import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.base.bean.TPBaseAd;
import com.tradplus.ads.open.splash.SplashAdListener;
import com.tradplus.ads.open.splash.TPSplash;

public class TradPlusSplashDemoActivity extends AppCompatActivity {
    private static final String TAG = "TradPlusSplashDemo";

    private TPSplash mAD;
    private FrameLayout mAdContainer;

    //控制开屏广告点击跳转
    private boolean canJump = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tradplus_splash_demo);
        initView();
        loadAd();
    }

    private void initView() {
        mAdContainer = (FrameLayout) findViewById(R.id.ad_container);
    }

    private void loadAd() {
        mAD = new TPSplash(this, AppConfig.TRAD_PLUS_SPLASH_AD);
        mAD.setAdListener(new SplashAdListener(){
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo, TPBaseAd tpBaseAd) {
                Log.d(TAG, "onAdLoaded:" + getThreadName());
                if (mAD.isReady()) {
                    mAD.showAd(mAdContainer);
                }
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.d(TAG, "onAdClicked:" + getThreadName());
                canJump = true;
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.d(TAG, "onAdImpression:" + getThreadName());
            }

            @Override
            public void onAdLoadFailed(TPAdError tpAdError) {
                Log.d(TAG, "onAdLoadFailed:" + tpAdError.getErrorCode() + ";" + tpAdError.getErrorMsg() + "=" + getThreadName());
                goToMainActivity();
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.d(TAG, "onAdClosed:" + getThreadName());
                goToMainActivity();
            }

            @Override
            public void onZoomOutStart(TPAdInfo tpAdInfo) {
                Log.d(TAG, "onZoomOutStart:" + getThreadName());
            }

            @Override
            public void onZoomOutEnd(TPAdInfo tpAdInfo) {
                Log.d(TAG, "onZoomOutEnd:" + getThreadName());
            }
        });
        mAD.loadAd(null);
    }

    private void goToMainActivity() {
        this.startActivity(new Intent(this, GroupActivity.class));
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canJump) {
            goToMainActivity();
        }
//        canJump = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
//        canJump = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private String getThreadName() {
        return Thread.currentThread().getName();
    }

}