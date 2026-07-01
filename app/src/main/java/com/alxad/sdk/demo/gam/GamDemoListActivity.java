package com.alxad.sdk.demo.gam;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.alxad.sdk.demo.BaseListViewActivity;
import com.alxad.sdk.demo.R;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class GamDemoListActivity extends BaseListViewActivity {

    private static final String TAG = "GamDemoListActivity";

    private static final AtomicBoolean isAdSDKInit = new AtomicBoolean(false);

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initAdSDK();
    }

    @Override
    public List<AdapterData> initAdapterData() {
        List<AdapterData> list = new ArrayList<>();

        AdapterData bannerItem = new AdapterData(getString(R.string.banner_ad), GamBannerActivity.class);
        AdapterData rewardItem = new AdapterData(getString(R.string.reward_ad), GamRewardVideoActivity.class);
        AdapterData interstitialItem = new AdapterData(getString(R.string.interstitial_ad), GamInterstitialActivity.class);
        AdapterData nativeItem = new AdapterData(getString(R.string.native_ad), GamNativeActivity.class);
        list.add(bannerItem);
        list.add(rewardItem);
        list.add(interstitialItem);
        list.add(nativeItem);
        return list;
    }

    public void initAdSDK(){
        if (isAdSDKInit.getAndSet(true)) {
            Log.d(TAG, "Gam SDK has been initialized");
            return;
        }
        Log.d(TAG, "Gam SDK start initialize");

        new Thread(new Runnable() {
            @Override
            public void run() {
                MobileAds.initialize(getApplicationContext(), new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(@NonNull InitializationStatus status) {
                        Map<String, AdapterStatus> map = status.getAdapterStatusMap();
                        for (Map.Entry<String, AdapterStatus> entry : map.entrySet()) {
                            AdapterStatus adapterStatus = entry.getValue();
                            String desc = adapterStatus != null ? adapterStatus.getDescription() : "";
                            Log.d(TAG, entry.getKey() + "=" + desc);
                        }
                    }
                });
            }
        }).start();

    }

}