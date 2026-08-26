package com.alxad.sdk.demo.ironsource;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.R;
import com.unity3d.mediation.LevelPlayAdError;
import com.unity3d.mediation.LevelPlayAdInfo;
import com.unity3d.mediation.LevelPlayAdSize;
import com.unity3d.mediation.banner.LevelPlayBannerAdView;
import com.unity3d.mediation.banner.LevelPlayBannerAdViewListener;

public class IronSourceBannerActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "IronSourceBanner";

    private FrameLayout mAdContainerView;
    private View mBnLoad;
    private TextView mTvClearLog;
    private TextView mTvShowLog;
    private LevelPlayBannerAdView bannerView;

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
            bannerView.destroy();
        }

        LevelPlayAdSize adSize = LevelPlayAdSize.BANNER;
        LevelPlayBannerAdView.Config adConfig = new LevelPlayBannerAdView.Config.Builder()
                .setAdSize(adSize)
                .setPlacementName("middle")
                .build();

        // Create the banner view and set the ad unit id
        bannerView = new LevelPlayBannerAdView(this, AdConfig.IRON_SOURCE_BANNER_AD, adConfig);

        bannerView.setBannerListener(new LevelPlayBannerAdViewListener() {
            @Override
            public void onAdLoaded(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
                Log.d(TAG, "onAdLoaded");
                mBnLoad.setEnabled(true);
                showLogMessage("onAdLoaded");
                showLogMessage(getString(R.string.load_success));
                showAd();
            }

            @Override
            public void onAdLoadFailed(@NonNull LevelPlayAdError levelPlayAdError) {
                String msg = "errorCode=" + levelPlayAdError.getErrorCode() + ":errorMsg=" + levelPlayAdError.getErrorMessage();
                Log.d(TAG, "onAdLoadFailed: " + msg);
                mBnLoad.setEnabled(true);
                showLogMessage("onAdLoadFailed");
                showLogMessage(getString(R.string.format_load_failed, msg));
            }

            @Override
            public void onAdDisplayed(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
                LevelPlayBannerAdViewListener.super.onAdDisplayed(levelPlayAdInfo);
                Log.d(TAG, "onAdDisplayed");
                showLogMessage("onAdDisplayed");
            }

            @Override
            public void onAdDisplayFailed(@NonNull LevelPlayAdInfo levelPlayAdInfo, @NonNull LevelPlayAdError levelPlayAdError) {
                LevelPlayBannerAdViewListener.super.onAdDisplayFailed(levelPlayAdInfo, levelPlayAdError);
                String msg = "errorCode=" + levelPlayAdError.getErrorCode() + ":errorMsg=" + levelPlayAdError.getErrorMessage();
                Log.d(TAG, "onAdDisplayFailed:" + msg);
                showLogMessage("onAdDisplayFailed:" + msg);
            }

            @Override
            public void onAdClicked(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
                LevelPlayBannerAdViewListener.super.onAdClicked(levelPlayAdInfo);
                Log.d(TAG, "onAdClicked");
                showLogMessage("onAdClicked");
            }

            @Override
            public void onAdExpanded(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
                LevelPlayBannerAdViewListener.super.onAdExpanded(levelPlayAdInfo);
                Log.d(TAG, "onAdExpanded");
                showLogMessage("onAdExpanded");
            }

            @Override
            public void onAdCollapsed(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
                LevelPlayBannerAdViewListener.super.onAdCollapsed(levelPlayAdInfo);
                Log.d(TAG, "onAdCollapsed");
                showLogMessage("onAdCollapsed");
            }

            @Override
            public void onAdLeftApplication(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
                LevelPlayBannerAdViewListener.super.onAdLeftApplication(levelPlayAdInfo);
                Log.d(TAG, "onAdLeftApplication");
                showLogMessage("onAdLeftApplication");
            }

        });

        bannerView.loadAd();
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
            bannerView.destroy();
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