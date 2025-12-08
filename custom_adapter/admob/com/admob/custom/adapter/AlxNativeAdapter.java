package com.admob.custom.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.mediation.Adapter;
import com.google.android.gms.ads.mediation.InitializationCompleteCallback;
import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationConfiguration;
import com.google.android.gms.ads.mediation.MediationNativeAdCallback;
import com.google.android.gms.ads.mediation.MediationNativeAdConfiguration;
import com.google.android.gms.ads.mediation.NativeAdMapper;
import com.google.android.gms.ads.VersionInfo;
import com.rixengine.api.AlxAdParam;
import com.rixengine.api.AlxAdSDK;
import com.rixengine.api.AlxImage;
import com.rixengine.api.AlxSdkInitCallback;
import com.rixengine.api.nativead.AlxMediaView;
import com.rixengine.api.nativead.AlxNativeAd;
import com.rixengine.api.nativead.AlxNativeAdLoadedListener;
import com.rixengine.api.nativead.AlxNativeAdLoader;
import com.rixengine.api.nativead.AlxNativeAdView;
import com.rixengine.api.nativead.AlxNativeEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Google Mobile ads RixEngine Native Adapter
 */
public class AlxNativeAdapter extends Adapter {
    private static final String TAG = "AlxNativeAdapter";

    private String unitid = "";
    private String appid = "";
    private String sid = "";
    private String token = "";
    private String host = "";
    private Boolean isDebug = null;
    private JSONObject extras = null;
    private MediationAdLoadCallback<NativeAdMapper, MediationNativeAdCallback> mMediationLoadCallback;
    private MediationNativeAdCallback mMediationEventCallback;

    private AlxNativeAd nativeAd;
    private CustomNativeAdMapper nativeAdMapper;

    @Override
    public void initialize(@NonNull Context context, @NonNull InitializationCompleteCallback initializationCompleteCallback, @NonNull List<MediationConfiguration> list) {
        Log.d(TAG, "alx-admob-adapter: initialize");
        Log.d(TAG, "sdk-version:" + MobileAds.getVersion().toString());
        if (context == null) {
            initializationCompleteCallback.onInitializationFailed(
                    "Initialization Failed: Context is null.");
            return;
        }
        initializationCompleteCallback.onInitializationSucceeded();
    }

    @Override
    public void loadNativeAdMapper(@NonNull MediationNativeAdConfiguration configuration, @NonNull MediationAdLoadCallback<NativeAdMapper, MediationNativeAdCallback> callback) throws RemoteException {
        Log.d(TAG, "sdk-version:" + MobileAds.getVersion().toString());
        Log.d(TAG, "alx-admob-adapter-version:" + AlxMetaInf.ADAPTER_VERSION);
        Log.d(TAG, "alx-admob-adapter: loadNativeAd " + Thread.currentThread().getName());
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
            Log.e(TAG,"host url is null, please check it, now use default host : " + AlxMetaInf.ADAPTER_SDK_HOST_URL);
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
                    loadAds(context, unitid);
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
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
            loadError(1, "alx sdk init error");
        }
    }

    private void loadAds(final Context context, String adId) {
        AlxNativeAdLoader loader = new AlxNativeAdLoader.Builder(context, adId).build();
        loader.loadAd(new AlxAdParam.Builder().build(), new AlxNativeAdLoadedListener() {
            @Override
            public void onAdFailed(int errorCode, String errorMsg) {
                Log.i(TAG, "onAdLoadedFail:" + errorCode + ";" + errorMsg);
                loadError(errorCode, errorMsg);
            }

            @Override
            public void onAdLoaded(List<AlxNativeAd> list) {
                Log.i(TAG, "onAdLoaded:");

                if (list == null || list.isEmpty()) {
                    loadError(100, "no data ads");
                    return;
                }

                try {
                    nativeAd = list.get(0);
                    if (nativeAd == null) {
                        loadError(100, "no data ads");
                        return;
                    }

                    nativeAdMapper = new CustomNativeAdMapper(context, nativeAd);
                    if (mMediationLoadCallback != null) {
                        Log.i(TAG, "onAdLoaded:listener-ok");
                        mMediationEventCallback = mMediationLoadCallback.onSuccess(nativeAdMapper);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                    loadError(101, e.getMessage());
                }

            }
        });
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

    private class CustomNativeAdMapper extends NativeAdMapper {

        private AlxNativeAd bean;
        private Context context;
        private AlxNativeAdView mRootView;

        public CustomNativeAdMapper(Context context, AlxNativeAd bean) {
            this.bean = bean;
            this.context = context;
            bindListener();
            init();
        }

        private void init() {
            if (bean == null) {
                return;
            }
            setHeadline(bean.getTitle());
            setBody(bean.getDescription());
            setPrice(bean.getPrice() + "");
            setAdvertiser(bean.getAdSource());
            setCallToAction(bean.getCallToAction());
            setIcon(new SimpleImage(bean.getIcon()));
            setImages(getImageList());
            setHasVideoContent(bean.getMediaContent().hasVideo());

            mRootView = new AlxNativeAdView(context);
            AlxMediaView mediaView = new AlxMediaView(context);
            mediaView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mediaView.setMediaContent(bean.getMediaContent());
            mRootView.setMediaView(mediaView);
            setMediaView(mediaView);
        }

        @Override
        public void trackViews(@NonNull View view, @NonNull Map<String, View> map, @NonNull Map<String, View> map1) {
            Log.i(TAG, "trackViews");
            if (view instanceof ViewGroup) {
                Log.i(TAG, "trackViews: rootView is ViewGroup");
                ViewGroup rootView = (ViewGroup) view;
                try {
                    if (mRootView != null) {
                        rootView.removeView(mRootView);
                    }
                    if (mRootView == null) {
                        mRootView = new AlxNativeAdView(context);
                    }
                    mRootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    if (map != null && !map.isEmpty()) {
                        for (Map.Entry<String, View> entry : map.entrySet()) {
                            Log.i(TAG, "register:key=" + entry.getKey());
                            mRootView.addView(entry.getKey(), entry.getValue());
                        }
                    }
                    if (map1 != null && !map1.isEmpty()) {
                        for (Map.Entry<String, View> entry : map1.entrySet()) {
                            Log.i(TAG, "register2:key=" + entry.getKey());
                        }
                    }
                    mRootView.setNativeAd(bean);
                    rootView.addView(mRootView, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            } else {
                Log.i(TAG, "trackViews: rootView is other");
            }
        }

        @Override
        public void untrackView(@NonNull View view) {
            Log.i(TAG, "untrackView");
        }

        private List<NativeAd.Image> getImageList() {
            List<NativeAd.Image> imageList = new ArrayList<>();
            if (bean.getImages() != null && bean.getImages().size() > 0) {
                for (AlxImage item : bean.getImages()) {
                    if (item != null) {
                        imageList.add(new SimpleImage(item));
                    }
                }
            }
            return imageList;
        }

        public AlxNativeAdView getAlgorixAdView() {
            return mRootView;
        }

        private void bindListener() {
            if (bean == null) {
                return;
            }
            bean.setNativeEventListener(new AlxNativeEventListener() {
                @Override
                public void onAdClicked() {
                    Log.d(TAG, "onAdClick");
                    if (mMediationEventCallback != null) {
                        mMediationEventCallback.reportAdClicked();
                    }
                }

                @Override
                public void onAdImpression() {
                    Log.d(TAG, "onAdShow");
                    if (mMediationEventCallback != null) {
                        mMediationEventCallback.reportAdImpression();
                        mMediationEventCallback.onAdOpened();
                    }
                }

                @Override
                public void onAdClosed() {
                    Log.d(TAG, "onAdClose");
                    if (mMediationEventCallback != null) {
                        mMediationEventCallback.onAdClosed();
                    }
                }
            });
        }


        private class SimpleImage extends NativeAd.Image {

            private AlxImage image;

            public SimpleImage(AlxImage image) {
                this.image = image;
            }

            @Override
            public double getScale() {
                return 0;
            }

            @Nullable
            @Override
            public Drawable getDrawable() {
                return null;
            }

            @Nullable
            @Override
            public Uri getUri() {
                if (image != null) {
                    return Uri.parse(image.getImageUrl());
                }
                return null;
            }
        }
    }

    private void loadError(int code, String message) {
        if (mMediationLoadCallback != null) {
            mMediationLoadCallback.onFailure(new AdError(code, message, AlxAdSDK.getNetWorkName()));
        }
    }

    @NonNull
    @Override
    public VersionInfo getSDKVersionInfo() {
        String versionString = AlxAdSDK.getNetWorkVersion();
        VersionInfo result = getAdapterVersionInfo(versionString);
        if (result != null) {
            return result;
        }
        return new VersionInfo(0, 0, 0);
    }

    @NonNull
    @Override
    public VersionInfo getVersionInfo() {
        String versionString = AlxAdSDK.getNetWorkVersion();
        VersionInfo result = getAdapterVersionInfo(versionString);
        if (result != null) {
            return result;
        }
        return new VersionInfo(0, 0, 0);
    }

    private VersionInfo getAdapterVersionInfo(String version) {
        if (TextUtils.isEmpty(version)) {
            return null;
        }
        try {
            String[] arr = version.split("\\.");
            if (arr == null || arr.length < 3) {
                return null;
            }
            int major = Integer.parseInt(arr[0]);
            int minor = Integer.parseInt(arr[1]);
            int micro = Integer.parseInt(arr[2]);
            return new VersionInfo(major, minor, micro);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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