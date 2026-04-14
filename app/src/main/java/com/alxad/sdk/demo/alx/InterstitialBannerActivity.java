package com.alxad.sdk.demo.alx;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rixengine.api.AlxInterstitialAD;
import com.rixengine.api.AlxInterstitialADListener;
import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.R;

public class InterstitialBannerActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "AlxInterstitialBannerActivity";

    private TextView mTvTip;
    private TextView mTvShow;
    private AlxInterstitialAD mInterstitialAD;
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_load){
            loadAd();
        }else if (v.getId() == R.id.tv_show){
            if (mInterstitialAD == null) {
                Toast.makeText(this, getString(R.string.show_ad_no_load), Toast.LENGTH_SHORT).show();
                return;
            }
            if (mInterstitialAD.isReady()) {
                mInterstitialAD.show(this);
            } else {
                loadAd();
            }
        }
    }

    /**
     * load Ad
     */
    public void loadAd() {
        mTvTip.setText(R.string.loading);
        startTime = System.currentTimeMillis();

        mInterstitialAD = new AlxInterstitialAD();
        mInterstitialAD.load(this, AdConfig.ALX_INTERSTITIAL_BANNER_AD_ID, new AlxInterstitialADListener() {

            @Override
            public void onInterstitialAdLoaded() {
                Log.i(TAG, "onInterstitialAdLoaded");
                mTvShow.setEnabled(true);
                Toast.makeText(getBaseContext(), getString(R.string.load_success), Toast.LENGTH_SHORT).show();
                mTvTip.setText(getString(R.string.format_load_success, (System.currentTimeMillis() - startTime) / 1000) + "｜ ecpm:" + mInterstitialAD.getPrice());

                mInterstitialAD.reportChargingUrl();
                mInterstitialAD.reportBiddingUrl();
            }

            @Override
            public void onInterstitialAdLoadFail(int errorCode, String errorMsg) {
                Log.i(TAG, "onInterstitialAdLoadFail:  " + errorCode + " " + errorMsg);
                mTvShow.setEnabled(false);
                Toast.makeText(getBaseContext(), getString(R.string.load_failed), Toast.LENGTH_SHORT).show();
                mTvTip.setText(R.string.load_failed);
            }

            @Override
            public void onInterstitialAdClicked() {
                Log.i(TAG, "onInterstitialAdClicked");
            }

            @Override
            public void onInterstitialAdShow() {
                Log.i(TAG, "onInterstitialAdShow");
            }

            @Override
            public void onInterstitialAdClose() {
                Log.i(TAG, "onInterstitialAdClose");
                mTvShow.setEnabled(false);
                mTvTip.setText(R.string.tip_message);
            }

            @Override
            public void onInterstitialAdVideoStart() {
                Log.i(TAG, "onInterstitialAdVideoStart");
            }

            @Override
            public void onInterstitialAdVideoEnd() {
                Log.i(TAG, "onInterstitialAdVideoEnd");
            }

            @Override
            public void onInterstitialAdVideoError(int errorCode, String errorMsg) {
                Log.i(TAG, "onInterstitialAdVideoError:  " + errorCode + "," + errorMsg);
            }

        });

    }
}