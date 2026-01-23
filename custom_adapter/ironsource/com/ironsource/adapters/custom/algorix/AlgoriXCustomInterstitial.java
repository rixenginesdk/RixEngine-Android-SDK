package com.ironsource.adapters.custom.algorix;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.ironsource.mediationsdk.adunit.adapter.BaseInterstitial;
import com.ironsource.mediationsdk.adunit.adapter.listener.InterstitialAdListener;
import com.ironsource.mediationsdk.adunit.adapter.listener.NetworkInitializationListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType;
import com.ironsource.mediationsdk.model.NetworkSettings;
import com.rixengine.api.AlxInterstitialAD;
import com.rixengine.api.AlxInterstitialADListener;

/**
 * IronSource 激励广告适配器
 */
public class AlgoriXCustomInterstitial extends BaseInterstitial<AlgoriXCustomAdapter> {

    private static final String TAG = "AlgoriXCustomInterstitial";
    private AlxInterstitialAD alxInterstitialAD;
    private String unitid = "";
    AlgoriXCustomAdapter algoriXCustomAdapter = getNetworkAdapter();
    InterstitialAdListener mInterstitialAdListener;
    private Context mContext;

    public AlgoriXCustomInterstitial(NetworkSettings networkSettings) {
        super(networkSettings);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void loadAd(final AdData adData, Activity activity, InterstitialAdListener interstitialAdListener) {
        Log.d(TAG, "loadAd:");
        mContext = activity;
        try {
            mInterstitialAdListener = interstitialAdListener;
            algoriXCustomAdapter.init(adData, activity, new NetworkInitializationListener() {
                @Override
                public void onInitSuccess() {
                    unitid = (String) adData.getConfiguration().get("unitid");
                    Log.d(TAG, "onInitSuccess: unitid :" + unitid);

                    startAdLoad(mContext);
                }

                @SuppressLint("LongLogTag")
                @Override
                public void onInitFailed(int i, String s) {
                    Log.d(TAG, "Init Failed errCode:" + i + " errMsg: " + s);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("LongLogTag")
    private void startAdLoad(Context context) {
        Log.d(TAG, "startAdLoad:");
        alxInterstitialAD = new AlxInterstitialAD();
        alxInterstitialAD.load(context, unitid, new AlxInterstitialADListener() {

            @Override
            public void onInterstitialAdLoaded() {
                if (mInterstitialAdListener != null) {
                    mInterstitialAdListener.onAdLoadSuccess();
                }
            }

            @Override
            public void onInterstitialAdLoadFail(int errorCode, String errorMsg) {
                if (mInterstitialAdListener != null) {
                    mInterstitialAdListener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL, errorCode, errorMsg);
                }
            }

            @Override
            public void onInterstitialAdClicked() {
                if (mInterstitialAdListener != null) {
                    mInterstitialAdListener.onAdClicked();
                }
            }

            @Override
            public void onInterstitialAdShow() {
                if (mInterstitialAdListener != null) {
                    mInterstitialAdListener.onAdShowSuccess();
                }
            }

            @Override
            public void onInterstitialAdClose() {
                if (mInterstitialAdListener != null) {
                    mInterstitialAdListener.onAdClosed();
                }
            }

            @Override
            public void onInterstitialAdVideoStart() {
                if (mInterstitialAdListener != null) {
                }
            }

            @Override
            public void onInterstitialAdVideoEnd() {
                if (mInterstitialAdListener != null) {

                }
            }

            @Override
            public void onInterstitialAdVideoError(int errorCode, String errorMsg) {
                if (mInterstitialAdListener != null) {
                }
            }
        });
    }

    @SuppressLint("LongLogTag")
    @Override
    public void showAd(AdData adData, InterstitialAdListener interstitialAdListener) {
        if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            if (alxInterstitialAD != null) {
                alxInterstitialAD.show(activity);
            }
        } else {
            Log.e(TAG, "context is not an Activity");
        }

    }

    @Override
    public boolean isAdAvailable(AdData adData) {
        if (alxInterstitialAD != null) {
            return alxInterstitialAD.isReady();
        }
        return false;

    }

}