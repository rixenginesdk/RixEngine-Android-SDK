package com.rixengine.max.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.rixengine.AppConfig;
import com.rixengine.R;

;


public class MaxRewardVideoActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = "MaxRewardVideoActivity";

    private TextView mTvTip;
    private TextView mTvVideoShow;
    private long startTime;

    private MaxRewardedAd mAdObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_max_interstitial_demo);
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_video_load) {
            bnLoad();
        } else if (id == R.id.tv_video_show) {
            bnShow();
        }
    }

    private void bnLoad() {
        mTvTip.setText("The ad is loading...");
        startTime = System.currentTimeMillis();
        mTvVideoShow.setEnabled(false);

        mAdObject = MaxRewardedAd.getInstance(AppConfig.MAX_REWARD_VIDEO_AD, this);
        mAdObject.setListener(mMaxRewardedAdListener);
        mAdObject.loadAd();
    }

    private void bnShow() {
        if (mAdObject == null) {
            Toast.makeText(this, "First load AD", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mAdObject.isReady()) {
            mAdObject.showAd();
        } else {
            Toast.makeText(this, "isReady()==false", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdObject != null) {
            mAdObject.destroy();
        }
    }

    private MaxRewardedAdListener mMaxRewardedAdListener = new MaxRewardedAdListener() {

        @Override
        public void onAdLoaded(MaxAd ad) {
            Log.d(TAG, "onAdLoaded--Reward ad loads success");
            Toast.makeText(getBaseContext(), "Reward ad loads success", Toast.LENGTH_SHORT).show();
            mTvTip.setText("Reward ad loads success--Consume time-" + (System.currentTimeMillis() - startTime) / 1000 + "-秒");
            mTvVideoShow.setEnabled(true);
        }

        @Override
        public void onAdLoadFailed(String adUnitId, MaxError error) {
            Log.d(TAG, "onAdLoadFailed--Reward ad loads fail " + error.getCode() + " " + error.getMessage());
            Toast.makeText(getBaseContext(), "Reward ad loads fail", Toast.LENGTH_SHORT).show();
            mTvTip.setText("Reward ad loads fail" + error.getMessage());
            mTvVideoShow.setEnabled(false);
        }

        @Override
        public void onAdDisplayed(MaxAd ad) {
            Log.d(TAG, "onAdDisplayed");
        }

        @Override
        public void onAdHidden(MaxAd ad) {
            Log.d(TAG, "onAdHidden");
        }

        @Override
        public void onAdClicked(MaxAd ad) {
            Log.d(TAG, "onAdClicked");
        }

        @Override
        public void onAdDisplayFailed(MaxAd ad, MaxError error) {
            Log.d(TAG, "onAdDisplayFailed:" + error.getCode() + ";" + error.getMessage());
        }

        @Override
        public void onUserRewarded(MaxAd ad, MaxReward reward) {
            Log.d(TAG, "onUserRewarded:" + reward.getLabel());
        }
    };

}