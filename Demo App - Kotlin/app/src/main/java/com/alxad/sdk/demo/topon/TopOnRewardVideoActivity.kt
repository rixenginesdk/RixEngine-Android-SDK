package com.alxad.sdk.demo.topon

import android.annotation.SuppressLint
import android.content.Context
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
import com.secmtp.sdk.core.api.ATNetworkConfirmInfo
import com.secmtp.sdk.rewardvideo.api.ATRewardVideoAd
import com.secmtp.sdk.rewardvideo.api.ATRewardVideoExListener

class TopOnRewardVideoActivity : BaseActivity(), View.OnClickListener {
    override val TAG = "TopOnRewardVideoActivity"
    private var mTvTip: TextView? = null
    private var mTvShow: TextView? = null
    private var mVideoAD: ATRewardVideoAd? = null
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
            loadAd()
        }else if (v.id == R.id.tv_show) {
            if(mVideoAD == null) {
                Toast.makeText(this, getString(R.string.show_ad_no_load), Toast.LENGTH_SHORT).show()
                return
            }
            if (mVideoAD?.isAdReady == true) {
                mVideoAD?.show(this)
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

        mVideoAD = ATRewardVideoAd(this, AdConfig.TOPON_VIDEO_AD_ID)
        mVideoAD?.setAdListener(object : ATRewardVideoExListener {
            override fun onRewardFailed(atAdInfo: ATAdInfo?) {
            }

            override fun onDeeplinkCallback(adInfo: ATAdInfo, isSuccess: Boolean) {
                Log.i(
                    TAG,
                    "onDeeplinkCallback:" + adInfo.toString() + "--status:" + isSuccess + ";" + getCurrentThreadName()
                )
            }

            override fun onDownloadConfirm(
                context: Context?,
                ATAdInfo: ATAdInfo?,
                ATNetworkConfirmInfo: ATNetworkConfirmInfo?
            ) {
            }

            override fun onRewardedVideoAdAgainPlayStart(atAdInfo: ATAdInfo?) {
            }

            override fun onRewardedVideoAdAgainPlayEnd(atAdInfo: ATAdInfo?) {
            }

            override fun onRewardedVideoAdAgainPlayFailed(adError: AdError?, atAdInfo: ATAdInfo?) {
            }

            override fun onRewardedVideoAdAgainPlayClicked(atAdInfo: ATAdInfo?) {
            }

            override fun onAgainReward(atAdInfo: ATAdInfo?) {
            }

            override fun onAgainRewardFailed(atAdInfo: ATAdInfo?) {
            }

            override fun onRewardedVideoAdLoaded() {
                Log.i(TAG, "onRewardedVideoAdLoaded:" + getCurrentThreadName())
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

            override fun onRewardedVideoAdFailed(errorCode: AdError) {
                Log.i(
                    TAG,
                    "onRewardedVideoAdFailed:" + errorCode.getCode() + " " + errorCode.getDesc() + ";" + getCurrentThreadName()
                )
                Toast.makeText(
                    baseContext,
                    getString(R.string.load_failed),
                    Toast.LENGTH_SHORT
                ).show()
                mTvTip?.setText(R.string.load_failed)
                mTvShow?.setEnabled(false)
            }

            override fun onRewardedVideoAdPlayStart(entity: ATAdInfo?) {
                Log.i(TAG, "onRewardedVideoAdPlayStart:" + getCurrentThreadName())
            }

            override fun onRewardedVideoAdPlayEnd(entity: ATAdInfo?) {
                Log.i(TAG, "onRewardedVideoAdPlayEnd:" + getCurrentThreadName())
            }

            override fun onRewardedVideoAdPlayFailed(errorCode: AdError?, entity: ATAdInfo?) {
                Log.i(
                    TAG,
                    "onRewardedVideoAdPlayFailed:" + getCurrentThreadName()
                )
            }

            override fun onRewardedVideoAdClosed(entity: ATAdInfo?) {
                Log.i(TAG, "onRewardedVideoAdClosed:" + getCurrentThreadName())
                mTvShow?.setEnabled(false)
                mTvTip?.setText("")
            }

            override fun onRewardedVideoAdPlayClicked(entity: ATAdInfo?) {
                Log.i(
                    TAG,
                    "onRewardedVideoAdPlayClicked:" + getCurrentThreadName()
                )
            }

            override fun onReward(entity: ATAdInfo?) {
                Log.i(TAG, "onReward: " + getCurrentThreadName())
            }
        })
        mVideoAD?.load()
    }

}