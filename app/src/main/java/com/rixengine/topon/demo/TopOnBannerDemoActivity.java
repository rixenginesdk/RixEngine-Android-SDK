package com.rixengine.topon.demo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.rixengine.AppConfig;
import com.rixengine.R;
import com.thinkup.banner.api.TUBannerListener;
import com.thinkup.banner.api.TUBannerView;
import com.thinkup.core.api.AdError;
import com.thinkup.core.api.TUAdInfo;

public class TopOnBannerDemoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TopOnBannerDemoActivity";

    private FrameLayout mAdContainerView;
    private View mBnLoad;
    private TextView mTvTip;
    TUBannerView bannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topon_banner_demo);
        initView();
    }

    private void initView() {
        mAdContainerView = (FrameLayout) findViewById(R.id.ad_container);
        mTvTip = findViewById(R.id.tv_tip);
        mBnLoad = findViewById(R.id.bn_load);
        mBnLoad.setOnClickListener(this);
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
            bannerView.destroy();
        }

        bannerView = new TUBannerView(this);
        bannerView.setPlacementId(AppConfig.TOPON_BANNER_AD_PID);
        bannerView.setBannerAdListener(new TUBannerListener() {
            @Override
            public void onBannerLoaded() {
                Log.d(TAG, "onBannerLoaded");
                mBnLoad.setEnabled(true);
                mTvTip.setText(R.string.load_success);
                showAd();
            }

            @SuppressLint("StringFormatInvalid")
            @Override
            public void onBannerFailed(AdError adError) {
                mBnLoad.setEnabled(true);
                String msg = adError.getCode() + ":" + adError.getDesc();
                Log.d(TAG, "onBannerFailed:" + msg);
                mTvTip.setText(getString(R.string.load_failed, msg));
                Toast.makeText(getBaseContext(), getString(R.string.load_failed), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBannerClicked(TUAdInfo atAdInfo) {
                Log.d(TAG, "onBannerClicked");
            }

            @Override
            public void onBannerShow(TUAdInfo atAdInfo) {
                Log.d(TAG, "onBannerShow");
            }

            @Override
            public void onBannerClose(TUAdInfo atAdInfo) {
                Log.d(TAG, "onBannerClose");
            }

            @Override
            public void onBannerAutoRefreshed(TUAdInfo atAdInfo) {
                Log.d(TAG, "onBannerAutoRefreshed");
            }

            @Override
            public void onBannerAutoRefreshFail(AdError adError) {
                Log.d(TAG, "onBannerAutoRefreshFail:" + adError.getCode() + "," + adError.getDesc());
            }
        });
        bannerView.loadAd();
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
            bannerView.destroy();
        }
    }
}
