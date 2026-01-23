package com.ironsource.adapters.custom.algorix;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.adunit.adapter.BaseBanner;
import com.ironsource.mediationsdk.adunit.adapter.listener.BannerAdListener;
import com.ironsource.mediationsdk.adunit.adapter.listener.NetworkInitializationListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType;
import com.ironsource.mediationsdk.model.NetworkSettings;
import com.rixengine.api.AlxBannerView;
import com.rixengine.api.AlxBannerViewAdListener;

public class AlgoriXCustomBanner extends BaseBanner<AlgoriXCustomAdapter> {
    public static final String TAG = "AlgoriXCustomBanner";

    AlgoriXCustomAdapter algoriXCustomAdapter = getNetworkAdapter();
    private AlxBannerView mBannerView;
    private BannerAdListener mListener;

    public AlgoriXCustomBanner(NetworkSettings networkSettings) {
        super(networkSettings);
    }

    @Override
    public void loadAd(final AdData adData, final Activity activity, ISBannerSize isBannerSize, BannerAdListener listener) {
        Log.d(TAG, "loadAd");
        mListener = listener;
        algoriXCustomAdapter.init(adData, activity, new NetworkInitializationListener() {
            @Override
            public void onInitSuccess() {
                String unitid = (String) adData.getConfiguration().get("unitid");
                Log.d(TAG, "onInitSuccess: unitid :" + unitid);
                if (TextUtils.isEmpty(unitid)) {
                    if (mListener != null) {
                        mListener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL, 1, "unitid is empty");
                    }
                    return;
                }
                requestBanner(unitid, activity);
            }

            @Override
            public void onInitFailed(int i, String s) {
                Log.d(TAG, "onInitFailed: errorCode=" + i + ";errorMsg=" + s);
                if (mListener != null) {
                    mListener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL, i, s);
                }
            }
        });
    }

    private void requestBanner(String unitid, Activity activity) {
        mBannerView = new AlxBannerView(activity);
        mBannerView.setBannerRefresh(0);
        AlxBannerViewAdListener alxBannerADListener = new AlxBannerViewAdListener() {
            @Override
            public void onAdLoaded() {
                if (mListener != null) {
                    FrameLayout.LayoutParams params;
                    if (mBannerView.getLayoutParams() != null && mBannerView.getLayoutParams() instanceof FrameLayout.LayoutParams) {
                        params = (FrameLayout.LayoutParams) mBannerView.getLayoutParams();
                    } else {
                        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        params.gravity = Gravity.CENTER;
                    }
                    mListener.onAdLoadSuccess(mBannerView, params);
                }
            }

            @Override
            public void onAdError(int errorCode, String errorMsg) {
                if (mListener != null) {
                    mListener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL, errorCode, errorMsg);
                }
            }

            @Override
            public void onAdClicked() {
                if (mListener != null) {
                    mListener.onAdClicked();
                    mListener.onAdLeftApplication();
                }
            }

            @Override
            public void onAdShow() {
                if (mListener != null) {
                    mListener.onAdOpened();
                }
            }

            @Override
            public void onAdClose() {
            }
        };
        mBannerView.loadAd(unitid, alxBannerADListener);
    }

    @Override
    public void destroyAd(AdData adData) {
        if (mBannerView != null) {
            mBannerView.destroy();
        }
    }
}
