package com.anythink.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import com.rixengine.api.AlxAdSDK;
import com.rixengine.api.AlxSplashAd;
import com.rixengine.api.AlxSplashAdListener;
import com.secmtp.sdk.core.api.ATBiddingListener;
import com.secmtp.sdk.core.api.ATBiddingResult;
import com.secmtp.sdk.core.api.MediationInitCallback;
import com.secmtp.sdk.splashad.unitgroup.api.CustomSplashAdapter;

import java.util.Map;

/**
 * Chinese: TopOn 开屏广告适配器
 * English: TopOn Splash Adapter
 */
public class AlxSplashAdapter extends CustomSplashAdapter {
    private static final String TAG = "AlxSplashAdapter";
    private String unitid = "";
    private String appid = "";
    private String sid = "";
    private String token = "";
    private String host = "";
    private AlxSplashAd mAdObj;
    private boolean isReady = false;

    public void startLoadAd(Context context) {
        Log.d(TAG, "startLoadAd");
        mAdObj = new AlxSplashAd();
        mAdObj.load(context, unitid,new AlxSplashAdListener() {
            @Override
            public void onAdLoaded() {
                isReady = true;
                if (mLoadListener != null) {
                    Log.d(TAG, "load success");
                    mLoadListener.onAdCacheLoaded();
                }
                if (mBiddingListener != null) {
                    double bidPrice = mAdObj.getPrice();
                    Log.d(TAG, "bidding load success: bid price = " + bidPrice);
                    mBiddingListener.onC2SBiddingResultWithCache(AlxSdkInitManager.getBiddingSuccessBean(bidPrice), null);
                }
            }

            @Override
            public void onAdLoadFail(int errorCode, String errorMsg) {
                isReady = false;
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError(errorCode + "", errorMsg);
                }
                if (mBiddingListener != null) {
                    mBiddingListener.onC2SBiddingResultWithCache(ATBiddingResult.fail(errorMsg), null);
                }
            }

            @Override
            public void onAdShow() {
                if (mImpressionListener != null) {
                    mImpressionListener.onSplashAdShow();
                }
            }

            @Override
            public void onAdClicked() {
                if (mImpressionListener != null) {
                    mImpressionListener.onSplashAdClicked();
                }
            }

            @Override
            public void onAdClose() {
                if (mImpressionListener != null) {
                    mImpressionListener.onSplashAdDismiss();
                }
            }
        });
    }


    @Override
    public boolean startBiddingRequest(final Context context, Map<String, Object> serverExtra, Map<String, Object> localExtra, final ATBiddingListener biddingListener) {
        Log.d(TAG, "startBiddingRequest");
        AlxSdkInitManager.printSDKInfo(TAG);

        //从serverExtra中获取后台配置的自定义平台的广告位ID
        mBiddingListener = biddingListener;
        isReady = false;
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
                mBiddingListener.onC2SBiddingResultWithCache(ATBiddingResult.fail("alx  unitid | token | sid | appid is empty"), null);
            }
        }

        return true;
    }


    @Override
    public void loadCustomNetworkAd(Context context, Map<String, Object> serverExtras, Map<String, Object> localExtras) {
        Log.d(TAG, "loadCustomNetworkAd");
        AlxSdkInitManager.printSDKInfo(TAG);

        isReady = false;
        if (parseServer(serverExtras)) {
            AlxSdkInitManager.getInstance().initSDK(context, serverExtras, new MediationInitCallback() {
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
                mLoadListener.onAdLoadError("", "alx apppid | token | sid | appid is empty.");
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
        } catch (Exception e) {
            Log.e(TAG, "alx parseServer error:" + e.getMessage());
        }

        if (TextUtils.isEmpty(host) && !TextUtils.isEmpty(AlxMetaInf.ADAPTER_SDK_HOST_URL)) {
            host = AlxMetaInf.ADAPTER_SDK_HOST_URL;
            Log.e(TAG,"host url is null, please check it, now use default host : " + AlxMetaInf.ADAPTER_SDK_HOST_URL);
        }

        if (TextUtils.isEmpty(host) || TextUtils.isEmpty(unitid) || TextUtils.isEmpty(token) || TextUtils.isEmpty(sid) || TextUtils.isEmpty(appid)) {
            Log.i(TAG, "alx host | unitid | token | sid | appid is empty");
            return false;
        }
        return true;
    }

    @Override
    public void destory() {
        if (mAdObj != null) {
            mAdObj.destroy();
        }
    }

    @Override
    public void show(Activity activity, ViewGroup viewGroup) {
        if (mAdObj != null && mAdObj.isReady()) {
            mAdObj.show(activity);
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
        return isReady;
    }
}
