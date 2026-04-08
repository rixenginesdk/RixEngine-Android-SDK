package com.alxad.sdk.demo.alx;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.R;
import com.rixengine.api.AlxBannerView;
import com.rixengine.api.AlxBannerViewAdListener;

public class BannerActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "AlxBannerActivity";

    private Button mBnLoad;
    private Button mBnShow;
    private TextView mTvTip;
    private Button mBnLoadAndShow;

    private FrameLayout mAdContainer;
    private AlxBannerView mAlxBannerView;
    private AlxBannerView mAlxBannerView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        setActionBar();
        checkNavigationBar(findViewById(R.id.root_view));

        mTvTip = (TextView) findViewById(R.id.tv_tip);
        mBnLoad = (Button) findViewById(R.id.bn_load);
        mBnShow = (Button) findViewById(R.id.bn_show);
        mBnLoadAndShow = (Button) findViewById(R.id.bn_load_show);
        mAdContainer = (FrameLayout) findViewById(R.id.ad_container);
        mAlxBannerView = (AlxBannerView) findViewById(R.id.do_ad_banner);

        mBnLoad.setOnClickListener(this);
        mBnShow.setOnClickListener(this);
        mBnLoadAndShow.setOnClickListener(this);
        mBnShow.setEnabled(false);
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
        }else if (v.getId() == R.id.bn_show) {
            bnShow();
        }else if (v.getId() == R.id.bn_load_show) {
            bnLoadAndShow();
        }
    }

    private void bnPreLoad() {
        mBnLoad.setEnabled(false);
        final long startTime = System.currentTimeMillis();

        mAlxBannerView2 = new AlxBannerView(this);
        mAlxBannerView2.setBannerCanClose(false);
        mAlxBannerView2.setBannerRefresh(0);//不自动刷新
        mAlxBannerView2.setVisibility(View.VISIBLE);
        mAlxBannerView2.loadAd(AdConfig.ALX_BANNER_AD_ID, new AlxBannerViewAdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded");
                mBnShow.setEnabled(true);
                mBnLoad.setEnabled(true);
                mTvTip.setText(getString(R.string.format_load_success, (System.currentTimeMillis() - startTime) / 1000) + "｜ ecpm:" + mAlxBannerView2.getPrice());

                mAlxBannerView2.reportBiddingUrl();
                mAlxBannerView2.reportChargingUrl();
            }

            @Override
            public void onAdError(int errorCode, String errorMsg) {
                Log.d(TAG, "onAdError: errorMsg=" + errorMsg + "  errorCode=" + errorCode);
                mBnShow.setEnabled(false);
                mBnLoad.setEnabled(true);
                mTvTip.setText(R.string.load_failed);
            }

            @Override
            public void onAdClicked() {
                Log.d(TAG, "onAdClicked");
            }

            @Override
            public void onAdShow() {
                Log.d(TAG, "onAdShow");
            }

            @Override
            public void onAdClose() {
                Log.d(TAG, "onAdClose");
            }
        });
    }

    private void bnShow() {
        if (mAlxBannerView2 != null && mAlxBannerView2.isReady()) {
            mAdContainer.removeAllViews();
            mAdContainer.addView(mAlxBannerView2);
            mTvTip.setText("");
        }
    }

    private void bnLoadAndShow() {
        mAlxBannerView.setBannerCanClose(true);
        mAlxBannerView.loadAd(AdConfig.ALX_BANNER_AD_ID, new AlxBannerViewAdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded:  | ecpm：" + mAlxBannerView.getPrice());
            }

            @Override
            public void onAdError(int errorCode, String errorMsg) {
                Log.d(TAG, "onAdError: errorMsg=" + errorMsg + "  errorCode=" + errorCode);
                Toast.makeText(getBaseContext(), getString(R.string.load_failed), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClicked() {
                Log.d(TAG, "onAdClicked");
            }

            @Override
            public void onAdShow() {
                Log.d(TAG, "onAdShow");
            }

            @Override
            public void onAdClose() {
                Log.d(TAG, "onAdClose");
            }
        });
    }

}