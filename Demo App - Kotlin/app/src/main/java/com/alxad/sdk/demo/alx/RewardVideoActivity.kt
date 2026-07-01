package com.alxad.sdk.demo.alx

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.alxad.sdk.demo.AdConfig
import com.alxad.sdk.demo.BaseActivity
import com.alxad.sdk.demo.R
import com.rixengine.api.AlxAdParam
import com.rixengine.api.AlxRewardVideoAD
import com.rixengine.api.AlxRewardVideoADListener

class RewardVideoActivity : BaseActivity(), View.OnClickListener {

    override val TAG = "AlxRewardVideoActivity"

    private var mTvTip: TextView? = null
    private var mTvShow: TextView? = null
    private var mVideoAD: AlxRewardVideoAD? = null
    private var startTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_and_show)
        setActionBar()
        initView()
    }

    private fun initView() {
        val tvLoad: TextView = findViewById(R.id.tv_load)
        mTvShow = findViewById(R.id.tv_show)
        mTvTip = findViewById(R.id.tv_tip)
        mTvShow?.isEnabled = false
        tvLoad.setOnClickListener(this)
        mTvShow?.setOnClickListener(this)
    }

    @SuppressLint("NonConstantResourceId")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_load -> loadAd()
            R.id.tv_show -> showAd()
        }
    }

    /**
     * load Ad
     */
    fun loadAd() {
        mTvTip?.setText(R.string.loading)
        startTime = System.currentTimeMillis()

        val userExtras: MutableMap<String, String> = HashMap()
        userExtras["bid_floor"] = "1.5"
        val builder = AlxAdParam.Builder().setUserExtras(userExtras)
        mVideoAD = AlxRewardVideoAD()
        mVideoAD?.load(
            this,
            AdConfig.ALX_REWARD_VIDEO_AD_ID,
            builder.build(),
            object : AlxRewardVideoADListener() {
                override fun onRewardedVideoAdLoaded(var1: AlxRewardVideoAD) {
                    Log.i(TAG, "onRewardedVideoAdLoaded")
                    Toast.makeText(
                        baseContext,
                        getString(R.string.load_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    mTvShow?.isEnabled = true
                    mTvTip?.let {
                        val msg = getString(
                            R.string.format_load_success,
                            (System.currentTimeMillis() - startTime) / 1000
                        ) + "｜ ecpm:" + mVideoAD?.price
                        it.text = msg
                    }

                    mVideoAD?.reportChargingUrl()
                    mVideoAD?.reportBiddingUrl()
                }

                override fun onRewardedVideoAdFailed(
                    var1: AlxRewardVideoAD,
                    errCode: Int,
                    errMsg: String
                ) {
                    Log.i(TAG, "onRewardedVideoAdFailed：$errCode; $errMsg")
                    Toast.makeText(baseContext, getString(R.string.load_failed), Toast.LENGTH_SHORT)
                        .show()
                    mTvTip?.text = getString(R.string.load_failed)
                    mTvShow?.isEnabled = false
                }

                override fun onRewardedVideoAdPlayStart(var1: AlxRewardVideoAD) {
                    Log.i(TAG, "onRewardedVideoAdPlayStart")
                }

                override fun onRewardedVideoAdPlayEnd(var1: AlxRewardVideoAD) {
                    Log.i(TAG, "onRewardedVideoAdPlayEnd")
                }

                override fun onRewardedVideoAdPlayFailed(
                    var2: AlxRewardVideoAD,
                    errCode: Int,
                    errMsg: String
                ) {
                    Log.i(TAG, "onRewardedVideoAdPlayFailed:$errCode;$errMsg")
                }

                override fun onRewardedVideoAdClosed(var1: AlxRewardVideoAD) {
                    Log.i(TAG, "onRewardedVideoAdClosed")
                    mTvShow?.isEnabled = false
                    mTvTip?.setText(R.string.tip_message)
                }

                override fun onRewardedVideoAdPlayClicked(var1: AlxRewardVideoAD) {
                    Log.i(TAG, "onRewardedVideoAdPlayClicked")
                }

                override fun onReward(var1: AlxRewardVideoAD) {
                    Log.i(TAG, "onReward")
                }

                override fun onRewardVideoCache(isSuccess: Boolean) {
                    Log.i(
                        TAG,
                        "onRewardVideoCache:" + isSuccess + ";" + Thread.currentThread().name
                    )
                }
            })
    }

    private fun showAd(){
        if (mVideoAD == null) {
            Toast.makeText(this, getString(R.string.show_ad_no_load), Toast.LENGTH_SHORT).show()
            return
        }
        if (mVideoAD?.isReady == true) {
            mVideoAD?.showVideo(this)
        } else {
            loadAd()
        }
    }
}