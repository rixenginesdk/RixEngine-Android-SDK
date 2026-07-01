package com.alxad.sdk.demo.topon

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.alxad.sdk.demo.AdConfig
import com.alxad.sdk.demo.BaseActivity
import com.alxad.sdk.demo.R
import com.thinkup.core.api.AdError
import com.thinkup.core.api.TUAdInfo
import com.thinkup.splashad.api.TUSplashAd
import com.thinkup.splashad.api.TUSplashAdExtraInfo
import com.thinkup.splashad.api.TUSplashAdListener

class TopOnSplashActivity : BaseActivity() {

    override val TAG = "TopOnSplashActivity"
    private var mAD: TUSplashAd? = null
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
        mAD = TUSplashAd(this, AdConfig.TOPON_SPLASH_ID, object : TUSplashAdListener {
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

            override fun onAdShow(atAdInfo: TUAdInfo) {
                Log.d(TAG, "onAdShow:${getCurrentThreadName()}")
            }

            override fun onAdClick(atAdInfo: TUAdInfo) {
                Log.d(TAG, "onAdClick:${getCurrentThreadName()}")
                canJump = true
            }

            override fun onAdDismiss(atAdInfo: TUAdInfo, atSplashAdExtraInfo: TUSplashAdExtraInfo) {
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