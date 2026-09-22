package com.alxad.sdk.demo.ironsource

import android.os.Bundle
import android.util.Log
import com.alxad.sdk.demo.AdConfig
import com.alxad.sdk.demo.BaseListViewActivity
import com.alxad.sdk.demo.R
import com.unity3d.mediation.LevelPlay
import com.unity3d.mediation.LevelPlayConfiguration
import com.unity3d.mediation.LevelPlayInitError
import com.unity3d.mediation.LevelPlayInitListener
import com.unity3d.mediation.LevelPlayInitRequest
import java.util.concurrent.atomic.AtomicBoolean

class IronSourceDemoListActivity : BaseListViewActivity() {

    override val TAG: String = "IronSourceDemoListActivity"
    val isAdSDKInit: AtomicBoolean = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdSDK()
    }

    override fun initAdapterData(): MutableList<AdapterData>? {
        val list: MutableList<AdapterData> = ArrayList<AdapterData>()
        list.add(AdapterData(getString(R.string.reward_ad), IronSourceRewardedVideoActivity::class.java))
        list.add(AdapterData(getString(R.string.interstitial_ad), IronSourceInterstitialActivity::class.java))
        list.add(AdapterData(getString(R.string.banner_ad), IronSourceBannerActivity::class.java))
        return list
    }

    private fun initAdSDK() {
        if (isAdSDKInit.getAndSet(true)) {
            Log.d(TAG, "IronSource SDK has been initialized")
            return
        }
        Log.d(TAG, "IronSource SDK start initialize")


        //IronSource初始化
        val initRequest = LevelPlayInitRequest.Builder(AdConfig.IRON_SOURCE_APP_KEY)
            .withUserId(AdConfig.IRON_SOURCE_USER_ID)
            .build()
        val initListener: LevelPlayInitListener = object : LevelPlayInitListener {
            override fun onInitSuccess(levelPlayConfiguration: LevelPlayConfiguration) {
                Log.d(TAG, "onInitSuccess")
            }

            override fun onInitFailed(levelPlayInitError: LevelPlayInitError) {
                Log.d(TAG,"onInitFailed:" + levelPlayInitError.errorMessage)
            }
        }
        LevelPlay.init(this.applicationContext, initRequest, initListener)

    }
}