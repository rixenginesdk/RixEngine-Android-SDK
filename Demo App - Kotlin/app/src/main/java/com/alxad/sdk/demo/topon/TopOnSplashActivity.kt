package com.alxad.sdk.demo.topon

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.alxad.sdk.demo.AdConfig
import com.alxad.sdk.demo.BaseActivity
import com.alxad.sdk.demo.R
import com.secmtp.sdk.core.api.AdError
import com.secmtp.sdk.core.api.ATAdInfo
import com.secmtp.sdk.splashad.api.ATSplashAd
import com.secmtp.sdk.splashad.api.ATSplashAdExtraInfo
import com.secmtp.sdk.splashad.api.ATSplashAdListener

class TopOnSplashActivity : BaseActivity() {

    override val TAG = "TopOnSplashActivity"
    private var mAD: ATSplashAd? = null
    private var mAdContainer: FrameLayout? = null

    //控制开屏广告点击跳转
    private var canJump = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topon_splash)
        initView()
        loadAd()
    }

    private fun initView() {
        mAdContainer = findViewById<View>(R.id.ad_container) as FrameLayout
    }

    private fun loadAd() {
        mAD = ATSplashAd(this, AdConfig.TOPON_SPLASH_ID, object : ATSplashAdListener {
            override fun onAdLoaded(b: Boolean) {
                Log.d(TAG, "onAdLoaded:${getCurrentThreadName()}")
                if (mAD?.isAdReady == true) {
                    mAD?.show(this@TopOnSplashActivity, mAdContainer)
                }
            }

            override fun onAdLoadTimeout() {
                Log.d(TAG, "onAdLoadTimeout:${getCurrentThreadName()}")
            }
            override fun onNoAdError(adError: AdError) {
                Log.d(TAG, "onNoAdError:" + adError.code + ";" + adError.desc + "=" + getCurrentThreadName())
                goToMainActivity()
            }

            override fun onAdShow(atAdInfo: ATAdInfo) {
                Log.d(TAG, "onAdShow:${getCurrentThreadName()}")
            }

            override fun onAdClick(atAdInfo: ATAdInfo) {
                Log.d(TAG, "onAdClick:${getCurrentThreadName()}")
                canJump = true
            }

            override fun onAdDismiss(atAdInfo: ATAdInfo, atSplashAdExtraInfo: ATSplashAdExtraInfo) {
                Log.d(TAG, "onAdDismiss:${getCurrentThreadName()}")
                goToMainActivity()
            }
        })
        mAD?.loadAd()
    }

    private fun goToMainActivity() {
        this.startActivity(Intent(this, TopOnDemoListActivity::class.java))
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
        if (mAD != null) {
            mAD?.onDestory()
        }
    }

}