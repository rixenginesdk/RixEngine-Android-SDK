package com.alxad.sdk.demo.topon

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.alxad.sdk.demo.AdConfig
import com.alxad.sdk.demo.BaseActivity
import com.alxad.sdk.demo.R
import com.secmtp.sdk.core.api.AdError
import com.secmtp.sdk.core.api.ATAdInfo
import com.secmtp.sdk.interstitial.api.ATInterstitial
import com.secmtp.sdk.interstitial.api.ATInterstitialListener

class TopOnInterstitialActivity : BaseActivity(), View.OnClickListener {

    override val TAG = "TopOnInterstitialActivity"
    private var mTvTip: TextView? = null
    private var mTvShow: TextView? = null
    private var mAD: ATInterstitial? = null
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
            if(mAD == null) {
                Toast.makeText(this, getString(R.string.show_ad_no_load), Toast.LENGTH_SHORT).show()
                return
            }
            if (mAD?.isAdReady == true) {
                mAD?.show(this)
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

        mAD = ATInterstitial(this, AdConfig.TOPON_INTERSTITIAL_ID)
        mAD?.setAdListener(object : ATInterstitialListener {
            override fun onInterstitialAdLoaded() {
                Log.i(TAG, "onInterstitialAdLoaded:"+ getCurrentThreadName())
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

            override fun onInterstitialAdLoadFail(adError: AdError) {
                Log.e(
                    TAG,
                    "onInterstitialAdLoadFail:" + adError.getCode() + ";" + adError.getDesc() + ";" + getCurrentThreadName()
                )
                Toast.makeText(
                    baseContext,
                    getString(R.string.load_failed),
                    Toast.LENGTH_SHORT
                ).show()
                mTvTip?.setText(R.string.load_failed)
                mTvShow?.setEnabled(false)
            }

            override fun onInterstitialAdClicked(atAdInfo: ATAdInfo?) {
                Log.i(TAG, "onInterstitialAdClicked:" + getCurrentThreadName())
            }

            override fun onInterstitialAdShow(atAdInfo: ATAdInfo?) {
                Log.i(TAG, "onInterstitialAdShow:" + getCurrentThreadName())
            }

            override fun onInterstitialAdClose(atAdInfo: ATAdInfo?) {
                Log.i(TAG, "onInterstitialAdClose:" + getCurrentThreadName())
                mTvShow?.setEnabled(false)
                mTvTip?.text = ""
            }

            override fun onInterstitialAdVideoStart(atAdInfo: ATAdInfo?) {
                Log.i(
                    TAG,
                    "onInterstitialAdVideoStart:" + getCurrentThreadName()
                )
            }

            override fun onInterstitialAdVideoEnd(atAdInfo: ATAdInfo?) {
                Log.i(TAG, "onInterstitialAdVideoEnd:" + getCurrentThreadName())
            }

            override fun onInterstitialAdVideoError(adError: AdError) {
                Log.i(
                    TAG,
                    "onInterstitialAdVideoError:" + adError.getCode() + ";" + adError.getDesc() + ";" + getCurrentThreadName()
                )
            }
        })
        mAD?.load()
    }

}