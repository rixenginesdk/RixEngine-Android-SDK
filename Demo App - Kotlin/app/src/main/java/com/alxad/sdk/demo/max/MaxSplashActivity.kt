package com.alxad.sdk.demo.max

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.alxad.sdk.demo.AdConfig
import com.alxad.sdk.demo.BaseActivity
import com.alxad.sdk.demo.R
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAppOpenAd

class MaxSplashActivity : BaseActivity(), View.OnClickListener {
    override val TAG = "MaxSplashActivity"
    private var mAd: MaxAppOpenAd? = null
    private var mTvTip: TextView? = null
    private var mTvShow: TextView? = null
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

    @SuppressLint("NonConstantResourceId")
    override fun onClick(v: View) {
        if (v.id == R.id.tv_load) {
            bnLoad()
        } else if (v.id == R.id.tv_show) {
            showAd()
        }
    }

    private fun showAd() {
        if (mAd == null) {
            Toast.makeText(this, getString(R.string.show_ad_no_load), Toast.LENGTH_SHORT).show()
            return
        }
        mAd?.let {
            if (it.isReady) {
                it.showAd()
            } else {
                Toast.makeText(this, "isReady()==false", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun bnLoad() {
        mTvTip?.setText(R.string.loading)
        startTime = System.currentTimeMillis()

        mAd = MaxAppOpenAd(AdConfig.MAX_SPLASH_AD, this)

        mAd?.setListener(mMaxAdListener)
        mAd?.loadAd()
    }

    private val mMaxAdListener: MaxAdListener = object : MaxAdListener {
        override fun onAdLoaded(ad: MaxAd) {
            val revenue = ad.revenue * 1000
            val message = " NetworkName:" + ad.networkName + "; ecpm:" + revenue
            Log.d(TAG, "onAdLoaded | $message")
            mTvTip?.text = getString(R.string.load_success) + message
            mTvShow?.setEnabled(true)
        }

        override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
            Log.d(TAG, "onAdLoadFailed:" + error.code + " " + error.message)
            Toast.makeText(baseContext, getString(R.string.load_failed), Toast.LENGTH_SHORT)
                .show()
            mTvTip?.text = getString(R.string.format_load_failed, error.message)
            mTvShow?.setEnabled(false)
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
            val msg = "errorCode=" + error.code + ";errorMsg=" + error.message
            Log.d(TAG, "onAdDisplayFailed: $msg")
        }
    }

}