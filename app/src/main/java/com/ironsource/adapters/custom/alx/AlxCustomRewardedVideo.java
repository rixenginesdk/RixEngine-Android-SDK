package com.ironsource.adapters.custom.alx;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ironsource.mediationsdk.adunit.adapter.BaseRewardedVideo;
import com.ironsource.mediationsdk.adunit.adapter.listener.NetworkInitializationListener;
import com.ironsource.mediationsdk.adunit.adapter.listener.RewardedVideoAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType;
import com.ironsource.mediationsdk.model.NetworkSettings;
import com.rixengine.api.AlxRewardVideoAD;
import com.rixengine.api.AlxRewardVideoADListener;

/**
 * Unity LevelPlay(IronSource) RewardVideo Adapter
 */
public class AlxCustomRewardedVideo extends BaseRewardedVideo<AlxCustomAdapter> {

    private static final String TAG = "AlxCustomRewardedVideo";
    private AlxRewardVideoAD alxRewardVideoAD;

    private String unitid = "";
    AlxCustomAdapter alxCustomAdapter = getNetworkAdapter();
    RewardedVideoAdListener mRewardedVideoAdListener;

    private Context mContext;

    public AlxCustomRewardedVideo(NetworkSettings networkSettings) {
        super(networkSettings);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void showAd(@NonNull AdData adData, @NonNull Activity activity, @NonNull RewardedVideoAdListener listener) {
        if(alxRewardVideoAD != null){
            alxRewardVideoAD.showVideo(activity);
        }
    }

    @Override
    public boolean isAdAvailable(@NonNull AdData adData) {
        if (alxRewardVideoAD != null) {
            return alxRewardVideoAD.isReady();
        }
        return false;

    }

    @Override
    public void destroyAd(@NonNull AdData adData) {
        if (alxRewardVideoAD != null) {
            alxRewardVideoAD.destroy();
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public void loadAd(@NonNull AdData adData, @NonNull Context context, @NonNull RewardedVideoAdListener listener) {
        Log.d(TAG, "loadAd:");
        mContext = context;
        mRewardedVideoAdListener = listener;
        alxCustomAdapter.init(adData, context, new NetworkInitializationListener() {
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