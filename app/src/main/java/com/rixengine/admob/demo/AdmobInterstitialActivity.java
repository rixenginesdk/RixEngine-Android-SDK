package com.rixengine.admob.demo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.rixengine.AppConfig;
import com.rixengine.R;

public class AdmobInterstitialActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "AdmobInterstitial";
    private InterstitialAd mAd;
    private TextView mTvTip;
    private TextView mTvVideoShow;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admob_interstitial_demo);
        initView();
    }

    private void initView() {
        TextView tv_video_load = findViewById(R.id.tv_video_load);
        mTvVideoShow = findViewById(R.id.tv_video_show);
        mTvTip = findViewById(R.id.tv_tip);
        mTvVideoShow.setEnabled(false);
        tv_video_load.setOnClickListener(this);
        mTvVideoShow.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_video_load:
                bnLoad();
                break;
            case R.id.tv_video_show:
                bnShow();
                break;
        }
    }

    private void bnLoad() {
        mTvTip.setText("The ad is loading...");
        startTime = System.currentTimeMillis();
        mTvVideoShow.setEnabled(false);

        AdRequest adRequest = new AdRequest.Builder()
                .build();
        InterstitialAd.load(this, AppConfig.ADMOB_INTERSTITIAL_ID, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        Log.d(TAG, "onAdLoaded--InterstitialAd ad loads success");
                        Toast.makeText(getBaseContext(), "InterstitialAd ad loads success", Toast.LENGTH_SHORT).show();
                        mTvTip.setText("InterstitialAd ad loads success--Consume time-" + (System.currentTimeMillis() - startTime) / 1000 + "-秒");
                        mTvVideoShow.setEnabled(true);

                        mAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        // Handle the error
                        Log.d(TAG, "onAdFailedToLoad--InterstitialAd ad loads fail" + adError.getCode() + " " + adError.getMessage());
                        Toast.makeText(getBaseContext(), "InterstitialAd ad loads fail", Toast.LENGTH_SHORT).show();
                        mTvTip.setText("InterstitialAd ad loads fail " + adError.getMessage());
                        mTvVideoShow.setEnabled(false);

                        mAd = null;
                    }
                });
    }

    private void bnShow() {
        if (mAd == null) {
            Toast.makeText(this, "Load your ads first", Toast.LENGTH_SHORT).show();
            return;
        }
        mAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                Log.d(TAG,"onAdFailedToShowFullScreenContent:"+adError.getCode()+";"+adError.getMessage());
            }

            @Override
            public void onAdShowedFullScreenContent() {
                Log.d(TAG,"onAdShowedFullScreenContent");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                Log.d(TAG,"onAdDismissedFullScreenContent");
            }

            @Override
            public void onAdImpression() {
                Log.d(TAG,"onAdImpression");
            }

            @Override
            public void onAdClicked() {
                Log.d(TAG,"onAdClicked");
            }
        });
        mAd.show(this);
    }

}