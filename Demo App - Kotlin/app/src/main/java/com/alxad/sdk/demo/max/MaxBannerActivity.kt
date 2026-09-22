package com.alxad.sdk.demo.max

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.alxad.sdk.demo.AdConfig
import com.alxad.sdk.demo.BaseActivity
import com.alxad.sdk.demo.R
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView

class MaxBannerActivity : BaseActivity(), View.OnClickListener {

    override val TAG = "MaxBannerActivity"

    private var mAdContainerView: FrameLayout? = null
    private var mBnLoad: View? = null
    private var mTvTip: TextView? = null
    private var mAdView: MaxAdView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_ads)
        setActionBar()
        initView()
    }

    private fun initView() {
        mAdContainerView = findViewById(R.id.ad_container) as? FrameLayout
        mTvTip = findViewById(R.id.tv_tip)
        mBnLoad = findViewById(R.id.bn_load)
        mBnLoad?.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.bn_load) {
            loadAd()
        }
    }

    private fun loadAd() {
        mTvTip?.setText(R.string.loading)
        mBnLoad?.setEnabled(false)

        mAdView = MaxAdView(AdConfig.MAX_BANNER_AD, this)
        mAdView?.setListener(maxAdViewAdListener)
        mAdView?.stopAutoRefresh()
        mAdView?.loadAd()
    }

    private val maxAdViewAdListener: MaxAdViewAdListener = object : MaxAdViewAdListener {
        override fun onAdLoaded(ad: MaxAd) {
            val revenue = ad.revenue * 1000
            val message = " NetworkName:" + ad.networkName + "; ecpm:" + revenue
            Log.d(TAG, "onAdLoaded |$message")
            mTvTip?.text = getString(R.string.load_success) + message
            mBnLoad?.setEnabled(true)
            showAd()
        }

        override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
            val msg = "${error.code}: ${error.message}"
            Log.d(TAG, "onAdLoadFailed:${msg}")
            mBnLoad?.setEnabled(true)
            mTvTip?.text = getString(R.string.format_load_failed, msg)
        }

        override fun onAdExpanded(ad: MaxAd) {
            Log.d(TAG, "onAdExpanded")
        }

        override fun onAdCollapsed(ad: MaxAd) {
            Log.d(TAG, "onAdCollapsed")
        }

        override fun onAdDisplayed(ad: MaxAd) {
            Log.d(TAG, "onAdDisplayed")
        }

        override fun onAdHidden(ad: MaxAd) {
            Log.d(TAG, "onAdHidden")
        }

        override fun onAdClicked(ad: MaxAd) {
            Log.d(TAG, "onAdClicked")
        }

        override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
            Log.d(TAG, "onAdDisplayFailed:" + error.code + ";" + error.message)
        }
    }

    private fun showAd() {
        mAdContainerView?.removeAllViews()
        mAdView?.let {
            it.setLayoutParams(
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dip2px(this, 50f)
                )
            )
            mAdContainerView?.addView(it)
        }
    }

    companion object {
        /**
         * 将dip或dp值转换为px值，保证尺寸大小不变
         *
         * @param dipValue
         * @return
         */
        fun dip2px(context: Context?, dipValue: Float): Int {
            if (context == null) {
                return 0
            }
            val metrics = context.resources.displayMetrics
            val scale = metrics.density
            return (dipValue * scale + 0.5f).toInt()
        }
    }
}