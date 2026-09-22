package com.alxad.sdk.demo.admob;

import android.annotation.SuppressLint;
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
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.R;

public class AdmobRewardVideoActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "AdmobRewardVideoActivity";
    private RewardedAd mRewardedAd;
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

    @SuppressLint("NonConstantResourceId")
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

    private void bnShow() {
        if (mRewardedAd == null) {
            Toast.makeText(this, getString(R.string.show_ad_no_load), Toast.LENGTH_SHORT).show();
            return;
        }

        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                Log.d(TAG, "onAdFailedToShowFullScreenContent: errorCode=" + adError.getCode() + ";errorMsg=" + adError.getMessage());
                showLogMessage("onAdFailedToShowFullScreenContent: errorCode=" + adError.getCode() + ";errorMsg=" + adError.getMessage());
            }

            @Override
            public void onAdShowedFullScreenContent() {
                Log.d(TAG, "onAdShowedFullScreenContent: Rewarded ad opened");
                showLogMessage("onAdShowedFullScreenContent");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                Log.d(TAG, "onAdDismissedFullScreenContent: Rewarded ad closed");
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

        mRewardedAd.show(this, new OnUserEarnedRewardListener() {

            @Override
            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                Log.d(TAG, "onUserEarnedReward:" + rewardItem.getType());
                showLogMessage("onUserEarnedReward");
            }

        });
    }

    public void bnLoad() {
        showLogMessage(getString(R.string.loading));
        startTime = System.currentTimeMillis();

        RewardedAd.load(this, AdConfig.ADMOB_REWARD_ID, new AdRequest.Builder().build(), new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                Log.d(TAG, "onAdLoaded:" + getCurrentThreadName());
                showLogMessage("onAdLoaded");
                showLogMessage(getString(R.string.format_load_success, (System.currentTimeMillis() - startTime) / 1000));
                mTvShow.setEnabled(true);

                mRewardedAd = rewardedAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                String msg = "errorCode=" + loadAdError.getCode() + ";errorMsg=" + loadAdError.getMessage();
                Log.d(TAG, "onAdFailedToLoad:" + msg);
                showLogMessage("onAdFailedToLoad");
                showLogMessage(getString(R.string.format_load_failed, msg));

                mTvShow.setEnabled(false);
            }
        });
    }

    private void clearLog() {
        mTvShowLog.setText("");
    }

    private void showLogMessage(String msg) {
        mTvShowLog.append(msg);
        mTvShowLog.append("\r\n");
    }

}