package com.alxad.sdk.demo.admob

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.alxad.sdk.demo.AdConfig
import com.alxad.sdk.demo.BaseActivity
import com.alxad.sdk.demo.R
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback

class AdmobSplashActivity : BaseActivity(), View.OnClickListener {
    override val TAG = "AdmobSplashActivity"
    private var mAd: AppOpenAd? = null
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
        }else if (v.id == R.id.tv_show) {
            showAd()
        }
    }

    private fun showAd() {
        if (mAd == null) {
            Toast.makeText(this, getString(R.string.show_ad_no_load), Toast.LENGTH_SHORT).show()
            return
        }
        mAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.d(
                    TAG,
                    "onAdFailedToShowFullScreenContent:" + adError.code + ";" + adError.message
                )
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "onAdShowedFullScreenContent")
                Toast.makeText(
                    this@AdmobSplashActivity,
                    "Rewarded ad opened",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "onAdDismissedFullScreenContent")
                bnLoad()
                Toast.makeText(
                    this@AdmobSplashActivity,
                    "Rewarded ad closed",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAdImpression() {
                Log.d(TAG, "onAdImpression")
            }

            override fun onAdClicked() {
                Log.d(TAG, "onAdClicked")
            }
        }
        mAd?.show(this)
    }

    fun bnLoad() {
        mTvTip?.setText(R.string.loading)
        startTime = System.currentTimeMillis()

        val adRequest = AdRequest.Builder().build()
        AppOpenAd.load(
            this, AdConfig.ADMOB_SPLASH_ID, adRequest,
            object : AppOpenAdLoadCallback() {
                override fun onAdLoaded(appOpenAd: AppOpenAd) {
                    // The mInterstitialAd reference will be null until
                    Log.d(TAG, "onAdLoaded:${getCurrentThreadName()}")
                    mTvTip?.text =getString(R.string.format_load_success, (System.currentTimeMillis() - startTime) / 1000)
                    mTvShow?.isEnabled = true

                    mAd = appOpenAd
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(
                        TAG,
                        "onRewardedAdFailedToLoad: " + adError.code + " " + adError.message + ";" + getCurrentThreadName()
                    )
                    Toast.makeText(baseContext, getString(R.string.load_failed), Toast.LENGTH_SHORT).show()
                    mTvTip?.text = "load failed: ${adError.message}"
                    mTvShow?.isEnabled = false

                    mAd = null
                }
            })

    }

}