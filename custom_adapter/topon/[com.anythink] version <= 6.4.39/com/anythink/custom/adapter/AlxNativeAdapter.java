package com.anythink.custom.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATBiddingListener;
import com.anythink.core.api.ATBiddingNotice;
import com.anythink.core.api.ATBiddingResult;
import com.anythink.core.api.MediationInitCallback;
import com.anythink.nativead.api.ATNativePrepareInfo;
import com.anythink.nativead.unitgroup.api.CustomNativeAd;
import com.anythink.nativead.unitgroup.api.CustomNativeAdapter;
import com.rixengine.api.AlxAdParam;
import com.rixengine.api.AlxAdSDK;
import com.rixengine.api.AlxImage;
import com.rixengine.api.nativead.AlxMediaContent;
import com.rixengine.api.nativead.AlxMediaView;
import com.rixengine.api.nativead.AlxNativeAd;
import com.rixengine.api.nativead.AlxNativeAdLoadedListener;
import com.rixengine.api.nativead.AlxNativeAdLoader;
import com.rixengine.api.nativead.AlxNativeAdView;
import com.rixengine.api.nativead.AlxNativeEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * TopOn 信息流广告适配器
 */
public class AlxNativeAdapter extends CustomNativeAdapter {
    private final String TAG = AlxNativeAdapter.class.getSimpleName();

    private String unitid = "";
    private String appid = "";
    private String sid = "";
    private String token = "";
    private String host = "";
    private Boolean isDebug = null;

    private ATBiddingListener mBiddingListener;

    public void startBid(Context context) {
        Log.d(TAG,"startBid ");
        startAdLoad(context);

    }

    @Override
    public boolean startBiddingRequest(final Context context, Map<String, Object> serverExtra, Map<String, 	Object> localExtra, final ATBiddingListener biddingListener) {
        //从serverExtra中获取后台配置的自定义平台的广告位ID
        mBiddingListener = biddingListener;
        loadCustomNetworkAd(context,serverExtra,localExtra);
        //必须return true
        return true;
    }

    @Override
    public void loadCustomNetworkAd(Context context, Map<String, Object> serverExtra, Map<String, Object> map1) {
        Log.d(TAG, "alx-topon-adapter-version:" + AlxMetaInf.ADAPTER_VERSION);
        Log.i(TAG, "loadCustomNetworkAd");
        if (parseServer(serverExtra)) {
            initSdk(context,serverExtra);
        } else {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError("", "alx host | unitid | token | sid | appid is empty.");
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

    private void startAdLoad(final Context context) {
        AlxNativeAdLoadedListener loadListener = new AlxNativeAdLoadedListener() {
            @Override
            public void onAdFailed(int errorCode, String errorMsg) {
                Log.i(TAG, "onAdLoadedFail:" + errorCode + ";" + errorMsg);
                if (mLoadListener != null) {
                    mLoadListener.onAdLoadError(errorCode + "", errorMsg);
                }
            }

            @Override
            public void onAdLoaded(List<AlxNativeAd> ads) {
                if (ads == null || ads.isEmpty()) {
                    if (mLoadListener != null) {
                        mLoadListener.onAdLoadError("100", "no fill");
                    }
                    return;
                }

                AlgorixNativeAd[] result = new AlgorixNativeAd[ads.size()];
                AlgorixNativeAd price = new AlgorixNativeAd(context,ads.get(0));
                boolean isOk = false;
                try {
                    for (int i = 0; i < ads.size(); i++) {
                        AlxNativeAd item = ads.get(i);
                        AlgorixNativeAd bean = new AlgorixNativeAd(context, item);
                        bean.setAdData();
                        result[i] = bean;

                        Log.d(TAG,"startBid  load success");

                        //get price
                        double bidPrice = item.getPrice();
                        Log.d(TAG,"bidPrice: "+bidPrice);
                        //get currency
                        ATAdConst.CURRENCY currency = ATAdConst.CURRENCY.USD;

                        //uuid
                        String token = UUID.randomUUID().toString();

                        //biddingNotice
                        ATBiddingNotice biddingNotice = null;

                        Log.d(TAG,"startBid  price "+bidPrice);
                        Log.d(TAG,"startBid  token "+token);
                        if (mBiddingListener != null) {
                            mBiddingListener.onC2SBiddingResultWithCache(
                                    ATBiddingResult.success(bidPrice, token, biddingNotice, currency), bean);
                        }
                    }
                    isOk = true;
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                    isOk = false;
                    if (mLoadListener != null) {
                        mLoadListener.onAdLoadError("101", e.getMessage());
                    }
                }
                if (isOk) {
                    if (mLoadListener != null) {
                        mLoadListener.onAdCacheLoaded(result);
                    }
                }

            }
        };

        AlxNativeAdLoader loader = new AlxNativeAdLoader.Builder(context, unitid).build();
        loader.loadAd(new AlxAdParam.Builder().build(), loadListener);
    }

    private class AlgorixNativeAd extends CustomNativeAd {

        private Context mContext;

        private AlxNativeAd mNativeAd;
        private AlxNativeAdView mAdContainer;
        private AlxMediaView mMediaView;

        public AlgorixNativeAd(Context context, AlxNativeAd nativeAd) {
            mContext = context.getApplicationContext();
            mNativeAd = nativeAd;
        }

        public void setAdData() {
            if (mNativeAd == null) {
                return;
            }
            bindListener();

            setTitle(mNativeAd.getTitle());
            setDescriptionText(mNativeAd.getDescription());

            String iconUrl = "";
            String imageUrl = "";
            if (mNativeAd.getIcon() != null) {
                iconUrl = mNativeAd.getIcon().getImageUrl();
            }
            List<String> list = new ArrayList<>();
            List<AlxImage> imageList = mNativeAd.getImages();
            if (imageList != null && imageList.size() > 0) {
                AlxImage image0 = imageList.get(0);
                if (image0 != null) {
                    imageUrl = image0.getImageUrl();
                }
                for (AlxImage item : imageList) {
                    if (item != null && item.getImageUrl() != null) {
                        list.add(item.getImageUrl());
                    }
                }
            }
            setIconImageUrl(iconUrl);
            setMainImageUrl(imageUrl);
            setImageUrlList(list);
            setAdFrom(mNativeAd.getAdSource());
            setCallToActionText(mNativeAd.getCallToAction());
        }

        @Override
        public Bitmap getAdLogo() {
            if (mNativeAd != null) {
                return mNativeAd.getAdLogo();
            }
            return null;
        }

        @Override
        public void prepare(View view, ATNativePrepareInfo nativePrepareInfo) {
            if (view == null) {
                return;
            }

            try {
                if (mAdContainer == null) {
                    return;
                }
                if (nativePrepareInfo != null) {
                    List<View> clickViewList = nativePrepareInfo.getClickViewList();
                    if (clickViewList != null && !clickViewList.isEmpty()) {
                        for (int i = 0; i < clickViewList.size(); i++) {
                            String key = String.valueOf(1000 + i);
                            mAdContainer.addView(key, clickViewList.get(i));
                        }
                    }
                }
                if (mMediaView != null) {
                    mAdContainer.setMediaView(mMediaView);
                }
                mAdContainer.setNativeAd(mNativeAd);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }

        @Override
        public boolean isNativeExpress() {
            Log.d(TAG, "isNativeExpress");
            return false;
        }

        @Override
        public ViewGroup getCustomAdContainer() {
            Log.d(TAG, "getCustomAdContainer");
            mAdContainer = new AlxNativeAdView(mContext);
            return mAdContainer;
        }

        @Override
        public View getAdMediaView(Object... objects) {
            Log.d(TAG, "getAdMediaView");
            try {
                if (mMediaView != null) {
                    mMediaView.destroy();
                    mMediaView = null;
                }
                mMediaView = new AlxMediaView(mContext);
                if (mNativeAd != null && mNativeAd.getMediaContent() != null) {
                    mNativeAd.getMediaContent().setVideoLifecycleListener(new AlxMediaContent.VideoLifecycleListener() {

                        @Override
                        public void onVideoStart() {
                            notifyAdVideoStart();
                        }

                        @Override
                        public void onVideoEnd() {
                            notifyAdVideoEnd();
                        }
                    });
                    mMediaView.setMediaContent(mNativeAd.getMediaContent());
                }
                return mMediaView;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        @Override
        public void clear(View view) {
            Log.d(TAG, "clear");
            try {
                if (mMediaView != null) {
                    mMediaView.destroy();
                    mMediaView = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
        }

        @Override
        public void destroy() {
            Log.d(TAG, "destroy");
            try {
                if (mMediaView != null) {
                    mMediaView.destroy();
                    mMediaView = null;
                }
                if (mAdContainer != null) {
                    mAdContainer.destroy();
                    mAdContainer = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
        }

        private void bindListener() {
            if (mNativeAd == null) {
                return;
            }
            mNativeAd.setNativeEventListener(new AlxNativeEventListener() {
                @Override
                public void onAdClicked() {
                    notifyAdClicked();
                }

                @Override
                public void onAdImpression() {
                    notifyAdImpression();
                }

                @Override
                public void onAdClosed() {
                    notifyAdDislikeClick();
                }
            });
        }

    }

    @Override
    public void destory() {
    }

    @Override
    public String getNetworkName() {
        return AlxAdSDK.getNetWorkName();
    }

    @Override
    public String getNetworkPlacementId() {
        return unitid;
    }

    @Override
    public String getNetworkSDKVersion() {
        return AlxAdSDK.getNetWorkVersion();
    }
}