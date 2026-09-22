package com.alxad.sdk.demo.max;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.R;

public class MaxInterstitialActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MaxInterstitialActivity";

    private TextView mTvClearLog;
    private TextView mTvShowLog;
    private TextView mTvShow;
    private long startTime;

    private MaxInterstitialAd mAdObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_and_show);
        setActionBar();
        initView();
    }

    private void initView() {
        TextView tv_load = findViewById(R.id.tv_load);
        mTvShow = findViewById(R.id.tv_show);
        mTvClearLog = (TextView) findViewById(R.id.tv_clear_log);
        mTvShowLog = (TextView) findViewById(R.id.tv_show_log);
        mTvShow.setEnabled(false);

        mTvShowLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTvClearLog.setOnClickListener(this);
        tv_load.setOnClickListener(this);
        mTvShow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_load) {
            loadAd();
        } else if (v.getId() == R.id.tv_show) {
            showAd();
        } else if (v.getId() == R.id.tv_clear_log) {
            clearLog();
        }
    }

    private void loadAd() {
        showLogMessage(getString(R.string.loading));
        startTime = System.currentTimeMillis();
        mTvShow.setEnabled(false);

        mAdObject = new MaxInterstitialAd(AdConfig.MAX_INTERSTITIAL_AD, this);

        mAdObject.setListener(mMaxAdListener);
        mAdObject.loadAd();
    }

    private void showAd() {
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
            double revenue = ad.getRevenue() * 1000;
            String message = " NetworkName:" + ad.getNetworkName() + "; ecpm:" + revenue;
            Log.d(TAG, "onAdLoaded |" + message);
            showLogMessage("onAdLoaded");
            showLogMessage(getString(R.string.load_success) + message);

            mTvShow.setEnabled(true);
        }

        @Override
        public void onAdLoadFailed(String adUnitId, MaxError error) {
            String msg = "errorCode=" + error.getCode() + ";errorMsg=" + error.getMessage();
            Log.d(TAG, "onAdLoadFailed:" + msg);
            showLogMessage("onAdLoadFailed");
            showLogMessage(getString(R.string.format_load_failed, msg));

            mTvShow.setEnabled(false);
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

    private void clearLog() {
        mTvShowLog.setText("");
    }

    private void showLogMessage(String msg) {
        mTvShowLog.append(msg);
        mTvShowLog.append("\r\n");
    }

}