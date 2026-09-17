package com.alxad.sdk.demo.tradplus;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.R;
import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.open.banner.BannerAdListener;
import com.tradplus.ads.open.banner.TPBanner;

public class TradPlusBannerActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "TradPlusBannerActivity";

    private FrameLayout mAdContainerView;
    private View mBnLoad;
    private TextView mTvClearLog;
    private TextView mTvShowLog;

    private TPBanner bannerView;

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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bn_load) {
            loadAd();
        } else if (v.getId() == R.id.tv_clear_log) {
            clearLog();
        }
    }

    private void loadAd() {
        showLogMessage(getString(R.string.loading));
        mBnLoad.setEnabled(false);

        if (bannerView != null) {
            bannerView.onDestroy();
        }

        bannerView = new TPBanner(this);
        bannerView.setAdListener(new BannerAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {
                Log.d(TAG, "onAdLoaded:" + getCurrentThreadName());
                mBnLoad.setEnabled(true);
                showLogMessage("onAdLoaded");
                showLogMessage(getString(R.string.load_success));
                showAd();
            }

            @Override
            public void onAdLoadFailed(TPAdError tpAdError) {
                mBnLoad.setEnabled(true);
                String msg = "errorCode=" + tpAdError.getErrorCode() + ";errorMsg=" + tpAdError.getErrorMsg();
                Log.d(TAG, "onAdLoadFailed:" + msg);
                showLogMessage("onAdLoadFailed");
                showLogMessage(getString(R.string.format_load_failed, msg));
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.d(TAG, "onAdClicked:" + getCurrentThreadName());
                showLogMessage("onAdClicked");
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.d(TAG, "onAdImpression:" + getCurrentThreadName());
                showLogMessage("onAdImpression");
            }

            @Override
            public void onAdShowFailed(TPAdError tpAdError, TPAdInfo tpAdInfo) {
                String msg = "errorCode=" + tpAdError.getErrorCode() + ";errorMsg=" + tpAdError.getErrorMsg();
                Log.d(TAG, "onAdShowFailed:" + msg + getCurrentThreadName());
                showLogMessage("onAdShowFailed:" + msg);
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.d(TAG, "onAdClosed:" + getCurrentThreadName());
                showLogMessage("onAdClosed");
            }

            @Override
            public void onBannerRefreshed() {
                Log.d(TAG, "onBannerRefreshed:" + getCurrentThreadName());
                showLogMessage("onBannerRefreshed");
            }
        });
        bannerView.loadAd(AdConfig.TRAD_PLUS_BANNER_AD);
    }

    private void showAd() {
        mAdContainerView.removeAllViews();
        if (bannerView != null) {
            mAdContainerView.addView(bannerView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bannerView != null) {
            bannerView.onDestroy();
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