package com.alxad.sdk.demo.tradplus;

import android.os.Bundle;
import android.util.Log;

import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseListViewActivity;
import com.alxad.sdk.demo.R;
import com.tradplus.ads.open.TradPlusSdk;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TradPlusDemoListActivity extends BaseListViewActivity {

    private static final String TAG = "AdmobDemoListActivity";
    private static final AtomicBoolean isAdSDKInit = new AtomicBoolean(false);

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initAdSDK();
    }

    @Override
    public List<AdapterData> initAdapterData() {
        List<AdapterData> list = new ArrayList<>();

        AdapterData bannerItem = new AdapterData(getString(R.string.banner_ad), TradPlusBannerActivity.class);
        AdapterData rewardItem = new AdapterData(getString(R.string.reward_ad), TradPlusRewardVideoActivity.class);
        AdapterData interstitialItem = new AdapterData(getString(R.string.interstitial_ad), TradPlusInterstitialActivity.class);
        AdapterData nativeItem = new AdapterData(getString(R.string.native_ad), TradPlusNativeActivity.class);
        AdapterData splashItem = new AdapterData(getString(R.string.splash_ad), TradPlusSplashActivity.class);
        list.add(bannerItem);
        list.add(rewardItem);
        list.add(interstitialItem);
        list.add(nativeItem);
        list.add(splashItem);
        return list;
    }

    private void initAdSDK() {
        if (isAdSDKInit.getAndSet(true)) {
            Log.d(TAG, "TradPlus SDK has been initialized");
            return;
        }
        Log.d(TAG, "TradPlus SDK start initialize");

        TradPlusSdk.initSdk(this.getApplicationContext(), AdConfig.TRAD_PLUS_APP_ID);
    }


}