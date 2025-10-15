package com.rixengine.ironsource.demo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.integration.IntegrationHelper;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.LevelPlayBannerListener;
import com.rixengine.R;

public class IronSourceBannerActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "IronSourceBanner";

    private FrameLayout mAdContainerView;
    private View mBnLoad;
    private TextView mTvTip;
    private IronSourceBannerLayout bannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iron_source_banner);
        initView();
        initIronSource();
    }

    private void initView() {
        mAdContainerView = (FrameLayout) findViewById(R.id.ad_container);
        mTvTip = findViewById(R.id.tv_tip);
        mBnLoad = findViewById(R.id.bn_load);
        mBnLoad.setOnClickListener(this);
    }

    private void initIronSource() {
        String advertisingId = IronSource.getAdvertiserId(IronSourceBannerActivity.this);
        // we're using an advertisingId as the 'userId'
        //initIronSource(APP_KEY, advertisingId);
        Log.d(TAG, "advertisid : " + advertisingId);
        IronSource.setAdaptersDebug(true);
        IntegrationHelper.validateIntegration(this);
        IronSource.setUserId(advertisingId);
        IronSource.getAdvertiserId(this);
        //Network Connectivity Status
        IronSource.shouldTrackNetworkState(this, true);
//         IronSource.init(this, "6315421",IronSource.AD_UNIT.INTERSTITIAL);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bn_load) {
            loadAd();
        }
    }

    private void loadAd() {
        mTvTip.setText(R.string.loading);
        mBnLoad.setEnabled(false);

        if (bannerView != null) {
            IronSource.destroyBanner(bannerView);
        }

        bannerView = IronSource.createBanner(this, ISBannerSize.BANNER);
        bannerView.setLevelPlayBannerListener(new LevelPlayBannerListener() {
            @Override
            public void onAdLoaded(AdInfo adInfo) {
                Log.d(TAG, "onAdLoaded");
                mBnLoad.setEnabled(true);
                mTvTip.setText(R.string.load_success);
                showAd();
            }

            @SuppressLint("StringFormatInvalid")
            @Override
            public void onAdLoadFailed(IronSourceError ironSourceError) {
                String msg = ironSourceError.getErrorCode() + ":" + ironSourceError.getErrorMessage();
                Log.d(TAG, "onAdLoadFailed" + msg);
                mBnLoad.setEnabled(true);
                mTvTip.setText(getString(R.string.load_failed, msg));
                Toast.makeText(getBaseContext(), getString(R.string.load_failed), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClicked(AdInfo adInfo) {
                Log.d(TAG, "onAdClicked");
            }

            @Override
            public void onAdLeftApplication(AdInfo adInfo) {
                Log.d(TAG, "onAdLeftApplication");
            }

            @Override
            public void onAdScreenPresented(AdInfo adInfo) {
                Log.d(TAG, "onAdScreenPresented");
            }

            @Override
            public void onAdScreenDismissed(AdInfo adInfo) {
                Log.d(TAG, "onAdScreenDismissed");
            }
        });

        IronSource.loadBanner(bannerView);
    }

    private void showAd() {
        mAdContainerView.removeAllViews();
        if (bannerView != null) {
            mAdContainerView.addView(bannerView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bannerView != null) {
            IronSource.destroyBanner(bannerView);
        }
    }


}