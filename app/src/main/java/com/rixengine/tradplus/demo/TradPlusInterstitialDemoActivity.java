package com.rixengine.tradplus.demo;

import android.annotation.SuppressLint;
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
import com.tradplus.ads.open.interstitial.InterstitialAdListener;
import com.tradplus.ads.open.interstitial.TPInterstitial;

@SuppressLint("LongLogTag")
public class TradPlusInterstitialDemoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TradPlusInterstitialDemo";

    private TextView mTvTip;
    private TextView mTvVideoShow;
    private TPInterstitial mAdObj;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tradplus_interstitial_demo);
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
            if (mAdObj != null && mAdObj.isReady()) {
                mAdObj.showAd(this, null);
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
        mAdObj = new TPInterstitial(this, AppConfig.TRAD_PLUS_INTERSTITIAL_AD);
        mAdObj.setAdListener(new InterstitialAdListener() {

            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdLoaded:" + getThreadName());
                Toast.makeText(getBaseContext(), "Interstitial AD load success", Toast.LENGTH_SHORT).show();
                mTvTip.setText("Interstitial AD load success--Consume time--" + (System.currentTimeMillis() - startTime) / 1000 + "-秒");
                mTvVideoShow.setEnabled(true);
            }

            @Override
            public void onAdFailed(TPAdError tpAdError) {
                Log.i(TAG, "onAdFailed： " + tpAdError.getErrorCode() + " " + tpAdError.getErrorMsg() + ";" + getThreadName());
                Toast.makeText(getBaseContext(), "Interstitial AD load fail", Toast.LENGTH_SHORT).show();
                mTvTip.setText("Interstitial AD load fail");
                mTvVideoShow.setEnabled(false);
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClicked:" + getThreadName());
                Toast.makeText(getBaseContext(), "Interstitial AD clicked", Toast.LENGTH_SHORT).show();
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
            public void onAdVideoError(TPAdInfo tpAdInfo, TPAdError tpAdError) {
                Log.i(TAG, "onAdVideoError:" + tpAdError.getErrorCode() + " " + tpAdError.getErrorMsg() + ";" + getThreadName());
            }

            @Override
            public void onAdVideoStart(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdVideoStart");
            }

            @Override
            public void onAdVideoEnd(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdVideoEnd");
            }
        });
        mAdObj.loadAd();
    }

    private String getThreadName() {
        return Thread.currentThread().getName();
    }
}