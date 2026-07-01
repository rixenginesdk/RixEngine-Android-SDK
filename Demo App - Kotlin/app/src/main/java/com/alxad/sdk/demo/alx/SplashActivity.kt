package com.alxad.sdk.demo.alx

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import com.alxad.sdk.demo.AdConfig
import com.alxad.sdk.demo.BaseActivity
import com.alxad.sdk.demo.MainActivity
import com.alxad.sdk.demo.R
import com.rixengine.api.AlxAdParam
import com.rixengine.api.AlxSplashAd
import com.rixengine.api.AlxSplashAdListener

class SplashActivity: BaseActivity() {

    override val TAG = "AlxSplashActivity"

    //[ZH] 开屏广告加载的超时时间5s
    //[EN] Splash Ad Load Timeout 5s
    private val AD_TIMEOUT = 5 * 1000


    private var mAdContainerView: FrameLayout? = null
    private var mIvWelcome: ImageView? = null

    //[ZH] 控制开屏广告点击跳转
    //[EN] Control the click-through of in-screen advertisements
    private var canJump = false
    private var mSlashAd: AlxSplashAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initView()
        loadAd()
    }

    private fun initView() {
        mAdContainerView = findViewById<View>(R.id.ad_container) as FrameLayout
        mIvWelcome = findViewById<View>(R.id.iv_welcome) as ImageView
    }

    private fun loadAd() {
        initSplashAd()
    }

    private fun initSplashAd() {
        //[ZH] 初始化广告位。仅调用一次。
        //[EN] Initialize the ad spot. Only call once.
        val userExtras: MutableMap<String, String> = HashMap()
        userExtras["bid_floor"] = "1.5"
        val builder = AlxAdParam.Builder().setUserExtras(userExtras)

        mSlashAd = AlxSplashAd(this, AdConfig.ALX_SPLASH_AD_ID,builder.build())
        Log.d(TAG, "ad start load")
        mSlashAd?.load(object : AlxSplashAdListener() {
            override fun onAdLoadSuccess() {
                Log.d(TAG, "onAdLoadSuccess: | price：" + mSlashAd?.price)
                mSlashAd?.showAd(mAdContainerView)
                mSlashAd?.reportChargingUrl()
                mSlashAd?.reportBiddingUrl()
            }

            override fun onAdLoadFail(errorCode: Int, errorMsg: String) {
                Log.e(TAG, "onAdLoadFail:$errorCode--$errorMsg")
                goToMainActivity()
            }

            override fun onAdShow() {
                Log.d(TAG, "onAdShow")
                mIvWelcome?.visibility = View.GONE
            }

            override fun onAdClick() {
                Log.d(TAG, "onAdClick")
                canJump = true
            }

            override fun onAdDismissed() {
                Log.d(TAG, "onAdDismissed")
                Toast.makeText(baseContext, "onAdDismissed be called", Toast.LENGTH_SHORT)
                    .show()
                goToMainActivity()
            }
        }, AD_TIMEOUT)
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