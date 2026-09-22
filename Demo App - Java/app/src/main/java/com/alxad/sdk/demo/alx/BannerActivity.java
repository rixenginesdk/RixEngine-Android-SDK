package com.alxad.sdk.demo.alx;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.R;
import com.rixengine.api.AlxAdParam;
import com.rixengine.api.AlxBannerView;
import com.rixengine.api.AlxBannerViewAdListener;

import java.util.HashMap;
import java.util.Map;

public class BannerActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "AlxBannerActivity";

    private TextView mBnLoad;
    private TextView mBnShow;

    private TextView mAdInfo;

    private TextView mTvClearLog;
    private TextView mTvShowLog;
    private TextView mBnLoadAndShow;

    private FrameLayout mAdContainer;
    private AlxBannerView mAlxBannerView;
    private AlxBannerView mAlxBannerView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        setActionBar();

        mAdInfo = (TextView) findViewById(R.id.ad_info);
        mTvClearLog = (TextView) findViewById(R.id.tv_clear_log);
        mTvShowLog = (TextView) findViewById(R.id.tv_show_log);
        mBnLoad = (TextView) findViewById(R.id.bn_load);
        mBnShow = (TextView) findViewById(R.id.bn_show);
        mBnLoadAndShow = (TextView) findViewById(R.id.bn_load_show);
        mAdContainer = (FrameLayout) findViewById(R.id.ad_container);
        mAlxBannerView = (AlxBannerView) findViewById(R.id.do_ad_banner);

        mTvShowLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTvClearLog.setOnClickListener(this);
        mBnLoad.setOnClickListener(this);
        mBnShow.setOnClickListener(this);
        mBnLoadAndShow.setOnClickListener(this);
        mBnShow.setEnabled(false);

        setAdInfo(AdConfig.ALX_BANNER_AD_ID);
    }

    @Override
    protected void onDestroy() {
        if (mAlxBannerView2 != null) {
            mAlxBannerView2.destroy();
        }
        if (mAlxBannerView != null) {
            mAlxBannerView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bn_load) {
            bnPreLoad();
        } else if (v.getId() == R.id.bn_show) {
            bnShow();
        } else if (v.getId() == R.id.bn_load_show) {
            bnLoadAndShow();
        } else if (v.getId() == R.id.tv_clear_log) {
            clearLog();
        }
    }

    private void bnPreLoad() {
        mBnLoad.setEnabled(false);
        final long startTime = System.currentTimeMillis();

        mAlxBannerView2 = new AlxBannerView(this);
        mAlxBannerView2.setBannerCanClose(false);
        mAlxBannerView2.setBannerRefresh(0);//[ZH]不自动刷新  |  [EN]No automatic refresh
        mAlxBannerView2.setVisibility(View.VISIBLE);
        Map<String, String> userExtras = new HashMap<>();
        userExtras.put("bid_floor", "1.5");
        AlxAdParam.Builder builder = new AlxAdParam.Builder().setUserExtras(userExtras);
        mAlxBannerView2.loadAd(AdConfig.ALX_BANNER_AD_ID, builder.build(), new AlxBannerViewAdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded");
                mBnShow.setEnabled(true);
                mBnLoad.setEnabled(true);
                showLogMessage("onAdLoaded");
                showLogMessage(getString(R.string.format_load_success, (System.currentTimeMillis() - startTime) / 1000) + "｜ ecpm:" + mAlxBannerView2.getPrice());

                mAlxBannerView2.reportBiddingUrl();
                mAlxBannerView2.reportChargingUrl();
            }

            @Override
            public void onAdError(int errorCode, String errorMsg) {
                Log.d(TAG, "onAdError: errorMsg=" + errorMsg + "  errorCode=" + errorCode);
                mBnShow.setEnabled(false);
                mBnLoad.setEnabled(true);
                String msg = "errorCode=" + errorCode + ";errorMsg=" + errorMsg;
                showLogMessage("onAdError");
                showLogMessage(getString(R.string.format_load_failed, msg));
            }

            @Override
            public void onAdClicked() {
                Log.d(TAG, "onAdClicked");
                showLogMessage("onAdClicked");
            }

            @Override
            public void onAdShow() {
                Log.d(TAG, "onAdShow");
                showLogMessage("onAdShow");
            }

            @Override
            public void onAdClose() {
                Log.d(TAG, "onAdClose");
                showLogMessage("onAdClose");
            }
        });
    }

    private void bnShow() {
        if (mAlxBannerView2 != null && mAlxBannerView2.isReady()) {
            mAdContainer.removeAllViews();
            mAdContainer.addView(mAlxBannerView2);
//            mTvTip.setText("");
        }
    }

    private void bnLoadAndShow() {
        final long startTime = System.currentTimeMillis();
        mAlxBannerView.setBannerCanClose(true);
        mAlxBannerView.loadAd(AdConfig.ALX_BANNER_AD_ID, new AlxBannerViewAdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded:  | ecpm：" + mAlxBannerView.getPrice());
                showLogMessage("onAdLoaded");
                showLogMessage(getString(R.string.format_load_success, (System.currentTimeMillis() - startTime) / 1000) + "｜ ecpm:" + mAlxBannerView.getPrice());
            }

            @Override
            public void onAdError(int errorCode, String errorMsg) {
                Log.d(TAG, "onAdError: errorMsg=" + errorMsg + "  errorCode=" + errorCode);
                String msg = "errorCode=" + errorCode + ";errorMsg=" + errorMsg;
                showLogMessage("onAdError");
                showLogMessage(getString(R.string.format_load_failed, msg));
            }

            @Override
            public void onAdClicked() {
                Log.d(TAG, "onAdClicked");
                showLogMessage("onAdClicked");
            }

            @Override
            public void onAdShow() {
                Log.d(TAG, "onAdShow");
                showLogMessage("onAdShow");
            }

            @Override
            public void onAdClose() {
                Log.d(TAG, "onAdClose");
                showLogMessage("onAdClose");
            }
        });
    }

    private void clearLog() {
        mTvShowLog.setText("");
    }

    private void showLogMessage(String msg) {
        mTvShowLog.append(msg);
        mTvShowLog.append("\r\n");
    }

    private void setAdInfo(String unitId){
        mAdInfo.setText(getString(R.string.format_ad_unitid, getString(R.string.banner_ad), unitId));
    }

}