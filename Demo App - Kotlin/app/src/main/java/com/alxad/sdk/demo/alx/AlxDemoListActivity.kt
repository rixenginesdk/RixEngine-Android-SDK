package com.alxad.sdk.demo.alx


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.alxad.sdk.demo.BaseListViewActivity
import com.alxad.sdk.demo.R
import com.rixengine.api.AlxAdSDK

class AlxDemoListActivity: BaseListViewActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addBottomLayout()
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

    private fun addBottomLayout() {
        val convertView = LayoutInflater.from(this).inflate(R.layout.layout_bottom_content, null)
        val tvContent = convertView.findViewById<View?>(R.id.bottom_tv_content) as TextView
        val sb = "SDK Name: " +
                AlxAdSDK.getNetWorkName() +
                "\r\n" +
                "SDK Version: " +
                AlxAdSDK.getNetWorkVersion()
        tvContent.setText(sb)
        mListView?.addFooterView(convertView)
    }


}