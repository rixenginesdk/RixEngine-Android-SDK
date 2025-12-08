package com.anythink.custom.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;


import com.rixengine.api.AlxAdSDK;
import com.rixengine.api.AlxBannerView;
import com.rixengine.api.AlxBannerViewAdListener;
import com.thinkup.banner.unitgroup.api.CustomBannerAdapter;
import com.thinkup.core.api.BaseAd;
import com.thinkup.core.api.MediationInitCallback;
import com.thinkup.core.api.TUAdConst;
import com.thinkup.core.api.TUBiddingListener;
import com.thinkup.core.api.TUBiddingNotice;
import com.thinkup.core.api.TUBiddingResult;

import java.util.Map;
import java.util.UUID;

/**
 * TopOn Banner广告适配器
 */
public class AlxBannerAdapter extends CustomBannerAdapter {
    private static final String TAG = "AlxBannerAdapter";
    private String unitid = "";
    private String appid = "";
    private String sid = "";
    private String token = "";
    private String host = "";
    private Boolean isDebug = null;
    AlxBannerView mBannerView;
    private TUBiddingListener mBiddingListener;



    public void startBid(Context context) {
        Log.d(TAG,"startBid ");
        mBannerView = new AlxBannerView(context);
        loadAd(context);

    }

    @Override
    public boolean startBiddingRequest(final Context context, Map<String, Object> serverExtra, Map<String, 	Object> localExtra, final TUBiddingListener biddingListener) {
        //从serverExtra中获取后台配置的自定义平台的广告位ID
        mBiddingListener = biddingListener;
        loadCustomNetworkAd(context,serverExtra,localExtra);
        return true;
    }


    @Override
    public void loadCustomNetworkAd(Context context, Map<String, Object> serverExtra, Map<String, Object> localExtras) {
        Log.d(TAG, "alx-topon-adapter-version:" + AlxMetaInf.ADAPTER_VERSION);
        if (parseServer(serverExtra)) {
            initSdk(context,serverExtra);
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
            Log.e(TAG,"host url is null, please check it, now use default host : " + AlxMetaInf.ADAPTER_SDK_HOST_URL);

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
                Log.d(TAG,"AlxSdkInit success");
                startBid(context);
            }

            @Override
            public void onFail(String s) {
                Log.d(TAG,"AlxSdkInit fail : "+s);
                //通过ATBiddingListener，回调竞价失败
                if (mBiddingListener != null) {
                    mBiddingListener.onC2SBiddingResultWithCache(TUBiddingResult.fail(s), null);
                }
            }
        });

    }


    private void loadAd(Context context) {
        mBannerView = new AlxBannerView(context);
        final AlxBannerViewAdListener alxBannerADListener = new AlxBannerViewAdListener() {
            @Override
            public void onAdLoaded() {
                if (mLoadListener != null) {
                    mLoadListener.onAdCacheLoaded();
                }

                //get price
                double bidPrice = mBannerView.getPrice();

                Log.d(TAG,"bidPrice: "+bidPrice);

                //get currency
                TUAdConst.CURRENCY currency = TUAdConst.CURRENCY.USD;

                //get uuid
                String token = UUID.randomUUID().toString();

                //BiddingNotice
                TUBiddingNotice biddingNotice = null;

                //BaseAd
                BaseAd basead = null;
                if (mBiddingListener != null) {
                    mBiddingListener.onC2SBiddingResultWithCache(
                            TUBiddingResult.success(bidPrice, token, biddingNotice, currency), basead);
                }
            }

            @Override
            public void onAdError(int errorCode, String errorMsg) {
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError(errorCode + "", errorMsg);
                }
                if (mBiddingListener != null) {
                    mBiddingListener.onC2SBiddingResultWithCache(TUBiddingResult.fail(errorMsg), null);
                }
            }

            @Override
            public void onAdClicked() {
                if (mImpressionEventListener != null) {
                    mImpressionEventListener.onBannerAdClicked();
                }
            }

            @Override
            public void onAdShow() {
                if (mImpressionEventListener != null) {
                    mImpressionEventListener.onBannerAdShow();
                }
            }

            @Override
            public void onAdClose() {
                if (mImpressionEventListener != null) {
                    mImpressionEventListener.onBannerAdClose();
                }
            }
        };
        // auto refresh ad  default = open = 1, 0 = close
        mBannerView.setBannerRefresh(0);
        mBannerView.loadAd(unitid, alxBannerADListener);
    }

    @Override
    public View getBannerView() {
        return mBannerView;
    }

    @Override
    public void destory() {
        if (mBannerView != null) {
            mBannerView.destroy();
            mBannerView = null;
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

}