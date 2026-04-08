package com.alxad.sdk.demo.ironsource;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseListViewActivity;
import com.alxad.sdk.demo.R;
import com.unity3d.mediation.LevelPlay;
import com.unity3d.mediation.LevelPlayConfiguration;
import com.unity3d.mediation.LevelPlayInitError;
import com.unity3d.mediation.LevelPlayInitListener;
import com.unity3d.mediation.LevelPlayInitRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class IronSourceDemoListActivity extends BaseListViewActivity {

    private static final String TAG = "IronSourceDemoActivity";
    private static final AtomicBoolean isAdSDKInit = new AtomicBoolean(false);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAdSDK();
    }


    @Override
    public List<AdapterData> initAdapterData() {
        List<AdapterData> list = new ArrayList<>();

        //IronSourceDemoListActivity.AdapterData bannerItem = new IronSourceDemoListActivity.AdapterData("banner 广告", MoPubBannerActivity.class);
        AdapterData rewardItem = new AdapterData(getString(R.string.reward_ad), IronSourceRewardedVideoActivity.class);
        AdapterData interstitialItem = new AdapterData(getString(R.string.interstitial_ad), IronSourceInterstitialActivity.class);
        AdapterData bannerItem = new AdapterData(getString(R.string.banner_ad), IronSourceBannerActivity.class);
        // IronSourceDemoListActivity.AdapterData nativeItem = new IronSourceDemoListActivity.AdapterData("native 广告", MoPubNativeActivity.class);
        //list.add(bannerItem);
        list.add(rewardItem);
        list.add(interstitialItem);
        list.add(bannerItem);
        //list.add(nativeItem);
        return list;
    }

    //广告配置
    private void initAdSDK() {
        if (isAdSDKInit.getAndSet(true)) {
            Log.d(TAG, "IronSource SDK has been initialized");
            return;
        }
        Log.d(TAG, "IronSource SDK start initialize");

        //IronSource初始化
        LevelPlayInitRequest initRequest = new LevelPlayInitRequest.Builder(AdConfig.IRON_SOURCE_APP_KEY)
                .withUserId(AdConfig.IRON_SOURCE_USER_ID)
                .build();
        LevelPlayInitListener initListener = new LevelPlayInitListener() {

            @Override
            public void onInitSuccess(@NonNull LevelPlayConfiguration levelPlayConfiguration) {
                Log.d(TAG, "onInitSuccess");
            }

            @Override
            public void onInitFailed(@NonNull LevelPlayInitError levelPlayInitError) {
                Log.d(TAG, "onInitFailed:"+levelPlayInitError.getErrorMessage());
            }
        };
        LevelPlay.init(this.getApplicationContext(), initRequest, initListener);
    }

}