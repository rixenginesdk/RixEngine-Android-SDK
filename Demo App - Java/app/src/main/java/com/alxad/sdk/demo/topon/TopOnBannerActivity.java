package com.alxad.sdk.demo.topon;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.R;
import com.secmtp.sdk.banner.api.ATBannerListener;
import com.secmtp.sdk.banner.api.ATBannerView;
import com.secmtp.sdk.core.api.ATAdInfo;
import com.secmtp.sdk.core.api.AdError;

public class TopOnBannerActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "TopOnBannerActivity";

    private FrameLayout mAdContainerView;
    private View mBnLoad;
    private TextView mTvClearLog;
    private TextView mTvShowLog;
    ATBannerView bannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_ads);
        setActionBar();
        initView();
    }

    private void initView() {
        mAdContainerView = (FrameLayout) findViewById(R.id.ad_container);
        mTvClearLog = (TextView) findViewById(R.id.tv_clear_log);
        mTvShowLog = (TextView) findViewById(R.id.tv_show_log);
        mBnLoad = findViewById(R.id.bn_load);

        mTvShowLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTvClearLog.setOnClickListener(this);
        mBnLoad.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bn_load) {
            loadAd();
        } else if (v.getId() == R.id.tv_clear_log) {
            clearLog();
        }
    }

    private void loadAd() {
        showLogMessage(getString(R.string.loading));
        mBnLoad.setEnabled(false);

        if (bannerView != null) {
            bannerView.destroy();
        }

        bannerView = new ATBannerView(this);
        bannerView.setPlacementId(AdConfig.TOPON_BANNER_AD_ID);
        bannerView.setBannerAdListener(new ATBannerListener() {
            @Override
            public void onBannerLoaded() {
                Log.d(TAG, "onBannerLoaded");
                mBnLoad.setEnabled(true);
                showLogMessage("onBannerLoaded");
                showLogMessage(getString(R.string.load_success));
                showAd();
            }

            @Override
            public void onBannerFailed(AdError adError) {
                mBnLoad.setEnabled(true);
                String msg = "errorCode=" + adError.getCode() + ";errorMsg=" + adError.getDesc();
                Log.d(TAG, "onBannerFailed:" + msg);
                showLogMessage("onBannerFailed");
                showLogMessage(getString(R.string.format_load_failed, msg));
            }

            @Override
            public void onBannerClicked(ATAdInfo atAdInfo) {
                Log.d(TAG, "onBannerClicked");
                showLogMessage("onBannerClicked");
            }

            @Override
            public void onBannerShow(ATAdInfo atAdInfo) {
                Log.d(TAG, "onBannerShow");
                showLogMessage("onBannerShow");
            }

            @Override
            public void onBannerClose(ATAdInfo atAdInfo) {
                Log.d(TAG, "onBannerClose");
                showLogMessage("onBannerClose");
            }

            @Override
            public void onBannerAutoRefreshed(ATAdInfo atAdInfo) {
                Log.d(TAG, "onBannerAutoRefreshed");
                showLogMessage("onBannerAutoRefreshed");
            }

            @Override
            public void onBannerAutoRefreshFail(AdError adError) {
                String msg = "errorCode=" + adError.getCode() + ";errorMsg=" + adError.getDesc();
                Log.d(TAG, "onBannerAutoRefreshFail:" + msg);
                showLogMessage("onBannerAutoRefreshFail:" + msg);
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

    private void clearLog() {
        mTvShowLog.setText("");
    }

    private void showLogMessage(String msg) {
        mTvShowLog.append(msg);
        mTvShowLog.append("\r\n");
    }
}
