package com.alxad.sdk.demo.tradplus;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.R;
import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.open.banner.BannerAdListener;
import com.tradplus.ads.open.banner.TPBanner;

public class TradPlusBannerActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "TradPlusBannerDemo";

    private FrameLayout mAdContainerView;
    private View mBnLoad;
    private TextView mTvTip;

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
        mTvTip = findViewById(R.id.tv_tip);
        mBnLoad = findViewById(R.id.bn_load);
        mBnLoad.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bn_load) {
            loadAd();
        }
    }

    private void loadAd() {
        mTvTip.setText(R.string.loading);
        mBnLoad.setEnabled(false);

        if (bannerView != null) {
            bannerView.onDestroy();
        }

        bannerView = new TPBanner(this);
        bannerView.setAdListener(new BannerAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {
                Log.d(TAG, "onAdLoaded:" + getThreadName());
                mBnLoad.setEnabled(true);
                mTvTip.setText(R.string.load_success);
                showAd();
            }

            @Override
            public void onAdLoadFailed(TPAdError tpAdError) {
                mBnLoad.setEnabled(true);
                String msg = tpAdError.getErrorCode() + ":" + tpAdError.getErrorMsg();
                Log.d(TAG, "onAdLoadFailed:" + msg);
                mTvTip.setText(getString(R.string.format_load_failed, msg));
                Toast.makeText(getBaseContext(), getString(R.string.load_failed), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.d(TAG, "onAdClicked:" + getThreadName());
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.d(TAG, "onAdImpression:" + getThreadName());
            }

            @Override
            public void onAdShowFailed(TPAdError tpAdError, TPAdInfo tpAdInfo) {
                Log.d(TAG, "onAdShowFailed:" + tpAdError.getErrorCode() + "-" + tpAdError.getErrorMsg() + getThreadName());
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.d(TAG, "onAdClosed:" + getThreadName());
            }

            @Override
            public void onBannerRefreshed() {
                Log.d(TAG, "onBannerRefreshed:" + getThreadName());
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

    private String getThreadName() {
        return Thread.currentThread().getName();
    }

}