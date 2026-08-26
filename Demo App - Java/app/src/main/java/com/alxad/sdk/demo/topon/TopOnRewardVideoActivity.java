package com.alxad.sdk.demo.topon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.R;
import com.secmtp.sdk.core.api.AdError;
import com.secmtp.sdk.core.api.ATAdInfo;
import com.secmtp.sdk.core.api.ATNetworkConfirmInfo;
import com.secmtp.sdk.rewardvideo.api.ATRewardVideoAd;
import com.secmtp.sdk.rewardvideo.api.ATRewardVideoExListener;

public class TopOnRewardVideoActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "TopOnRewardVideoActivity";
    private TextView mTvClearLog;
    private TextView mTvShowLog;
    private TextView mTvShow;
    private ATRewardVideoAd mVideoAD;
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
        if (v.getId() == R.id.tv_load) {
            loadAd();
        } else if (v.getId() == R.id.tv_show) {
            showAd();
        } else if (v.getId() == R.id.tv_clear_log) {
            clearLog();
        }
    }

    /**
     * 加载广告
     */
    public void loadAd() {
        showLogMessage(getString(R.string.loading));
        startTime = System.currentTimeMillis();

        mVideoAD = new ATRewardVideoAd(this, AdConfig.TOPON_VIDEO_AD_ID);
        mVideoAD.setAdListener(new ATRewardVideoExListener() {

            @Override
            public void onRewardFailed(ATAdInfo atAdInfo) {
                Log.d(TAG, "onRewardFailed:" + getCurrentThreadName());
                showLogMessage("onRewardFailed");
            }

            @Override
            public void onDeeplinkCallback(ATAdInfo adInfo, boolean isSuccess) {
                Log.i(TAG, "onDeeplinkCallback:" + adInfo.toString() + "--status:" + isSuccess + ";" + getCurrentThreadName());
                showLogMessage("onDeeplinkCallback");
            }

            @Override
            public void onDownloadConfirm(Context context, ATAdInfo tuAdInfo, ATNetworkConfirmInfo tuNetworkConfirmInfo) {
                showLogMessage("onDownloadConfirm");
            }

            @Override
            public void onRewardedVideoAdAgainPlayStart(ATAdInfo atAdInfo) {
                showLogMessage("onRewardedVideoAdAgainPlayStart");
            }

            @Override
            public void onRewardedVideoAdAgainPlayEnd(ATAdInfo atAdInfo) {
                showLogMessage("onRewardedVideoAdAgainPlayEnd");
            }

            @Override
            public void onRewardedVideoAdAgainPlayFailed(AdError adError, ATAdInfo atAdInfo) {
                String msg = "errorCode=" + adError.getCode() + ";errorMsg=" + adError.getDesc();
                Log.d(TAG, "onRewardedVideoAdAgainPlayFailed:" + msg);
                showLogMessage("onRewardedVideoAdAgainPlayFailed:" + msg);
            }

            @Override
            public void onRewardedVideoAdAgainPlayClicked(ATAdInfo atAdInfo) {
                Log.i(TAG, "onRewardedVideoAdAgainPlayClicked:" + getCurrentThreadName());
                showLogMessage("onRewardedVideoAdAgainPlayClicked");
            }

            @Override
            public void onAgainReward(ATAdInfo atAdInfo) {
                Log.i(TAG, "onAgainReward:" + getCurrentThreadName());
                showLogMessage("onAgainReward");
            }

            @Override
            public void onAgainRewardFailed(ATAdInfo atAdInfo) {
                Log.i(TAG, "onAgainRewardFailed:" + getCurrentThreadName());
                showLogMessage("onAgainRewardFailed");
            }

            @Override
            public void onRewardedVideoAdLoaded() {
                Log.i(TAG, "onRewardedVideoAdLoaded:" + getCurrentThreadName());
                showLogMessage("onRewardedVideoAdLoaded");
                showLogMessage(getString(R.string.format_load_success, (System.currentTimeMillis() - startTime) / 1000));

                mTvShow.setEnabled(true);
            }

            @Override
            public void onRewardedVideoAdFailed(AdError adError) {
                String msg = "errorCode=" + adError.getCode() + ";errorMsg=" + adError.getDesc();
                Log.d(TAG, "onInterstitialAdLoadFail:" + msg);
                showLogMessage("onInterstitialAdLoadFail");
                showLogMessage(getString(R.string.format_load_failed, msg));

                mTvShow.setEnabled(false);
            }

            @Override
            public void onRewardedVideoAdPlayStart(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdPlayStart:" + getCurrentThreadName());
                showLogMessage("onRewardedVideoAdPlayStart");
            }

            @Override
            public void onRewardedVideoAdPlayEnd(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdPlayEnd:" + getCurrentThreadName());
            }

            @Override
            public void onRewardedVideoAdPlayFailed(AdError errorCode, ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdPlayFailed:" + getCurrentThreadName());
            }

            @Override
            public void onRewardedVideoAdClosed(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdClosed:" + getCurrentThreadName());
                showLogMessage("onRewardedVideoAdClosed");
                mTvShow.setEnabled(false);
            }

            @Override
            public void onRewardedVideoAdPlayClicked(ATAdInfo entity) {
                Log.i(TAG, "onRewardedVideoAdPlayClicked:" + getCurrentThreadName());
                showLogMessage("onRewardedVideoAdPlayClicked");
            }

            @Override
            public void onReward(ATAdInfo entity) {
                Log.i(TAG, "onReward: " + getCurrentThreadName());
                showLogMessage("onReward");
            }
        });
        mVideoAD.load();
    }

    private void showAd() {
        if (mVideoAD == null) {
            Toast.makeText(this, getString(R.string.show_ad_no_load), Toast.LENGTH_SHORT).show();
            return;
        }
        if (mVideoAD.isAdReady()) {
            mVideoAD.show(this);
        } else {
            Toast.makeText(this, "isAdReady()==false", Toast.LENGTH_SHORT).show();
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