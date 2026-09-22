package com.alxad.sdk.demo.max;

import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.R;

public class MaxBannerActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "MaxBannerActivity";

    private FrameLayout mAdContainerView;
    private View mBnLoad;
    private TextView mTvClearLog;
    private TextView mTvShowLog;

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

        mAdView = new MaxAdView(AdConfig.MAX_BANNER_AD, this);
        mAdView.setListener(maxAdViewAdListener);
        mAdView.stopAutoRefresh();
        mAdView.loadAd();
    }

    private MaxAdViewAdListener maxAdViewAdListener = new MaxAdViewAdListener() {

        @Override
        public void onAdLoaded(MaxAd ad) {
            double revenue = ad.getRevenue() * 1000;
            String message = " NetworkName:" + ad.getNetworkName() + "; ecpm:" + revenue;
            Log.d(TAG, "onAdLoaded |" + message);
            showLogMessage("onAdLoaded");
            showLogMessage(getString(R.string.load_success) + message);

            mBnLoad.setEnabled(true);
            showAd();
        }

        @Override
        public void onAdLoadFailed(String adUnitId, MaxError error) {
            String msg = "errorCode=" + error.getCode() + ";errorMsg=" + error.getMessage();
            Log.d(TAG, "onAdLoadFailed:" + msg);
            showLogMessage("onAdLoadFailed");
            showLogMessage(getString(R.string.format_load_failed, msg));

            mBnLoad.setEnabled(true);
        }

        @Override
        public void onAdExpanded(MaxAd ad) {
            Log.d(TAG, "onAdExpanded");
            showLogMessage("onAdExpanded");
        }

        @Override
        public void onAdCollapsed(MaxAd ad) {
            Log.d(TAG, "onAdCollapsed");
            showLogMessage("onAdCollapsed");
        }

        @Override
        public void onAdDisplayed(MaxAd ad) {
            Log.d(TAG, "onAdDisplayed");
            showLogMessage("onAdDisplayed");
        }

        @Override
        public void onAdHidden(MaxAd ad) {
            Log.d(TAG, "onAdHidden");
            showLogMessage("onAdHidden");
        }

        @Override
        public void onAdClicked(MaxAd ad) {
            Log.d(TAG, "onAdClicked");
            showLogMessage("onAdClicked");
        }

        @Override
        public void onAdDisplayFailed(MaxAd ad, MaxError error) {
            String msg = "errorCode=" + error.getCode() + ";errorMsg=" + error.getMessage();
            Log.d(TAG, "onAdDisplayFailed:" + msg);
            showLogMessage("onAdDisplayFailed:" + msg);
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

    private void clearLog() {
        mTvShowLog.setText("");
    }

    private void showLogMessage(String msg) {
        mTvShowLog.append(msg);
        mTvShowLog.append("\r\n");
    }
}
