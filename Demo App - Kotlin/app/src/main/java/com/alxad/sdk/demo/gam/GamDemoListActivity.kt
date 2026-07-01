package com.alxad.sdk.demo.gam

import android.os.Bundle
import android.util.Log
import com.alxad.sdk.demo.BaseListViewActivity
import com.alxad.sdk.demo.R
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean


class GamDemoListActivity : BaseListViewActivity() {

    override val TAG: String = "GamDemoListActivity"
    val isAdSDKInit: AtomicBoolean = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdSDK()
    }

    override fun initAdapterData(): MutableList<AdapterData>? {
        val list: MutableList<AdapterData> = ArrayList<AdapterData>()
        list.add(AdapterData(getString(R.string.banner_ad), GamBannerActivity::class.java))
        list.add(AdapterData(getString(R.string.reward_ad), GamRewardVideoActivity::class.java))
        list.add(AdapterData(getString(R.string.interstitial_ad), GamInterstitialActivity::class.java))
        list.add(AdapterData(getString(R.string.native_ad), GamNativeActivity::class.java))

        return list
    }

    private fun initAdSDK() {
        if (isAdSDKInit.getAndSet(true)) {
            Log.d(TAG, "Gam SDK has been initialized")
            return
        }
        Log.d(TAG, "Gam SDK start initialize")

        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(applicationContext) { status ->
                val map = status.adapterStatusMap
                for (entry in map.entries) {
                    Log.d(TAG, entry.key + "=" + entry.value?.description)
                }
            }
        }

    }

}