package com.alxad.sdk.demo.max;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.R;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;

public class MaxInterstitialActivity extends BaseActivity implements View.OnClickListener {
    private static String TAG = "MaxInterstitialActivity";

    private TextView mTvTip;
    private TextView mTvShow;
    private long startTime;

    private MaxInterstitialAd mAdObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_and_show);
        initView();
    }

    private void initView() {
        TextView tv_load = findViewById(R.id.tv_load);
        mTvShow = findViewById(R.id.tv_show);
        mTvTip = findViewById(R.id.tv_tip);
        mTvShow.setEnabled(false);
        tv_load.setOnClickListener(this);
        mTvShow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_load) {
            bnLoad();
        } else if (id == R.id.tv_show) {
            bnShow();
        }
    }

    private void bnLoad() {
        mTvTip.setText(R.string.loading);
        startTime = System.currentTimeMillis();
        mTvShow.setEnabled(false);

        mAdObject = new MaxInterstitialAd(AdConfig.MAX_INTERSTITIAL_AD, this);

        mAdObject.setListener(mMaxAdListener);
        mAdObject.loadAd();
    }

    private void bnShow() {
        if (mAdObject == null) {
            Toast.makeText(this, getString(R.string.show_ad_no_load), Toast.LENGTH_SHORT).show();
            return;
        }
        if (mAdObject.isReady()) {
            mAdObject.showAd(this);
        } else {
            Toast.makeText(this, "isReady()==false", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdObject != null) {
            mAdObject.destroy();
        }
    }

    private MaxAdListener mMaxAdListener = new MaxAdListener() {

        @Override
        public void onAdLoaded(MaxAd ad) {
            Log.d(TAG, "onAdLoaded |ecpm:" + ad.getRevenue());
            Toast.makeText(getBaseContext(), getString(R.string.load_success), Toast.LENGTH_SHORT).show();
            mTvTip.setText(getString(R.string.load_success) + "|ecpm:" + ad.getRevenue());
            mTvShow.setEnabled(true);
        }

        @Override
        public void onAdLoadFailed(String adUnitId, MaxError error) {
            Log.d(TAG, "onAdLoadFailed:" + error.getCode() + " " + error.getMessage());
            Toast.makeText(getBaseContext(), getString(R.string.load_failed), Toast.LENGTH_SHORT).show();
            mTvTip.setText(getString(R.string.format_load_failed, error.getMessage()));
            mTvShow.setEnabled(false);
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

}