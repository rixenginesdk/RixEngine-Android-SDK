package com.alxad.sdk.demo.alx;

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
import com.rixengine.api.AlxAdParam;
import com.rixengine.api.AlxSplashAd;
import com.rixengine.api.AlxSplashAdListener;

import java.util.HashMap;
import java.util.Map;

public class SplashBannerActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "AlxSplashBannerActivity";

    private TextView mAdInfo;
    private TextView mTvClearLog;
    private TextView mTvShowLog;
    private TextView mTvShow;
    private AlxSplashAd mSplashAd;
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
        mAdInfo = (TextView) findViewById(R.id.ad_info);
        mTvClearLog = (TextView) findViewById(R.id.tv_clear_log);
        mTvShowLog = (TextView) findViewById(R.id.tv_show_log);
        mTvShow.setEnabled(false);

        mTvShowLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTvClearLog.setOnClickListener(this);
        tv_load.setOnClickListener(this);
        mTvShow.setOnClickListener(this);

        mAdInfo.setVisibility(View.VISIBLE);
        setAdInfo(AdConfig.ALX_SPLASH_BANNER_AD_ID);
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
     * load Ad
     */
    public void loadAd() {
        showLogMessage(getString(R.string.loading));
        startTime = System.currentTimeMillis();

        mSplashAd = new AlxSplashAd();
        Map<String, String> userExtras = new HashMap<>();
        userExtras.put("bid_floor", "1.5");
        AlxAdParam.Builder builder = new AlxAdParam.Builder().setUserExtras(userExtras);
        mSplashAd.load(this, AdConfig.ALX_SPLASH_BANNER_AD_ID, builder.build(), new AlxSplashAdListener() {

            @Override
            public void onAdLoaded() {
                Log.i(TAG, "onAdLoaded");
                mTvShow.setEnabled(true);
                showLogMessage("onAdLoaded");
                showLogMessage(getString(R.string.format_load_success, (System.currentTimeMillis() - startTime) / 1000) + "｜ ecpm:" + mSplashAd.getPrice());

                mSplashAd.reportChargingUrl();
                mSplashAd.reportBiddingUrl();
            }

            @Override
            public void onAdLoadFail(int errorCode, String errorMsg) {
                Log.i(TAG, "onAdLoadFail:  " + errorCode + " " + errorMsg);
                mTvShow.setEnabled(false);
                String msg = "errorCode=" + errorCode + ";errorMsg=" + errorMsg;
                showLogMessage("onAdLoadFail");
                showLogMessage(getString(R.string.format_load_failed, msg));
            }

            @Override
            public void onAdClicked() {
                Log.i(TAG, "onAdClicked");
                showLogMessage("onAdClicked");
            }

            @Override
            public void onAdShow() {
                Log.i(TAG, "onAdShow");
                showLogMessage("onAdShow");
            }

            @Override
            public void onAdClose() {
                Log.i(TAG, "onAdClose");
                showLogMessage("onAdClose");
            }

            @Override
            public void onAdVideoStart() {
                Log.i(TAG, "onAdVideoStart");
                showLogMessage("onAdVideoStart");
            }

            @Override
            public void onAdVideoEnd() {
                Log.i(TAG, "onAdVideoEnd");
                showLogMessage("onAdVideoEnd");
            }

            @Override
            public void onAdVideoError(int errorCode, String errorMsg) {
                Log.i(TAG, "onAdVideoError:  " + errorCode + "," + errorMsg);
                showLogMessage("onAdVideoError:  " + errorCode + "," + errorMsg);
            }

        });

    }

    private void showAd() {
        if (mSplashAd == null || !mSplashAd.isReady()) {
            Toast.makeText(this, getString(R.string.show_ad_no_load), Toast.LENGTH_SHORT).show();
            return;
        }
        mSplashAd.show(this);
    }

    private void clearLog() {
        mTvShowLog.setText("");
    }

    private void showLogMessage(String msg) {
        mTvShowLog.append(msg);
        mTvShowLog.append("\r\n");
    }

    private void setAdInfo(String unitId){
        mAdInfo.setText(getString(R.string.format_ad_unitid, getString(R.string.splash_ad), unitId));
    }

}