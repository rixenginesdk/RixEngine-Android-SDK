package com.rixengine.max.demo;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.rixengine.AppConfig;
import com.rixengine.R;

;


public class MaxBannerActivity extends AppCompatActivity {
    private final String TAG = "MaxBannerActivity";

    private MaxAdView mAdView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_max_banner_demo);
        initView();
        loadAd();
    }

    private void initView() {
        FrameLayout rootView = (FrameLayout) findViewById(R.id.ad_container);
        mAdView = new MaxAdView(AppConfig.MAX_BANNER_AD, this);
        mAdView.setListener(maxAdViewAdListener);
        mAdView.stopAutoRefresh();

        mAdView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(this, 50)));
        rootView.addView(mAdView);
    }

    private void loadAd() {
        mAdView.loadAd();
    }

    private MaxAdViewAdListener maxAdViewAdListener = new MaxAdViewAdListener() {
        @Override
        public void onAdExpanded(MaxAd ad) {
            Log.d(TAG, "onAdExpanded");
        }

        @Override
        public void onAdCollapsed(MaxAd ad) {
            Log.d(TAG, "onAdCollapsed");
        }

        @Override
        public void onAdLoaded(MaxAd ad) {
            Log.d(TAG, "onAdLoaded");
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
        public void onAdLoadFailed(String adUnitId, MaxError error) {
            Log.d(TAG, "onAdLoadFailed:" + error.getCode() + ";" + error.getMessage());
        }

        @Override
        public void onAdDisplayFailed(MaxAd ad, MaxError error) {
            Log.d(TAG, "onAdDisplayFailed:" + error.getCode() + ";" + error.getMessage());
        }
    };


    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        if (context == null) {
            return 0;
        }
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float scale = metrics.density;
        return (int) (dipValue * scale + 0.5f);
    }
}
