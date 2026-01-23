package com.ironsource.adapters.custom.algorix;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.ironsource.mediationsdk.adunit.adapter.BaseRewardedVideo;
import com.ironsource.mediationsdk.adunit.adapter.listener.NetworkInitializationListener;
import com.ironsource.mediationsdk.adunit.adapter.listener.RewardedVideoAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType;
import com.ironsource.mediationsdk.model.NetworkSettings;
import com.rixengine.api.AlxRewardVideoAD;
import com.rixengine.api.AlxRewardVideoADListener;

/**
 * IronSource 激励广告适配器
 */
public class AlgoriXCustomRewardedVideo extends BaseRewardedVideo<AlgoriXCustomAdapter> {

    private static final String TAG = "AlgoriXCustomRewardedVideo";
    private AlxRewardVideoAD alxRewardVideoAD;

    private String unitid = "";
    AlgoriXCustomAdapter algoriXCustomAdapter = getNetworkAdapter();
    RewardedVideoAdListener mRewardedVideoAdListener;

    private Context mContext;

    public AlgoriXCustomRewardedVideo(NetworkSettings networkSettings) {
        super(networkSettings);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void showAd(AdData adData, RewardedVideoAdListener listener) {
        if(mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            if(alxRewardVideoAD != null){
                alxRewardVideoAD.showVideo(activity);
            }
        }else {
            Log.e(TAG, "context is not an Activity");
        }

    }

    @Override
    public boolean isAdAvailable(AdData adData) {
        if (alxRewardVideoAD != null) {
            return alxRewardVideoAD.isReady();
        }
        return false;

    }

    @SuppressLint("LongLogTag")
    @Override
    public void loadAd(final AdData adData, Activity activity, RewardedVideoAdListener rewardedVideoAdListener) {
        Log.d(TAG, "loadAd:");
        mContext = activity;
        mRewardedVideoAdListener = rewardedVideoAdListener;
        algoriXCustomAdapter.init(adData, activity, new NetworkInitializationListener() {
            @Override
            public void onInitSuccess() {
                unitid = (String) adData.getConfiguration().get("unitid");
                Log.d(TAG, "onInitSuccess: unitid :" + unitid);

                startAdLoad(mContext);
            }

            @Override
            public void onInitFailed(int i, String s) {
                Log.d(TAG, "Init Failed errCode:" + i + " errMsg: " + s);
            }
        });
    }
    @SuppressLint("LongLogTag")
    private void startAdLoad(Context context){
        Log.d(TAG, "startAdLoad:");
        alxRewardVideoAD = new AlxRewardVideoAD();
        alxRewardVideoAD.load(context, unitid, new AlxRewardVideoADListener() {
            @Override
            public void onRewardedVideoAdLoaded(AlxRewardVideoAD var1) {

                Log.d(TAG, "onRewardedVideoAdLoaded:");
                if(mRewardedVideoAdListener != null){
                    mRewardedVideoAdListener.onAdLoadSuccess();
                }
            }

            @Override
            public void onRewardedVideoAdFailed(AlxRewardVideoAD var1, int errCode, String errMsg) {
                Log.d(TAG, "onRewardedVideoAdFailed:");
                if(mRewardedVideoAdListener != null){
                    mRewardedVideoAdListener.onAdLoadFailed(AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL, errCode, errMsg);
                }
            }

            @Override
            public void onRewardedVideoAdPlayStart(AlxRewardVideoAD var1) {
                if(mRewardedVideoAdListener != null){
                    mRewardedVideoAdListener.onAdStarted();
                    mRewardedVideoAdListener.onAdOpened();
                }
            }

            @Override
            public void onRewardedVideoAdPlayEnd(AlxRewardVideoAD var1) {
                if(mRewardedVideoAdListener != null){
                    mRewardedVideoAdListener.onAdEnded();
                }
            }

            @Override
            public void onRewardedVideoAdPlayFailed(AlxRewardVideoAD var2, int errCode, String errMsg) {
                if(mRewardedVideoAdListener != null){
                    mRewardedVideoAdListener.onAdShowFailed(errCode,errMsg);
                }
            }

            @Override
            public void onRewardedVideoAdClosed(AlxRewardVideoAD var1) {
                if(mRewardedVideoAdListener != null){
                    mRewardedVideoAdListener.onAdClosed();
                }
            }

            @Override
            public void onRewardedVideoAdPlayClicked(AlxRewardVideoAD var1) {
                if(mRewardedVideoAdListener != null){
                    mRewardedVideoAdListener.onAdClicked();
                }
            }

            @Override
            public void onReward(AlxRewardVideoAD var1) {
                if(mRewardedVideoAdListener != null){
                    mRewardedVideoAdListener.onAdRewarded();
                }

            }
        });
    }
}