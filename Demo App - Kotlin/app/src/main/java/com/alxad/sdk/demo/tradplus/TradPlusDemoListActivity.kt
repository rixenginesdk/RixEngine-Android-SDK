package com.alxad.sdk.demo.tradplus

import android.os.Bundle
import android.util.Log
import com.alxad.sdk.demo.AdConfig
import com.alxad.sdk.demo.BaseListViewActivity
import com.alxad.sdk.demo.R
import com.tradplus.ads.open.TradPlusSdk
import java.util.concurrent.atomic.AtomicBoolean

class TradPlusDemoListActivity : BaseListViewActivity() {

    override val TAG: String = "TradPlusDemoListActivity"
    val isAdSDKInit: AtomicBoolean = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdSDK()
    }

    override fun initAdapterData(): MutableList<AdapterData>? {
        val list: MutableList<AdapterData> = ArrayList<AdapterData>()
        list.add(AdapterData(getString(R.string.banner_ad), TradPlusBannerActivity::class.java))
        list.add(AdapterData(getString(R.string.reward_ad), TradPlusRewardVideoActivity::class.java))
        list.add(AdapterData(getString(R.string.interstitial_ad), TradPlusInterstitialActivity::class.java))
        list.add(AdapterData(getString(R.string.native_ad), TradPlusNativeActivity::class.java))
        list.add(AdapterData(getString(R.string.splash_ad), TradPlusSplashActivity::class.java))

        return list
    }

    private fun initAdSDK() {
        if (isAdSDKInit.getAndSet(true)) {
            Log.d(TAG, "TradPlus SDK has been initialized")
            return
        }
        Log.d(TAG, "TradPlus SDK start initialize")

        TradPlusSdk.initSdk(this.applicationContext, AdConfig.TRAD_PLUS_APP_ID)

    }

}