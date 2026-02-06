package com.alxad.sdk.demo.topon;

import android.os.Bundle;
import android.util.Log;

import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseListViewActivity;
import com.alxad.sdk.demo.R;
import com.thinkup.core.api.TUSDK;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class TopOnDemoListActivity extends BaseListViewActivity {

    private static final String TAG = "TopOnDemoListActivity";
    private static final AtomicBoolean isAdSDKInit = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAdSDK();
    }

    @Override
    public List<AdapterData> initAdapterData() {
        List<AdapterData> list = new ArrayList<>();

        AdapterData bannerItem = new AdapterData(getString(R.string.banner_ad), TopOnBannerActivity.class);
        AdapterData rewardItem = new AdapterData(getString(R.string.reward_ad), TopOnRewardVideoActivity.class);
        AdapterData interstitialItem = new AdapterData(getString(R.string.interstitial_ad), TopOnInterstitialActivity.class);
        AdapterData nativeItem = new AdapterData(getString(R.string.native_ad), TopOnNativeActivity.class);
        AdapterData splashItem = new AdapterData(getString(R.string.splash_ad), TopOnSplashActivity.class);
        list.add(bannerItem);
        list.add(rewardItem);
        list.add(interstitialItem);
        list.add(nativeItem);
        list.add(splashItem);
        return list;
    }

    private void initAdSDK(){
        if (isAdSDKInit.getAndSet(true)) {
            Log.d(TAG, "TopOn SDK has been initialized");
            return;
        }
        Log.d(TAG, "TopOn SDK start initialize");
        TUSDK.init(getApplicationContext(), AdConfig.TOPON_APP_ID, AdConfig.TOPON_KEY);
        TUSDK.setNetworkLogDebug(true);
    }

}