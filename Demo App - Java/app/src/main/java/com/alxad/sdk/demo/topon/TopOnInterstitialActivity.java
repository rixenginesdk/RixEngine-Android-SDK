package com.alxad.sdk.demo.topon;

import android.annotation.SuppressLint;
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
import com.secmtp.sdk.interstitial.api.ATInterstitial;
import com.secmtp.sdk.interstitial.api.ATInterstitialListener;

public class TopOnInterstitialActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "TopOnInterstitialActivity";
    private TextView mTvClearLog;
    private TextView mTvShowLog;
    private TextView mTvShow;
    private ATInterstitial mAD;
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

        mAD = new ATInterstitial(this, AdConfig.TOPON_INTERSTITIAL_ID);
        mAD.setAdListener(new ATInterstitialListener() {

            @Override
            public void onInterstitialAdLoaded() {
                Log.i(TAG, "onInterstitialAdLoaded:" + getCurrentThreadName());
                showLogMessage("onInterstitialAdLoaded");
                showLogMessage(getString(R.string.format_load_success, (System.currentTimeMillis() - startTime) / 1000));
                mTvShow.setEnabled(true);
            }

            @Override
            public void onInterstitialAdLoadFail(AdError adError) {
                String msg = "errorCode=" + adError.getCode() + ";errorMsg=" + adError.getDesc();
                Log.d(TAG, "onInterstitialAdLoadFail:" + msg);
                showLogMessage("onInterstitialAdLoadFail");
                showLogMessage(getString(R.string.format_load_failed, msg));

                mTvShow.setEnabled(false);
            }

            @Override
            public void onInterstitialAdClicked(ATAdInfo atAdInfo) {
                Log.i(TAG, "onInterstitialAdClicked:" + getCurrentThreadName());
                showLogMessage("onInterstitialAdClicked");
            }

            @Override
            public void onInterstitialAdShow(ATAdInfo atAdInfo) {
                Log.i(TAG, "onInterstitialAdShow:" + getCurrentThreadName());
                showLogMessage("onInterstitialAdShow");
            }

            @Override
            public void onInterstitialAdClose(ATAdInfo atAdInfo) {
                Log.i(TAG, "onInterstitialAdClose:" + getCurrentThreadName());
                showLogMessage("onInterstitialAdClose");
                mTvShow.setEnabled(false);
            }

            @Override
            public void onInterstitialAdVideoStart(ATAdInfo atAdInfo) {
                Log.i(TAG, "onInterstitialAdVideoStart:" + getCurrentThreadName());
                showLogMessage("onInterstitialAdVideoStart");
            }

            @Override
            public void onInterstitialAdVideoEnd(ATAdInfo atAdInfo) {
                Log.i(TAG, "onInterstitialAdVideoEnd:" + getCurrentThreadName());
                showLogMessage("onInterstitialAdVideoEnd");
            }

            @Override
            public void onInterstitialAdVideoError(AdError adError) {
                String msg = "errorCode=" + adError.getCode() + ";errorMsg=" + adError.getDesc();
                Log.i(TAG, "onInterstitialAdVideoError:" + msg + ";" + getCurrentThreadName());
                showLogMessage("onInterstitialAdVideoError:" + msg);
            }

        });
        mAD.load();
    }

    private void showAd() {
        if (mAD == null) {
            Toast.makeText(this, getString(R.string.show_ad_no_load), Toast.LENGTH_SHORT).show();
            return;
        }
        if (mAD.isAdReady()) {
            mAD.show(this);
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
