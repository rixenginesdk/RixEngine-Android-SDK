package com.alxad.sdk.demo.tradplus;

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
import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.open.interstitial.InterstitialAdListener;
import com.tradplus.ads.open.interstitial.TPInterstitial;

@SuppressLint("LongLogTag")
public class TradPlusInterstitialActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "TradPlusInterstitialDemo";

    private TextView mTvClearLog;
    private TextView mTvShowLog;
    private TextView mTvShow;
    private TPInterstitial mAdObj;
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
        mAdObj = new TPInterstitial(this, AdConfig.TRAD_PLUS_INTERSTITIAL_AD);
        mAdObj.setAdListener(new InterstitialAdListener() {

            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdLoaded:" + getCurrentThreadName());
                showLogMessage("onAdLoaded");
                showLogMessage(getString(R.string.format_load_success, (System.currentTimeMillis() - startTime) / 1000));
                mTvShow.setEnabled(true);
            }

            @Override
            public void onAdFailed(TPAdError tpAdError) {
                String msg = "errorCode=" + tpAdError.getErrorCode() + ";errorMsg=" + tpAdError.getErrorMsg();
                Log.i(TAG, "onAdFailed：" + msg + ";" + getCurrentThreadName());
                showLogMessage("onAdFailed");
                showLogMessage(getString(R.string.format_load_failed, msg));
                mTvShow.setEnabled(false);
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClicked:" + getCurrentThreadName());
                showLogMessage("onAdClicked");
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdImpression:" + getCurrentThreadName());
                showLogMessage("onAdImpression");
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClosed:" + getCurrentThreadName());
                showLogMessage("onAdClosed");
            }

            @Override
            public void onAdVideoError(TPAdInfo tpAdInfo, TPAdError tpAdError) {
                String msg = "errorCode=" + tpAdError.getErrorCode() + ";errorMsg=" + tpAdError.getErrorMsg();
                Log.i(TAG, "onAdVideoError：" + msg + ";" + getCurrentThreadName());
                showLogMessage("onAdVideoError：" + msg);
            }

            @Override
            public void onAdVideoStart(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdVideoStart");
                showLogMessage("onAdVideoStart");
            }

            @Override
            public void onAdVideoEnd(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdVideoEnd");
                showLogMessage("onAdVideoEnd");
            }
        });
        mAdObj.loadAd();
    }

    private void showAd() {
        if (mAdObj == null) {
            Toast.makeText(this, getString(R.string.show_ad_no_load), Toast.LENGTH_SHORT).show();
            return;
        }
        if (mAdObj.isReady()) {
            mAdObj.showAd(this, null);
        } else {
            Toast.makeText(this, "isReady()==false", Toast.LENGTH_SHORT).show();
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