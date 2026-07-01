package com.alxad.sdk.demo.admob

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
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class AdmobInterstitialActivity : BaseActivity(), View.OnClickListener {
    override val TAG = "AdmobInterstitial"
    private var mAd: InterstitialAd? = null
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

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.tv_load) {
            bnLoad()
        } else if (id == R.id.tv_show) {
            bnShow()
        }
    }

    private fun bnLoad() {
        mTvTip?.setText(R.string.loading)
        startTime = System.currentTimeMillis()
        mTvShow?.isEnabled = false

        val adRequest = AdRequest.Builder()
            .build()
        InterstitialAd.load(this, AdConfig.ADMOB_INTERSTITIAL_ID, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    Log.d(TAG, "onAdLoaded")
                    Toast.makeText(baseContext, getString(R.string.load_success), Toast.LENGTH_SHORT).show()
                    mTvTip?.let{
                        val msg = getString(
                            R.string.format_load_success,
                            (System.currentTimeMillis() - startTime) / 1000
                        )
                        it.text = msg
                    }
                    mTvShow?.isEnabled = true

                    mAd = interstitialAd
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Handle the error
                    Log.d(TAG, "onAdFailedToLoad: " + adError.code + " " + adError.message)
                    Toast.makeText(baseContext, getString(R.string.load_failed), Toast.LENGTH_SHORT).show()
                    mTvTip?.text = getString(R.string.format_load_failed, adError.message)
                    mTvShow?.isEnabled = false

                    mAd = null
                }
            })
    }

    private fun bnShow() {
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
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "onAdDismissedFullScreenContent")
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
}