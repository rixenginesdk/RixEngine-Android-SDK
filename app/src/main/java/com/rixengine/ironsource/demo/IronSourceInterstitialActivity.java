package com.rixengine.ironsource.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.integration.IntegrationHelper;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.rixengine.R;

public class IronSourceInterstitialActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "IronSourceInterstitial";
    private IronSource mIronSource;
    private TextView mTvTip;
    private TextView mTvVideoShow;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iron_sourcxe_interstitial);
        initView();
        String advertisingId = IronSource.getAdvertiserId(IronSourceInterstitialActivity.this);
        // we're using an advertisingId as the 'userId'
        //initIronSource(APP_KEY, advertisingId);
        Log.d(TAG,"advertisid : "+advertisingId);
        IntegrationHelper.validateIntegration(this);
        IronSource.setUserId(advertisingId);
        IronSource.getAdvertiserId(this);
        //Network Connectivity Status
        IronSource.shouldTrackNetworkState(this, true);
       // IronSource.init(this, "6315421",IronSource.AD_UNIT.INTERSTITIAL);
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
            bnLoad();
        } else if (id == R.id.tv_video_show) {
            bnShow();
        }
    }

    private void bnLoad() {
        mTvTip.setText("The ad is loading...");
        startTime = System.currentTimeMillis();
        mTvVideoShow.setEnabled(false);
        IronSource.setInterstitialListener(new InterstitialListener() {
            @Override
            public void onInterstitialAdReady() {
                Log.d(TAG, "onAdLoaded--InterstitialAd ad loads success");
                Toast.makeText(getBaseContext(), "InterstitialAd ad loads success", Toast.LENGTH_SHORT).show();
                mTvTip.setText("InterstitialAd ad loads success--Consume time--" + (System.currentTimeMillis() - startTime) / 1000 + "-秒");
                mTvVideoShow.setEnabled(true);
            }

            @Override
            public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {
                Log.d(TAG, "InterstitialAd ad loads fail" + ironSourceError);
                Toast.makeText(getBaseContext(), "InterstitialAd ad loads fail", Toast.LENGTH_SHORT).show();
                mTvTip.setText("InterstitialAd ad loads fail" + ironSourceError.toString());
                mTvVideoShow.setEnabled(false);
            }

            @Override
            public void onInterstitialAdOpened() {

            }

            @Override
            public void onInterstitialAdClosed() {
                Log.d(TAG, "onInterstitialAdClosed");
            }

            @Override
            public void onInterstitialAdShowSucceeded() {
                Log.d(TAG, "onInterstitialAdShowSucceeded");
            }

            @Override
            public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {
                Log.d(TAG, "onInterstitialAdShowFailed: " + ironSourceError.toString());
            }

            @Override
            public void onInterstitialAdClicked() {
                Log.d(TAG, "onInterstitialAdClicked");
            }
        });
        IronSource.loadInterstitial();
    }


    private void bnShow() {
        if (IronSource.isInterstitialReady()) {
            IronSource.showInterstitial();
        }
    }

    protected void onResume() {
        super.onResume();
        IronSource.onResume(this);
    }

    protected void onPause() {
        super.onPause();
        IronSource.onPause(this);
    }
}