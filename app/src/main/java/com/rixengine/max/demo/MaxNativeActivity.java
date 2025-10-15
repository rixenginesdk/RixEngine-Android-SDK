package com.rixengine.max.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder;
import com.rixengine.AppConfig;
import com.rixengine.R;

;


public class MaxNativeActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "MaxNativeActivity";

    private TextView mTvLoad;
    private TextView mTvTip;
    private FrameLayout mAdContainer;
    private long mStartTime;

    private MaxNativeAdLoader mAdLoader;
    private MaxAd mMaxAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_max_native_demo);
        initView();
    }

    private void initView() {
        mTvLoad = (TextView) findViewById(R.id.tv_load);
        mTvTip = (TextView) findViewById(R.id.tv_tip);
        mAdContainer = (FrameLayout) findViewById(R.id.ad_container);

        mTvLoad.setOnClickListener(this);
    }

    private void loadAd() {
        mTvTip.setText("The ad is loading...");
        mTvLoad.setEnabled(false);
        mStartTime = System.currentTimeMillis();

        mAdLoader = new MaxNativeAdLoader(AppConfig.MAX_NATIVE_AD, this);
        mAdLoader.setNativeAdListener(mMaxNativeAdListener);
//        mAdLoader.loadAd();
        mAdLoader.loadAd(createNativeAdView());//自渲染
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_load) {
            loadAd();
        }
    }

    private MaxNativeAdListener mMaxNativeAdListener = new MaxNativeAdListener() {
        @Override
        public void onNativeAdLoaded(@Nullable MaxNativeAdView maxNativeAdView, MaxAd maxAd) {
            Log.d(TAG, "onNativeAdLoaded");
            mTvLoad.setEnabled(true);
            mTvTip.setText("Native ad loads success--Consume time--" + (System.currentTimeMillis() - mStartTime) / 1000 + "-秒");

            if (mMaxAd != null) {
                mAdLoader.destroy(mMaxAd);
            }
            mMaxAd = maxAd;

            if (maxNativeAdView != null) {
                mAdContainer.removeAllViews();
                mAdContainer.addView(maxNativeAdView);
            } else {
                Log.d(TAG, "maxNativeAdView is empty");
            }
        }

        @Override
        public void onNativeAdLoadFailed(String s, MaxError maxError) {
            Log.d(TAG, "onNativeAdLoadFailed:" + s + ";" + ";" + maxError.getCode() + ";" + maxError.getMessage());
            mTvLoad.setEnabled(true);
            mTvTip.setText("InterstitialAd ad loads fail--Consume time--" + (System.currentTimeMillis() - mStartTime) / 1000 + "-秒\r\n失败原因:" + maxError.getMessage());
        }

        @Override
        public void onNativeAdClicked(MaxAd maxAd) {
            Log.d(TAG, "onNativeAdClicked");
        }
    };

    private MaxNativeAdView createNativeAdView() {
        MaxNativeAdViewBinder binder = new MaxNativeAdViewBinder.Builder(R.layout.max_native_custom_ad_view)
                .setTitleTextViewId(R.id.tv_ad_title)
                .setBodyTextViewId(R.id.tv_ad_desc)
                .setAdvertiserTextViewId(R.id.ad_advertiser)
                .setIconImageViewId(R.id.iv_ad_icon)
                .setMediaContentViewGroupId(R.id.media_view_container)
                .setCallToActionButtonId(R.id.cta_button)
                .build();
        return new MaxNativeAdView(binder, this);
    }

    @Override
    protected void onDestroy() {
        if (mAdLoader != null) {
            if (mMaxAd != null) {
                mAdLoader.destroy(mMaxAd);
            }
            mAdLoader.destroy();
        }
        super.onDestroy();
    }
}