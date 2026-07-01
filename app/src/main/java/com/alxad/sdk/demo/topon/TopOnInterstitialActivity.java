package com.alxad.sdk.demo.topon;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.R;
import com.thinkup.core.api.AdError;
import com.thinkup.core.api.TUAdInfo;
import com.thinkup.interstitial.api.TUInterstitial;
import com.thinkup.interstitial.api.TUInterstitialListener;

public class TopOnInterstitialActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "TopOnInterstitialActivity";
    private TextView mTvTip;
    private TextView mTvShow;
    private TUInterstitial mAD;
    private long startTime;

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
            if (mAD == null) {
                Toast.makeText(this, getString(R.string.show_ad_no_load), Toast.LENGTH_SHORT).show();
                return;
            }
            if (mAD.isAdReady()) {
                mAD.show(this);
            } else {
                Toast.makeText(this, "isAdReady()==false", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 加载广告
     */
    public void loadAd() {
        mTvTip.setText(R.string.loading);
        startTime = System.currentTimeMillis();

        mAD = new TUInterstitial(this, AdConfig.TOPON_INTERSTITIAL_ID);
        mAD.setAdListener(new TUInterstitialListener() {

            @Override
            public void onInterstitialAdLoaded() {
                Log.i(TAG, "onInterstitialAdLoaded:" + getCurrentThreadName());
                Toast.makeText(getBaseContext(), getString(R.string.load_success), Toast.LENGTH_SHORT).show();
                mTvTip.setText(getString(R.string.format_load_success, (System.currentTimeMillis() - startTime) / 1000));
                mTvShow.setEnabled(true);
            }

            @Override
            public void onInterstitialAdLoadFail(AdError adError) {
                Log.e(TAG, "onInterstitialAdLoadFail:" + adError.getCode() + ";" + adError.getDesc() + ";" + getCurrentThreadName());
                Toast.makeText(getBaseContext(), getString(R.string.load_failed), Toast.LENGTH_SHORT).show();
                mTvTip.setText(R.string.load_failed);
                mTvShow.setEnabled(false);
            }

            @Override
            public void onInterstitialAdClicked(TUAdInfo atAdInfo) {
                Log.i(TAG, "onInterstitialAdClicked:" + getCurrentThreadName());
            }

            @Override
            public void onInterstitialAdShow(TUAdInfo atAdInfo) {
                Log.i(TAG, "onInterstitialAdShow:" + getCurrentThreadName());
            }

            @Override
            public void onInterstitialAdClose(TUAdInfo atAdInfo) {
                Log.i(TAG, "onInterstitialAdClose:" + getCurrentThreadName());
                mTvShow.setEnabled(false);
                mTvTip.setText("");
            }

            @Override
            public void onInterstitialAdVideoStart(TUAdInfo atAdInfo) {
                Log.i(TAG, "onInterstitialAdVideoStart:" + getCurrentThreadName());
            }

            @Override
            public void onInterstitialAdVideoEnd(TUAdInfo atAdInfo) {
                Log.i(TAG, "onInterstitialAdVideoEnd:" + getCurrentThreadName());
            }

            @Override
            public void onInterstitialAdVideoError(AdError adError) {
                Log.i(TAG, "onInterstitialAdVideoError:" + adError.getCode() + ";" + adError.getDesc() + ";" + getCurrentThreadName());
            }

        });
        mAD.load();
    }
    
}
