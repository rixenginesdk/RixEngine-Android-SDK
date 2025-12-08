package com.admob.custom.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VersionInfo;
import com.google.android.gms.ads.mediation.Adapter;
import com.google.android.gms.ads.mediation.InitializationCompleteCallback;
import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationConfiguration;
import com.google.android.gms.ads.mediation.MediationRewardedAd;
import com.google.android.gms.ads.mediation.MediationRewardedAdCallback;
import com.google.android.gms.ads.mediation.MediationRewardedAdConfiguration;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.rixengine.api.AlxAdSDK;
import com.rixengine.api.AlxRewardVideoAD;
import com.rixengine.api.AlxRewardVideoADListener;
import com.rixengine.api.AlxSdkInitCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Google Mobile ads RixEngine Reward Video Adapter
 */
public class AlxRewardVideoAdapter extends Adapter implements MediationRewardedAd {
    private final String TAG = "AlxRewardVideoAdapter";
    private static final String ALX_AD_UNIT_KEY = "parameter";

    private AlxRewardVideoAD alxRewardVideoAD;
    private String unitid = "";
    private String appid = "";
    private String sid = "";
    private String token = "";
    private String host = "";
    private Boolean isDebug = null;
    private JSONObject extras = null;
    private Context mContext;
    private MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback> mediationAdLoadCallBack;
    private MediationRewardedAdCallback mMediationRewardedAdCallback;

    @Override
    public void initialize(Context context, InitializationCompleteCallback initializationCompleteCallback
            , List<MediationConfiguration> list) {
        Log.d(TAG, "alx-admob-adapter-version:" + AlxMetaInf.ADAPTER_VERSION);
        Log.e(TAG, "alx initialize...");
        Log.d(TAG, "sdk-version:" + MobileAds.getVersion().toString());
        for (MediationConfiguration configuration : list) {
            Bundle serverParameters = configuration.getServerParameters();
            String serviceString = serverParameters.getString(ALX_AD_UNIT_KEY);
            if (!TextUtils.isEmpty(serviceString)) {
                parseServer(serviceString);
            }
        }
        if (initSDk(context)) {
            initializationCompleteCallback.onInitializationSucceeded();
        } else {
            initializationCompleteCallback.onInitializationFailed("alx sdk init error");
        }
    }

    @Override
    public VersionInfo getVersionInfo() {
        String versionString = AlxAdSDK.getNetWorkVersion();
        String[] splits = versionString.split("\\.");

        if (splits.length >= 3) {
            int major = Integer.parseInt(splits[0]);
            int minor = Integer.parseInt(splits[1]);
            int micro = Integer.parseInt(splits[2]) * 100 + Integer.parseInt(splits[3]);
            return new VersionInfo(major, minor, micro);
        }

        return new VersionInfo(0, 0, 0);
    }

    @Override
    public VersionInfo getSDKVersionInfo() {
        String versionString = AlxAdSDK.getNetWorkVersion();
        String[] splits = versionString.split("\\.");
        if (splits.length >= 3) {
            int major = Integer.parseInt(splits[0]);
            int minor = Integer.parseInt(splits[1]);
            int micro = Integer.parseInt(splits[2]);
            return new VersionInfo(major, minor, micro);
        }
        return new VersionInfo(0, 0, 0);
    }

    @Override
    public void showAd(Context context) {
        Log.e(TAG, "alx showAd...");
        if (!(context instanceof Activity)) {
            Log.e(TAG, "context is not Activity");
            mMediationRewardedAdCallback.onAdFailedToShow(new AdError(1,
                    "An activity context is required to show Sample rewarded ad."
                    , AlxAdSDK.getNetWorkName())
            );
            return;
        }
        mContext = context;
        if (!alxRewardVideoAD.isReady()) {
            mMediationRewardedAdCallback.onAdFailedToShow(new AdError(1, "No ads to show."
                    , AlxAdSDK.getNetWorkName()));
            return;
        }
        alxRewardVideoAD.showVideo((Activity) context);
    }

    @Override
    public void loadRewardedAd(MediationRewardedAdConfiguration configuration
            , MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback> mediationAdLoadCallback) {
        Log.d(TAG, "sdk-version:" + MobileAds.getVersion().toString());
        Log.d(TAG, "alx-admob-adapter-version:" + AlxMetaInf.ADAPTER_VERSION);
        Log.d(TAG, "alx loadRewardedAd");
        Context context = configuration.getContext();
        mediationAdLoadCallBack = mediationAdLoadCallback;
        Bundle serverParameters = configuration.getServerParameters();
        String serviceString = serverParameters.getString(ALX_AD_UNIT_KEY);
        if (!TextUtils.isEmpty(serviceString)) {
            parseServer(serviceString);
        }
        initSDk(context);
    }

    private boolean initSDk(final Context context) {
        if (TextUtils.isEmpty(unitid)) {
            Log.d(TAG, "alx unitid is empty");
            mediationAdLoadCallBack.onFailure(new AdError(1, "alx unitid is empty."
                    , AlxAdSDK.getNetWorkName()));
            return false;
        }
        if (TextUtils.isEmpty(sid)) {
            Log.d(TAG, "alx sid is empty");
            mediationAdLoadCallBack.onFailure(new AdError(1, "alx sid is empty."
                    , AlxAdSDK.getNetWorkName()));
            return false;
        }
        if (TextUtils.isEmpty(appid)) {
            Log.d(TAG, "alx appid is empty");
            mediationAdLoadCallBack.onFailure(new AdError(1, "alx appid is empty."
                    , AlxAdSDK.getNetWorkName()));
            return false;
        }
        if (TextUtils.isEmpty(token)) {
            Log.d(TAG, "alx token is empty");
            mediationAdLoadCallBack.onFailure(new AdError(1, "alx token is empty"
                    , AlxAdSDK.getNetWorkName()));
            return false;
        }
        if (TextUtils.isEmpty(host)) {
            if (TextUtils.isEmpty(AlxMetaInf.ADAPTER_SDK_HOST_URL)) {
                Log.d(TAG, "alx host is empty");
                mediationAdLoadCallBack.onFailure(new AdError(1, "alx host is empty", AlxAdSDK.getNetWorkName()));
                return false;
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
                    //sdk init success, begin load ad
                    alxRewardVideoAD = new AlxRewardVideoAD();
                    alxRewardVideoAD.load(context, unitid, new AlxRewardVideoADListener() {
                        @Override
                        public void onRewardedVideoAdLoaded(AlxRewardVideoAD var1) {
                            Log.d(TAG, "onRewardedVideoAdLoaded");
                            if (mediationAdLoadCallBack != null)
                                mMediationRewardedAdCallback = (MediationRewardedAdCallback) mediationAdLoadCallBack
                                        .onSuccess(AlxRewardVideoAdapter.this);
                        }


                        @Override
                        public void onRewardedVideoAdFailed(AlxRewardVideoAD var1, int errCode, String errMsg) {
                            Log.d(TAG, "onRewardedVideoAdFailed: " + errMsg);
                            if (mediationAdLoadCallBack != null) mediationAdLoadCallBack
                                    .onFailure(new AdError(errCode, errMsg, AlxAdSDK.getNetWorkName()));
                        }

                        @Override
                        public void onRewardedVideoAdPlayStart(AlxRewardVideoAD var1) {
                            if (mMediationRewardedAdCallback != null && mContext instanceof Activity) {
                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // runOnUiThread
                                        mMediationRewardedAdCallback.reportAdImpression();
                                        mMediationRewardedAdCallback.onAdOpened();
                                        mMediationRewardedAdCallback.onVideoStart();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onRewardedVideoAdPlayEnd(AlxRewardVideoAD var1) {
                            Log.d(TAG, "onRewardedVideoAdPlayEnd: ");
                            if (mMediationRewardedAdCallback != null)
                                mMediationRewardedAdCallback.onVideoComplete();
                        }

                        @Override
                        public void onRewardedVideoAdPlayFailed(AlxRewardVideoAD var2, int errCode, String errMsg) {
                            Log.d(TAG, "onShowFail: " + errMsg);
                            if (mMediationRewardedAdCallback != null)
                                mMediationRewardedAdCallback.onAdFailedToShow(
                                        new AdError(errCode, errMsg, AlxAdSDK.getNetWorkName()));
                        }

                        @Override
                        public void onRewardedVideoAdClosed(AlxRewardVideoAD var1) {
                            Log.d(TAG, "onRewardedVideoAdClosed: ");
                            if (mMediationRewardedAdCallback != null) {
                                mMediationRewardedAdCallback.onAdClosed();
                            }
                        }

                        @Override
                        public void onRewardedVideoAdPlayClicked(AlxRewardVideoAD var1) {
                            Log.d(TAG, "onRewardedVideoAdPlayClicked: ");
                            if (mMediationRewardedAdCallback != null)
                                mMediationRewardedAdCallback.reportAdClicked();
                        }

                        @Override
                        public void onReward(AlxRewardVideoAD var1) {
                            Log.d(TAG, "onReward: ");
                            if (mMediationRewardedAdCallback != null) {
                                mMediationRewardedAdCallback.onUserEarnedReward(new RewardItem() {
                                    @Override
                                    public String getType() {
                                        return "";
                                    }

                                    @Override
                                    public int getAmount() {
                                        return 1;
                                    }
                                });
                            }
                        }
                    });
                }
            });
            Map<String, Object> extraParameters = getAlxExtraParameters(extras);
            printExtraParameters(extraParameters);
            setAlxExtraParameters(extraParameters);
//            // set GDPR
//            AlxAdSDK.setSubjectToGDPR(true);
//            // set GDPR Consent
//            AlxAdSDK.setUserConsent("1");
//            // set COPPA
//            AlxAdSDK.setBelowConsentAge(true);
//            // set CCPA
//            AlxAdSDK.subjectToUSPrivacy("1YYY");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
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
            extras = json.optJSONObject("extras");
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

    private void setAlxExtraParameters(Map<String, Object> parameters) {
        if (parameters != null && !parameters.isEmpty()) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                AlxAdSDK.addExtraParameters(entry.getKey(), entry.getValue());
            }
        }
    }

    private Map<String, Object> getAlxExtraParameters(JSONObject extras) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (extras == null) {
                return map;
            }
            Iterator<String> keys = extras.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = extras.get(key);
                map.put(key, value);
            }
        } catch (Exception e) {
            Log.e(TAG, "alx extras field error:" + e.getMessage());
        }
        return map;
    }

    private void printExtraParameters(Map<String, Object> map) {
        try {
            if (map == null || map.isEmpty()) {
                Log.d(TAG, "alx Extra Parameters:null");
                return;
            }
            JSONObject json = new JSONObject(map);
            Log.d(TAG, "alx Extra Parameters:" + json.toString());
        } catch (Exception e) {
            Log.e(TAG, "printExtraParameters error:" + e.getMessage());
        }
    }

}
