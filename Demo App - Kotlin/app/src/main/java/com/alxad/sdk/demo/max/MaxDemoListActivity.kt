package com.alxad.sdk.demo.max

import android.os.Bundle
import android.util.Log
import com.alxad.sdk.demo.AdConfig
import com.alxad.sdk.demo.BaseListViewActivity
import com.alxad.sdk.demo.R
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdk.SdkInitializationListener
import com.applovin.sdk.AppLovinSdkConfiguration
import com.applovin.sdk.AppLovinSdkInitializationConfiguration
import java.util.concurrent.atomic.AtomicBoolean

class MaxDemoListActivity : BaseListViewActivity() {

    override val TAG: String = "MaxDemoListActivity"
    val isAdSDKInit: AtomicBoolean = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdSDK()
    }

    override fun initAdapterData(): MutableList<AdapterData>? {
        val list: MutableList<AdapterData> = ArrayList<AdapterData>()
        list.add(AdapterData(getString(R.string.banner_ad), MaxBannerActivity::class.java))
        list.add(AdapterData(getString(R.string.reward_ad), MaxRewardVideoActivity::class.java))
        list.add(AdapterData(getString(R.string.interstitial_ad), MaxInterstitialActivity::class.java))
        list.add(AdapterData(getString(R.string.native_ad), MaxNativeActivity::class.java))

        return list
    }

    private fun initAdSDK() {
        if (isAdSDKInit.getAndSet(true)) {
            Log.d(TAG, "Max SDK has been initialized")
            return
        }
        Log.d(TAG, "Max SDK start initialize")

        val initConfig = AppLovinSdkInitializationConfiguration.builder(
            AdConfig.MAX_APP_KEY,
            this.applicationContext
        )
            .setMediationProvider(AppLovinMediationProvider.MAX)
            .build()


        // Initialize the SDK with the configuration
        AppLovinSdk.getInstance(this).initialize(initConfig, object : SdkInitializationListener {
            override fun onSdkInitialized(sdkConfig: AppLovinSdkConfiguration?) {
                Log.i(TAG, "AppLovinSdk init")
            }
        })

    }
}