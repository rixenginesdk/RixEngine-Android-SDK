package com.rixengine.tradplus.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rixengine.AppConfig;
import com.rixengine.R;
import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.open.reward.RewardAdListener;
import com.tradplus.ads.open.reward.TPReward;

public class TradPlusRewardVideoDemoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TradPlusRewardVideoDemo";

    private TextView mTvTip;
    private TextView mTvVideoShow;
    private TPReward mVideoAD;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tradplus_video_demo);
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
            loadAd();
        } else if (id == R.id.tv_video_show) {
            if (mVideoAD != null && mVideoAD.isReady()) {
                mVideoAD.showAd(this, null);
            } else {
                loadAd();
            }
        }
    }

    /**
     * 加载广告
     */
    public void loadAd() {
        mTvTip.setText("The ad loading...");
        startTime = System.currentTimeMillis();
        mVideoAD = new TPReward(this, AppConfig.TRAD_PLUS_REWARD_AD);
        mVideoAD.setAdListener(new RewardAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdLoaded:" + getThreadName());
                Toast.makeText(getBaseContext(), "Reward AD load success", Toast.LENGTH_SHORT).show();
                mTvTip.setText("Reward AD load success --Consume time--" + (System.currentTimeMillis() - startTime) / 1000 + "-秒");
                mTvVideoShow.setEnabled(true);
            }

            @Override
            public void onAdFailed(TPAdError tpAdError) {
                Log.i(TAG, "onAdFailed： " + tpAdError.getErrorCode() + " " + tpAdError.getErrorMsg() + ";" + getThreadName());
                Toast.makeText(getBaseContext(), "Reward AD load Fail", Toast.LENGTH_SHORT).show();
                mTvTip.setText("Reward AD load fail");
                mTvVideoShow.setEnabled(false);
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClicked:" + getThreadName());
                Toast.makeText(getBaseContext(), "onAdClicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdImpression:" + getThreadName());
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClosed:" + getThreadName());
            }

            @Override
            public void onAdReward(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdReward--onAdReward-" + getThreadName());
            }

            @Override
            public void onAdVideoStart(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdVideoStart");
            }

            @Override
            public void onAdVideoEnd(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdVideoEnd");
            }

            @Override
            public void onAdVideoError(TPAdInfo tpAdInfo, TPAdError tpAdError) {
                Log.i(TAG, "onAdVideoError：" + tpAdError.getErrorCode() + " " + tpAdError.getErrorMsg() + ";" + getThreadName());
                Toast.makeText(getBaseContext(), "onAdVideoError：", Toast.LENGTH_SHORT).show();
            }


        });
        mVideoAD.loadAd();
    }

    private String getThreadName() {
        return Thread.currentThread().getName();
    }
}