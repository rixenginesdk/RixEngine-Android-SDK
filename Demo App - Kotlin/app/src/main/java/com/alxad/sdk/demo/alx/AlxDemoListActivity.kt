package com.alxad.sdk.demo.alx


import android.os.Bundle
import com.alxad.sdk.demo.BaseListViewActivity
import com.alxad.sdk.demo.R

class AlxDemoListActivity: BaseListViewActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initAdapterData(): MutableList<AdapterData>? {
        val list: MutableList<AdapterData> = ArrayList<AdapterData>()
        list.add(AdapterData(getString(R.string.banner_ad), BannerActivity::class.java))
        list.add(AdapterData(getString(R.string.reward_ad), RewardVideoActivity::class.java))
        list.add(AdapterData(getString(R.string.interstitial_video_ad),InterstitialVideoActivity::class.java))
        list.add(AdapterData(getString(R.string.interstitial_banner_ad),InterstitialBannerActivity::class.java))
        list.add(AdapterData(getString(R.string.native_ad), NativeActivity::class.java))
        return list
    }


}