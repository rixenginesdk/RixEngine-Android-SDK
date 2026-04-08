package com.alxad.sdk.demo.max;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.R;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;

public class MaxBannerActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "MaxBannerActivity";

    private FrameLayout mAdContainerView;
    private View mBnLoad;
    private TextView mTvTip;

    private MaxAdView mAdView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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

        mAdView = new MaxAdView(AdConfig.MAX_BANNER_AD, this);
        mAdView.setListener(maxAdViewAdListener);
        mAdView.stopAutoRefresh();
        mAdView.loadAd();
    }

    private MaxAdViewAdListener maxAdViewAdListener = new MaxAdViewAdListener() {

        @Override
        public void onAdLoaded(MaxAd ad) {
            Log.d(TAG, "onAdLoaded ecpm:" + ad.getRevenue());
            mBnLoad.setEnabled(true);
            mTvTip.setText(getString(R.string.load_success) + "|ecpm:" + ad.getRevenue());
            showAd();
        }

        @Override
        public void onAdLoadFailed(String adUnitId, MaxError error) {
            String msg = error.getCode() + ":" + error.getMessage();
            Log.d(TAG, "onAdLoadFailed:" + msg);
            mBnLoad.setEnabled(true);
            mTvTip.setText(getString(R.string.format_load_failed, msg));
        }

        @Override
        public void onAdExpanded(MaxAd ad) {
            Log.d(TAG, "onAdExpanded");
        }

        @Override
        public void onAdCollapsed(MaxAd ad) {
            Log.d(TAG, "onAdCollapsed");
        }

        @Override
        public void onAdDisplayed(MaxAd ad) {
            Log.d(TAG, "onAdDisplayed");
        }

        @Override
        public void onAdHidden(MaxAd ad) {
            Log.d(TAG, "onAdHidden");
        }

        @Override
        public void onAdClicked(MaxAd ad) {
            Log.d(TAG, "onAdClicked");
        }

        @Override
        public void onAdDisplayFailed(MaxAd ad, MaxError error) {
            Log.d(TAG, "onAdDisplayFailed:" + error.getCode() + ";" + error.getMessage());
        }
    };

    private void showAd() {
        mAdContainerView.removeAllViews();
        if (mAdView != null) {
            mAdView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(this, 50)));
            mAdContainerView.addView(mAdView);
        }
    }

    public static int dip2px(Context context, float dipValue) {
        if (context == null) {
            return 0;
        }
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float scale = metrics.density;
        return (int) (dipValue * scale + 0.5f);
    }
}
