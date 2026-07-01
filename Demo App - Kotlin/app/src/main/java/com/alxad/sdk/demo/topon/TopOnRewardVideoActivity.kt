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
import com.thinkup.core.api.AdError
import com.thinkup.core.api.TUAdInfo
import com.thinkup.core.api.TUNetworkConfirmInfo
import com.thinkup.rewardvideo.api.TURewardVideoAd
import com.thinkup.rewardvideo.api.TURewardVideoExListener

class TopOnRewardVideoActivity : BaseActivity(), View.OnClickListener {
    override val TAG = "TopOnRewardVideoActivity"
    private var mTvTip: TextView? = null
    private var mTvShow: TextView? = null
    private var mVideoAD: TURewardVideoAd? = null
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

        mVideoAD = TURewardVideoAd(this, AdConfig.TOPON_VIDEO_AD_ID)
        mVideoAD?.setAdListener(object : TURewardVideoExListener {
            override fun onRewardFailed(atAdInfo: TUAdInfo?) {
            }

            override fun onDeeplinkCallback(adInfo: TUAdInfo, isSuccess: Boolean) {
                Log.i(
                    TAG,
                    "onDeeplinkCallback:" + adInfo.toString() + "--status:" + isSuccess + ";" + getCurrentThreadName()
                )
            }

            override fun onDownloadConfirm(
                context: Context?,
                tuAdInfo: TUAdInfo?,
                tuNetworkConfirmInfo: TUNetworkConfirmInfo?
            ) {
            }

            override fun onRewardedVideoAdAgainPlayStart(atAdInfo: TUAdInfo?) {
            }

            override fun onRewardedVideoAdAgainPlayEnd(atAdInfo: TUAdInfo?) {
            }

            override fun onRewardedVideoAdAgainPlayFailed(adError: AdError?, atAdInfo: TUAdInfo?) {
            }

            override fun onRewardedVideoAdAgainPlayClicked(atAdInfo: TUAdInfo?) {
            }

            override fun onAgainReward(atAdInfo: TUAdInfo?) {
            }

            override fun onAgainRewardFailed(atAdInfo: TUAdInfo?) {
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

            override fun onRewardedVideoAdPlayStart(entity: TUAdInfo?) {
                Log.i(TAG, "onRewardedVideoAdPlayStart:" + getCurrentThreadName())
            }

            override fun onRewardedVideoAdPlayEnd(entity: TUAdInfo?) {
                Log.i(TAG, "onRewardedVideoAdPlayEnd:" + getCurrentThreadName())
            }

            override fun onRewardedVideoAdPlayFailed(errorCode: AdError?, entity: TUAdInfo?) {
                Log.i(
                    TAG,
                    "onRewardedVideoAdPlayFailed:" + getCurrentThreadName()
                )
            }

            override fun onRewardedVideoAdClosed(entity: TUAdInfo?) {
                Log.i(TAG, "onRewardedVideoAdClosed:" + getCurrentThreadName())
                mTvShow?.setEnabled(false)
                mTvTip?.setText("")
            }

            override fun onRewardedVideoAdPlayClicked(entity: TUAdInfo?) {
                Log.i(
                    TAG,
                    "onRewardedVideoAdPlayClicked:" + getCurrentThreadName()
                )
            }

            override fun onReward(entity: TUAdInfo?) {
                Log.i(TAG, "onReward: " + getCurrentThreadName())
            }
        })
        mVideoAD?.load()
    }

}