package com.alxad.sdk.demo.alx;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rixengine.api.AlxAdParam;
import com.rixengine.api.AlxInterstitialAD;
import com.rixengine.api.AlxInterstitialADListener;
import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.R;

import java.util.HashMap;
import java.util.Map;

public class InterstitialVideoActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "AlxInterstitialVideoActivity";

    private TextView mTvClearLog;
    private TextView mTvShowLog;
    private TextView mTvShow;
    private AlxInterstitialAD mInterstitialAD;
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
     * load Ad
     */
    public void loadAd() {
        showLogMessage(getString(R.string.loading));
        startTime = System.currentTimeMillis();

        Map<String, String> userExtras = new HashMap<>();
        userExtras.put("bid_floor", "1.5");
        AlxAdParam.Builder builder = new AlxAdParam.Builder().setUserExtras(userExtras);
        mInterstitialAD = new AlxInterstitialAD();
        mInterstitialAD.load(this, AdConfig.ALX_INTERSTITIAL_VIDEO_AD_ID, builder.build(), new AlxInterstitialADListener() {

            @Override
            public void onInterstitialAdLoaded() {
                Log.i(TAG, "onInterstitialAdLoaded");
                mTvShow.setEnabled(true);
                showLogMessage("onInterstitialAdLoaded");
                showLogMessage(getString(R.string.format_load_success, (System.currentTimeMillis() - startTime) / 1000) + "｜ ecpm:" + mInterstitialAD.getPrice());

                mInterstitialAD.reportChargingUrl();
                mInterstitialAD.reportBiddingUrl();
            }

            @Override
            public void onInterstitialAdLoadFail(int errorCode, String errorMsg) {
                Log.i(TAG, "onInterstitialAdLoadFail:  " + errorCode + " " + errorMsg);
                mTvShow.setEnabled(false);
                String msg = "errorCode=" + errorCode + ";errorMsg=" + errorMsg;
                showLogMessage("onInterstitialAdLoadFail");
                showLogMessage(getString(R.string.format_load_failed, msg));
            }

            @Override
            public void onInterstitialAdClicked() {
                Log.i(TAG, "onInterstitialAdClicked");
                showLogMessage("onInterstitialAdClicked");
            }

            @Override
            public void onInterstitialAdShow() {
                Log.i(TAG, "onInterstitialAdShow");
                showLogMessage("onInterstitialAdShow");
            }

            @Override
            public void onInterstitialAdClose() {
                Log.i(TAG, "onInterstitialAdClose");
                showLogMessage("onInterstitialAdClose");
            }

            @Override
            public void onInterstitialAdVideoStart() {
                Log.i(TAG, "onInterstitialAdVideoStart");
                showLogMessage("onInterstitialAdVideoStart");
            }

            @Override
            public void onInterstitialAdVideoEnd() {
                Log.i(TAG, "onInterstitialAdVideoEnd");
                showLogMessage("onInterstitialAdVideoEnd");
            }

            @Override
            public void onInterstitialAdVideoError(int errorCode, String errorMsg) {
                Log.i(TAG, "onInterstitialAdVideoError:  " + errorCode + "," + errorMsg);
                showLogMessage("onInterstitialAdVideoError:  " + errorCode + "," + errorMsg);
            }

        });

    }

    private void showAd() {
        if (mInterstitialAD == null || !mInterstitialAD.isReady()) {
            Toast.makeText(this, getString(R.string.show_ad_no_load), Toast.LENGTH_SHORT).show();
            return;
        }
        mInterstitialAD.show(this);
    }

    private void clearLog() {
        mTvShowLog.setText("");
    }

    private void showLogMessage(String msg) {
        mTvShowLog.append(msg);
        mTvShowLog.append("\r\n");
    }
}