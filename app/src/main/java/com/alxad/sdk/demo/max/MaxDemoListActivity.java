package com.alxad.sdk.demo.max;

import android.os.Bundle;
import android.util.Log;

import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseListViewActivity;
import com.alxad.sdk.demo.R;
import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.applovin.sdk.AppLovinSdkInitializationConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MaxDemoListActivity extends BaseListViewActivity {
    private static final String TAG = "MaxDemoListActivity";
    private static final AtomicBoolean isAdSDKInit = new AtomicBoolean(false);

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initAdSDK();
    }


    @Override
    public List<AdapterData> initAdapterData() {
        List<AdapterData> list = new ArrayList<>();

        AdapterData bannerItem = new AdapterData(getString(R.string.banner_ad), MaxBannerActivity.class);
        AdapterData rewardItem = new AdapterData(getString(R.string.reward_ad), MaxRewardVideoActivity.class);
        AdapterData interstitialItem = new AdapterData(getString(R.string.interstitial_ad), MaxInterstitialActivity.class);
        AdapterData nativeItem = new AdapterData(getString(R.string.native_ad), MaxNativeActivity.class);
        list.add(bannerItem);
        list.add(rewardItem);
        list.add(interstitialItem);
        list.add(nativeItem);
        return list;
    }


    //Applovin广告初始化
    private void initAdSDK() {
        if (isAdSDKInit.getAndSet(true)) {
            Log.d(TAG, "Max SDK has been initialized");
            return;
        }
        Log.d(TAG, "Max SDK start initialize");

//        Max 13版本及以上
        AppLovinSdkInitializationConfiguration initConfig = AppLovinSdkInitializationConfiguration.builder
                (AdConfig.MAX_APP_KEY, this.getApplicationContext())
                .setMediationProvider(AppLovinMediationProvider.MAX)
                .build();

        // Initialize the SDK with the configuration
        AppLovinSdk.getInstance( this ).initialize( initConfig, new AppLovinSdk.SdkInitializationListener()
        {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration sdkConfig)
            {
                Log.i(TAG, "AppLovinSdk init" );
            }
        } );
    }

//    Max 13版本以下
//    private void initMaxBelow13(){
//        AppLovinSdk.getInstance(mContext.getApplicationContext()).setMediationProvider("algorix");
//        AppLovinSdk.initializeSdk(this, new AppLovinSdk.SdkInitializationListener() {
//            @Override
//            public void onSdkInitialized(AppLovinSdkConfiguration config) {
//                String name = config.getConsentDialogState().name();
//                Log.i(TAG, "AppLovinSdk-init:" + name);
//                // showDialog();
//            }
//        });
//    }

//    private void showDialog() {
//        Log.i(TAG, "showDialog:" + Thread.currentThread().getName());
//        final AlertDialog dialog = new AlertDialog.Builder(mContext)
//                .setTitle("update config info")
//                .setMessage("update config info")
//                .setPositiveButton("set to true", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        AppLovinPrivacySettings.setHasUserConsent(true, mContext);
//                        AppLovinPrivacySettings.setDoNotSell(true, mContext);
//                        Log.d(TAG, "true-onclick:" + AppLovinPrivacySettings.hasUserConsent(mContext));
//                        dialog.dismiss();
//                    }
//                })
//                .setNegativeButton("set to false", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        AppLovinPrivacySettings.setHasUserConsent(false, mContext);
//                        AppLovinPrivacySettings.setDoNotSell(false, mContext);
//                        Log.d(TAG, "false-onclick:" + AppLovinPrivacySettings.hasUserConsent(mContext));
//                        dialog.dismiss();
//                    }
//                })
//                .setNeutralButton("cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .setCancelable(false)
//                .show();
//    }

}