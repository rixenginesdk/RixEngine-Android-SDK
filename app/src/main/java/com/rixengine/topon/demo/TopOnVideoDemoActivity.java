package com.rixengine.topon.demo;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rixengine.AppConfig;
import com.rixengine.R;
import com.thinkup.core.api.AdError;
import com.thinkup.core.api.TUAdInfo;
import com.thinkup.core.api.TUNetworkConfirmInfo;
import com.thinkup.rewardvideo.api.TURewardVideoAd;
import com.thinkup.rewardvideo.api.TURewardVideoExListener;

public class TopOnVideoDemoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TopOnVideoDemoActivity";
    private TextView mTvTip;
    private TextView mTvVideoShow;
    private TURewardVideoAd mVideoAD;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topon_video_demo);
        initView();
    }

    private void initView() {
        TextView tv_video_load = findViewById(R.id.tv_video_load);
        mTvVideoShow = findViewById(R.id.tv_video_show);
        mTvTip = findViewById(R.id.tv_tip);
        mTvVideoShow.setEnabled(false);
        tv_video_load.setOnClickListener(this);
        mTvVideoShow.setOnClickListener(this);
        mVideoAD = new TURewardVideoAd(this, AppConfig.TOPON_VIDEO_AD_PID);
        mVideoAD.setAdListener(new TURewardVideoExListener() {

            @Override
            public void onRewardFailed(TUAdInfo atAdInfo) {

            }

            @Override
            public void onDeeplinkCallback(TUAdInfo adInfo, boolean isSuccess) {
                Log.i(TAG, "onDeeplinkCallback:" + adInfo.toString() + "--status:" + isSuccess+";"+getThreadName());
            }

            @Override
            public void onDownloadConfirm(Context context, TUAdInfo atAdInfo, TUNetworkConfirmInfo atNetworkConfirmInfo) {

            }

            @Override
            public void onRewardedVideoAdAgainPlayStart(TUAdInfo atAdInfo) {

            }

            @Override
            public void onRewardedVideoAdAgainPlayEnd(TUAdInfo atAdInfo) {

            }

            @Override
            public void onRewardedVideoAdAgainPlayFailed(AdError adError, TUAdInfo atAdInfo) {

            }

            @Override
            public void onRewardedVideoAdAgainPlayClicked(TUAdInfo atAdInfo) {

            }

            @Override
            public void onAgainReward(TUAdInfo atAdInfo) {

            }

            @Override
            public void onAgainRewardFailed(TUAdInfo atAdInfo) {

            }

            @Override
            public void onRewardedVideoAdLoaded() {
                Log.i(TAG, "onRewardedVideoAdLoaded:"+getThreadName());
                Toast.makeText(getBaseContext(), "Reward AD load success", Toast.LENGTH_SHORT).show();
                mTvTip.setText("Reward AD load success--Consume time-" + (System.currentTimeMillis() - startTime) / 1000 + "-秒");
                mTvVideoShow.setEnabled(true);
            }

            @Override
            public void onRewardedVideoAdFailed(AdError errorCode) {
                Log.i(TAG, "onRewardedVideoAdFailed:" + errorCode.getCode() + " " + errorCode.getDesc()+";"+getThreadName());
                Toast.makeText(getBaseContext(), "Reward AD load fail", Toast.LENGTH_SHORT).show();
                mTvTip.setText("Reward AD load fail");
                mTvVideoShow.setEnabled(false);
            }

            @Override
            public void onRewardedVideoAdPlayStart(TUAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdPlayStart:"+getThreadName());
                Toast.makeText(getBaseContext(), "Reward AD start", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdPlayEnd(TUAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdPlayEnd:"+getThreadName());
                Toast.makeText(getBaseContext(), "onRewardedVideoAdPlayEnd", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdPlayFailed(AdError errorCode, TUAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdPlayFailed:"+getThreadName());
            }

            @Override
            public void onRewardedVideoAdClosed(TUAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdClosed:"+getThreadName());
                mTvVideoShow.setEnabled(false);
                mTvTip.setText("广告未加载");
            }

            @Override
            public void onRewardedVideoAdPlayClicked(TUAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdPlayClicked:"+getThreadName());
                Toast.makeText(getBaseContext(), "onRewardedVideoAdPlayClicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReward(TUAdInfo entity) {
                Log.i(TAG, "onReward: onReward--"+getThreadName());
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_video_load) {
            loadAd();
        } else if (id == R.id.tv_video_show) {
            if (mVideoAD.isAdReady()) {
                mVideoAD.show(this);
            } else {
                loadAd();
            }
        }
    }

    /**
     * 加载广告
     */
    public void loadAd() {
        mTvTip.setText("广告加载中...");
        startTime = System.currentTimeMillis();
        mVideoAD.load();
    }

    private String getThreadName() {
        return Thread.currentThread().getName();
    }

}