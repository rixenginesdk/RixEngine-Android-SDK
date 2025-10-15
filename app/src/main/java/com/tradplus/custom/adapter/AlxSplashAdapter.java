package com.tradplus.custom.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.rixengine.api.AlxAdSDK;
import com.rixengine.api.AlxSdkInitCallback;
import com.rixengine.api.AlxSplashAd;
import com.rixengine.api.AlxSplashAdListener;
import com.tradplus.ads.base.adapter.splash.TPSplashAdapter;
import com.tradplus.ads.base.common.TPError;

import java.util.Map;

/**
 * TradPlus 开屏广告适配器
 */
public class AlxSplashAdapter extends TPSplashAdapter {
    private static final String TAG = "AlxSplashAdapter";
    private String unitid = "";
    private String appid = "";
    private String sid = "";
    private String host = "";
    private String token = "";
    private Boolean isdebug = false;
    private AlxSplashAd mAdObj;
    private boolean isReady = false;

    @Override
    public void loadCustomAd(Context context, Map<String, Object> map, Map<String, String> tpParams) {
        Log.d(TAG, "rixengine-tradplus-adapter-version:" + AlxMetaInf.ADAPTER_VERSION);
        Log.i(TAG, "loadCustomAd");
        isReady = false;
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
                unitid = (String) serverExtras.get("unitid");
            }
            if (serverExtras.containsKey("appid")) {
                appid = (String) serverExtras.get("appid");

            } else if (serverExtras.containsKey("appkey")) {
                appid = (String) serverExtras.get("appkey");
            }
            if (serverExtras.containsKey("appkey")) {
                sid = (String) serverExtras.get("appkey");
            } else if (serverExtras.containsKey("sid")) {
                sid = (String) serverExtras.get("sid");
            }
            if (serverExtras.containsKey("host")) {
                host = (String) serverExtras.get("host");
            }
            if (serverExtras.containsKey("license")) {
                token = (String) serverExtras.get("license");
            } else if (serverExtras.containsKey("token")) {
                token = (String) serverExtras.get("token");
            }

            if (serverExtras.containsKey("isdebug")) {
                String test = serverExtras.get("isdebug").toString();
                Log.e(TAG, "rixengine debug mode:" + test);
                if (test.equals("true")) {
                    isdebug = true;
                } else {
                    isdebug = false;
                }
            } else {
                Log.e(TAG, "rixengine debug mode: false");
            }
            if (serverExtras.containsKey("tag")) {
                String tag = serverExtras.get("tag").toString();
                Log.e(TAG, "rixengine json tag:" + tag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(unitid) || TextUtils.isEmpty(host) || TextUtils.isEmpty(token) || TextUtils.isEmpty(sid) || TextUtils.isEmpty(appid)) {
            Log.i(TAG, "rixengine unitid | host | token | sid | appid is empty");
            if (mLoadAdapterListener != null) {
                mLoadAdapterListener.loadAdapterLoadFailed(
                        new TPError(TPError.ADAPTER_CONFIGURATION_ERROR + ":rixengine unitid | host | token | sid | appid is empty."));
            }
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

    private void loadAd(final Context context) {
        mAdObj = new AlxSplashAd(context, unitid);
        mAdObj.load(new AlxSplashAdListener() {
            @Override
            public void onAdLoadSuccess() {
                isReady = true;
                if (mLoadAdapterListener != null) {
                    mLoadAdapterListener.loadAdapterLoaded(null);
                }
            }

            @Override
            public void onAdLoadFail(int errorCode, String errorMsg) {
                isReady = false;
                if (mLoadAdapterListener != null) {
                    mLoadAdapterListener.loadAdapterLoadFailed(new TPError(errorCode + "", errorMsg));
                }
            }

            @Override
            public void onAdShow() {
                if (mShowListener != null) {
                    mShowListener.onAdShown();
                }
            }

            @Override
            public void onAdClick() {
                if (mShowListener != null) {
                    mShowListener.onAdClicked();
                }
            }

            @Override
            public void onAdDismissed() {
                if (mShowListener != null) {
                    mShowListener.onAdClosed();
                }
            }
        });
    }

    @Override
    public void showAd() {
        if (mAdObj != null && isReady) {
            mAdObj.showAd(mAdContainerView);
        }
    }

    @Override
    public void clean() {
        if (mAdObj != null) {
            mAdObj.destroy();
        }
    }

    @Override
    public boolean isReady() {
        return isReady;
    }

    @Override
    public String getNetworkName() {
        return AlxAdSDK.getNetWorkName();
    }

    @Override
    public String getNetworkVersion() {
        return AlxAdSDK.getNetWorkVersion();
    }


}
