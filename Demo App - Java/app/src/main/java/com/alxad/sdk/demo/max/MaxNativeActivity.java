package com.alxad.sdk.demo.max;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder;
import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.R;


public class MaxNativeActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "MaxNativeActivity";

    private View mBnLoad;
    private TextView mTvClearLog;
    private TextView mTvShowLog;
    private FrameLayout mAdContainerView;
    private long mStartTime;

    private MaxNativeAdLoader mAdLoader;
    private MaxAd mMaxAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_ads);
        setActionBar();
        initView();
    }

    private void initView() {
        mAdContainerView = (FrameLayout) findViewById(R.id.ad_container);
        mTvClearLog = (TextView) findViewById(R.id.tv_clear_log);
        mTvShowLog = (TextView) findViewById(R.id.tv_show_log);
        mBnLoad = findViewById(R.id.bn_load);

        mTvShowLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTvClearLog.setOnClickListener(this);
        mBnLoad.setOnClickListener(this);
    }

    private void loadAd() {
        showLogMessage(getString(R.string.loading));
        mBnLoad.setEnabled(false);
        mStartTime = System.currentTimeMillis();

        mAdLoader = new MaxNativeAdLoader(AdConfig.MAX_NATIVE_AD);
        mAdLoader.setNativeAdListener(mMaxNativeAdListener);
//        mAdLoader.loadAd();
        mAdLoader.loadAd(createNativeAdView());//自渲染
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bn_load) {
            loadAd();
        } else if (v.getId() == R.id.tv_clear_log) {
            clearLog();
        }
    }

    private final MaxNativeAdListener mMaxNativeAdListener = new MaxNativeAdListener() {
        @Override
        public void onNativeAdLoaded(@Nullable MaxNativeAdView maxNativeAdView, MaxAd maxAd) {
            double revenue = maxAd.getRevenue() * 1000;
            String message = " NetworkName:" + maxAd.getNetworkName() + "; ecpm:" + revenue;
            Log.d(TAG, "onNativeAdLoaded |" + message);
            showLogMessage("onNativeAdLoaded");
            showLogMessage(getString(R.string.load_success) + message);
            mBnLoad.setEnabled(true);

            if (mMaxAd != null) {
                mAdLoader.destroy(mMaxAd);
            }
            mMaxAd = maxAd;

            if (maxNativeAdView != null) {
                mAdContainerView.removeAllViews();
                mAdContainerView.addView(maxNativeAdView);
            } else {
                Log.d(TAG, "maxNativeAdView is empty");
            }
        }

        @Override
        public void onNativeAdLoadFailed(String s, MaxError error) {
            String msg = "errorCode=" + error.getCode() + ";errorMsg=" + error.getMessage();
            Log.d(TAG, "onNativeAdLoadFailed:" + msg);
            showLogMessage("onNativeAdLoadFailed");
            showLogMessage(getString(R.string.format_load_failed, msg));

            mBnLoad.setEnabled(true);
        }

        @Override
        public void onNativeAdClicked(MaxAd maxAd) {
            Log.d(TAG, "onNativeAdClicked");
            showLogMessage("onNativeAdClicked");
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
        super.onDestroy();
        if (mAdLoader != null) {
            if (mMaxAd != null) {
                mAdLoader.destroy(mMaxAd);
            }
            mAdLoader.destroy();
        }
    }

    private void clearLog() {
        mTvShowLog.setText("");
    }

    private void showLogMessage(String msg) {
        mTvShowLog.append(msg);
        mTvShowLog.append("\r\n");
    }
}