package com.rixengine.topon.demo;

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
import com.thinkup.interstitial.api.TUInterstitial;
import com.thinkup.interstitial.api.TUInterstitialListener;


public class TopOnInterstitialDemoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TopOnInterstitialDemo";
    private TextView mTvTip;
    private TextView mTvVideoShow;
    private TUInterstitial mAD;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topon_interstitial_demo);
        initView();
    }

    private void initView() {
        TextView tv_video_load = findViewById(R.id.tv_video_load);
        mTvVideoShow = findViewById(R.id.tv_video_show);
        mTvTip = findViewById(R.id.tv_tip);
        mTvVideoShow.setEnabled(false);
        tv_video_load.setOnClickListener(this);
        mTvVideoShow.setOnClickListener(this);
        mAD = new TUInterstitial(this, AppConfig.TOPON_INTERSTITIAL_PID);

        mAD.setAdListener(new TUInterstitialListener() {

            @Override
            public void onInterstitialAdLoaded() {
                Log.i(TAG, "onInterstitialAdLoaded:InterstitialAd ad loads success" +getThreadName());
                Toast.makeText(getBaseContext(), "InterstitialAd ad loads success", Toast.LENGTH_SHORT).show();
                mTvTip.setText("InterstitialAd ad loads success--Consume time--" + (System.currentTimeMillis() - startTime) / 1000 + "-秒");
                mTvVideoShow.setEnabled(true);
            }

            @Override
            public void onInterstitialAdLoadFail(AdError adError) {
                Log.e(TAG, "onInterstitialAdLoadFail:"+adError.getCode()+";"+adError.getDesc()+";"+getThreadName());
                Toast.makeText(getBaseContext(), "InterstitialAd ad loads fail", Toast.LENGTH_SHORT).show();
                mTvTip.setText("InterstitialAd ad loads fail");
                mTvVideoShow.setEnabled(false);
            }

            @Override
            public void onInterstitialAdClicked(TUAdInfo atAdInfo) {
                Log.i(TAG, "onInterstitialAdClicked:"+getThreadName());
                Toast.makeText(getBaseContext(), "InterstitialAd ad cliced", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInterstitialAdShow(TUAdInfo atAdInfo) {
                Log.i(TAG, "onInterstitialAdShow:"+getThreadName());
            }

            @Override
            public void onInterstitialAdClose(TUAdInfo atAdInfo) {
                Log.i(TAG, "onInterstitialAdClose:"+getThreadName());
                mTvVideoShow.setEnabled(false);
                mTvTip.setText("InterstitialAd ad not load");
            }

            @Override
            public void onInterstitialAdVideoStart(TUAdInfo atAdInfo) {
                Log.i(TAG, "onInterstitialAdVideoStart:"+getThreadName());
                Toast.makeText(getBaseContext(), "InterstitialAd ad show success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInterstitialAdVideoEnd(TUAdInfo atAdInfo) {
                Log.i(TAG, "onInterstitialAdVideoEnd:"+getThreadName());
                Toast.makeText(getBaseContext(), "onInterstitialAdVideoEnd  ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInterstitialAdVideoError(AdError adError) {
                Log.i(TAG, "onInterstitialAdVideoError:"+adError.getCode()+";"+adError.getDesc()+";"+getThreadName());
            }

        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_video_load) {
            loadAd();
        } else if (id == R.id.tv_video_show) {
            if (!mAD.isAdReady()) {
                Toast.makeText(this, "Please load AD first", Toast.LENGTH_SHORT).show();
                return;
            }
            mAD.show(this);
        }
    }

    /**
     * 加载广告
     */
    public void loadAd() {
        mTvTip.setText("The ad loading...");
        startTime = System.currentTimeMillis();
        mAD.load();
    }

    private String getThreadName(){
        return Thread.currentThread().getName();
    }

}
