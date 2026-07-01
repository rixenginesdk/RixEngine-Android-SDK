package com.alxad.sdk.demo.topon

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
import com.thinkup.banner.api.TUBannerListener
import com.thinkup.banner.api.TUBannerView
import com.thinkup.core.api.AdError
import com.thinkup.core.api.TUAdInfo

class TopOnBannerActivity : BaseActivity(), View.OnClickListener {
    override val TAG = "TopOnBannerActivity"

    private var mAdContainerView: FrameLayout? = null
    private var mBnLoad: View? = null
    private var mTvTip: TextView? = null
    private var bannerView: TUBannerView? = null

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
        mBnLoad?.setEnabled(false)

        if (bannerView != null) {
            bannerView?.destroy()
        }

        bannerView = TUBannerView(this)
        bannerView?.setPlacementId(AdConfig.TOPON_BANNER_AD_ID)
        bannerView?.setBannerAdListener(object : TUBannerListener {
            override fun onBannerLoaded() {
                Log.d(TAG, "onBannerLoaded")
                mBnLoad?.setEnabled(true)
                mTvTip?.setText(R.string.load_success)
                showAd()
            }

            override fun onBannerFailed(adError: AdError) {
                mBnLoad?.setEnabled(true)
                val msg = adError.getCode() + ":" + adError.getDesc()
                Log.d(TAG, "onBannerFailed:" + msg)
                mTvTip?.text = getString(R.string.format_load_failed, msg)
                Toast.makeText(
                    baseContext,
                    getString(R.string.load_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onBannerClicked(atAdInfo: TUAdInfo?) {
                Log.d(TAG, "onBannerClicked")
            }

            override fun onBannerShow(atAdInfo: TUAdInfo?) {
                Log.d(TAG, "onBannerShow")
            }

            override fun onBannerClose(atAdInfo: TUAdInfo?) {
                Log.d(TAG, "onBannerClose")
            }

            override fun onBannerAutoRefreshed(atAdInfo: TUAdInfo?) {
                Log.d(TAG, "onBannerAutoRefreshed")
            }

            override fun onBannerAutoRefreshFail(adError: AdError) {
                Log.d(
                    TAG,
                    "onBannerAutoRefreshFail:" + adError.getCode() + "," + adError.getDesc()
                )
            }
        })
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