package com.alxad.sdk.demo.alx;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.rixengine.api.AlxSplashAd;
import com.rixengine.api.AlxSplashAdListener;
import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.MainActivity;
import com.alxad.sdk.demo.R;

public class SplashActivity extends BaseActivity {
    private final String TAG = "AlxSplashActivity";
    private final int AD_TIMEOUT = 5 * 1000;//开屏广告加载的超时时间5s

    private FrameLayout mAdContainer;
    private ImageView mIvWelcome;

    //控制开屏广告点击跳转
    private boolean canJump = false;
    private AlxSplashAd mSlashAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
        loadAd();
    }

    private void initView() {
        mAdContainer = (FrameLayout) findViewById(R.id.ad_container);
        mIvWelcome = (ImageView) findViewById(R.id.iv_welcome);
    }

    private void loadAd() {
        initSplashAd();
    }

    private void initSplashAd() {
        //初始化广告位。仅调用一次。
        mSlashAd = new AlxSplashAd(this, AdConfig.ALX_SPLASH_AD_ID);
        Log.d(TAG, "ad start load");
        mSlashAd.load(new AlxSplashAdListener() {
            @Override
            public void onAdLoadSuccess() {
                Log.d(TAG, "onAdLoadSuccess: | 单价：" + mSlashAd.getPrice());
                mSlashAd.showAd(mAdContainer);
                mSlashAd.reportChargingUrl();
                mSlashAd.reportBiddingUrl();
            }

            @Override
            public void onAdLoadFail(int errorCode, String errorMsg) {
                Log.e(TAG, "onAdLoadFail:" + errorCode + "--" + errorMsg);
                goToMainActivity();
            }

            @Override
            public void onAdShow() {
                Log.d(TAG, "onAdShow");
                mIvWelcome.setVisibility(View.GONE);
            }

            @Override
            public void onAdClick() {
                Log.d(TAG, "onAdClick");
                canJump = true;
            }

            @Override
            public void onAdDismissed() {
                Log.d(TAG, "onAdDismissed");
                Toast.makeText(SplashActivity.this, "onAdDismissed be called", Toast.LENGTH_SHORT).show();
                goToMainActivity();
            }
        }, AD_TIMEOUT);
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
        if (mSlashAd != null) {
            mSlashAd.destroy();
        }
    }

    private void goToMainActivity() {
        this.startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }

}