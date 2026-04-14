package com.alxad.sdk.demo.admob;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.alxad.sdk.demo.BaseListViewActivity;
import com.alxad.sdk.demo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AdmobDemoListActivity extends BaseListViewActivity {

    private static final String TAG = "AdmobDemoListActivity";
    private static final AtomicBoolean isAdSDKInit = new AtomicBoolean(false);

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initAdSDK();
    }

    @Override
    public List<BaseListViewActivity.AdapterData> initAdapterData() {
        List<AdapterData> list = new ArrayList<>();

        AdapterData bannerItem = new AdapterData(getString(R.string.banner_ad), AdmobBannerActivity.class);
        AdapterData rewardItem = new AdapterData(getString(R.string.reward_ad), AdmobRewardVideoActivity.class);
        AdapterData interstitialItem = new AdapterData(getString(R.string.interstitial_ad), AdmobInterstitialActivity.class);
        AdapterData nativeItem = new AdapterData(getString(R.string.native_ad), AdmobNativeActivity.class);
        list.add(bannerItem);
        list.add(rewardItem);
        list.add(interstitialItem);
        list.add(nativeItem);
        return list;
    }

    public void initAdSDK(){
        if (isAdSDKInit.getAndSet(true)) {
            Log.d(TAG, "Admob SDK has been initialized");
            return;
        }
        Log.d(TAG, "Admob SDK start initialize");

        new Thread(new Runnable() {
            @Override
            public void run() {
                MobileAds.initialize(getApplicationContext(), new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus status) {
                        Log.d(TAG,status.toString());
                    }
                });
            }
        }).start();

    }

}