package com.alxad.sdk.demo.max;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.R;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder;


public class MaxNativeActivity extends BaseActivity implements View.OnClickListener {
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
        setContentView(R.layout.activity_max_native);
        initView();
    }

    private void initView() {
        mTvLoad = (TextView) findViewById(R.id.tv_load);
        mTvTip = (TextView) findViewById(R.id.tv_tip);
        mAdContainer = (FrameLayout) findViewById(R.id.ad_container);

        mTvLoad.setOnClickListener(this);
    }

    private void loadAd() {
        mTvTip.setText(R.string.loading);
        mTvLoad.setEnabled(false);
        mStartTime = System.currentTimeMillis();

        mAdLoader = new MaxNativeAdLoader(AdConfig.MAX_NATIVE_AD, this);
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
            Log.d(TAG, "onNativeAdLoaded |ecpm:" + maxAd.getRevenue());
            mTvLoad.setEnabled(true);
            mTvTip.setText(getString(R.string.load_success) + "| ecpm:" + maxAd.getRevenue());

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
            mTvTip.setText(getString(R.string.format_load_failed, maxError.getMessage()));
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