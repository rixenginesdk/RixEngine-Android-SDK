package com.alxad.sdk.demo.ironsource;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.R;
import com.unity3d.mediation.LevelPlayAdError;
import com.unity3d.mediation.LevelPlayAdInfo;
import com.unity3d.mediation.interstitial.LevelPlayInterstitialAd;
import com.unity3d.mediation.interstitial.LevelPlayInterstitialAdListener;

public class IronSourceInterstitialActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = "IronSourceInterstitial";
    private TextView mTvTip;
    private TextView mTvShow;
    private long startTime;

    private LevelPlayInterstitialAd mAd;

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

        mAd = new LevelPlayInterstitialAd(AdConfig.IRON_SOURCE_INTERSTITIAL_AD);
        mAd.setListener(new LevelPlayInterstitialAdListener() {

            @Override
            public void onAdLoaded(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
                Log.d(TAG, "onAdLoaded");
                Toast.makeText(getBaseContext(), getString(R.string.load_success), Toast.LENGTH_SHORT).show();
                mTvTip.setText(getString(R.string.format_load_success, (System.currentTimeMillis() - startTime) / 1000));
                mTvShow.setEnabled(true);
            }

            @Override
            public void onAdLoadFailed(@NonNull LevelPlayAdError levelPlayAdError) {
                Log.d(TAG, "onAdLoadFailed: " + levelPlayAdError.getErrorCode() + ";" + levelPlayAdError.getErrorMessage());
                Toast.makeText(getBaseContext(), getString(R.string.load_failed), Toast.LENGTH_SHORT).show();
                mTvTip.setText(getString(R.string.format_load_failed, levelPlayAdError.getErrorMessage()));
                mTvShow.setEnabled(false);
            }

            @Override
            public void onAdDisplayed(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
                Log.d(TAG, "onAdDisplayed");
            }

            @Override
            public void onAdDisplayFailed(@NonNull LevelPlayAdError levelPlayAdError, @NonNull LevelPlayAdInfo levelPlayAdInfo) {
                LevelPlayInterstitialAdListener.super.onAdDisplayFailed(levelPlayAdError, levelPlayAdInfo);
                Log.d(TAG, "onAdDisplayFailed:" + levelPlayAdError.getErrorMessage());
            }

            @Override
            public void onAdClicked(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
                LevelPlayInterstitialAdListener.super.onAdClicked(levelPlayAdInfo);
                Log.d(TAG, "onAdClicked");
            }

            @Override
            public void onAdClosed(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
                LevelPlayInterstitialAdListener.super.onAdClosed(levelPlayAdInfo);
                Log.d(TAG, "onAdClosed");
            }

            @Override
            public void onAdInfoChanged(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
                LevelPlayInterstitialAdListener.super.onAdInfoChanged(levelPlayAdInfo);
            }
        });

        mAd.loadAd();
    }


    private void bnShow() {
        if (mAd != null && mAd.isAdReady()) {
            mAd.showAd(this);
        }
    }
}