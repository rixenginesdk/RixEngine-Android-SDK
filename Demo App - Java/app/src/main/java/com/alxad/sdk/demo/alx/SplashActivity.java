package com.alxad.sdk.demo.alx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.MainActivity;
import com.alxad.sdk.demo.R;
import com.rixengine.api.AlxAdParam;
import com.rixengine.api.AlxSplashAd;
import com.rixengine.api.AlxSplashAdListener;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends BaseActivity {
    private final String TAG = "AlxSplashActivity";

    //[ZH] 开屏广告加载的超时时间5s
    //[EN] Splash Ad Load Timeout 5s
    private final int LOAD_AD_TIMEOUT = 5 * 1000;

    //[ZH] 控制开屏广告点击跳转
    //[EN] Control the click-through of in-screen advertisements
    private boolean canJump = false;
    private AlxSplashAd mSlashAd;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mActivity = this;
        loadAd();
    }

    private void loadAd() {
        initSplashAd();
    }

    private void initSplashAd() {
        //[ZH] 初始化广告位。仅调用一次。
        //[EN] Initialize the ad spot. Only call once.
        Map<String, String> userExtras = new HashMap<>();
        userExtras.put("bid_floor", "1.5");
        AlxAdParam.Builder builder = new AlxAdParam.Builder().setUserExtras(userExtras);

        mSlashAd = new AlxSplashAd();
        Log.d(TAG, "ad start load");
        mSlashAd.load(this, AdConfig.ALX_SPLASH_BANNER_AD_ID, builder.build(), new AlxSplashAdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded: | price：" + mSlashAd.getPrice());
                mSlashAd.show(mActivity);
                mSlashAd.reportChargingUrl();
                mSlashAd.reportBiddingUrl();
            }

            @Override
            public void onAdLoadFail(int errorCode, String errorMsg) {
                String msg = "errorCode=" + errorCode + ";errorMsg=" + errorMsg;
                Log.e(TAG, "onAdLoadFail:" + msg);
                goToMainActivity();
            }

            @Override
            public void onAdShow() {
                Log.d(TAG, "onAdShow");
//                mIvWelcome.setVisibility(View.GONE);
            }

            @Override
            public void onAdClicked() {
                Log.d(TAG, "onAdClick");
//                canJump = true;
            }

            @Override
            public void onAdClose() {
                Log.d(TAG, "onAdClose");
                Toast.makeText(mActivity, "onAdClose be called", Toast.LENGTH_SHORT).show();
                goToMainActivity();
            }

            @Override
            public void onAdVideoStart() {
                Log.d(TAG, "onAdVideoStart");
            }

            @Override
            public void onAdVideoEnd() {
                Log.d(TAG, "onAdVideoEnd");
            }

            @Override
            public void onAdVideoError(int errorCode, String errorMsg) {
                String msg = "errorCode=" + errorCode + ";errorMsg=" + errorMsg;
                Log.d(TAG, "onAdVideoError:" + msg);
            }
        }, LOAD_AD_TIMEOUT);
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