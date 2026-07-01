package com.alxad.sdk.demo.tradplus

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
import com.tradplus.ads.base.bean.TPAdError
import com.tradplus.ads.base.bean.TPAdInfo
import com.tradplus.ads.open.banner.BannerAdListener
import com.tradplus.ads.open.banner.TPBanner

class TradPlusBannerActivity : BaseActivity(), View.OnClickListener {

    override val TAG = "TradPlusBannerActivity"

    private var mAdContainerView: FrameLayout? = null
    private var mBnLoad: View? = null
    private var mTvTip: TextView? = null
    private var bannerView: TPBanner? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_ads)
        setActionBar()
        initView()
    }

    private fun initView() {
        mAdContainerView = findViewById(R.id.ad_container) as? FrameLayout
        mTvTip = findViewById(R.id.tv_tip);
        mBnLoad = findViewById(R.id.bn_load);
        mBnLoad?.setOnClickListener(this);
    }

    override fun onClick(v: View) {
        if (v.id == R.id.bn_load) {
            loadAd()
        }
    }

    private fun loadAd() {
        mTvTip?.setText(R.string.loading)
        mBnLoad?.setEnabled(false)

        bannerView = TPBanner(this)
        bannerView?.setAdListener(object : BannerAdListener() {
            override fun onAdLoaded(tpAdInfo: TPAdInfo) {
                Log.d(TAG, "onAdLoaded:${getCurrentThreadName()}")
                mBnLoad?.isEnabled = true
                mTvTip?.text = ""
                showAd()
            }

            override fun onAdLoadFailed(tpAdError: TPAdError) {
                val msg = tpAdError.errorCode.toString() + ":" + tpAdError.errorMsg
                Log.d(TAG, "onAdLoadFailed:" + msg)
                mBnLoad?.setEnabled(true)
                mTvTip?.text = getString(R.string.format_load_failed, msg)
                Toast.makeText(
                    baseContext,
                    getString(R.string.load_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAdClicked(tpAdInfo: TPAdInfo) {
                Log.d(TAG, "onAdClicked:${getCurrentThreadName()}")
            }

            override fun onAdImpression(tpAdInfo: TPAdInfo) {
                Log.d(TAG, "onAdImpression:${getCurrentThreadName()}")
            }

            override fun onAdShowFailed(tpAdError: TPAdError, tpAdInfo: TPAdInfo) {
                Log.d(
                    TAG,
                    "onAdShowFailed:" + tpAdError.errorCode + "-" + tpAdError.errorMsg + getCurrentThreadName()
                )
            }

            override fun onAdClosed(tpAdInfo: TPAdInfo) {
                Log.d(TAG, "onAdClosed:${getCurrentThreadName()}")
            }

            override fun onBannerRefreshed() {
                Log.d(TAG, "onBannerRefreshed:${getCurrentThreadName()}")
            }
        })
        bannerView?.loadAd(AdConfig.TRAD_PLUS_BANNER_AD)
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
        bannerView?.onDestroy()
    }
}