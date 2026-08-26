package com.alxad.sdk.demo.ironsource;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
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
    private TextView mTvClearLog;
    private TextView mTvShowLog;
    private TextView mTvShow;
    private long startTime;

    private LevelPlayRewardedAd mAd;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_and_show);
        setActionBar();
        initView();
    }

    public void initView() {
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
        if (v.getId() == R.id.tv_load) {
            bnLoad();
        } else if (v.getId() == R.id.tv_show) {
            bnShow();
        } else if (v.getId() == R.id.tv_clear_log) {
            clearLog();
        }
    }

    private void bnLoad() {
        showLogMessage(getString(R.string.loading));
        startTime = System.currentTimeMillis();
        mTvShow.setEnabled(false);

        mAd = new LevelPlayRewardedAd(AdConfig.IRON_SOURCE_REWARD_VIDEO_AD);
        mAd.setListener(new LevelPlayRewardedAdListener() {

            @Override
            public void onAdLoaded(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
                Log.d(TAG, "onAdLoaded");
                showLogMessage("onAdLoaded");
                showLogMessage(getString(R.string.format_load_success, (System.currentTimeMillis() - startTime) / 1000));
                mTvShow.setEnabled(true);
            }

            @Override
            public void onAdLoadFailed(@NonNull LevelPlayAdError levelPlayAdError) {
                String msg = "errorCode=" + levelPlayAdError.getErrorCode() + ":errorMsg=" + levelPlayAdError.getErrorMessage();
                Log.d(TAG, "onAdLoadFailed: " + msg);
                showLogMessage("onAdLoadFailed");
                showLogMessage(getString(R.string.format_load_failed, msg));
                mTvShow.setEnabled(false);
            }

            @Override
            public void onAdDisplayed(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
                Log.d(TAG, "onAdDisplayed");
                showLogMessage("onAdDisplayed");
            }

            @Override
            public void onAdRewarded(@NonNull LevelPlayReward levelPlayReward, @NonNull LevelPlayAdInfo levelPlayAdInfo) {
                Log.d(TAG, "onAdRewarded");
                showLogMessage("onAdRewarded");
            }

            @Override
            public void onAdDisplayFailed(@NonNull LevelPlayAdError levelPlayAdError, @NonNull LevelPlayAdInfo levelPlayAdInfo) {
                LevelPlayRewardedAdListener.super.onAdDisplayFailed(levelPlayAdError, levelPlayAdInfo);
                String msg = "errorCode=" + levelPlayAdError.getErrorCode() + ":errorMsg=" + levelPlayAdError.getErrorMessage();
                Log.d(TAG, "onAdDisplayFailed:" + msg);
                showLogMessage("onAdDisplayFailed:" + msg);
            }

            @Override
            public void onAdClicked(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
                LevelPlayRewardedAdListener.super.onAdClicked(levelPlayAdInfo);
                Log.d(TAG, "onAdClicked");
                showLogMessage("onAdClicked");
            }

            @Override
            public void onAdClosed(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
                LevelPlayRewardedAdListener.super.onAdClosed(levelPlayAdInfo);
                Log.d(TAG, "onAdClosed");
                showLogMessage("onAdClosed");
            }

            @Override
            public void onAdInfoChanged(@NonNull LevelPlayAdInfo levelPlayAdInfo) {
                LevelPlayRewardedAdListener.super.onAdInfoChanged(levelPlayAdInfo);
                Log.d(TAG, "onAdInfoChanged");
                showLogMessage("onAdInfoChanged");
            }
        });

        mAd.loadAd();

    }


    private void bnShow() {
        if (mAd != null && mAd.isAdReady()) {
            mAd.showAd(this);
        }else{
            Toast.makeText(this, getString(R.string.show_ad_no_load), Toast.LENGTH_SHORT).show();
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