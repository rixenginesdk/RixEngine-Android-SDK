package com.alxad.sdk.demo.topon;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.R;
import com.secmtp.sdk.core.api.AdError;
import com.secmtp.sdk.core.api.ATAdInfo;
import com.secmtp.sdk.splashad.api.ATSplashAd;
import com.secmtp.sdk.splashad.api.ATSplashAdExtraInfo;
import com.secmtp.sdk.splashad.api.ATSplashAdListener;

public class TopOnSplashActivity extends BaseActivity {
    private static final String TAG = "TopOnSplashActivity";

    private ATSplashAd mAD;
    private FrameLayout mAdContainer;

    //控制开屏广告点击跳转
    private boolean canJump = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topon_splash);
        initView();
        loadAd();
    }

    private void initView() {
        mAdContainer = (FrameLayout) findViewById(R.id.ad_container);
    }

    private void loadAd() {
        mAD = new ATSplashAd(this, AdConfig.TOPON_SPLASH_ID, new ATSplashAdListener() {
            @Override
            public void onAdLoaded(boolean b) {
                Log.d(TAG, "onAdLoaded:" + getCurrentThreadName());
                if (mAD.isAdReady()) {
                    mAD.show(TopOnSplashActivity.this, mAdContainer);
                }
            }

            @Override
            public void onAdLoadTimeout() {
                Log.d(TAG, "onAdLoadTimeout:" + getCurrentThreadName());
            }

            @Override
            public void onNoAdError(AdError adError) {
                Log.d(TAG, "onNoAdError:" + adError.getCode() + ";" + adError.getDesc() + "=" + getCurrentThreadName());
                goToMainActivity();
            }

            @Override
            public void onAdShow(ATAdInfo atAdInfo) {
                Log.d(TAG, "onAdShow:" + getCurrentThreadName());

            }

            @Override
            public void onAdClick(ATAdInfo atAdInfo) {
                Log.d(TAG, "onAdClick:" + getCurrentThreadName());
                canJump = true;
            }

            @Override
            public void onAdDismiss(ATAdInfo atAdInfo, ATSplashAdExtraInfo atSplashAdExtraInfo) {
                Log.d(TAG, "onAdDismiss:" + getCurrentThreadName());
                goToMainActivity();
            }

        });
        mAD.loadAd();
    }

    private void goToMainActivity() {
        this.startActivity(new Intent(this, TopOnDemoListActivity.class));
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
        if (mAD != null) {
            mAD.onDestory();
        }
    }
    
}