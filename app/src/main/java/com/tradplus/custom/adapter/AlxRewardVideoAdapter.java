package com.tradplus.custom.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.rixengine.api.AlxAdSDK;
import com.rixengine.api.AlxRewardVideoAD;
import com.rixengine.api.AlxRewardVideoADListener;
import com.rixengine.api.AlxSdkInitCallback;
import com.tradplus.ads.base.adapter.reward.TPRewardAdapter;
import com.tradplus.ads.base.common.TPError;

import java.util.HashMap;
import java.util.Map;

/**
 * TradPlus 激励广告适配器
 */
public class AlxRewardVideoAdapter extends TPRewardAdapter {

    private static final String TAG = "AlxRewardVideoAdapter";
    private AlxRewardVideoAD mAlxRewardVideoAD;
    private String unitid = "";
    private String appid = "";
    private String sid = "";
    private String token = "";
    private String host = "";
    private Boolean isDebug = null;

    private OnC2STokenListener onC2STokenListener = null;
    private boolean isBiddingLoaded = false;

    @Override
    public void loadCustomAd(Context context, Map<String, Object> map, Map<String, String> tpParams) {
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

    @Override
    public void getC2SBidding(final Context context, final Map<String, Object> localParams, final Map<String, String> tpParams, final OnC2STokenListener onC2STokenListener) {

        this.onC2STokenListener = onC2STokenListener;
        this.isBiddingLoaded = false;
        loadCustomAd(context, localParams, tpParams);
    }
    /**
     * 竞价失败时的上报接⼝ V10.1.0.1 support
     * @param auctionPrice 胜出者的第⼀名价格，单位是美元
     * @param auctionPriceCny 胜出者的第⼀名价格，单位是元（人民币）
     * @param lossReason 竞价失败的原因 返回null，原因 竞价失败
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

    @Override
    public boolean isReady() {
        if (mAlxRewardVideoAD != null) {
            return mAlxRewardVideoAD.isReady();
        }
        return false;
    }

    @Override
    public String getNetworkName() {
        return AlxAdSDK.getNetWorkName();
    }

    @Override
    public String getNetworkVersion() {
        return AlxAdSDK.getNetWorkVersion();
    }

    @Override
    public void showAd() {
        /*
         * mShowListener在showAd()重写时生成，用户实现调用show()后的事件回调
         * 回调方法onAdVideoStart ：广告开始展示
         * 回调方法onAdVideoEnd ：广告关闭
         * 回调方法onAdVideoError ：广告展示失败 ，参数一：ErrorCode错误码；参数2：ErrorMsg错误信息
         * 回调方法onAdVideoClicked ：广告被点击
         * 回调方法onReward ：广告奖励
         * */
        if (mAlxRewardVideoAD != null && mAlxRewardVideoAD.isReady()) {
            mAlxRewardVideoAD.showVideo(null);
        } else {
            if (mShowListener != null) {
                mShowListener.onAdVideoError(new TPError("Alx Video Show Failed"));
            }
        }

    }

    @Override
    public void clean() {
        super.clean();
        Log.i(TAG, "clean");
        if (mAlxRewardVideoAD != null) {
            mAlxRewardVideoAD.destroy();
            mAlxRewardVideoAD = null;
        }
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

    private void startAdLoad(Context context) {
        if (onC2STokenListener != null && isBiddingLoaded) {
            if (mLoadAdapterListener != null) {
                mLoadAdapterListener.loadAdapterLoaded(null);
            }
            return;
        }
        mAlxRewardVideoAD = new AlxRewardVideoAD();
        mAlxRewardVideoAD.load(context, unitid, new AlxRewardVideoADListener() {

            @Override
            public void onRewardedVideoAdLoaded(AlxRewardVideoAD var1) {
                if (mLoadAdapterListener != null) {
                    mLoadAdapterListener.loadAdapterLoaded(null);
                }

                if (onC2STokenListener != null) {
                    // 根据三方文档，loaded成功后获取ECPM
                    String ecpmLevel = String.valueOf(mAlxRewardVideoAD.getPrice());
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
            public void onRewardedVideoAdFailed(AlxRewardVideoAD var1, int errCode, String errMsg) {
                if (mLoadAdapterListener != null) {
                    mLoadAdapterListener.loadAdapterLoadFailed(new TPError(errCode + "", errMsg));
                }
                // 根据三方文档，失败无法获取ecpm，将失败原因回传给TradPlusSDK
                if (onC2STokenListener != null) {
                    onC2STokenListener.onC2SBiddingFailed(errCode + " ", errMsg);
                }
            }

            @Override
            public void onRewardedVideoAdPlayStart(AlxRewardVideoAD var1) {
                if (mShowListener != null) {
                    mShowListener.onAdVideoStart();
                }
                if (onC2STokenListener != null) {
                    Log.i(TAG,"alx bid onRewardedVideoAdPlayStart");
                }
            }

            @Override
            public void onRewardedVideoAdPlayEnd(AlxRewardVideoAD var1) {
            }

            @Override
            public void onRewardedVideoAdPlayFailed(AlxRewardVideoAD var2, int errCode, String errMsg) {
                if (mShowListener != null) {
                    mShowListener.onAdVideoError(new TPError(errCode + "", errMsg));
                }
                // 根据三方文档，失败无法获取ecpm，将失败原因回传给TradPlusSDK
                if (onC2STokenListener != null) {
                    onC2STokenListener.onC2SBiddingFailed(errCode + " ", errMsg);
                }
            }

            @Override
            public void onRewardedVideoAdClosed(AlxRewardVideoAD var1) {
                Log.i(TAG, "onRewardedVideoAdClosed");
                if (mShowListener != null) {
                    mShowListener.onAdClosed();
                }
//                if (mShowListener != null) {
//                    mShowListener.onAdVideoEnd(); //此方法也是关闭
//                }
            }

            @Override
            public void onRewardedVideoAdPlayClicked(AlxRewardVideoAD var1) {
                if (mShowListener != null) {
                    mShowListener.onAdClicked();
                }
            }

            @Override
            public void onReward(AlxRewardVideoAD var1) {
                if (mShowListener != null) {
                    mShowListener.onReward();
                }
            }

        });
    }

}
