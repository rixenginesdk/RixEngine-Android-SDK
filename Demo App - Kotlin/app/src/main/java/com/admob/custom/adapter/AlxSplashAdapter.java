package com.admob.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationAppOpenAd;
import com.google.android.gms.ads.mediation.MediationAppOpenAdCallback;
import com.google.android.gms.ads.mediation.MediationAppOpenAdConfiguration;
import com.rixengine.api.AlxAdSDK;
import com.rixengine.api.AlxSdkInitCallback;
import com.rixengine.api.AlxSplashAd;
import com.rixengine.api.AlxSplashAdListener;

import org.json.JSONObject;

/**
 * Google Mobile ads RixEngine Splash Adapter
 */
public class AlxSplashAdapter extends AlxBaseAdapter implements MediationAppOpenAd {

    private static final String TAG = "AlxSplashAdapter";

    private String unitid = "";
    private String appid = "";
    private String sid = "";
    private String token = "";
    private String host = "";
    private Boolean isDebug = null;

    private MediationAdLoadCallback<MediationAppOpenAd, MediationAppOpenAdCallback> mMediationLoadCallback;
    private MediationAppOpenAdCallback mMediationEventCallback;

    AlxSplashAd mSplashAd;

    @Override
    public void loadAppOpenAd(@NonNull MediationAppOpenAdConfiguration configuration, @NonNull MediationAdLoadCallback<MediationAppOpenAd, MediationAppOpenAdCallback> callback) {
        Log.d(TAG, "alx-admob-adapter-version:" + AlxMetaInf.ADAPTER_VERSION);
        Log.d(TAG, "admob-sdk-version:" + MobileAds.getVersion().toString());
        Log.d(TAG, "alx-admob-adapter: loadAppOpenAd");
        mMediationLoadCallback = callback;
        String parameter = configuration.getServerParameters().getString("parameter");
        if (!TextUtils.isEmpty(parameter)) {
            parseServer(parameter);
        }
        initSdk(configuration.getContext());
    }

    private void initSdk(final Context context) {
        if (TextUtils.isEmpty(unitid)) {
            Log.d(TAG, "alx unitid is empty");
            loadError(1, "alx unitid is empty.");
            return;
        }
        if (TextUtils.isEmpty(sid)) {
            Log.d(TAG, "alx sid is empty");
            loadError(1, "alx sid is empty.");
            return;
        }
        if (TextUtils.isEmpty(appid)) {
            Log.d(TAG, "alx appid is empty");
            loadError(1, "alx appid is empty.");
            return;
        }
        if (TextUtils.isEmpty(token)) {
            Log.d(TAG, "alx token is empty");
            loadError(1, "alx token is empty");
            return;
        }
        if (TextUtils.isEmpty(host)) {
            if (TextUtils.isEmpty(AlxMetaInf.ADAPTER_SDK_HOST_URL)) {
                Log.d(TAG, "alx host is empty");
                loadError(1, "alx host is empty");
                return;
            }
            host = AlxMetaInf.ADAPTER_SDK_HOST_URL;
            Log.e(TAG, "host url is null, please check it, now use default host : " + AlxMetaInf.ADAPTER_SDK_HOST_URL);
        }

        try {
            Log.i(TAG, "alx host: " + host + " alx token: " + token + " alx appid: " + appid + " alx sid: " + sid);
            // init
            if (isDebug != null) {
                AlxAdSDK.setDebug(isDebug);
            }
            AlxAdSDK.init(context, host, token, sid, appid, new AlxSdkInitCallback() {
                @Override
                public void onInit(boolean isOk, String msg) {
                    AlxBaseAdapter.sdkInfo();
                    preloadAd(context);
                }
            });
//            // set GDPR
//            AlxAdSDK.setSubjectToGDPR(true);
//            // set GDPR Consent
//            AlxAdSDK.setUserConsent("1");
//            // set COPPA
//            AlxAdSDK.setBelowConsentAge(true);
//            // set CCPA
//            AlxAdSDK.subjectToUSPrivacy("1YYY");
        } catch (Exception e) {
            Log.e(TAG, "error:" + e.getMessage());
            loadError(1, "alx sdk init error");
        }
    }

    @Override
    public void showAd(@NonNull Context context) {
        Log.i(TAG, "alx showAd");
        if (mSplashAd != null) {
            if (!mSplashAd.isReady()) {
                if (mMediationEventCallback != null) {
                    mMediationEventCallback.onAdFailedToShow(new AdError(1, "isReady: Ad not loaded or Ad load failed"
                            , AlxAdSDK.getNetWorkName()));
                }
                return;
            }
            if (context instanceof Activity) {
                mSplashAd.show((Activity) context);
            } else {
                Log.i(TAG, "context is not an Activity");
                mSplashAd.show(null);
            }
        }
    }

    private void loadError(int code, String message) {
        if (mMediationLoadCallback != null) {
            mMediationLoadCallback.onFailure(new AdError(code, message, AlxAdSDK.getNetWorkName()));
        }
    }

    private void parseServer(String s) {
        if (TextUtils.isEmpty(s)) {
            Log.d(TAG, "serviceString  is empty ");
            return;
        }
        Log.d(TAG, "serviceString   " + s);
        try {
            JSONObject json = new JSONObject(s);
            host = json.optString("host"); //配置可选
            appid = json.getString("appid");
            sid = json.getString("sid");
            token = json.getString("token");
            unitid = json.getString("unitid");
            String debug = json.optString("isdebug");
            if (debug != null) {
                if (debug.equalsIgnoreCase("true")) {
                    isDebug = Boolean.TRUE;
                } else if (debug.equalsIgnoreCase("false")) {
                    isDebug = Boolean.FALSE;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "error:" + e.getMessage());
        }
    }

    private void preloadAd(final Context context) {
        mSplashAd = new AlxSplashAd();
        mSplashAd.load(context, unitid, null, new AlxSplashAdListener() {

            @Override
            public void onAdLoaded() {
                if (mMediationLoadCallback != null) {
                    mMediationEventCallback = mMediationLoadCallback.onSuccess(AlxSplashAdapter.this);
                }
            }

            @Override
            public void onAdLoadFail(int errorCode, String errorMsg) {
                loadError(errorCode, errorMsg);
            }

            @Override
            public void onAdClicked() {
                if (mMediationEventCallback != null) {
                    mMediationEventCallback.reportAdClicked();
                }
            }

            @Override
            public void onAdShow() {
                if (mMediationEventCallback != null) {
                    mMediationEventCallback.reportAdImpression();
                    mMediationEventCallback.onAdOpened();
                }
            }

            @Override
            public void onAdClose() {
                if (mMediationEventCallback != null) {
                    mMediationEventCallback.onAdClosed();
                }
            }
        }, 5 * 1000);
    }

}