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
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.rixengine.AppConfig;
import com.rixengine.R;

public class AdmobRewardVideoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AdmobRewardVideo";
    private RewardedAd mRewardedAd;
    private TextView mTvTip;
    private TextView mTvVideoShow;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admob_reward_video_demo);
        initView();
        createAndLoadRewardedAd();
    }

    private void initView() {
        mTvVideoShow = findViewById(R.id.tv_video_show);
        mTvTip = findViewById(R.id.tv_tip);
        mTvVideoShow.setEnabled(false);
        mTvVideoShow.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_video_show) {
            showAd();
        }
    }

    private void showAd(){
        if(mRewardedAd==null){
            createAndLoadRewardedAd();
            return;
        }

        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                Log.d(TAG, "onAdFailedToShowFullScreenContent:"+adError.getCode()+";"+adError.getMessage());
            }

            @Override
            public void onAdShowedFullScreenContent() {
                Log.d(TAG, "onAdShowedFullScreenContent");
                Toast.makeText(AdmobRewardVideoActivity.this,
                        "Rewarded ad opened",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                Log.d(TAG, "onAdDismissedFullScreenContent");
                createAndLoadRewardedAd();
                Toast.makeText(AdmobRewardVideoActivity.this,
                        "Rewarded ad closed",
                        Toast.LENGTH_SHORT).show();
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

        mRewardedAd.show(this,new OnUserEarnedRewardListener(){

            @Override
            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                Log.d(TAG, "onUserEarnedReward:"+rewardItem.getType());
            }

        });
    }

    public void createAndLoadRewardedAd() {
        mTvTip.setText("The ad is loading...");
        startTime = System.currentTimeMillis();

        RewardedAd.load(this, AppConfig.ADMOB_REWARD_ID, new AdRequest.Builder().build(), new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                Log.d(TAG, "onAdLoaded:"+getThreadName());
                Toast.makeText(getBaseContext(), "RewardVideo AD load succes", Toast.LENGTH_SHORT).show();
                mTvTip.setText("RewardVideo AD load success--Consume time-" + (System.currentTimeMillis() - startTime) / 1000 + "-秒");
                mTvVideoShow.setEnabled(true);

                mRewardedAd=rewardedAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                Log.d(TAG, "Rewarded AD load fail: " + adError.getCode() + " " + adError.getMessage()+";"+getThreadName());
                Toast.makeText(getBaseContext(), "Rewarded AD load fail", Toast.LENGTH_SHORT).show();
                mTvTip.setText("Rewarded AD load fail: " + adError.getMessage());
                mTvVideoShow.setEnabled(false);
            }
        });
    }

    private String getThreadName() {
        return Thread.currentThread().getName();
    }

}