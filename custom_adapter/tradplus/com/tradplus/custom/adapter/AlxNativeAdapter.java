package com.tradplus.custom.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.rixengine.api.AlxAdParam;
import com.rixengine.api.AlxAdSDK;
import com.rixengine.api.AlxImage;
import com.rixengine.api.AlxSdkInitCallback;
import com.rixengine.api.nativead.AlxMediaContent;
import com.rixengine.api.nativead.AlxMediaView;
import com.rixengine.api.nativead.AlxNativeAd;
import com.rixengine.api.nativead.AlxNativeAdLoadedListener;
import com.rixengine.api.nativead.AlxNativeAdLoader;
import com.rixengine.api.nativead.AlxNativeAdView;
import com.rixengine.api.nativead.AlxNativeEventListener;
import com.tradplus.ads.base.adapter.nativead.TPNativeAdView;
import com.tradplus.ads.base.adapter.nativead.TPNativeAdapter;
import com.tradplus.ads.base.bean.TPBaseAd;
import com.tradplus.ads.base.common.TPError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TradPlus平台 信息流广告适配器
 * 【tradPlus 无法获取三方平台的容器getCustomAdContainer()（部分三方源需要用他们提供的容器），导致没法上报，所以不推荐使用原生广告适配器】
 */
public class AlxNativeAdapter extends TPNativeAdapter {
    private final String TAG = "AlxNativeAdapter";

    private String unitid = "";
    private String appid = "";
    private String sid = "";
    private String token = "";
    private String host = "";

    private Boolean isDebug = null;

    private AlxNativeAd nativeAd;

    private OnC2STokenListener onC2STokenListener = null;
    private boolean isBiddingLoaded = false;

    @Override
    public void loadCustomAd(Context context, Map<String, Object> userParams, Map<String, String> tpParams) {
        Log.d(TAG, "alx-tradplus-adapter-version:" + AlxMetaInf.ADAPTER_VERSION);
        Log.i(TAG, "loadCustomAd");
        if (tpParams != null && parseServer(tpParams)) {
            initSdk(context);
        } else {
            if (mLoadAdapterListener != null) {
                mLoadAdapterListener.loadAdapterLoadFailed(new TPError("alx host | apppid | token | sid | appid is empty."));
            }
        }
    }

    // TradPlus Native C2S 模式测试后无法展示，目前不可用
    @Override
    public void getC2SBidding(final Context context, final Map<String, Object> localParams, final Map<String, String> tpParams, final OnC2STokenListener onC2STokenListener) {

        this.onC2STokenListener = onC2STokenListener;
        this.isBiddingLoaded = false;
        loadCustomAd(context, localParams, tpParams);
    }

    /**
     * 竞价失败时的上报接⼝ V10.1.0.1 support
     *
     * @param auctionPrice    胜出者的第⼀名价格，单位是美元
     * @param auctionPriceCny 胜出者的第⼀名价格，单位是元（人民币）
     * @param lossReason      竞价失败的原因 返回null，原因 竞价失败
     */
    @Override
    public void setLossNotifications(String auctionPrice, String auctionPriceCny, String lossReason) {
        // auctionPrice和 auctionPriceCny均需要判空，部分平台规定不可以回传价格
    }

    private boolean parseServer(Map<String, String> serverExtras) {
        try {
            if (serverExtras.containsKey("host")) {
                host = serverExtras.get("host");
            }
            if (serverExtras.containsKey("appid")) {
                appid = serverExtras.get("appid");
            }
            if (serverExtras.containsKey("sid")) {
                sid = serverExtras.get("sid");
            }
            if (serverExtras.containsKey("token")) {
                token = serverExtras.get("token");
            }
            if (serverExtras.containsKey("unitid")) {
                unitid = serverExtras.get("unitid");
            }

            if (serverExtras.containsKey("isdebug")) {
                String debug = serverExtras.get("isdebug");
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
                    //if (isOk){
                    startAdLoad(context);
                    //}
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startAdLoad(final Context context) {
        if (onC2STokenListener != null && isBiddingLoaded) {
            if (mLoadAdapterListener != null) {
                mLoadAdapterListener.loadAdapterLoaded(null);
            }
            return;
        }

        AlxNativeAdLoadedListener loadListener = new AlxNativeAdLoadedListener() {
            @Override
            public void onAdFailed(int errorCode, String errorMsg) {
                Log.i(TAG, "onAdLoadedFail:" + errorCode + ";" + errorMsg);
                if (mLoadAdapterListener != null) {
                    mLoadAdapterListener.loadAdapterLoadFailed(new TPError(errorCode + "", errorMsg));
                }

                // 根据三方文档，失败无法获取ecpm，将失败原因回传给TradPlusSDK
                if (onC2STokenListener != null) {
                    onC2STokenListener.onC2SBiddingFailed(errorCode + " ", errorMsg);
                }
            }

            @Override
            public void onAdLoaded(List<AlxNativeAd> ads) {
                if (ads == null || ads.isEmpty()) {
                    if (mLoadAdapterListener != null) {
                        mLoadAdapterListener.loadAdapterLoadFailed(new TPError("no ad fill"));
                    }
                    return;
                }

                try {
                    AlxNativeAd info = ads.get(0);
                    if (info == null) {
                        if (mLoadAdapterListener != null) {
                            mLoadAdapterListener.loadAdapterLoadFailed(new TPError("no ad fill"));
                        }
                        return;
                    }

                    AlxCustomNativeAd customNativeAd = new AlxCustomNativeAd(context, info);
                    customNativeAd.init();
                    if (mLoadAdapterListener != null) {
                        mLoadAdapterListener.loadAdapterLoaded(customNativeAd);
                    }

                    if (onC2STokenListener != null) {
                        // 根据三方文档，loaded成功后获取ECPM
                        String ecpmLevel = String.valueOf(info.getPrice());
                        if (TextUtils.isEmpty(ecpmLevel)) {
                            //价格返回空，无意义，通过onC2STokenListener调用失败回调
                            onC2STokenListener.onC2SBiddingFailed("", "ecpmLevel is Empty");
                            return;
                        }
                        Log.i(TAG, "alx ecpm:" + ecpmLevel);
                        // 成功获取price，通过onC2STokenListener将价格传给TradPlusSDK（美金）
                        Map<String, Object> hashMap = new HashMap<>();
                        // key必须是"ecpm"，value是double类型
                        hashMap.put("ecpm", Double.parseDouble(ecpmLevel));
                        onC2STokenListener.onC2SBiddingResult(hashMap);
                    }
                    isBiddingLoaded = true;
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                    if (mLoadAdapterListener != null) {
                        mLoadAdapterListener.loadAdapterLoadFailed(new TPError(e.getMessage()));
                    }
                }
            }
        };

        AlxNativeAdLoader loader = new AlxNativeAdLoader.Builder(context, unitid).build();
        loader.loadAd(new AlxAdParam.Builder().build(), loadListener);
    }


    @Override
    public void clean() {
        if (nativeAd != null) {
            nativeAd.destroy();
            nativeAd = null;
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

    public class AlxCustomNativeAd extends TPBaseAd {

        private Context mContext;

        private AlxNativeAd mNativeAd;
        private AlxNativeAdView mAdContainer;
        private AlxMediaView mMediaView;

        public AlxCustomNativeAd(Context context, AlxNativeAd nativeAd) {
            mContext = context.getApplicationContext();
            mNativeAd = nativeAd;
        }

        public void init() {
            bindListener();
        }

        @Override
        public Object getNetworkObj() {
            Log.d(TAG, "getNetworkObj");
            return mNativeAd;
        }

        /**
         * 自渲染模式中，开发者在渲染结束后，会把可点击的view传过来(addview前调用)
         *
         * @param viewGroup  开发者自己的布局，主要的几个view用tag标记过了
         * @param clickViews 可点击的广告view
         */
        @Override
        public void registerClickView(ViewGroup viewGroup, ArrayList<View> clickViews) {
            Log.d(TAG, "registerClickView");
//            if (viewGroup == null) {
//                return;
//            }
            try {
                if (mAdContainer == null) {
                    return;
                }
                if (clickViews != null && !clickViews.isEmpty()) {
                    for (int i = 0; i < clickViews.size(); i++) {
                        String key = String.valueOf(1000 + i);
                        mAdContainer.addView(key, clickViews.get(i));
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


        /**
         * 获取tradplus包装过的对象，里面可以获取自渲染的所有素材
         * 对应广告类型AD_TYPE_NORMAL_NATIVE
         *
         * @return
         */
        @Override
        public TPNativeAdView getTPNativeView() {
            Log.d(TAG, "TPNativeAdView");
            if (mNativeAd == null) {
                return null;
            }
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

            if (mAdContainer != null) {
                mAdContainer.destroy();
            }
            mAdContainer = new AlxNativeAdView(mContext);

            TPNativeAdView obj = new TPNativeAdView();
            obj.setIconImageUrl(iconUrl);
            obj.setMainImageUrl(imageUrl);
            obj.setPicUrls(list);
            obj.setTitle(mNativeAd.getTitle());
            obj.setSubTitle(mNativeAd.getDescription());
            obj.setCallToAction(mNativeAd.getCallToAction());
            obj.setAdSource(mNativeAd.getAdSource());
            obj.setMediaView(getAdMediaView());
            return obj;
        }

        public View getAdMediaView() {
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
                            if (mShowListener != null) {
                                mShowListener.onAdVideoStart();
                            }
                        }

                        @Override
                        public void onVideoEnd() {
                            if (mShowListener != null) {
                                mShowListener.onAdVideoEnd();
                            }
                        }

                        @Override
                        public void onVideoPlayError(int code, String error) {
                            if (mShowListener != null) {
                                mShowListener.onAdVideoError(new TPError(error));
                            }
                        }

                        @Override
                        public void onVideoMute(boolean isMute) {
                            super.onVideoMute(isMute);
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

        /**
         * 获取广告类型，
         * AD_TYPE_NORMAL_NATIVE：自渲染，
         * AD_TYPE_NATIVE_EXPRESS：模板渲染，
         * AD_TYPE_NATIVE_LIST：多个模板view
         *
         * @return
         */
        @Override
        public int getNativeAdType() {
            Log.d(TAG, "getNativeAdType");
            return AD_TYPE_NORMAL_NATIVE;
        }

        /**
         * 获取模板渲染的view，对应广告类型AD_TYPE_NATIVE_EXPRESS
         *
         * @return
         */
        @Override
        public View getRenderView() {
            Log.d(TAG, "getRenderView");
            return null;
        }

        /**
         * 获取多模板渲染view，对应AD_TYPE_NATIVE_LIST
         *
         * @return
         */
        @Override
        public List<View> getMediaViews() {
            Log.d(TAG, "getMediaViews");
            return null;
        }

        /**
         * 获取三方平台的容器（部分三方源需要用他们提供的容器）
         *
         * @return
         */
        @Override
        public ViewGroup getCustomAdContainer() {
            Log.d(TAG, "getCustomAdContainer");
            if (mAdContainer == null) {
                mAdContainer = new AlxNativeAdView(mContext);
            }
            return mAdContainer;
        }

        @Override
        public void clean() {
            Log.d(TAG, "clean");
            try {
                if (mMediaView != null) {
                    mMediaView = null;
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

                public void onAdClicked() {
                    if (mShowListener != null) {
                        mShowListener.onAdClicked();
                    }
                }

                public void onAdImpression() {
                    if (mShowListener != null) {
                        mShowListener.onAdShown();
                    }
                    if (onC2STokenListener != null) {
                        Log.i(TAG, "alx bid onAdImpression");
                    }
                }

                public void onAdClosed() {
                    if (mShowListener != null) {
                        mShowListener.onAdClosed();
                    }
                }
            });
        }
    }


}