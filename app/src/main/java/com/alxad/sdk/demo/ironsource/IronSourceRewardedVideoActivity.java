package com.alxad.sdk.demo.ironsource;

import android.annotation.SuppressLint;
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
import com.unity3d.mediation.rewarded.LevelPlayReward;
import com.unity3d.mediation.rewarded.LevelPlayRewardedAd;
import com.unity3d.mediation.rewarded.LevelPlayRewardedAdListener;

public class IronSourceRewardedVideoActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = "IronSourceRewardedVideo";
    private TextView mTvTip;
    private TextView mTvShow;
    private long startTime;

    private LevelPlayRewardedAd mAd;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_and_show);
        initView();
    }

    public void initView() {
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

        mAd = new LevelPlayRewardedAd(AdConfig.IRON_SOURCE_REWARD_VIDEO_AD);
        mAd.setListener(new LevelPlayRewardedAdListener() {

            @Override
            public void onAdLoaded(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
                Log.d(TAG, "onAdLoaded");
                Toast.makeText(getBaseContext(), getString(R.string.load_success), Toast.LENGTH_SHORT).show();
                mTvTip.setText(getString(R.string.format_load_success, (System.currentTimeMillis() - startTime) / 1000));
                mTvShow.setEnabled(true);
            }

            @Override
            public void onAdLoadFailed(@NonNull LevelPlayAdError levelPlayAdError) {
                Log.d(TAG, "onAdShowFailed: " + levelPlayAdError.getErrorCode() + ";" + levelPlayAdError.getErrorMessage());
                Toast.makeText(getBaseContext(), getString(R.string.load_failed), Toast.LENGTH_SHORT).show();
                mTvTip.setText(getString(R.string.format_load_failed, levelPlayAdError.getErrorMessage()));
                mTvShow.setEnabled(false);
            }

            @Override
            public void onAdDisplayed(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
                Log.d(TAG, "onAdDisplayed");
            }

            @Override
            public void onAdRewarded(@NonNull LevelPlayReward levelPlayReward, @NonNull LevelPlayAdInfo levelPlayAdInfo) {
                Log.d(TAG, "onAdRewarded");
            }

            @Override
            public void onAdDisplayFailed(@NonNull LevelPlayAdError levelPlayAdError, @NonNull LevelPlayAdInfo levelPlayAdInfo) {
                LevelPlayRewardedAdListener.super.onAdDisplayFailed(levelPlayAdError, levelPlayAdInfo);
                Log.d(TAG, "onAdDisplayFailed:" + levelPlayAdError.getErrorMessage());
            }

            @Override
            public void onAdClicked(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
                LevelPlayRewardedAdListener.super.onAdClicked(levelPlayAdInfo);
                Log.d(TAG, "onAdClicked");
            }

            @Override
            public void onAdClosed(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
                LevelPlayRewardedAdListener.super.onAdClosed(levelPlayAdInfo);
                Log.d(TAG, "onAdClosed");
            }

            @Override
            public void onAdInfoChanged(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
                LevelPlayRewardedAdListener.super.onAdInfoChanged(levelPlayAdInfo);
                Log.d(TAG, "onAdInfoChanged");
            }
        });

        mAd.loadAd();

    }


    private void bnShow() {
        if (mAd != null && mAd.isAdReady()) {
            mAd.showAd(this);
        }
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }
}