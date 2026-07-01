package com.alxad.sdk.demo.ironsource

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.alxad.sdk.demo.AdConfig
import com.alxad.sdk.demo.BaseActivity
import com.alxad.sdk.demo.R
import com.unity3d.mediation.LevelPlayAdError
import com.unity3d.mediation.LevelPlayAdInfo
import com.unity3d.mediation.LevelPlayAdSize
import com.unity3d.mediation.banner.LevelPlayBannerAdView
import com.unity3d.mediation.banner.LevelPlayBannerAdViewListener

class IronSourceBannerActivity : BaseActivity(), View.OnClickListener {
    override val TAG = "IronSourceBanner"

    private var mAdContainerView: FrameLayout? = null
    private var mBnLoad: View? = null
    private var mTvTip: TextView? = null
    private var bannerView: LevelPlayBannerAdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_ads)
        setActionBar()
        initView()
    }

    private fun initView() {
        mAdContainerView = findViewById<View>(R.id.ad_container) as FrameLayout
        mTvTip = findViewById<View>(R.id.tv_tip) as TextView
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
        mBnLoad?.isEnabled = false

        bannerView?.destroy()

        val adSize = LevelPlayAdSize.BANNER
        val adConfig = LevelPlayBannerAdView.Config.Builder()
            .setAdSize(adSize)
            .setPlacementName("middle")
            .build()


        // Create the banner view and set the ad unit id
        bannerView = LevelPlayBannerAdView(this, AdConfig.IRON_SOURCE_BANNER_AD, adConfig)

        bannerView?.bannerListener = object : LevelPlayBannerAdViewListener {
            override fun onAdLoaded(levelPlayAdInfo: LevelPlayAdInfo) {
                Log.d(TAG, "onAdLoaded")
                mBnLoad?.setEnabled(true)
                mTvTip?.setText(R.string.load_success)
                showAd()
            }

            override fun onAdLoadFailed(levelPlayAdError: LevelPlayAdError) {
                val msg = levelPlayAdError.errorCode
                    .toString() + ":" + levelPlayAdError.errorMessage
                Log.d(TAG, "onAdLoadFailed: $msg")
                mBnLoad?.setEnabled(true)
                mTvTip?.text = getString(R.string.format_load_failed, msg)
                Toast.makeText(
                    baseContext,
                    getString(R.string.load_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAdDisplayed(levelPlayAdInfo: LevelPlayAdInfo) {
                super.onAdDisplayed(levelPlayAdInfo)
                Log.d(TAG, "onAdDisplayed")
            }

            override fun onAdDisplayFailed(
                levelPlayAdInfo: LevelPlayAdInfo,
                levelPlayAdError: LevelPlayAdError
            ) {
                super.onAdDisplayFailed(levelPlayAdInfo, levelPlayAdError)
                Log.d(TAG, "onAdDisplayFailed:" + levelPlayAdError.errorMessage)
            }

            override fun onAdClicked(levelPlayAdInfo: LevelPlayAdInfo) {
                super.onAdClicked(levelPlayAdInfo)
                Log.d(TAG, "onAdClicked")
            }

            override fun onAdExpanded(levelPlayAdInfo: LevelPlayAdInfo) {
                super.onAdExpanded(levelPlayAdInfo)
                Log.d(TAG, "onAdExpanded")
            }

            override fun onAdCollapsed(levelPlayAdInfo: LevelPlayAdInfo) {
                super.onAdCollapsed(levelPlayAdInfo)
                Log.d(TAG, "onAdCollapsed")
            }

            override fun onAdLeftApplication(levelPlayAdInfo: LevelPlayAdInfo) {
                super.onAdLeftApplication(levelPlayAdInfo)
                Log.d(TAG, "onAdLeftApplication")
            }
        }

        bannerView?.loadAd()
    }

    private fun showAd() {
        mAdContainerView?.removeAllViews()
        if (bannerView != null) {
            mAdContainerView?.addView(
                bannerView,
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bannerView?.destroy()
    }


}