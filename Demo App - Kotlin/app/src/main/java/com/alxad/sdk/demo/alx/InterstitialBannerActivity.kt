package com.alxad.sdk.demo.alx

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.alxad.sdk.demo.AdConfig
import com.alxad.sdk.demo.BaseActivity
import com.alxad.sdk.demo.R
import com.rixengine.api.AlxAdParam
import com.rixengine.api.AlxInterstitialAD
import com.rixengine.api.AlxInterstitialADListener

class InterstitialBannerActivity : BaseActivity(), View.OnClickListener {

    override val TAG = "AlxInterstitialBannerActivity"

    private var mTvTip: TextView? = null
    private var mTvShow: TextView? = null
    private var mInterstitialAD: AlxInterstitialAD? = null
    private var startTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_and_show)
        setActionBar()
        initView()
    }

    private fun initView() {
        val tvLoader = findViewById<TextView>(R.id.tv_load)
        mTvShow = findViewById(R.id.tv_show)
        mTvTip = findViewById(R.id.tv_tip)
        mTvShow?.isEnabled = false
        tvLoader.setOnClickListener(this)
        mTvShow?.setOnClickListener(this)
    }

    @SuppressLint("NonConstantResourceId")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_load -> loadAd()
            R.id.tv_show -> showAd()
        }
    }

    /**
     * load Ad
     */
    fun loadAd() {
        mTvTip?.setText(R.string.loading)
        startTime = System.currentTimeMillis()

        mInterstitialAD = AlxInterstitialAD()
        val userExtras: MutableMap<String, String> = HashMap()
        userExtras["bid_floor"] = "1.5"
        val builder = AlxAdParam.Builder().setUserExtras(userExtras)
        mInterstitialAD?.load(
            this,
            AdConfig.ALX_INTERSTITIAL_BANNER_AD_ID,
            builder.build(),
            object : AlxInterstitialADListener() {
                override fun onInterstitialAdLoaded() {
                    Log.i(TAG, "onInterstitialAdLoaded")
                    mTvShow?.isEnabled = true
                    mTvTip?.let {
                        val msg = getString(
                            R.string.format_load_success,
                            (System.currentTimeMillis() - startTime) / 1000
                        ) + "｜ ecpm:" + mInterstitialAD?.price
                        it.text = msg
                    }

                    mInterstitialAD?.reportChargingUrl()
                    mInterstitialAD?.reportBiddingUrl()
                }

                override fun onInterstitialAdLoadFail(errorCode: Int, errorMsg: String) {
                    Log.i(TAG, "onInterstitialAdLoadFail:  $errorCode $errorMsg")
                    mTvShow?.isEnabled = false
                    val msg = "errorCode=$errorCode;errorMsg=$errorMsg"
                    mTvTip?.text = getString(R.string.format_load_failed, msg)
                }

                override fun onInterstitialAdClicked() {
                    Log.i(TAG, "onInterstitialAdClicked")
                }

                override fun onInterstitialAdShow() {
                    Log.i(TAG, "onInterstitialAdShow")
                }

                override fun onInterstitialAdClose() {
                    Log.i(TAG, "onInterstitialAdClose")
                }

                override fun onInterstitialAdVideoStart() {
                    Log.i(TAG, "onInterstitialAdVideoStart")
                }

                override fun onInterstitialAdVideoEnd() {
                    Log.i(TAG, "onInterstitialAdVideoEnd: ")
                }

                override fun onInterstitialAdVideoError(errorCode: Int, errorMsg: String) {
                    Log.i(TAG, "onInterstitialAdVideoError:  $errorCode,$errorMsg")
                }
            })
    }

    private fun showAd() {
        if (mInterstitialAD == null || mInterstitialAD?.isReady == false) {
            Toast.makeText(this, getString(R.string.show_ad_no_load), Toast.LENGTH_SHORT).show()
            return
        }
        mInterstitialAD?.show(this)
    }
}