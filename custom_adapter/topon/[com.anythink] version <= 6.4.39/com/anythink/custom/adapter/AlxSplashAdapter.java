package com.anythink.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import com.anythink.splashad.unitgroup.api.CustomSplashAdapter;
import com.rixengine.api.AlxAdSDK;
import com.rixengine.api.AlxSdkInitCallback;
import com.rixengine.api.AlxSplashAd;
import com.rixengine.api.AlxSplashAdListener;

import java.util.Map;

/**
 * TopOn 开屏广告适配器
 */
public class AlxSplashAdapter extends CustomSplashAdapter {
    private static final String TAG = "AlxSplashAdapter";
    private String unitid = "";
    private String appid = "";
    private String sid = "";
    private String token = "";
    private String host = "";
    private int mImageWidth; //请求广告图的宽度：单位px
    private int mImageHeight; //请求广告图的高度: 单位px
    private Boolean isDebug = null;
    private AlxSplashAd mAdObj;
    private boolean isReady = false;


    @Override
    public void loadCustomNetworkAd(Context context, Map<String, Object> serverExtras, Map<String, Object> localExtras) {
        Log.d(TAG, "alx-topon-adapter-version:" + AlxMetaInf.ADAPTER_VERSION);
        Log.i(TAG, "loadCustomNetworkAd");
        isReady = false;
        if (parseServer(serverExtras)) {
            initSdk(context);
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

            try {
                String width = null;
                String height = null;
                if (serverExtras.containsKey("imageWidth")) {
                    width = (String) serverExtras.get("imageWidth");
                }
                if (serverExtras.containsKey("imageHeight")) {
                    height = (String) serverExtras.get("imageHeight");
                }
                if (!TextUtils.isEmpty(width) && !TextUtils.isEmpty(height)) {
                    mImageWidth = Integer.parseInt(width);
                    mImageHeight = Integer.parseInt(height);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
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

    private void initSdk(final Context context) {
        try {
            Log.i(TAG, "alx ver:" + AlxAdSDK.getNetWorkVersion() + " alx host: " + host + " alx token: " + token + " alx appid: " + appid + " alx sid: " + sid);

            if (isDebug != null) {
                AlxAdSDK.setDebug(isDebug.booleanValue());
            }

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
                if (mLoadListener != null) {
                    mLoadListener.onAdCacheLoaded();
                }
            }

            @Override
            public void onAdLoadFail(int errorCode, String errorMsg) {
                isReady = false;
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError(errorCode + "", errorMsg);
                }
            }

            @Override
            public void onAdShow() {
                if (mImpressionListener != null) {
                    mImpressionListener.onSplashAdShow();
                }
            }

            @Override
            public void onAdClick() {
                if (mImpressionListener != null) {
                    mImpressionListener.onSplashAdClicked();
                }
            }

            @Override
            public void onAdDismissed() {
                if (mImpressionListener != null) {
                    mImpressionListener.onSplashAdDismiss();
                }
            }
        });
    }

    @Override
    public void destory() {
        if (mAdObj != null) {
            mAdObj.destroy();
        }
    }

    @Override
    public void show(Activity activity, ViewGroup viewGroup) {
        if (mAdObj != null && isReady) {
            mAdObj.showAd(viewGroup);
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
