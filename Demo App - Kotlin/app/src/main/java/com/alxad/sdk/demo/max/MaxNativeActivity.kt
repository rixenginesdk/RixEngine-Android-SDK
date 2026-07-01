package com.alxad.sdk.demo.max

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.alxad.sdk.demo.AdConfig
import com.alxad.sdk.demo.BaseActivity
import com.alxad.sdk.demo.R
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder

class MaxNativeActivity : BaseActivity(), View.OnClickListener {

    override val TAG = "MaxNativeActivity"
    private var mBnLoad: View? = null
    private var mTvTip: TextView? = null
    private var mAdContainerView: FrameLayout? = null
    private var mStartTime: Long = 0
    private var mAdLoader: MaxNativeAdLoader? = null
    private var mMaxAd: MaxAd? = null
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

    private fun loadAd() {
        mTvTip?.setText(R.string.loading)
        mBnLoad?.isEnabled = false
        mStartTime = System.currentTimeMillis()

        mAdLoader = MaxNativeAdLoader(AdConfig.MAX_NATIVE_AD)
        mAdLoader?.setNativeAdListener(mMaxNativeAdListener)
//        mAdLoader?.loadAd();
        mAdLoader?.loadAd(createNativeAdView()) //自渲染
    }

    override fun onClick(v: View) {
        if (v.id == R.id.bn_load) {
            loadAd()
        }
    }

    private val mMaxNativeAdListener: MaxNativeAdListener = object : MaxNativeAdListener() {
        override fun onNativeAdLoaded(maxNativeAdView: MaxNativeAdView?, maxAd: MaxAd) {
            val revenue = maxAd.revenue * 1000
            val message = " NetworkName:" + maxAd.networkName + "; ecpm:" + revenue
            Log.d(TAG, "onNativeAdLoaded | $message")
            mTvTip?.text = getString(R.string.load_success) + message
            mBnLoad?.setEnabled(true)

            if (mMaxAd != null) {
                mAdLoader?.destroy(mMaxAd)
            }
            mMaxAd = maxAd
            if (maxNativeAdView != null) {
                mAdContainerView?.removeAllViews()
                mAdContainerView?.addView(maxNativeAdView)
            } else {
                Log.d(TAG, "maxNativeAdView is empty")
            }
        }

        override fun onNativeAdLoadFailed(s: String, maxError: MaxError) {
            Log.d(
                TAG,
                "onNativeAdLoadFailed:" + s + ";" + maxError.code + ";" + maxError.message
            )
            mBnLoad?.isEnabled = true
            mTvTip?.text = getString(R.string.format_load_failed, maxError.message);
        }

        override fun onNativeAdClicked(maxAd: MaxAd) {
            Log.d(TAG, "onNativeAdClicked")
        }
    }

    private fun createNativeAdView(): MaxNativeAdView {
        val binder = MaxNativeAdViewBinder.Builder(R.layout.max_native_custom_ad_view)
            .setTitleTextViewId(R.id.tv_ad_title)
            .setBodyTextViewId(R.id.tv_ad_desc)
            .setAdvertiserTextViewId(R.id.ad_advertiser)
            .setIconImageViewId(R.id.iv_ad_icon)
            .setMediaContentViewGroupId(R.id.media_view_container)
            .setCallToActionButtonId(R.id.cta_button)
            .build()
        return MaxNativeAdView(binder, this)
    }

    override fun onDestroy() {
        if (mAdLoader != null) {
            if (mMaxAd != null) {
                mAdLoader?.destroy(mMaxAd)
            }
            mAdLoader?.destroy()
        }
        super.onDestroy()
    }
}