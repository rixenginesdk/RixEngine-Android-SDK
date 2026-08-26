package com.anythink.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.rixengine.api.AlxAdSDK;
import com.rixengine.api.AlxRewardVideoAD;
import com.rixengine.api.AlxRewardVideoADListener;
import com.secmtp.sdk.core.api.ATBiddingListener;
import com.secmtp.sdk.core.api.ATBiddingResult;
import com.secmtp.sdk.core.api.MediationInitCallback;
import com.secmtp.sdk.rewardvideo.unitgroup.api.CustomRewardVideoAdapter;

import java.util.Map;

/**
 * Chinese: TopOn 激励广告适配器
 * English: TopOn RewardVideo Adapter
 */
public class AlxRewardVideoAdapter extends CustomRewardVideoAdapter {

    private static final String TAG = "AlxRewardVideoAdapter";
    private AlxRewardVideoAD alxRewardVideoAD;
    private String unitid = "";
    private String appid = "";
    private String sid = "";
    private String token = "";
    private String host = "";

    private ATBiddingListener mBiddingListener;

    private void startLoadAd(Context context) {
        Log.d(TAG, "startLoadAd");
        alxRewardVideoAD = new AlxRewardVideoAD();
        alxRewardVideoAD.load(context, unitid, new AlxRewardVideoADListener() {

            @Override
            public void onRewardedVideoAdLoaded(AlxRewardVideoAD var1) {
                if (mLoadListener != null) {
                    Log.d(TAG, "load success");
                    mLoadListener.onAdCacheLoaded();
                }
                if (mBiddingListener != null) {
                    double bidPrice = alxRewardVideoAD.getPrice();
                    Log.d(TAG, "bidding load success: bid price = " + bidPrice);
                    mBiddingListener.onC2SBiddingResultWithCache(AlxSdkInitManager.getBiddingSuccessBean(bidPrice), null);
                }
            }

            @Override
            public void onRewardedVideoAdFailed(AlxRewardVideoAD var1, int errCode, String errMsg) {
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError(errCode + "", errMsg);
                }
                if (mBiddingListener != null) {
                    mBiddingListener.onC2SBiddingResultWithCache(ATBiddingResult.fail(errMsg), null);
                }
            }

            @Override
            public void onRewardedVideoAdPlayStart(AlxRewardVideoAD var1) {
                if (mImpressionListener != null) {
                    mImpressionListener.onRewardedVideoAdPlayStart();
                }
            }

            @Override
            public void onRewardedVideoAdPlayEnd(AlxRewardVideoAD var1) {
                if (mImpressionListener != null) {
                    mImpressionListener.onRewardedVideoAdPlayEnd();
                }
            }

            @Override
            public void onRewardedVideoAdPlayFailed(AlxRewardVideoAD var2, int errCode, String errMsg) {
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError(errCode + "", errMsg);
                }
            }

            @Override
            public void onRewardedVideoAdClosed(AlxRewardVideoAD var1) {
                if (mImpressionListener != null) {
                    mImpressionListener.onRewardedVideoAdClosed();
                }
            }

            @Override
            public void onRewardedVideoAdPlayClicked(AlxRewardVideoAD var1) {
                if (mImpressionListener != null) {
                    mImpressionListener.onRewardedVideoAdPlayClicked();
                }
            }

            @Override
            public void onReward(AlxRewardVideoAD var1) {
                if (mImpressionListener != null) {
                    mImpressionListener.onReward();
                }
            }

        });
    }

    @Override
    public boolean startBiddingRequest(final Context context, Map<String, Object> serverExtra, Map<String, Object> localExtra, final ATBiddingListener biddingListener) {
        Log.d(TAG, "startBiddingRequest");
        AlxSdkInitManager.printSDKInfo(TAG);

        mBiddingListener = biddingListener;
        if (parseServer(serverExtra)) {
            AlxSdkInitManager.getInstance().initSDK(context, serverExtra, new MediationInitCallback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "AlxSdkInit success");
                    startLoadAd(context);
                }

                @Override
                public void onFail(String s) {
                    Log.d(TAG, "AlxSdkInit fail : " + s);
                    //Chinese: 通过ATBiddingListener，回调竞价失败
                    //English: With ATBiddingListener, the callback bid fails
                    if (mBiddingListener != null) {
                        mBiddingListener.onC2SBiddingResultWithCache(ATBiddingResult.fail(s), null);
                    }
                }
            });
        } else {
            if (mBiddingListener != null) {
                mBiddingListener.onC2SBiddingResultWithCache(ATBiddingResult.fail("alx unitid | token | sid | appid is empty"), null);
            }
        }

        return true;
    }


    @Override
    public void loadCustomNetworkAd(Context context, Map<String, Object> serverExtra, Map<String, Object> localExtras) {
        Log.d(TAG, "loadCustomNetworkAd");
        AlxSdkInitManager.printSDKInfo(TAG);

        if (parseServer(serverExtra)) {
            AlxSdkInitManager.getInstance().initSDK(context, serverExtra, new MediationInitCallback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "AlxSdkInit success");
                    startLoadAd(context);
                }

                @Override
                public void onFail(String s) {
                    Log.d(TAG, "AlxSdkInit fail : " + s);
                    //Chinese: 通过ATBiddingListener，回调竞价失败
                    //English: With ATBiddingListener, the callback bid fails
                    if (mLoadListener != null) {
                        mLoadListener.onAdLoadError("", "alx unitid | token | sid | appid is empty.");
                    }
                }
            });
        } else {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError("", "alx unitid | token | sid | appid is empty..");
            }
        }
    }

    private boolean parseServer(Map<String, Object> serverExtras) {
        try {
            if (serverExtras.containsKey("host")) {
                host = (String) serverExtras.get("host");
            }
            if (serverExtras.containsKey("appid")) {
                appid = (String) serverExtras.get("appid");
            }
            if (serverExtras.containsKey("sid")) {
                sid = (String) serverExtras.get("sid");
            }
            if (serverExtras.containsKey("token")) {
                token = (String) serverExtras.get("token");
            }
            if (serverExtras.containsKey("unitid")) {
                unitid = (String) serverExtras.get("unitid");
            }

            if (TextUtils.isEmpty(unitid) && serverExtras.containsKey("slot_id")) {
                unitid = (String) serverExtras.get("slot_id");
            }

        } catch (Exception e) {
            Log.e(TAG, "alx parseServer error:" + e.getMessage());
        }

        if (TextUtils.isEmpty(host) && !TextUtils.isEmpty(AlxMetaInf.ADAPTER_SDK_HOST_URL)) {
            host = AlxMetaInf.ADAPTER_SDK_HOST_URL;
            Log.e(TAG, "host url is null, please check it, now use default host : " + AlxMetaInf.ADAPTER_SDK_HOST_URL);
        }

        if (TextUtils.isEmpty(host) || TextUtils.isEmpty(unitid) || TextUtils.isEmpty(token) || TextUtils.isEmpty(sid) || TextUtils.isEmpty(appid)) {
            Log.i(TAG, "alx unitid | token | sid | appid is empty");
            return false;
        }
        return true;
    }



    @Override
    public void show(Activity activity) {
        if (alxRewardVideoAD != null) {
            alxRewardVideoAD.showVideo(activity);
        }
    }


    @Override
    public void destory() {
        if (alxRewardVideoAD != null) {
            alxRewardVideoAD.destroy();
            alxRewardVideoAD = null;
        }
    }

    @Override
    public String getNetworkPlacementId() {
        return unitid;
    }

    @Override
    public String getNetworkSDKVersion() {
        return AlxAdSDK.getNetWorkVersion();
    }

    @Override
    public String getNetworkName() {
        return AlxAdSDK.getNetWorkName();
    }

    @Override
    public boolean isAdReady() {
        if (alxRewardVideoAD != null) {
            return alxRewardVideoAD.isReady();
        }
        return false;
    }
}
