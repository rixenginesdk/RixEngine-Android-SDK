package com.rixengine.alx.demo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rixengine.AppConfig;
import com.rixengine.R;
import com.rixengine.api.AlxRewardVideoAD;
import com.rixengine.api.AlxRewardVideoADListener;

public class RewardVideoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AlxRewardVideoDemo";

    private TextView mTvTip;
    private TextView mTvVideoShow;
    private AlxRewardVideoAD mVideoAD;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_video);
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
                loadAd();
                break;
            case R.id.tv_video_show:
                if (mVideoAD != null && mVideoAD.isReady()) {
                    mVideoAD.showVideo(this);
                } else {
                    loadAd();
                }
                break;
        }
    }

    /**
     * load Ad
     */
    public void loadAd() {
        mTvTip.setText("Ad loading...");
        startTime = System.currentTimeMillis();
        mVideoAD = new AlxRewardVideoAD();
        mVideoAD.load(this, AppConfig.ALX_REWARD_VIDEO_AD_PID, new AlxRewardVideoADListener() {

            @Override
            public void onRewardedVideoAdLoaded(AlxRewardVideoAD var1) {
                Log.i(TAG, "onRewardedVideoAdLoaded");
                Toast.makeText(getBaseContext(), "Ad loaded success", Toast.LENGTH_SHORT).show();
                mTvTip.setText("Ad loaded time: " + (System.currentTimeMillis() - startTime) / 1000 + " -s | ecpm：" +
                        mVideoAD.getPrice());
                mTvVideoShow.setEnabled(true);
                mVideoAD.reportChargingUrl();
                mVideoAD.reportBiddingUrl();
            }

            @Override
            public void onRewardedVideoAdFailed(AlxRewardVideoAD var1, int errCode, String errMsg) {
                Log.i(TAG, "onRewardedVideoAdFailed：" + errCode + "; " + errMsg);
                Toast.makeText(getBaseContext(), "Ad loaded fail", Toast.LENGTH_SHORT).show();
                mTvTip.setText("Ad loaded fail");
                mTvVideoShow.setEnabled(false);
            }

            @Override
            public void onRewardedVideoAdPlayStart(AlxRewardVideoAD var1) {
                Log.i(TAG, "onRewardedVideoAdPlayStart");
            }

            @Override
            public void onRewardedVideoAdPlayEnd(AlxRewardVideoAD var1) {
                Log.i(TAG, "onRewardedVideoAdPlayEnd");
            }

            @Override
            public void onRewardedVideoAdPlayFailed(AlxRewardVideoAD var2, int errCode, String errMsg) {
                Log.i(TAG, "onRewardedVideoAdPlayFailed:" + errCode + ";" + errMsg);
            }

            @Override
            public void onRewardedVideoAdClosed(AlxRewardVideoAD var1) {
                Log.i(TAG, "onRewardedVideoAdClosed");
                mTvVideoShow.setEnabled(false);
                mTvTip.setText("Ad not loaded");
            }

            @Override
            public void onRewardedVideoAdPlayClicked(AlxRewardVideoAD var1) {
                Log.i(TAG, "onRewardedVideoAdPlayClicked");
                Toast.makeText(getBaseContext(), "onRewardedVideoAdPlayClicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReward(AlxRewardVideoAD var1) {
                Log.i(TAG, "onReward");
            }

            @Override
            public void onRewardVideoCache(boolean isSuccess) {
                Log.i(TAG, "onRewardVideoCache:" + isSuccess + ";" + Thread.currentThread().getName());
            }
        });

    }

}