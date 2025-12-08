package com.applovin.mediation.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.applovin.mediation.adapter.parameters.MaxAdapterParameters;
import com.applovin.sdk.AppLovinSdkSettings;
import com.rixengine.api.AlxAdParam;
import com.rixengine.api.AlxAdSDK;
import com.rixengine.api.AlxBannerView;
import com.rixengine.api.AlxBannerViewAdListener;
import com.rixengine.api.AlxImage;
import com.rixengine.api.AlxInterstitialAD;
import com.rixengine.api.AlxInterstitialADListener;
import com.rixengine.api.AlxRewardVideoAD;
import com.rixengine.api.AlxRewardVideoADListener;
import com.rixengine.api.AlxSdkInitCallback;
import com.rixengine.api.nativead.AlxMediaContent;
import com.rixengine.api.nativead.AlxMediaView;
import com.rixengine.api.nativead.AlxNativeAd;
import com.rixengine.api.nativead.AlxNativeAdLoadedListener;
import com.rixengine.api.nativead.AlxNativeAdLoader;
import com.rixengine.api.nativead.AlxNativeAdView;
import com.rixengine.api.nativead.AlxNativeEventListener;
import com.applovin.impl.sdk.utils.BundleUtils;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.adapter.MaxAdViewAdapter;
import com.applovin.mediation.adapter.MaxAdapterError;
import com.applovin.mediation.adapter.MaxInterstitialAdapter;
import com.applovin.mediation.adapter.MaxNativeAdAdapter;
import com.applovin.mediation.adapter.MaxRewardedAdapter;
import com.applovin.mediation.adapter.listeners.MaxAdViewAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxInterstitialAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxNativeAdAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxRewardedAdapterListener;
import com.applovin.mediation.adapter.parameters.MaxAdapterInitializationParameters;
import com.applovin.mediation.adapter.parameters.MaxAdapterResponseParameters;
import com.applovin.mediation.nativeAds.MaxNativeAd;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.applovin.sdk.AppLovinPrivacySettings;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkUtils;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Applovin ads RixEngine Adapter
 */
public class AlgorixMediationAdapter extends MediationAdapterBase implements MaxAdViewAdapter, MaxInterstitialAdapter, MaxRewardedAdapter, MaxNativeAdAdapter {

    String ADAPTER_VERSION = "3.9.1";
    // 服务器请求EndPoint域名, 由平台分配，请手动修改， 例如：https://yoursubdomain.svr.rixengine.com/rtb
    String ADAPTER_SDK_HOST_URL = "https://demo.use.svr.rixengine.com/rtb"; //测试HOST，正式需要修改

    private static final String TAG = "AlgorixMediationAdapter";

    private static final int DEFAULT_IMAGE_TASK_TIMEOUT_SECONDS = 10;

    private static final AtomicBoolean initialized = new AtomicBoolean();

    private static InitializationStatus status;

    private AlxBannerView bannerAD;
    private AlxInterstitialAD interstitialAD;
    private AlxRewardVideoAD rewardVideoAD;
    private AlxNativeAd nativeAD;
    private AlxNativeAdView nativeAdView;

    public AlgorixMediationAdapter(AppLovinSdk appLovinSdk) {
        super(appLovinSdk);
    }

    @Override
    public void initialize(MaxAdapterInitializationParameters parameters, Activity activity, final OnCompletionListener onCompletionListener) {
        Log.d(TAG, "initialize alx sdk……");
        Log.d(TAG, "alx-applovin-adapter-version:" + ADAPTER_VERSION);
        initSdk(parameters, activity, true, onCompletionListener);
    }

    @Override
    public String getSdkVersion() {
        return AlxAdSDK.getNetWorkVersion();
    }

    @Override
    public String getAdapterVersion() {
        return ADAPTER_VERSION;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (bannerAD != null) {
            bannerAD.destroy();
            bannerAD = null;
        }

        if (interstitialAD != null) {
            interstitialAD.destroy();
            interstitialAD = null;
        }

        if (rewardVideoAD != null) {
            rewardVideoAD.destroy();
            rewardVideoAD = null;
        }

        if (nativeAD != null) {
            nativeAD.destroy();
            nativeAD = null;
        }

        if (nativeAdView != null) {
            nativeAdView.destroy();
            nativeAdView = null;
        }
    }

    //banner load
    @Override
    public void loadAdViewAd(MaxAdapterResponseParameters parameters, MaxAdFormat maxAdFormat, Activity activity, final MaxAdViewAdapterListener listener) {
        if (initialized.get() == false) {
            initSdk(parameters, activity, false, null);
        }
        String adId = parameters.getThirdPartyAdPlacementId();
        Log.d(TAG, "loadAdViewAd ad id:" + adId);
        if (TextUtils.isEmpty(adId)) {
            listener.onAdViewAdLoadFailed(MaxAdapterError.INVALID_CONFIGURATION);
            return;
        }
        bannerAD = new AlxBannerView(activity);
        bannerAD.setBannerRefresh(0);
        final AlxBannerViewAdListener alxBannerADListener = new AlxBannerViewAdListener() {
            @Override
            public void onAdLoaded() {
                if (listener != null) {
                    listener.onAdViewAdLoaded(bannerAD);
                }
            }

            @Override
            public void onAdError(int errorCode, String errorMsg) {
                Log.e(TAG, "onAdError: errCode=" + errorCode + ";errMsg=" + errorMsg);
                if (listener != null) {
                    listener.onAdViewAdLoadFailed(MaxAdapterError.NO_FILL);
                }
            }

            @Override
            public void onAdClicked() {
                if (listener != null) {
                    listener.onAdViewAdClicked();
                }
            }

            @Override
            public void onAdShow() {
                if (listener != null) {
                    listener.onAdViewAdDisplayed();
                }
            }

            @Override
            public void onAdClose() {
                if (listener != null) {
                    listener.onAdViewAdHidden();
                }
            }
        };
        // 利用第三方统计，加入自定义打点事件（开发者可根据需要加入第三方埋点统计）
        // 统计请求事件
        // 320 * 50 banner
        bannerAD.loadAd(adId, alxBannerADListener);
        // MREC
        //bannerAD.loadAd(adId, AlxBannerView.AlxAdParam.FORMAT_MREC, alxBannerADListener);
    }

    //interstitial ad load
    @Override
    public void loadInterstitialAd(MaxAdapterResponseParameters parameters, Activity activity, final MaxInterstitialAdapterListener listener) {
        if (initialized.get() == false) {
            initSdk(parameters, activity, false, null);
        }
        String adId = parameters.getThirdPartyAdPlacementId();
        Log.d(TAG, "loadInterstitialAd ad id:" + adId);
        if (TextUtils.isEmpty(adId)) {
            listener.onInterstitialAdLoadFailed(MaxAdapterError.INVALID_CONFIGURATION);
            return;
        }
        interstitialAD = new AlxInterstitialAD();
        interstitialAD.load(activity, adId, new AlxInterstitialADListener() {
            @Override
            public void onInterstitialAdLoaded() {
                if (listener != null) {
                    listener.onInterstitialAdLoaded();
                }
            }

            @Override
            public void onInterstitialAdLoadFail(int errorCode, String errorMsg) {
                Log.e(TAG, "onInterstitialAdLoadFail: errCode=" + errorCode + ";errMsg=" + errorMsg);
                if (listener != null) {
                    listener.onInterstitialAdLoadFailed(MaxAdapterError.NO_FILL);
                }
            }

            @Override
            public void onInterstitialAdClicked() {
                if (listener != null) {
                    listener.onInterstitialAdClicked();
                }
            }

            @Override
            public void onInterstitialAdShow() {
                if (listener != null) {
                    listener.onInterstitialAdDisplayed();
                }
            }

            @Override
            public void onInterstitialAdClose() {
                if (listener != null) {
                    listener.onInterstitialAdHidden();
                }
            }

            @Override
            public void onInterstitialAdVideoStart() {

            }

            @Override
            public void onInterstitialAdVideoEnd() {

            }

            @Override
            public void onInterstitialAdVideoError(int errorCode, String errorMsg) {

            }
        });
    }

    //interstitial ad show
    @Override
    public void showInterstitialAd(MaxAdapterResponseParameters parameters, Activity activity, MaxInterstitialAdapterListener listener) {
        Log.d(TAG, "showInterstitialAd");
        if (interstitialAD != null && interstitialAD.isReady()) {
            interstitialAD.show(activity);
        } else {
            Log.d(TAG, "showInterstitialAd: ad no ready");
            listener.onInterstitialAdDisplayFailed(MaxAdapterError.AD_NOT_READY);
        }
    }

    //reward ad load
    @Override
    public void loadRewardedAd(MaxAdapterResponseParameters parameters, Activity activity, final MaxRewardedAdapterListener listener) {
        if (initialized.get() == false) {
            initSdk(parameters, activity, false, null);
        }
        String adId = parameters.getThirdPartyAdPlacementId();
        Log.d(TAG, "loadRewardedAd ad id:" + adId);
        if (TextUtils.isEmpty(adId)) {
            listener.onRewardedAdLoadFailed(MaxAdapterError.INVALID_CONFIGURATION);
            return;
        }
        rewardVideoAD = new AlxRewardVideoAD();
        rewardVideoAD.load(activity, adId, new AlxRewardVideoADListener() {
            @Override
            public void onRewardedVideoAdLoaded(AlxRewardVideoAD var1) {
                Log.d(TAG, "onRewardedVideoAdLoaded");
                if (listener != null) {
                    listener.onRewardedAdLoaded();
                }
            }

            @Override
            public void onRewardedVideoAdFailed(AlxRewardVideoAD var1, int errCode, String errMsg) {
                Log.e(TAG, "onRewardedVideoAdFailed: errCode=" + errCode + ";errMsg=" + errMsg);
                if (listener != null) {
                    listener.onRewardedAdLoadFailed(MaxAdapterError.NO_FILL);
                }
            }

            @Override
            public void onRewardedVideoAdPlayStart(AlxRewardVideoAD var1) {
                Log.d(TAG, "onRewardedVideoAdPlayStart");
                if (listener != null) {
                    listener.onRewardedAdDisplayed();
                }
            }

            @Override
            public void onRewardedVideoAdPlayEnd(AlxRewardVideoAD var1) {
                Log.d(TAG, "onRewardedVideoAdPlayEnd");
            }

            @Override
            public void onRewardedVideoAdPlayFailed(AlxRewardVideoAD var2, int errCode, String errMsg) {
                Log.d(TAG, "onRewardedVideoAdPlayFailed");
                if (listener != null) {
                    listener.onRewardedAdDisplayFailed(new MaxAdapterError(errCode, errMsg));
                }
            }

            @Override
            public void onRewardedVideoAdClosed(AlxRewardVideoAD var1) {
                Log.d(TAG, "onRewardedVideoAdClosed");
                if (listener != null) {
                    listener.onRewardedAdHidden();
                }
            }

            @Override
            public void onRewardedVideoAdPlayClicked(AlxRewardVideoAD var1) {
                Log.d(TAG, "onRewardedVideoAdPlayClicked");
                if (listener != null) {
                    listener.onRewardedAdClicked();
                }
            }

            @Override
            public void onReward(AlxRewardVideoAD var1) {
                Log.d(TAG, "onReward");
                if (listener != null) {
                    listener.onUserRewarded(getReward());
                }
            }
        });
    }

    //reward ad
    @Override
    public void showRewardedAd(MaxAdapterResponseParameters parameters, Activity activity, final MaxRewardedAdapterListener listener) {
        Log.d(TAG, "showRewardedAd");
        if (rewardVideoAD != null && rewardVideoAD.isReady()) {
            rewardVideoAD.showVideo(activity);
        } else {
            Log.d(TAG, "showRewardedAd: ad no ready");
            listener.onRewardedAdDisplayFailed(MaxAdapterError.AD_NOT_READY);
        }
    }

    //native ad
    @Override
    public void loadNativeAd(MaxAdapterResponseParameters parameters, Activity activity, final MaxNativeAdAdapterListener listener) {
        if (initialized.get() == false) {
            initSdk(parameters, activity, false, null);
        }
        String adId = parameters.getThirdPartyAdPlacementId();
        Log.d(TAG, "loadNativeAd ad id:" + adId);
        if (TextUtils.isEmpty(adId)) {
            listener.onNativeAdLoadFailed(MaxAdapterError.INVALID_CONFIGURATION);
            return;
        }
        Context applicationContext = (activity != null) ? activity.getApplicationContext() : getApplicationContext();
        NativeAdListener nativeAdListener = new NativeAdListener(parameters, applicationContext, listener);
        AlxNativeAdLoader loader = new AlxNativeAdLoader.Builder(activity, adId).build();
        loader.loadAd(new AlxAdParam.Builder().build(), nativeAdListener);
    }

    private class NativeAdListener implements AlxNativeAdLoadedListener {
        private MaxAdapterResponseParameters parameters;
        private MaxNativeAdAdapterListener listener;
        final Bundle serverParameters;
        private Context context;

        public NativeAdListener(MaxAdapterResponseParameters parameters, Context context, MaxNativeAdAdapterListener listener) {
            this.parameters = parameters;
            this.context = context;
            this.listener = listener;
            serverParameters = parameters.getServerParameters();
        }

        @Override
        public void onAdFailed(int errorCode, String errorMsg) {
            Log.e(TAG, "native-onAdLoadedFail: errCode=" + errorCode + ";errMsg=" + errorMsg);
            if (listener != null) {
                listener.onNativeAdLoadFailed(MaxAdapterError.NO_FILL);
            }
        }

        public void reportEvent(int event, String desc) {
//            if (alxNativeAD != null) {
//                alxNativeAD.reportEvent(event, desc);
//            }
        }

        @Override
        public void onAdLoaded(List<AlxNativeAd> ads) {
            if (ads == null || ads.isEmpty()) {
                if (listener != null) {
                    listener.onNativeAdLoadFailed(MaxAdapterError.NO_FILL);
                }
                return;
            }
            nativeAD = ads.get(0);
            if (nativeAD == null) {
                if (listener != null) {
                    listener.onNativeAdLoadFailed(MaxAdapterError.NO_FILL);
                }
                return;
            }

            String templateName = BundleUtils.getString("template", "", serverParameters);
            final boolean isTemplateAd = AppLovinSdkUtils.isValidString(templateName);
            if (isTemplateAd && TextUtils.isEmpty(nativeAD.getTitle())) {
                e("Native ad (" + nativeAD + ") does not have required assets.");
                listener.onNativeAdLoadFailed(new MaxAdapterError(-5400, "Missing Native Ad Assets"));
                return;
            }

            nativeAD.setNativeEventListener(new AlxNativeEventListener() {
                @Override
                public void onAdClicked() {
                    if (listener != null) {
                        listener.onNativeAdClicked();
                    }
                }

                @Override
                public void onAdImpression() {
                    if (listener != null) {
                        listener.onNativeAdDisplayed(null);
                    }
                }

                @Override
                public void onAdClosed() {
                }
            });

            getCachingExecutorService().execute(new Runnable() {
                @Override
                public void run() {
                    Future<Drawable> iconDrawableFuture = null;
                    try {
                        if (nativeAD.getIcon() != null && !TextUtils.isEmpty(nativeAD.getIcon().getImageUrl()) && context != null) {
                            reportEvent(202, "[max] icon load");
                            iconDrawableFuture = createDrawableFuture(nativeAD.getIcon().getImageUrl(), context.getResources());
                            reportEvent(203, "[max] icon success");
                        }
                    } catch (Throwable th) {
                        reportEvent(204, "[max] icon error:" + th.getMessage());
                        e("Image fetching tasks failed", th);
                    }

                    Future<Drawable> imageDrawableFuture = null;
                    try {
                        if (nativeAD.getImages() != null && nativeAD.getImages().size() > 0) {
                            AlxImage image = nativeAD.getImages().get(0);
                            if (image != null && !TextUtils.isEmpty(image.getImageUrl()) && context != null) {
                                reportEvent(202, "[max] image load");
                                imageDrawableFuture = createDrawableFuture(image.getImageUrl(), context.getResources());
                                reportEvent(203, "[max] image success");
                            }
                        }
                    } catch (Throwable th) {
                        reportEvent(204, "[max] image error:" + th.getMessage());
                        e("Image fetching tasks failed", th);
                    }

                    Drawable iconDrawable = null;
                    Drawable mediaViewImageDrawable = null;
                    try {
                        // Execute and timeout tasks if incomplete within the given time
                        int imageTaskTimeoutSeconds = BundleUtils.getInt("image_task_timeout_seconds", DEFAULT_IMAGE_TASK_TIMEOUT_SECONDS, parameters.getServerParameters());
                        if (iconDrawableFuture != null) {
                            iconDrawable = iconDrawableFuture.get(imageTaskTimeoutSeconds, TimeUnit.SECONDS);
                        }
                        if (imageDrawableFuture != null) {
                            mediaViewImageDrawable = imageDrawableFuture.get(imageTaskTimeoutSeconds, TimeUnit.SECONDS);
                        }
                    } catch (Throwable th) {
                        e("Image fetching tasks failed", th);
                    }

                    final MaxNativeAd.MaxNativeAdImage icon = iconDrawable != null ? new MaxNativeAd.MaxNativeAdImage(iconDrawable) : null;
                    final Drawable mediaDrawable = mediaViewImageDrawable;

                    AppLovinSdkUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (nativeAD == null) {
                                if (listener != null) {
                                    listener.onNativeAdLoadFailed(MaxAdapterError.NO_FILL);
                                }
                                return;
                            }

                            View mediaView = null;
                            AlxMediaContent mediaContent = nativeAD.getMediaContent();
                            if (mediaContent != null) {
                                AlxMediaView alxMediaView = new AlxMediaView(context);
                                alxMediaView.setMediaContent(mediaContent);
                                mediaView = alxMediaView;
                            } else if (mediaDrawable != null && context != null) {
                                ImageView imageView = new ImageView(context);
                                imageView.setImageDrawable(mediaDrawable);
                                mediaView = imageView;
                            }

                            ImageView logoView = null;
                            if (nativeAD.getAdLogo() != null && context != null) {
                                logoView = new ImageView(context);
                                logoView.setImageBitmap(nativeAD.getAdLogo());
                            }

                            MaxNativeAd.Builder maxBuilder = new MaxNativeAd.Builder()
                                    .setAdFormat(MaxAdFormat.NATIVE)
                                    .setTitle(nativeAD.getTitle())
                                    .setBody(nativeAD.getDescription())
                                    .setCallToAction(nativeAD.getCallToAction())
                                    .setAdvertiser(nativeAD.getAdSource())
                                    .setIcon(icon)
                                    .setMediaView(mediaView)
                                    .setOptionsView(logoView);

                            if (AppLovinSdk.VERSION_CODE >= 11_04_03_99) {
                                maxBuilder.setMainImage(new MaxNativeAd.MaxNativeAdImage(mediaDrawable));
                            }

                            MaxNativeAd maxNativeAd = new MaxAlgorixNativeAd(maxBuilder);

                            reportEvent(205, "[max] max show");
                            Log.d(TAG, "Native ad fully loaded:");
                            if (listener != null) {
                                listener.onNativeAdLoaded(maxNativeAd, null);
                            }
                        }
                    });
                }
            });
        }
    }

    private class MaxAlgorixNativeAd extends MaxNativeAd {

        public MaxAlgorixNativeAd(Builder builder) {
            super(builder);
        }

        @Override
        public void prepareViewForInteraction(final MaxNativeAdView maxNativeAdView) {
            final AlxNativeAd nativeAD = AlgorixMediationAdapter.this.nativeAD;
            if (nativeAD == null) {
                e("Failed to register native ad view. Native ad is null");
                return;
            }

            nativeAdView = new AlxNativeAdView(maxNativeAdView.getContext());
            View mainView = maxNativeAdView.getMainView();
            maxNativeAdView.removeView(mainView);
            nativeAdView.addView(mainView);
            maxNativeAdView.addView(nativeAdView);

            nativeAdView.setIconView(maxNativeAdView.getIconImageView());
            nativeAdView.setTitleView(maxNativeAdView.getTitleTextView());
            nativeAdView.setAdSourceView(maxNativeAdView.getAdvertiserTextView());
            nativeAdView.setDescriptionView(maxNativeAdView.getBodyTextView());
            nativeAdView.setCallToActionView(maxNativeAdView.getCallToActionButton());

            View mediaView = getMediaView();
            if (mediaView instanceof AlxMediaView) {
                nativeAdView.setMediaView((AlxMediaView) mediaView);
            } else if (mediaView instanceof ImageView) {
                nativeAdView.setImageView(mediaView);
            }
            nativeAdView.setNativeAd(nativeAD);
        }
    }

    private void initSdk(MaxAdapterParameters parameters, Activity activity, boolean isInit, final OnCompletionListener onCompletionListener) {
        try {
            status = InitializationStatus.INITIALIZING;
            Context context = (activity != null) ? activity.getApplicationContext() : getApplicationContext();

            Bundle bundle = parameters.getCustomParameters();
            String host = bundle.getString("host");
            String appid = bundle.getString("appid");
            String sid = bundle.getString("sid");
            String token = bundle.getString("token");
            String debug = bundle.getString("isdebug");
            Bundle extras = bundle.getBundle("extras");

            Boolean isDebug = null;
            if (debug != null) {
                if (debug.equalsIgnoreCase("true")) {
                    isDebug = Boolean.TRUE;
                } else if (debug.equalsIgnoreCase("false")) {
                    isDebug = Boolean.FALSE;
                }
            }

            if (TextUtils.isEmpty(host) && !TextUtils.isEmpty(ADAPTER_SDK_HOST_URL)) {
                host = ADAPTER_SDK_HOST_URL;
                Log.e(TAG, "host url is null, please check it, now use default host : " + ADAPTER_SDK_HOST_URL);
            }

            Log.d(TAG, "alx-applovin-init:host=" + host + " token=" + token + "  sid=" + sid + " appid=" + appid);
//            Log.d(TAG, "alx-applovin-init:extras=" + extras);

            if (TextUtils.isEmpty(host) || TextUtils.isEmpty(appid) || TextUtils.isEmpty(sid) || TextUtils.isEmpty(token)) {
                Log.d(TAG, "initialize alx params: host or appid or sid or token is null");
                status = InitializationStatus.DOES_NOT_APPLY;
                initialized.set(false);
                if (onCompletionListener != null) {
                    status = InitializationStatus.INITIALIZED_SUCCESS;
                    onCompletionListener.onCompletion(status, null);
                }
            } else {
                if (isDebug != null) {
                    AlxAdSDK.setDebug(isDebug.booleanValue());
                }
                AlxAdSDK.init(context, host, token, sid, appid, new AlxSdkInitCallback() {
                    @Override
                    public void onInit(boolean isOk, String msg) {
                        initialized.set(true);
                        status = InitializationStatus.INITIALIZED_SUCCESS;
                        if (onCompletionListener != null) {
                            onCompletionListener.onCompletion(status, null);
                        }
                    }
                });
                Map<String, Object> extraParameters = getExtraParameters(extras, context);
                printExtraParameters(extraParameters);
                setAlxExtraParameters(extraParameters);
                // // set GDPR
                // // Subject to GDPR Flag: Please pass a Boolean value to indicate if the user is subject to GDPR regulations or not.
                // // Your app should make its own determination as to whether GDPR is applicable to the user or not.
                // AlxAdSDK.setSubjectToGDPR(true);

                String tcString = null;
                try {
                    SharedPreferences Preferences = PreferenceManager.getDefaultSharedPreferences(context);
                    tcString = Preferences.getString("IABTCF_TCString", "");
                    int gdprApplies = Preferences.getInt("IABTCF_gdprApplies", 0);
                    Log.d(TAG, "alx-applovin-init:tcString= " + tcString + " gdprApplies: " + gdprApplies);
                    if (gdprApplies != 0) {
                        AlxAdSDK.setSubjectToGDPR(true);
                    } else {
                        AlxAdSDK.setSubjectToGDPR(false);
                    }
                } catch (Exception ignored) {

                }

                if (tcString != null && !tcString.isEmpty()) {
                    AlxAdSDK.setUserConsent(tcString);
                } else if (parameters != null) {
                    // Set GDPR Consent value
                    String strGDPRConsent = "0";
                    if (TextUtils.isEmpty(parameters.getConsentString())) {
                        if (AppLovinPrivacySettings.hasUserConsent(context)) {
                            try {
                                SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                                strGDPRConsent = mPreferences.getString("IABTCF_TCString", "");
                            } catch (Exception ignored) {

                            }
                            if (TextUtils.isEmpty(strGDPRConsent)) {
                                strGDPRConsent = "1";
                            }
                        }
                        AlxAdSDK.setUserConsent(strGDPRConsent);
                    } else {
                        AlxAdSDK.setUserConsent(parameters.getConsentString());
                    }
                    Log.i(TAG, "Max parameter hasUserConsent:" + parameters.hasUserConsent()
                            + " getConsentString:" + parameters.getConsentString()
                            + " isAgeRestrictedUser:" + parameters.isAgeRestrictedUser()
                            + " hasUserConsent-2:" + AppLovinPrivacySettings.hasUserConsent(context));
                }

                // // set COPPA true or false
                // AlxAdSDK.setBelowConsentAge(true);
                // // set CCPA
                // AlxAdSDK.subjectToUSPrivacy("1YYY");

            }
        } catch (Exception e) {
            Log.d(TAG, "initialize alx error:" + e.getMessage());
            status = InitializationStatus.INITIALIZED_FAILURE;
            if (onCompletionListener != null) {
                onCompletionListener.onCompletion(status, null);
            }
        }
    }

    private void setAlxExtraParameters(Map<String, Object> parameters) {
        if (parameters != null && !parameters.isEmpty()) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                AlxAdSDK.addExtraParameters(entry.getKey(), entry.getValue());
            }
        }
    }

    private Map<String, Object> getExtraParameters(Bundle extras, Context context) {
        Map<String, Object> map = null;
        try {
            map = getAlxExtraParameters(extras);
            if (map == null || map.isEmpty()) {
                map = getMaxExtraParameters(context);
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

    private Map<String, Object> getAlxExtraParameters(Bundle extras) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (extras == null) {
                return map;
            }
            for (String key : extras.keySet()) {
                try {
                    Object obj = extras.get(key);
                    map.put(key, obj);
                } catch (Exception e1) {
                    Log.e(TAG, "alx extras field " + key + " error:" + e1.getMessage());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "alx extras field error:" + e.getMessage());
        }
        return map;
    }

    private Map<String, Object> getMaxExtraParameters(Context context) {
        Map<String, Object> map = new HashMap<>();
        try {
            AppLovinSdkSettings settings = AppLovinSdk.getInstance(context).getSettings();
            Map<String, String> values = settings.getExtraParameters();
            if (values != null && !values.isEmpty()) {
                map.putAll(values);
            }
        } catch (Exception e) {
            Log.e(TAG, "max extras field error:" + e.getMessage());
        }
        return map;
    }

}