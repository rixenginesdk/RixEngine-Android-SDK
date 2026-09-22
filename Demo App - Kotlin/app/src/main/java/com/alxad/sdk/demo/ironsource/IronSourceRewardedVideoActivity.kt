package com.alxad.sdk.demo.ironsource

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.alxad.sdk.demo.AdConfig
import com.alxad.sdk.demo.BaseActivity
import com.alxad.sdk.demo.R
import com.unity3d.mediation.LevelPlayAdError
import com.unity3d.mediation.LevelPlayAdInfo
import com.unity3d.mediation.rewarded.LevelPlayReward
import com.unity3d.mediation.rewarded.LevelPlayRewardedAd
import com.unity3d.mediation.rewarded.LevelPlayRewardedAdListener

class IronSourceRewardedVideoActivity : BaseActivity(), View.OnClickListener {
    override val TAG = "IronSourceRewardedVideo"

    private var mTvTip: TextView? = null
    private var mTvShow: TextView? = null
    private var startTime: Long = 0

    private var mAd: LevelPlayRewardedAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_and_show)
        setActionBar()
        initView()
    }

    fun initView() {
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

        mAd = LevelPlayRewardedAd(AdConfig.IRON_SOURCE_REWARD_VIDEO_AD)
        mAd?.setListener(object : LevelPlayRewardedAdListener {
            override fun onAdLoaded(adInfo: LevelPlayAdInfo) {
                Log.d(TAG, "onAdLoaded")
                Toast.makeText(baseContext, getString(R.string.load_success), Toast.LENGTH_SHORT)
                    .show()
                mTvTip?.text = getString(
                    R.string.format_load_success,
                    (System.currentTimeMillis() - startTime) / 1000
                )
                mTvShow?.setEnabled(true)
            }

            override fun onAdLoadFailed(error: LevelPlayAdError) {
                Log.d(TAG, "onAdLoadFailed: " + error.errorCode + ";" + error.errorMessage)
                Toast.makeText(
                    baseContext,
                    getString(R.string.load_failed),
                    Toast.LENGTH_SHORT
                ).show()
                mTvTip?.text = getString(
                    R.string.format_load_failed,
                    error.errorMessage
                )
                mTvShow?.setEnabled(false)
            }

            override fun onAdInfoChanged(adInfo: LevelPlayAdInfo) {
                Log.d(TAG, "onAdInfoChanged")
            }

            override fun onAdDisplayed(adInfo: LevelPlayAdInfo) {
                Log.d(TAG, "onAdDisplayed")
            }

            override fun onAdDisplayFailed(error: LevelPlayAdError, adInfo: LevelPlayAdInfo) {
                Log.d(TAG, "onAdDisplayFailed: " + error.errorCode + ";" + error.errorMessage)
            }

            override fun onAdClicked(adInfo: LevelPlayAdInfo) {
                Log.d(TAG, "onAdClicked")
            }

            override fun onAdClosed(adInfo: LevelPlayAdInfo) {
                Log.d(TAG, "onAdClosed")
            }

            override fun onAdRewarded(reward: LevelPlayReward, adInfo: LevelPlayAdInfo) {
                Log.d(TAG, "onAdRewarded")
            }

        })
        mAd?.loadAd()
    }

    private fun bnShow() {
        if (mAd?.isAdReady == true) {
            mAd?.showAd(this)
        }
    }

}