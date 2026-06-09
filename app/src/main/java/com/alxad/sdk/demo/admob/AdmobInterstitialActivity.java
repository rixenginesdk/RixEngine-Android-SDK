package com.alxad.sdk.demo.admob;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.R;

public class AdmobInterstitialActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "AdmobInterstitial";
    private InterstitialAd mAd;
    private TextView mTvTip;
    private TextView mTvShow;
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
            bnLoad();
        } else if (id == R.id.tv_show) {
            bnShow();
        }
    }

    private void bnLoad() {
        mTvTip.setText(R.string.loading);
        startTime = System.currentTimeMillis();
        mTvShow.setEnabled(false);

        AdRequest adRequest = new AdRequest.Builder()
                .build();
        InterstitialAd.load(this, AdConfig.ADMOB_INTERSTITIAL_ID, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        Log.d(TAG, "onAdLoaded");
                        Toast.makeText(getBaseContext(), getString(R.string.load_success), Toast.LENGTH_SHORT).show();
                        mTvTip.setText(getString(R.string.format_load_success, (System.currentTimeMillis() - startTime) / 1000));
                        mTvShow.setEnabled(true);

                        mAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        // Handle the error
                        Log.d(TAG, "onAdFailedToLoad：" + adError.getCode() + " " + adError.getMessage());
                        Toast.makeText(getBaseContext(), getString(R.string.load_failed), Toast.LENGTH_SHORT).show();
                        mTvTip.setText(getString(R.string.format_load_failed, adError.getMessage()));
                        mTvShow.setEnabled(false);

                        mAd = null;
                    }
                });
    }

    private void bnShow() {
        if (mAd == null) {
            Toast.makeText(this, getString(R.string.show_ad_no_load), Toast.LENGTH_SHORT).show();
            return;
        }
        mAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                Log.d(TAG, "onAdFailedToShowFullScreenContent:" + adError.getCode() + ";" + adError.getMessage());
            }

            @Override
            public void onAdShowedFullScreenContent() {
                Log.d(TAG, "onAdShowedFullScreenContent");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                Log.d(TAG, "onAdDismissedFullScreenContent");
            }

            @Override
            public void onAdImpression() {
                Log.d(TAG, "onAdImpression");
            }

            @Override
            public void onAdClicked() {
                Log.d(TAG, "onAdClicked");
            }
        });
        mAd.show(this);
    }

}