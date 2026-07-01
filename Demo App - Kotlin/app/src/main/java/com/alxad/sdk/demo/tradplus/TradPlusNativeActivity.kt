package com.alxad.sdk.demo.tradplus

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.alxad.sdk.demo.AdConfig
import com.alxad.sdk.demo.BaseActivity
import com.alxad.sdk.demo.R
import com.tradplus.ads.base.bean.TPAdError
import com.tradplus.ads.base.bean.TPAdInfo
import com.tradplus.ads.base.bean.TPBaseAd
import com.tradplus.ads.open.nativead.NativeAdListener
import com.tradplus.ads.open.nativead.TPNative
import com.tradplus.ads.open.nativead.TPNativeAdRender

class TradPlusNativeActivity : BaseActivity(), View.OnClickListener {

    override val TAG = "TradPlusNativeActivity"
    private var mAdContainerView: ViewGroup? = null

    private var mBnLoad: View? = null
    private var mTvTip: TextView? = null
    private var mStartTime: Long = 0
    private var mAdObj: TPNative? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_ads)
        setActionBar()
        initView()
    }

    private fun initView() {
        mAdContainerView = findViewById<View>(R.id.ad_container) as FrameLayout
        mBnLoad = findViewById(R.id.bn_load)
        mTvTip = findViewById<View>(R.id.tv_tip) as TextView
        mBnLoad?.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.bn_load) {
            loadAd()
        }
    }

    /**
     * 加载广告
     */
    fun loadAd() {
        mTvTip?.setText(R.string.loading)
        mBnLoad?.isEnabled = false
        mStartTime = System.currentTimeMillis()

        mAdObj = TPNative(this, AdConfig.TRAD_PLUS_NATIVE_AD)
        mAdObj?.setAdListener(object : NativeAdListener() {
            override fun onAdLoaded(tpAdInfo: TPAdInfo, tpBaseAd: TPBaseAd?) {
                Log.i(TAG, "onAdLoaded: ${getCurrentThreadName()}")
                Toast.makeText(baseContext, getString(R.string.load_success), Toast.LENGTH_SHORT).show()
                mBnLoad?.isEnabled = true
                mTvTip?.text = getString(
                    R.string.format_load_success,
                    (System.currentTimeMillis() - mStartTime) / 1000
                )

                //以下两种方式任选其一都可以
//                mAdObj.showAd(mAdContainer,R.layout.tp_native_ad_list_item,null);
                mAdObj?.showAd(mAdContainerView, CustomAdRender(this@TradPlusNativeActivity), "")
            }

            override fun onAdLoadFailed(tpAdError: TPAdError) {
                Log.i(
                    TAG,
                    "onAdLoadFailed： " + tpAdError.errorCode + " " + tpAdError.errorMsg + ";" + getCurrentThreadName()
                )
                Toast.makeText(baseContext, getString(R.string.load_failed), Toast.LENGTH_SHORT).show()
                mTvTip?.setText(R.string.load_failed)
                mBnLoad?.isEnabled = true
            }

            override fun onAdShowFailed(tpAdError: TPAdError, tpAdInfo: TPAdInfo) {
                Log.i(
                    TAG,
                    "onAdShowFailed： " + tpAdError.errorCode + " " + tpAdError.errorMsg + ";" + getCurrentThreadName()
                )
            }

            override fun onAdClicked(tpAdInfo: TPAdInfo) {
                Log.i(TAG, "onAdClicked:${getCurrentThreadName()}")
            }

            override fun onAdImpression(tpAdInfo: TPAdInfo) {
                Log.i(TAG, "onAdImpression:${getCurrentThreadName()}")
            }

            override fun onAdClosed(tpAdInfo: TPAdInfo) {
                Log.i(TAG, "onAdClosed:${getCurrentThreadName()}")
            }
        })
        mAdObj?.loadAd()
    }

    override fun onDestroy() {
        super.onDestroy()
        mAdObj?.onDestroy()
    }

    private class CustomAdRender(private val context: Context) : TPNativeAdRender() {
        override fun createAdLayoutView(): ViewGroup {
            val view:ViewGroup = LayoutInflater.from(context)
                .inflate(R.layout.tradplus_native_custom_ad_view, null) as ViewGroup
            val titleView = view.findViewById<View>(R.id.native_title) as TextView
            setTitleView(titleView, true)

            val description = view.findViewById<View>(R.id.native_description) as TextView
            setSubTitleView(description, true)

            val iconView = view.findViewById<View>(R.id.native_icon) as ImageView
            setIconView(iconView, true)

            val mainView = view.findViewById<View>(R.id.native_image) as ImageView
            setImageView(mainView, true)

            val callToActionView = view.findViewById<View>(R.id.native_source) as TextView
            setCallToActionView(callToActionView, true)

            val adChoiceView = view.findViewById<View>(R.id.native_choice_container) as FrameLayout
            setAdChoicesContainer(adChoiceView, false)

//            ImageView adChoice=(ImageView)view.findViewById(R.id.native_choice);
//            setAdChoiceView(adChoice,true);
            return view
        }
    }

}