package com.alxad.sdk.demo.gam

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.alxad.sdk.demo.AdConfig
import com.alxad.sdk.demo.BaseActivity
import com.alxad.sdk.demo.R
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class GamRewardVideoActivity : BaseActivity(), View.OnClickListener {
    override val TAG = "GamRewardVideo"
    private var mRewardedAd: RewardedAd? = null
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

    @SuppressLint("NonConstantResourceId")
    override fun onClick(v: View) {
        if (v.id == R.id.tv_load) {
            bnLoad()
        }else if (v.id == R.id.tv_show) {
            showAd()
        }
    }

    private fun showAd() {
        if (mRewardedAd == null) {
            Toast.makeText(this, getString(R.string.show_ad_no_load), Toast.LENGTH_SHORT).show()
            return
        }
        mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.d(
                    TAG,
                    "onAdFailedToShowFullScreenContent:" + adError.code + ";" + adError.message
                )
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "onAdShowedFullScreenContent")
                Toast.makeText(
                    this@GamRewardVideoActivity,
                    "Rewarded ad opened",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "onAdDismissedFullScreenContent")
                bnLoad()
                Toast.makeText(
                    this@GamRewardVideoActivity,
                    "Rewarded ad closed",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onAdImpression() {
                Log.d(TAG, "onAdImpression")
            }

            override fun onAdClicked() {
                Log.d(TAG, "onAdClicked")
            }
        }
        mRewardedAd?.show(this) { rewardItem ->
            Log.d(
                TAG,
                "onUserEarnedReward:" + rewardItem.type
            )
        }
    }

    fun bnLoad() {
        mTvTip?.setText(R.string.loading)
        startTime = System.currentTimeMillis()

        RewardedAd.load(
            this,
            AdConfig.GAM_REWARD_ID,
            AdManagerAdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    Log.d(TAG, "onAdLoaded:${getCurrentThreadName()}")
                    mTvTip?.text =getString(R.string.format_load_success, (System.currentTimeMillis() - startTime) / 1000)
                    mTvShow?.isEnabled = true

                    mRewardedAd = rewardedAd
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(
                        TAG,
                        "onRewardedAdFailedToLoad: " + adError.code + " " + adError.message + ";" + getCurrentThreadName()
                    )
                    Toast.makeText(baseContext, getString(R.string.load_failed), Toast.LENGTH_SHORT).show()
                    mTvTip?.text = "load failed:" + adError.message
                    mTvShow?.isEnabled = false
                }
            })
    }

}