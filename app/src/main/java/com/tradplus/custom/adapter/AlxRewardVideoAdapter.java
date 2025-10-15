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
    private String host = "";
    private String token = "";
    private Boolean isdebug = true;


    @Override
    public void loadCustomAd(Context context, Map<String, Object> map, Map<String, String> tpParams) {
        Log.d(TAG, "rixengine-tradplus-adapter-version:" + AlxMetaInf.ADAPTER_VERSION);
        Log.i(TAG, "loadCustomAd");
        if (tpParams != null && parseServer(tpParams)) {
            initSdk(context);
        } else {
            if (mLoadAdapterListener != null) {
                mLoadAdapterListener.loadAdapterLoadFailed(new TPError("rixengine apppid | host | token | sid | appid is empty."));
            }
        }
    }


    private boolean parseServer(Map<String, String> serverExtras) {
        try {
            if (serverExtras.containsKey("unitid")) {
                unitid = (String) serverExtras.get("unitid");
            }
            if (serverExtras.containsKey("appid")) {
                appid = (String) serverExtras.get("appid");

            } else if (serverExtras.containsKey("appkey")) {
                appid = (String) serverExtras.get("appkey");
            }
            if (serverExtras.containsKey("appkey")) {
                sid = (String) serverExtras.get("appkey");
            } else if (serverExtras.containsKey("sid")) {
                sid = (String) serverExtras.get("sid");
            }
            if (serverExtras.containsKey("host")) {
                host = (String) serverExtras.get("host");
            }
            if (serverExtras.containsKey("license")) {
                token = (String) serverExtras.get("license");
            } else if (serverExtras.containsKey("token")) {
                token = (String) serverExtras.get("token");
            }

            if (serverExtras.containsKey("isdebug")) {
                String test = serverExtras.get("isdebug").toString();
                Log.e(TAG, "rixengine debug mode:" + test);
                if (test.equals("true")) {
                    isdebug = true;
                } else {
                    isdebug = false;
                }
            } else {
                Log.e(TAG, "rixengine debug mode: false");
            }
            if (serverExtras.containsKey("tag")) {
                String tag = serverExtras.get("tag").toString();
                Log.e(TAG, "rixengine json tag:" + tag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(unitid) || TextUtils.isEmpty(host) || TextUtils.isEmpty(token) || TextUtils.isEmpty(sid) || TextUtils.isEmpty(appid)) {
            Log.i(TAG, "rixengine unitid | host | token | sid | appid is empty");
            if (mLoadAdapterListener != null) {
                mLoadAdapterListener.loadAdapterLoadFailed(
                        new TPError(TPError.ADAPTER_CONFIGURATION_ERROR + ":rixengine unitid | host | token | sid | appid is empty."));
            }
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
                mShowListener.onAdVideoError(new TPError("rixengine Video Show Failed"));
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
            Log.i(TAG, "rixengine ver:" + AlxAdSDK.getNetWorkVersion() + " rixengine host: " + host + " rixengine token: " + token + " rixengine appid: " + appid + " rixengine sid: " + sid);

            AlxAdSDK.setDebug(isdebug);
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
        mAlxRewardVideoAD = new AlxRewardVideoAD();
        mAlxRewardVideoAD.load(context, unitid, new AlxRewardVideoADListener() {

            @Override
            public void onRewardedVideoAdLoaded(AlxRewardVideoAD var1) {
                if (mLoadAdapterListener != null) {
                    mLoadAdapterListener.loadAdapterLoaded(null);
                }
            }

            @Override
            public void onRewardedVideoAdFailed(AlxRewardVideoAD var1, int errCode, String errMsg) {
                if (mLoadAdapterListener != null) {
                    mLoadAdapterListener.loadAdapterLoadFailed(new TPError(errCode + "", errMsg));
                }
            }

            @Override
            public void onRewardedVideoAdPlayStart(AlxRewardVideoAD var1) {
                if (mShowListener != null) {
                    mShowListener.onAdVideoStart();
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
