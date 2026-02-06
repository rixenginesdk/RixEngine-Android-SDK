package com.alxad.sdk.demo.tradplus;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.R;
import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.open.interstitial.InterstitialAdListener;
import com.tradplus.ads.open.interstitial.TPInterstitial;

@SuppressLint("LongLogTag")
public class TradPlusInterstitialActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "TradPlusInterstitialDemo";

    private TextView mTvTip;
    private TextView mTvShow;
    private TPInterstitial mAdObj;
    private long startTime;

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
            loadAd();
        } else if (id == R.id.tv_show) {
            if (mAdObj == null) {
                Toast.makeText(this, getString(R.string.show_ad_no_load), Toast.LENGTH_SHORT).show();
                return;
            }
            if (mAdObj.isReady()) {
                mAdObj.showAd(this, null);
            } else {
                Toast.makeText(this, "isReady()==false", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 加载广告
     */
    public void loadAd() {
        mTvTip.setText(R.string.loading);
        startTime = System.currentTimeMillis();
        mAdObj = new TPInterstitial(this, AdConfig.TRAD_PLUS_INTERSTITIAL_AD);
        mAdObj.setAdListener(new InterstitialAdListener() {

            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdLoaded:" + getThreadName());
                Toast.makeText(getBaseContext(), getString(R.string.load_success), Toast.LENGTH_SHORT).show();
                mTvTip.setText(getString(R.string.format_load_success, (System.currentTimeMillis() - startTime) / 1000));
                mTvShow.setEnabled(true);
            }

            @Override
            public void onAdFailed(TPAdError tpAdError) {
                Log.i(TAG, "onAdFailed： " + tpAdError.getErrorCode() + " " + tpAdError.getErrorMsg() + ";" + getThreadName());
                Toast.makeText(getBaseContext(), getString(R.string.load_failed), Toast.LENGTH_SHORT).show();
                mTvTip.setText(R.string.load_failed);
                mTvShow.setEnabled(false);
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClicked:" + getThreadName());
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdImpression:" + getThreadName());
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClosed:" + getThreadName());
            }

            @Override
            public void onAdVideoError(TPAdInfo tpAdInfo, TPAdError tpAdError) {
                Log.i(TAG, "onAdVideoError:" + tpAdError.getErrorCode() + " " + tpAdError.getErrorMsg() + ";" + getThreadName());
            }

            @Override
            public void onAdVideoStart(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdVideoStart");
            }

            @Override
            public void onAdVideoEnd(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdVideoEnd");
            }
        });
        mAdObj.loadAd();
    }

    private String getThreadName() {
        return Thread.currentThread().getName();
    }
}