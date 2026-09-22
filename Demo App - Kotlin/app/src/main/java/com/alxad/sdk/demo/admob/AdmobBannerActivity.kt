package com.alxad.sdk.demo.admob

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alxad.sdk.demo.BaseActivity
import com.alxad.sdk.demo.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

class AdmobBannerActivity : BaseActivity() {
    override val TAG = "AdmobBannerActivity"
    private var mAdView: AdView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admob_banner)
        setActionBar()
        initView()
        loadAd()
    }

    private fun initView() {
        mAdView = findViewById<View>(R.id.ad_view) as AdView
        mAdView?.adListener = object : AdListener() {
            override fun onAdClosed() {
                Log.d(TAG, "onAdClosed")
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Log.d(TAG, "onAdFailedToLoad:" + loadAdError.message)
                Toast.makeText(baseContext, getString(R.string.load_failed), Toast.LENGTH_SHORT).show()
            }

            override fun onAdOpened() {
                Log.d(TAG, "onAdOpened")
            }

            override fun onAdLoaded() {
                Log.d(TAG, "onAdLoaded")
            }

            override fun onAdClicked() {
                Log.d(TAG, "onAdClicked")
            }
        }
    }

    private fun loadAd() {
        // Create an ad request.
        val extra = Bundle()
        extra.putBoolean("extra", true)
        val adRequest = AdRequest.Builder()
            .build()
        // Start loading the ad in the background.
        mAdView?.loadAd(adRequest)
    }

    /**
     * Called when leaving the activity
     */
    public override fun onPause() {
        super.onPause()
        mAdView?.pause()
    }

    /**
     * Called when returning to the activity
     */
    public override fun onResume() {
        super.onResume()
        mAdView?.resume()
    }

    /**
     * Called before the activity is destroyed
     */
    public override fun onDestroy() {
        super.onDestroy()
        mAdView?.destroy()
    }
}