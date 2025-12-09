package com.anythink.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.rixengine.api.AlxAdSDK;
import com.rixengine.api.AlxInterstitialAD;
import com.rixengine.api.AlxInterstitialADListener;
import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATBiddingListener;
import com.anythink.core.api.ATBiddingNotice;
import com.anythink.core.api.ATBiddingResult;
import com.anythink.core.api.BaseAd;
import com.anythink.core.api.MediationInitCallback;
import com.anythink.interstitial.unitgroup.api.CustomInterstitialAdapter;

import java.util.Map;
import java.util.UUID;

/**
 * TopOn 插屏广告适配器
 */
public class AlxInterstitialAdapterOld extends CustomInterstitialAdapter {

    private static final String TAG = "AlxInterstitialAdapter";

    private AlxInterstitialAD alxInterstitialAD;
    private String unitid = "";
    private String appid = "";
    private String sid = "";
    private String token = "";
    private Boolean isDebug = null;
    private ATBiddingListener mBiddingListener;




    public void startBid(Context context) {
        Log.d(TAG,"startBid ");
        startAdLoad(context);

    }

    @Override
    public boolean startBiddingRequest(final Context context, Map<String, Object> serverExtra, Map<String, 	Object> localExtra, final ATBiddingListener biddingListener) {
        //从serverExtra中获取后台配置的自定义平台的广告位ID
        mBiddingListener = biddingListener;
        loadCustomNetworkAd(context,serverExtra,localExtra);
        //必须return true
        return true;
    }


    @Override
    public void loadCustomNetworkAd(Context context, Map<String, Object> serverExtra, Map<String, Object> map1) {
        Log.d(TAG, "alx-topon-adapter-version:" + AlxMetaInf.ADAPTER_VERSION);
        Log.i(TAG, "loadCustomNetworkAd");
        if (parseServer(serverExtra)) {
            initSdk(context,serverExtra);
        }else {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError("", "alx unitid | token | sid | appid is empty.");
            }
        }
    }

    private boolean parseServer(Map<String, Object> serverExtras) {
        try {
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

            if (serverExtras.containsKey("isdebug")) {
                Object obj = serverExtras.get("isdebug");
                String debug = null;
                if (obj != null && obj instanceof String) {
                    debug = (String) obj;
                }
                Log.e(TAG, "alx debug mode:" + debug);
                if (debug != null) {
                    if (debug.equalsIgnoreCase("true")) {
                        isDebug = Boolean.TRUE;
                    } else if (debug.equalsIgnoreCase("false")) {
                        isDebug = Boolean.FALSE;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(unitid) || TextUtils.isEmpty(token) || TextUtils.isEmpty(sid) || TextUtils.isEmpty(appid)) {
            Log.i(TAG, "alx unitid | token | sid | appid is empty");
            return false;
        }
        return true;
    }

    private void initSdk(final Context context, Map<String, Object> serverExtra) {
        AlxSdkInitManager.getInstance().initSDK(context, serverExtra, new MediationInitCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG,"AlxSdkInit success");
                startBid(context);
            }

            @Override
            public void onFail(String s) {
                Log.d(TAG,"AlxSdkInit fail : "+s);
                //通过ATBiddingListener，回调竞价失败
                if (mBiddingListener != null) {
                    mBiddingListener.onC2SBiddingResultWithCache(ATBiddingResult.fail(s), null);
                }
            }
        });


//        try {
//            Log.i(TAG, "alx ver:" + AlxAdSDK.getNetWorkVersion() + " alx token: " + token + " alx appid: " + appid + " alx sid: " + sid);
//
//            if (isDebug != null) {
//                AlxAdSDK.setDebug(isDebug.booleanValue());
//            }
//            AlxAdSDK.init(context, token, sid, appid, new AlxSdkInitCallback() {
//                @Override
//                public void onInit(boolean isOk, String msg) {
//                    if (isOk){
//                    startAdLoad(context);
//                    }
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }



    private void startAdLoad(Context context) {
        alxInterstitialAD = new AlxInterstitialAD();
        alxInterstitialAD.load(context, unitid, new AlxInterstitialADListener() {

            @Override
            public void onInterstitialAdLoaded() {
                if (mLoadListener != null) {
                    mLoadListener.onAdCacheLoaded();
                }
                //get price
                double bidPrice = alxInterstitialAD.getPrice();
                Log.d(TAG,"bidPrice: "+bidPrice);

                //get currency
                ATAdConst.CURRENCY currency = ATAdConst.CURRENCY.USD;

                //get uuid
                String token = UUID.randomUUID().toString();

                //BiddingNotice
                ATBiddingNotice biddingNotice = null;

                //BaseAd
                com.thinkup.core.api.BaseAd basead = null;
                if (mBiddingListener != null) {
                    mBiddingListener.onC2SBiddingResultWithCache(
                            ATBiddingResult.success(bidPrice, token, biddingNotice, currency), basead);
                }
            }

            @Override
            public void onInterstitialAdLoadFail(int errorCode, String errorMsg) {
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError(errorCode + "", errorMsg);
                }
                if (mBiddingListener != null) {
                    mBiddingListener.onC2SBiddingResultWithCache(ATBiddingResult.fail(errorMsg), null);
                }

                Log.i(TAG, "onInterstitialAdLoadFail:" + errorCode + " msg:" + errorMsg);
            }

            @Override
            public void onInterstitialAdClicked() {
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdClicked();
                }
            }

            @Override
            public void onInterstitialAdShow() {
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdShow();
                }
            }

            @Override
            public void onInterstitialAdClose() {
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdClose();
                }
            }

            @Override
            public void onInterstitialAdVideoStart() {
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdVideoStart();
                }
            }

            @Override
            public void onInterstitialAdVideoEnd() {
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdVideoEnd();
                }
            }

            @Override
            public void onInterstitialAdVideoError(int errorCode, String errorMsg) {
                if (mImpressListener != null) {
                    mImpressListener.onInterstitialAdVideoError(String.valueOf(errorCode), errorMsg);
                }
            }
        });
    }


    @Override
    public void show(Activity activity) {
        if (alxInterstitialAD != null) {
            alxInterstitialAD.show(activity);
        }
    }

    @Override
    public void destory() {
        if (alxInterstitialAD != null) {
            alxInterstitialAD.destroy();
            alxInterstitialAD = null;
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
        if (alxInterstitialAD != null) {
            return alxInterstitialAD.isReady();
        }
        return false;
    }
}
