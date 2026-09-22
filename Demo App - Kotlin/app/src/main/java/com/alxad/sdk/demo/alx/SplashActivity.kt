package com.alxad.sdk.demo.alx

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.alxad.sdk.demo.AdConfig
import com.alxad.sdk.demo.BaseActivity
import com.alxad.sdk.demo.MainActivity
import com.alxad.sdk.demo.R
import com.rixengine.api.AlxAdParam
import com.rixengine.api.AlxSplashAd
import com.rixengine.api.AlxSplashAdListener

class SplashActivity : BaseActivity() {

    override val TAG = "AlxSplashActivity"

    //[ZH] 开屏广告加载的超时时间5s
    //[EN] Splash Ad Load Timeout 5s
    private val AD_TIMEOUT = 5 * 1000

    //[ZH] 控制开屏广告点击跳转
    //[EN] Control the click-through of in-screen advertisements
    private var canJump = false
    private var mSlashAd: AlxSplashAd? = null

    private var mActivity: Activity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        mActivity = this
        loadAd()
    }

    private fun loadAd() {
        initSplashAd()
    }

    private fun initSplashAd() {
        Log.d(TAG, "ad start load")
        //[ZH] 初始化广告位。仅调用一次。
        //[EN] Initialize the ad spot. Only call once.
        val userExtras: MutableMap<String, String> = HashMap()
        userExtras["bid_floor"] = "1.5"
        val builder = AlxAdParam.Builder().setUserExtras(userExtras)

        mSlashAd = AlxSplashAd()
        mSlashAd?.load(
            this,
            AdConfig.ALX_SPLASH_BANNER_AD_ID,
            builder.build(),
            object : AlxSplashAdListener() {
                override fun onAdLoaded() {
                    Log.d(TAG, "onAdLoaded: | price：" + mSlashAd?.price)
                    mSlashAd?.show(mActivity)
                    mSlashAd?.reportChargingUrl()
                    mSlashAd?.reportBiddingUrl()
                }

                override fun onAdLoadFail(errorCode: Int, errorMsg: String) {
                    Log.e(TAG, "onAdLoadFail:$errorCode--$errorMsg")
                    goToMainActivity()
                }

                override fun onAdShow() {
                    Log.d(TAG, "onAdShow")
                }

                override fun onAdClicked() {
                    Log.d(TAG, "onAdClicked")
                    canJump = true
                }

                override fun onAdClose() {
                    Log.d(TAG, "onAdClose")
                    Toast.makeText(baseContext, "onAdDismissed be called", Toast.LENGTH_SHORT)
                        .show()
                    goToMainActivity()
                }

                override fun onAdVideoStart() {
                    Log.d(TAG, "onAdVideoStart")
                }

                override fun onAdVideoEnd() {
                    Log.d(TAG, "onAdVideoEnd")
                }

                override fun onAdVideoError(errorCode: Int, errorMsg: String?) {
                    val msg = "errorCode=$errorCode;errorMsg=$errorMsg"
                    Log.d(TAG, "onAdVideoError:${msg}")
                }
            },
            AD_TIMEOUT
        )
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
        mSlashAd?.destroy()
    }

    private fun goToMainActivity() {
        this.startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}