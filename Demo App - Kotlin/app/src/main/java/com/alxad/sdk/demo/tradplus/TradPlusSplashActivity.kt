package com.alxad.sdk.demo.tradplus

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.alxad.sdk.demo.AdConfig
import com.alxad.sdk.demo.BaseActivity
import com.alxad.sdk.demo.R
import com.tradplus.ads.base.bean.TPAdError
import com.tradplus.ads.base.bean.TPAdInfo
import com.tradplus.ads.base.bean.TPBaseAd
import com.tradplus.ads.open.splash.SplashAdListener
import com.tradplus.ads.open.splash.TPSplash

class TradPlusSplashActivity : BaseActivity() {

    override val TAG = "TradPlusSplashActivity"
    private var mAD: TPSplash? = null
    private var mAdContainer: FrameLayout? = null

    //控制开屏广告点击跳转
    private var canJump = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tradplus_splash)
        initView()
        loadAd()
    }

    private fun initView() {
        mAdContainer = findViewById<View>(R.id.ad_container) as FrameLayout
    }

    private fun loadAd() {
        mAD = TPSplash(this, AdConfig.TRAD_PLUS_SPLASH_AD)
        mAD?.setAdListener(object : SplashAdListener() {
            override fun onAdLoaded(tpAdInfo: TPAdInfo, tpBaseAd: TPBaseAd) {
                Log.d(TAG, "onAdLoaded:${getCurrentThreadName()}")
                if (mAD?.isReady == true) {
                    mAD?.showAd(mAdContainer)
                }
            }

            override fun onAdClicked(tpAdInfo: TPAdInfo) {
                Log.d(TAG, "onAdClicked:${getCurrentThreadName()}")
                canJump = true
            }

            override fun onAdImpression(tpAdInfo: TPAdInfo) {
                Log.d(TAG, "onAdImpression:${getCurrentThreadName()}")
            }

            override fun onAdLoadFailed(tpAdError: TPAdError) {
                Log.d(
                    TAG,
                    "onAdLoadFailed:" + tpAdError.errorCode + ";" + tpAdError.errorMsg + "=" + getCurrentThreadName()
                )
                goToMainActivity()
            }

            override fun onAdClosed(tpAdInfo: TPAdInfo) {
                Log.d(TAG, "onAdClosed:${getCurrentThreadName()}")
                goToMainActivity()
            }

            override fun onZoomOutStart(tpAdInfo: TPAdInfo) {
                Log.d(TAG, "onZoomOutStart:${getCurrentThreadName()}")
            }

            override fun onZoomOutEnd(tpAdInfo: TPAdInfo) {
                Log.d(TAG, "onZoomOutEnd:${getCurrentThreadName()}")
            }
        })
        mAD?.loadAd(null)
    }

    private fun goToMainActivity() {
        this.startActivity(Intent(this, TradPlusDemoListActivity::class.java))
        finish()
    }

    override fun onResume() {
        super.onResume()
        if (canJump) {
            goToMainActivity()
        }
        //        canJump = true;
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
        //        canJump = false;
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}