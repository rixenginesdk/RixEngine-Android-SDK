package com.anythink.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATBiddingListener;
import com.anythink.core.api.ATBiddingNotice;
import com.anythink.core.api.ATBiddingResult;
import com.anythink.core.api.BaseAd;
import com.anythink.core.api.MediationInitCallback;
import com.anythink.rewardvideo.unitgroup.api.CustomRewardVideoAdapter;
import com.rixengine.api.AlxAdSDK;
import com.rixengine.api.AlxRewardVideoAD;
import com.rixengine.api.AlxRewardVideoADListener;

import java.util.Map;
import java.util.UUID;

/**
 * TopOn 激励广告适配器
 */
public class AlxRewardVideoAdapter extends CustomRewardVideoAdapter {

    private static final String TAG = "AlxRewardVideoAdapter";
    private AlxRewardVideoAD alxRewardVideoAD;
    private String unitid = "";
    private String appid = "";
    private String sid = "";
    private String token = "";
    private String host = "";
    private Boolean isDebug = null;

    private ATBiddingListener mBiddingListener;

    public void startBid(Context context) {
        Log.d(TAG, "startBid ");
        startAdLoad(context);

    }

    @Override
    public boolean startBiddingRequest(final Context context, Map<String, Object> serverExtra, Map<String, Object> localExtra, final ATBiddingListener biddingListener) {
        //从serverExtra中获取后台配置的自定义平台的广告位ID
        mBiddingListener = biddingListener;
        loadCustomNetworkAd(context, serverExtra, localExtra);
        //必须return true
        return true;
    }


    public void loadCustomNetworkAd(Context context, Map<String, Object> serverExtra, Map<String, Object> localExtras) {
        Log.d(TAG, "alx-topon-adapter-version:" + AlxMetaInf.ADAPTER_VERSION);
        Log.i(TAG, "loadCustomNetworkAd");
        if (parseServer(serverExtra)) {
            initSdk(context, serverExtra);
        } else {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError("", "alx unitid | token | sid | appid is empty.");
            }
        }
    }

//    @Override
//    public void loadCustomNetworkAd(Context context, Map<String, Object> serverExtras, Map<String, Object> map1) {
//        Log.d(TAG, "alx-topon-adapter-version:" + AlxMetaInf.ADAPTER_VERSION);
//        Log.i(TAG, "loadCustomNetworkAd");
//        if (parseServer(serverExtras)) {
//            initSdk(context);
//        } else {
//            if (mLoadListener != null) {
//                mLoadListener.onAdLoadError("", "alx host | unitid | token | sid | appid is empty.");
//            }
//        }
//    }

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

        if (TextUtils.isEmpty(host) && !TextUtils.isEmpty(AlxMetaInf.ADAPTER_SDK_HOST_URL)) {
            host = AlxMetaInf.ADAPTER_SDK_HOST_URL;
            Log.e(TAG, "host url is null, please check it, now use default host : " + AlxMetaInf.ADAPTER_SDK_HOST_URL);
        }

        if (TextUtils.isEmpty(host) || TextUtils.isEmpty(unitid) || TextUtils.isEmpty(token) || TextUtils.isEmpty(sid) || TextUtils.isEmpty(appid)) {
            Log.i(TAG, "alx host | unitid | token | sid | appid is empty");
            return false;
        }
        return true;
    }


    private void initSdk(final Context context, Map<String, Object> serverExtra) {
        AlxSdkInitManager.getInstance().initSDK(context, serverExtra, new MediationInitCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "AlxSdkInit success");
                startBid(context);
            }

            @Override
            public void onFail(String s) {
                Log.d(TAG, "AlxSdkInit fail : " + s);
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

//    private void initSdk(final Context context) {
//        try {
//            Log.i(TAG, "alx ver:" + AlxAdSDK.getNetWorkVersion() + " alx host: " + host + " alx token: " + token + " alx appid: " + appid + " alx sid: " + sid);
//
//            if (isDebug != null) {
//                AlxAdSDK.setDebug(isDebug.booleanValue());
//            }
//            AlxAdSDK.init(context, host, token, sid, appid, new AlxSdkInitCallback() {
//                @Override
//                public void onInit(boolean isOk, String msg) {
//                    //if (isOk){
//                    startAdLoad(context);
//                    //}
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    private void startAdLoad(Context context) {
        alxRewardVideoAD = new AlxRewardVideoAD();
        alxRewardVideoAD.load(context, unitid, new AlxRewardVideoADListener() {

            @Override
            public void onRewardedVideoAdLoaded(AlxRewardVideoAD var1) {
                if (mLoadListener != null) {
                    mLoadListener.onAdCacheLoaded();
                }

                Log.d(TAG, "startBid  load success");
                //get price
                double bidPrice = alxRewardVideoAD.getPrice();

                Log.d(TAG, "bidPrice: " + bidPrice);

                //get currency
                ATAdConst.CURRENCY currency = ATAdConst.CURRENCY.USD;

                //uuid
                String token = UUID.randomUUID().toString();

                //biddingNotice
                ATBiddingNotice biddingNotice = null;

                //native need request BaseAd ,other null
                BaseAd basead = null;
                if (mBiddingListener != null) {
                    mBiddingListener.onC2SBiddingResultWithCache(
                            ATBiddingResult.success(bidPrice, token, biddingNotice, currency), basead);
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
