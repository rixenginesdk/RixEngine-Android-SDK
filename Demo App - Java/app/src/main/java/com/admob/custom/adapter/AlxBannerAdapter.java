package com.admob.custom.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationBannerAd;
import com.google.android.gms.ads.mediation.MediationBannerAdCallback;
import com.google.android.gms.ads.mediation.MediationBannerAdConfiguration;
import com.rixengine.api.AlxAdSDK;
import com.rixengine.api.AlxBannerView;
import com.rixengine.api.AlxBannerViewAdListener;
import com.rixengine.api.AlxSdkInitCallback;

import org.json.JSONObject;

/**
 * Google Mobile ads RixEngine Banner Adapter
 */
public class AlxBannerAdapter extends AlxBaseAdapter implements MediationBannerAd {

    private static final String TAG = "AlxBannerAdapter";
    private String unitid = "";
    private String appid = "";
    private String sid = "";
    private String token = "";
    private String host = "";
    private Boolean isDebug = null;

    private MediationAdLoadCallback<MediationBannerAd, MediationBannerAdCallback> mMediationLoadCallback;
    private MediationBannerAdCallback mMediationEventCallback;

    AlxBannerView mBannerView;

    @Override
    public void loadBannerAd(@NonNull MediationBannerAdConfiguration configuration, @NonNull MediationAdLoadCallback<MediationBannerAd, MediationBannerAdCallback> callback) {
        Log.d(TAG, "alx-admob-adapter-version:" + AlxMetaInf.ADAPTER_VERSION);
        Log.d(TAG, "admob-sdk-version:" + MobileAds.getVersion().toString());
        Log.d(TAG, "alx-admob-adapter: loadBannerAd");
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
            Log.i(TAG, "alx host: " + host + " alx token: " + token + " alx appid: " + appid + "alx sid: " + sid);
            // init
            if (isDebug != null) {
                AlxAdSDK.setDebug(isDebug.booleanValue());
            }
            AlxAdSDK.init(context, host, token, sid, appid, new AlxSdkInitCallback() {
                @Override
                public void onInit(boolean isOk, String msg) {
                    AlxBaseAdapter.sdkInfo();
                    load(context);
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

    @NonNull
    @Override
    public View getView() {
        return mBannerView;
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
            Log.e(TAG, e.getMessage() + "");
        }
    }

    private void load(Context context) {
        mBannerView = new AlxBannerView(context);
        // auto refresh ad  default = open = 1, 0 = close
        mBannerView.setBannerRefresh(0);
        //mBannerView.setBannerRefresh(15);
        final AlxBannerViewAdListener alxBannerADListener = new AlxBannerViewAdListener() {
            @Override
            public void onAdLoaded() {
                if (mMediationLoadCallback != null) {
                    mMediationEventCallback = mMediationLoadCallback.onSuccess(AlxBannerAdapter.this);
                }
            }

            @Override
            public void onAdError(int errorCode, String errorMsg) {
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
        };
        mBannerView.loadAd(unitid, alxBannerADListener);
    }


    private void loadError(int code, String message) {
        if (mMediationLoadCallback != null) {
            mMediationLoadCallback.onFailure(new AdError(code, message, AlxAdSDK.getNetWorkName()));
        }
    }

}