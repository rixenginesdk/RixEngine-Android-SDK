package com.alxad.sdk.demo.admob;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.R;

public class AdmobBannerActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "AdmobBannerActivity";

    private TextView mTvClearLog;
    private TextView mTvShowLog;

    private AdView mAdView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admob_banner);
        setActionBar();
        initView();
    }

    private void initView() {
        TextView tv_load_and_show = findViewById(R.id.tv_load_show);
        mTvClearLog = (TextView) findViewById(R.id.tv_clear_log);
        mTvShowLog = (TextView) findViewById(R.id.tv_show_log);
        mAdView = (AdView) findViewById(R.id.ad_view);

        mTvShowLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTvClearLog.setOnClickListener(this);
        tv_load_and_show.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_load_show) {
            loadAd();
        } else if (v.getId() == R.id.tv_clear_log) {
            clearLog();
        }
    }

    private void loadAd() {
        mAdView.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded");
                showLogMessage("onAdLoaded");
                showLogMessage(getString(R.string.load_success));
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                String msg = "errorCode=" + loadAdError.getCode() + ";errorMsg=" + loadAdError.getMessage();
                Log.d(TAG, "onAdFailedToLoad:" + msg);
                showLogMessage("onAdFailedToLoad");
                showLogMessage(getString(R.string.format_load_failed, msg));
            }

            @Override
            public void onAdOpened() {
                Log.d(TAG, "onAdOpened");
                showLogMessage("onAdOpened");
            }

            @Override
            public void onAdClicked() {
                Log.d(TAG, "onAdClicked");
                showLogMessage("onAdClicked");
            }

            @Override
            public void onAdClosed() {
                Log.d(TAG, "onAdClosed");
                showLogMessage("onAdClosed");
            }

        });

        showLogMessage(getString(R.string.loading));
        // Create an ad request.
        Bundle extra = new Bundle();
        extra.putBoolean("extra", true);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
    }

    /**
     * Called when leaving the activity
     */
    @Override
    public void onPause() {
        super.onPause();
        if (mAdView != null) {
            mAdView.pause();
        }
    }

    /**
     * Called when returning to the activity
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdView != null) {
            mAdView.destroy();
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
