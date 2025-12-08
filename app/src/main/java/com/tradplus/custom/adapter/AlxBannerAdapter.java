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

import java.util.HashMap;
import java.util.Map;

/**
 * TradPlus平台 Banner广告适配器
 */
public class AlxBannerAdapter extends TPBannerAdapter {
    private static final String TAG = "AlxBannerAdapter";
    private String unitid = "";
    private String appid = "";
    private String sid = "";
    private String token = "";
    private String host = "";
    private Boolean isShowClose = false;
    private Boolean isDebug = null;
    AlxBannerView mBannerView;
    private TPBannerAdImpl mTPBannerAd;

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

    // TradPlus Banner C2S 模式测试后无法展示，目前不可用
    @Override
    public void getC2SBidding(final Context context, final Map<String, Object> localParams, final Map<String, String> tpParams, final OnC2STokenListener onC2STokenListener) {

        this.onC2STokenListener = onC2STokenListener;
        this.isBiddingLoaded = false;
        loadCustomAd(context, localParams, tpParams);
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

            if (serverExtras.containsKey("isShowClose")) {
                String test = serverExtras.get("isShowClose");
                Log.e(TAG, "alx show close:" + test);
                if (test != null && test.equals("true")) {
                    isShowClose = true;
                } else {
                    isShowClose = false;
                }
            } else {
                Log.e(TAG, "alx debug mode: false");
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
                    Log.i(TAG, "sdk onInit:" + isOk);
                    loadAd(context);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAd(Context context) {
        if (onC2STokenListener != null && isBiddingLoaded) {
            if (mLoadAdapterListener != null) {
                mLoadAdapterListener.loadAdapterLoaded(null);
            }
            return;
        }
        mBannerView = new AlxBannerView(context);
        final AlxBannerViewAdListener alxBannerADListener = new AlxBannerViewAdListener() {
            @Override
            public void onAdLoaded() {
                if (mLoadAdapterListener != null) {
                    mTPBannerAd = new TPBannerAdImpl(null, mBannerView);
                    mLoadAdapterListener.loadAdapterLoaded(mTPBannerAd);
                }
                if (onC2STokenListener != null) {
                    // 根据三方文档，loaded成功后获取ECPM
                    String ecpmLevel = String.valueOf(mBannerView.getPrice());
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
            }

            @Override
            public void onAdError(int errorCode, String errorMsg) {
                if (mLoadAdapterListener != null) {
                    mLoadAdapterListener.loadAdapterLoadFailed(new TPError(errorCode + "", errorMsg));
                }
                // 根据三方文档，失败无法获取ecpm，将失败原因回传给TradPlusSDK
                if (onC2STokenListener != null) {
                    onC2STokenListener.onC2SBiddingFailed(errorCode + " ", errorMsg);
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
                if(onC2STokenListener!=null){
                    Log.i(TAG, "alx Bidding onAdShow");
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