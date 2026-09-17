package com.alxad.sdk.demo.admob;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
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
    private final String TAG = "AdmobInterstitialActivity";
    private InterstitialAd mAd;
    private TextView mTvClearLog;
    private TextView mTvShowLog;
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
        int id = v.getId();
        if (id == R.id.tv_load) {
            bnLoad();
        } else if (id == R.id.tv_show) {
            bnShow();
        } else if (v.getId() == R.id.tv_clear_log) {
            clearLog();
        }
    }

    private void bnLoad() {
        showLogMessage(getString(R.string.loading));
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
                        showLogMessage("onAdLoaded");
                        showLogMessage(getString(R.string.format_load_success, (System.currentTimeMillis() - startTime) / 1000));
                        mTvShow.setEnabled(true);

                        mAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        String msg = "errorCode=" + loadAdError.getCode() + ";errorMsg=" + loadAdError.getMessage();
                        Log.d(TAG, "onAdFailedToLoad:" + msg);
                        showLogMessage("onAdFailedToLoad");
                        showLogMessage(getString(R.string.format_load_failed, msg));

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
                Log.d(TAG, "onAdFailedToShowFullScreenContent: errorCode=" + adError.getCode() + ";errorMsg=" + adError.getMessage());
                showLogMessage("onAdFailedToShowFullScreenContent: errorCode=" + adError.getCode() + ";errorMsg=" + adError.getMessage());
            }

            @Override
            public void onAdShowedFullScreenContent() {
                Log.d(TAG, "onAdShowedFullScreenContent");
                showLogMessage("onAdShowedFullScreenContent");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                Log.d(TAG, "onAdDismissedFullScreenContent");
                showLogMessage("onAdDismissedFullScreenContent");
            }

            @Override
            public void onAdImpression() {
                Log.d(TAG, "onAdImpression");
                showLogMessage("onAdImpression");
            }

            @Override
            public void onAdClicked() {
                Log.d(TAG, "onAdClicked");
                showLogMessage("onAdClicked");
            }
        });
        mAd.show(this);
    }

    private void clearLog() {
        mTvShowLog.setText("");
    }

    private void showLogMessage(String msg) {
        mTvShowLog.append(msg);
        mTvShowLog.append("\r\n");
    }

}