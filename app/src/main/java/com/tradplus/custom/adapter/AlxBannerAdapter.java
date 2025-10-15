package com.tradplus.custom.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.rixengine.api.AlxAdSDK;
import com.rixengine.api.AlxBannerView;
import com.rixengine.api.AlxBannerViewAdListener;
import com.rixengine.api.AlxSdkInitCallback;
import com.tradplus.ads.base.adapter.banner.TPBannerAdImpl;
import com.tradplus.ads.base.adapter.banner.TPBannerAdapter;
import com.tradplus.ads.base.common.TPError;

import java.util.Map;

/**
 * TradPlus平台 Banner广告适配器
 */
public class AlxBannerAdapter extends TPBannerAdapter {
    private static final String TAG = "AlxBannerAdapter";
    private String unitid = "";
    private String appid = "";
    private String sid = "";
    private String host = "";
    private String token = "";
    private Boolean isShowClose = false;
    private Boolean isdebug = false;
    AlxBannerView mBannerView;
    private TPBannerAdImpl mTPBannerAd;

    @Override
    public void loadCustomAd(Context context, Map<String, Object> userParams, Map<String, String> tpParams) {
        Log.d(TAG, "rixengine-tradplus-adapter-version:" + AlxMetaInf.ADAPTER_VERSION);
        Log.i(TAG, "loadCustomAd");
        if (tpParams != null && parseServer(tpParams)) {
            initSdk(context);
        } else {
            if (mLoadAdapterListener != null) {
                mLoadAdapterListener.loadAdapterLoadFailed(new TPError("rixengine apppid | host | token | sid | appid is empty."));
            }
        }
    }

    private boolean parseServer(Map<String, String> serverExtras) {
        try {
            if (serverExtras.containsKey("unitid")) {
                unitid = serverExtras.get("unitid");
            }
            if (serverExtras.containsKey("host")) {
                host = serverExtras.get("host");
            }
            if (serverExtras.containsKey("license")) {
                token = serverExtras.get("license");
            } else if (serverExtras.containsKey("token")) {
                token = serverExtras.get("token");
            }
            if (serverExtras.containsKey("appkey")) {
                sid = serverExtras.get("appkey");
            } else if (serverExtras.containsKey("sid")) {
                sid = serverExtras.get("sid");
            }
            if (serverExtras.containsKey("appid")) {
                appid = serverExtras.get("appid");
            }

            if (serverExtras.containsKey("isdebug")) {
                String test = serverExtras.get("isdebug");
                Log.e(TAG, "rixengine debug mode:" + test);
                if (test != null && test.equals("true")) {
                    isdebug = true;
                } else {
                    isdebug = false;
                }
            } else {
                Log.e(TAG, "rixengine debug mode: false");
            }

            if (serverExtras.containsKey("isShowClose")) {
                String test = serverExtras.get("isShowClose");
                Log.e(TAG, "rixengine show close:" + test);
                if (test != null && test.equals("true")) {
                    isShowClose = true;
                } else {
                    isShowClose = false;
                }
            } else {
                Log.e(TAG, "rixengine debug mode: false");
            }

            if (serverExtras.containsKey("tag")) {
                String tag = serverExtras.get("tag");
                Log.e(TAG, "rixengine json tag:" + tag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(unitid) || TextUtils.isEmpty(host) || TextUtils.isEmpty(token) || TextUtils.isEmpty(sid) || TextUtils.isEmpty(appid)) {
            Log.i(TAG, "rixengine unitid | host | token | id | appid is empty");
            return false;
        }
        return true;
    }

    private void initSdk(final Context context) {
        try {
            Log.i(TAG, "rixengine ver:" + AlxAdSDK.getNetWorkVersion() + " rixengine host: " + host + " rixengine token: " + token + " rixengine appid: " + appid + " rixengine sid: " + sid);

            AlxAdSDK.setDebug(isdebug);

            AlxAdSDK.init(context, host, token, sid, appid, new AlxSdkInitCallback() {
                @Override
                public void onInit(boolean isOk, String msg) {
                    Log.i(TAG, "sdk onInit:" + isOk);
                    loadAd(context);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAd(Context context) {
        mBannerView = new AlxBannerView(context);
        final AlxBannerViewAdListener alxBannerADListener = new AlxBannerViewAdListener() {
            @Override
            public void onAdLoaded() {
                if (mLoadAdapterListener != null) {
                    mTPBannerAd = new TPBannerAdImpl(null, mBannerView);
                    mLoadAdapterListener.loadAdapterLoaded(mTPBannerAd);
                }
            }

            @Override
            public void onAdError(int errorCode, String errorMsg) {
                if (mLoadAdapterListener != null) {
                    mLoadAdapterListener.loadAdapterLoadFailed(new TPError(errorCode + "", errorMsg));
                }
            }

            @Override
            public void onAdClicked() {
                if (mTPBannerAd != null) {
                    mTPBannerAd.adClicked();
                }
            }

            @Override
            public void onAdShow() {
                if (mTPBannerAd != null) {
                    mTPBannerAd.adShown();
                }
            }

            @Override
            public void onAdClose() {
                if (mTPBannerAd != null) {
                    mTPBannerAd.adClosed();
                }
            }
        };
        mBannerView.setBannerRefresh(0);
        mBannerView.setBannerCanClose(isShowClose);
        mBannerView.loadAd(unitid, alxBannerADListener);
    }


    @Override
    public void clean() {
        if (mBannerView != null) {
            mBannerView.destroy();
            mBannerView = null;
        }
    }

    @Override
    public String getNetworkVersion() {
        return AlxAdSDK.getNetWorkVersion();
    }

    @Override
    public String getNetworkName() {
        return AlxAdSDK.getNetWorkName();
    }

}