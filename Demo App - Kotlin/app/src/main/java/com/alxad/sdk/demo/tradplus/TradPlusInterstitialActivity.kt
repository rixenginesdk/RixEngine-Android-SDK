package com.alxad.sdk.demo.tradplus

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.alxad.sdk.demo.AdConfig
import com.alxad.sdk.demo.BaseActivity
import com.alxad.sdk.demo.R
import com.tradplus.ads.base.bean.TPAdError
import com.tradplus.ads.base.bean.TPAdInfo
import com.tradplus.ads.open.interstitial.InterstitialAdListener
import com.tradplus.ads.open.interstitial.TPInterstitial

@SuppressLint("LongLogTag")
class TradPlusInterstitialActivity : BaseActivity(), View.OnClickListener {

    override val TAG = "TradPlusInterstitialActivity"
    private var mTvTip: TextView? = null
    private var mTvShow: TextView? = null
    private var mAdObj: TPInterstitial? = null
    private var startTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_and_show)
        setActionBar()
        initView()
    }

    private fun initView() {
        val tvLoad = findViewById<TextView>(R.id.tv_load)
        mTvShow = findViewById(R.id.tv_show)
        mTvTip = findViewById(R.id.tv_tip)
        mTvShow?.isEnabled = false
        tvLoad.setOnClickListener(this)
        mTvShow?.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.tv_load) {
            loadAd()
        } else if (id == R.id.tv_show) {
            if(mAdObj == null) {
                Toast.makeText(this, getString(R.string.show_ad_no_load), Toast.LENGTH_SHORT).show()
                return
            }
            if (mAdObj?.isReady == true) {
                mAdObj?.showAd(this,null)
            }else{
                Toast.makeText(this, "isAdReady()==false", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 加载广告
     */
    fun loadAd() {
        mTvTip?.setText(R.string.loading)
        startTime = System.currentTimeMillis()
        mTvShow?.isEnabled = false

        mAdObj = TPInterstitial(this, AdConfig.TRAD_PLUS_INTERSTITIAL_AD)
        mAdObj?.setAdListener(object : InterstitialAdListener {
            override fun onAdLoaded(tpAdInfo: TPAdInfo) {
                Log.i(TAG, "onAdLoaded: ${getCurrentThreadName()}")
                Toast.makeText(
                    baseContext,
                    getString(R.string.load_success),
                    Toast.LENGTH_SHORT
                ).show()
                mTvTip?.text = getString(
                    R.string.format_load_success,
                    (System.currentTimeMillis() - startTime) / 1000
                )
                mTvShow?.setEnabled(true)
            }

            override fun onAdFailed(error: TPAdError) {
                Log.e(
                    TAG,
                    "onAdFailed:" + error.errorCode + ";" + error.errorMsg + ";" + getCurrentThreadName()
                )
                Toast.makeText(
                    baseContext,
                    getString(R.string.load_failed),
                    Toast.LENGTH_SHORT
                ).show()
                mTvTip?.setText(R.string.load_failed)
                mTvShow?.setEnabled(false)
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

            override fun onAdVideoError(tpAdInfo: TPAdInfo, tpAdError: TPAdError) {
                Log.i(
                    TAG,
                    "onAdVideoError:" + tpAdError.errorCode + " " + tpAdError.errorMsg + ";" + getCurrentThreadName()
                )
            }

            override fun onAdVideoStart(tpAdInfo: TPAdInfo) {
                Log.i(TAG, "onAdVideoStart")
            }

            override fun onAdVideoEnd(tpAdInfo: TPAdInfo) {
                Log.i(TAG, "onAdVideoEnd")
            }
        })
        mAdObj?.loadAd()
    }

}