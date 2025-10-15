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
import com.rixengine.api.AlxInterstitialAD;
import com.rixengine.api.AlxInterstitialADListener;

public class InterstitialActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AlxInterstitialDemo";

    private TextView mTvTip;
    private TextView mTvShow;
    private AlxInterstitialAD mInterstitialAD;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);
        initView();
    }

    private void initView() {
        TextView tv_video_load = findViewById(R.id.tv_load);
        mTvShow = findViewById(R.id.tv_show);
        mTvTip = findViewById(R.id.tv_tip);
        mTvShow.setEnabled(false);
        tv_video_load.setOnClickListener(this);
        mTvShow.setOnClickListener(this);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_load:
                loadAd();
                break;
            case R.id.tv_show:
                if (mInterstitialAD != null && mInterstitialAD.isReady()) {
                    mInterstitialAD.show(this);
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

        mInterstitialAD = new AlxInterstitialAD();
        mInterstitialAD.load(this, AppConfig.ALX_INTERSTITIAL_AD_PID, new AlxInterstitialADListener() {

            @Override
            public void onInterstitialAdLoaded() {
                Log.i(TAG, "onInterstitialAdLoaded");
                Toast.makeText(getBaseContext(), "Ad loaded success", Toast.LENGTH_SHORT).show();
                mTvTip.setText("Ad loaded success-Time-consuming" + (System.currentTimeMillis() - startTime) / 1000 + "-秒 | 单价：" +
                        mInterstitialAD.getPrice());
                mTvShow.setEnabled(true);
                mInterstitialAD.reportChargingUrl();
                mInterstitialAD.reportBiddingUrl();
            }

            @Override
            public void onInterstitialAdLoadFail(int errorCode, String errorMsg) {
                Log.i(TAG, "onInterstitialAdLoadFail:  " + errorCode + " " + errorMsg);
                Toast.makeText(getBaseContext(), "Ad loaded fail", Toast.LENGTH_SHORT).show();
                mTvTip.setText("Ad loaded fail");
                mTvShow.setEnabled(false);
            }

            @Override
            public void onInterstitialAdClicked() {
                Log.i(TAG, "onInterstitialAdClicked");
                Toast.makeText(getBaseContext(), "click", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInterstitialAdShow() {
                Log.i(TAG, "onInterstitialAdShow");
            }

            @Override
            public void onInterstitialAdClose() {
                Log.i(TAG, "onInterstitialAdClose");
                mTvShow.setEnabled(false);
                mTvTip.setText("Ad not loaded");
            }

            @Override
            public void onInterstitialAdVideoStart() {
                Log.i(TAG, "onInterstitialAdVideoStart");
            }

            @Override
            public void onInterstitialAdVideoEnd() {
                Log.i(TAG, "onInterstitialAdVideoEnd: ");
            }

            @Override
            public void onInterstitialAdVideoError(int errorCode, String errorMsg) {
                Log.i(TAG, "onInterstitialAdVideoError:  " + errorCode + "," + errorMsg);
            }

        });

    }
}