package com.rixengine.topon.demo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.rixengine.AppConfig;
import com.rixengine.GroupActivity;
import com.rixengine.R;
import com.thinkup.core.api.AdError;
import com.thinkup.core.api.TUAdInfo;
import com.thinkup.splashad.api.TUSplashAd;
import com.thinkup.splashad.api.TUSplashAdExtraInfo;
import com.thinkup.splashad.api.TUSplashAdListener;

public class TopOnSplashDemoActivity extends AppCompatActivity {
    private static final String TAG = "TopOnSplashDemoActivity";

    private TUSplashAd mAD;
    private FrameLayout mAdContainer;

    //控制开屏广告点击跳转
    private boolean canJump = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topon_splash_demo);
        initView();
        loadAd();
    }

    private void initView() {
        mAdContainer = (FrameLayout) findViewById(R.id.ad_container);
    }

    private void loadAd() {
        mAD = new TUSplashAd(this, AppConfig.TOPON_SPLASH_PID, new TUSplashAdListener() {
            @Override
            public void onAdLoaded(boolean b) {
                Log.d(TAG, "onAdLoaded:" + getThreadName());
                if (mAD.isAdReady()) {
                    mAD.show(TopOnSplashDemoActivity.this, mAdContainer);
                }
            }

            @Override
            public void onAdLoadTimeout() {

            }

            @Override
            public void onNoAdError(AdError adError) {
                Log.d(TAG, "onNoAdError:" + adError.getCode() + ";" + adError.getDesc() + "=" + getThreadName());
                goToMainActivity();
            }

            @Override
            public void onAdShow(TUAdInfo atAdInfo) {
                Log.d(TAG, "onAdShow:" + getThreadName());

            }

            @Override
            public void onAdClick(TUAdInfo atAdInfo) {
                Log.d(TAG, "onAdClick:" + getThreadName());
                canJump = true;
            }

            @Override
            public void onAdDismiss(TUAdInfo atAdInfo, TUSplashAdExtraInfo atSplashAdExtraInfo) {
                Log.d(TAG, "onAdDismiss:" + getThreadName());
                goToMainActivity();
            }

        });
        mAD.loadAd();
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
        if (mAD != null) {
            mAD.onDestory();
        }
    }

    private String getThreadName() {
        return Thread.currentThread().getName();
    }

}