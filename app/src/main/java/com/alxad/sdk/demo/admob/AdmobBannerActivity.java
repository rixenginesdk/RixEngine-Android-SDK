package com.alxad.sdk.demo.admob;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;


public class AdmobBannerActivity extends BaseActivity {
    private final String TAG = "AdmobBannerActivity";

    private AdView mAdView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admob_banner);
        initView();
        loadAd();
    }

    private void initView() {
        mAdView = (AdView) findViewById(R.id.ad_view);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Log.d(TAG, "onAdClosed");
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                Log.d(TAG, "onAdFailedToLoad:" + loadAdError.getMessage());
                Toast.makeText(getBaseContext(), getString(R.string.load_failed), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                Log.d(TAG, "onAdOpened");
            }

            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded");
            }

            @Override
            public void onAdClicked() {
                Log.d(TAG, "onAdClicked");
            }

        });
    }

    private void loadAd() {
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
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
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
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

}
