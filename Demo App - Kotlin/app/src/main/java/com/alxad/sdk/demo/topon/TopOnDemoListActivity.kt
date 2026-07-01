package com.alxad.sdk.demo.topon

import android.os.Bundle
import android.util.Log
import com.alxad.sdk.demo.AdConfig
import com.alxad.sdk.demo.BaseListViewActivity
import com.alxad.sdk.demo.R
import com.thinkup.core.api.TUSDK
import java.util.concurrent.atomic.AtomicBoolean

class TopOnDemoListActivity : BaseListViewActivity() {

    override val TAG: String = "TopOnDemoListActivity"
    val isAdSDKInit: AtomicBoolean = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdSDK()
    }

    override fun initAdapterData(): MutableList<AdapterData>? {
        val list: MutableList<AdapterData> = ArrayList<AdapterData>()
        list.add(AdapterData(getString(R.string.banner_ad), TopOnBannerActivity::class.java))
        list.add(AdapterData(getString(R.string.reward_ad), TopOnRewardVideoActivity::class.java))
        list.add(AdapterData(getString(R.string.interstitial_ad), TopOnInterstitialActivity::class.java))
        list.add(AdapterData(getString(R.string.native_ad), TopOnNativeActivity::class.java))
        list.add(AdapterData(getString(R.string.splash_ad), TopOnSplashActivity::class.java))
        return list
    }

    private fun initAdSDK() {
        if (isAdSDKInit.getAndSet(true)) {
            Log.d(TAG, "TopOn SDK has been initialized")
            return
        }
        Log.d(TAG, "TopOn SDK start initialize")

        TUSDK.init(applicationContext, AdConfig.TOPON_APP_ID, AdConfig.TOPON_KEY)
        TUSDK.setNetworkLogDebug(true)

    }

}