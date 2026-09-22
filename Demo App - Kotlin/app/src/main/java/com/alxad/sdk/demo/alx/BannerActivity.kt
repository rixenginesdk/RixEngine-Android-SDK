package com.alxad.sdk.demo.alx

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import com.alxad.sdk.demo.AdConfig
import com.alxad.sdk.demo.BaseActivity
import com.alxad.sdk.demo.R
import com.rixengine.api.AlxAdParam
import com.rixengine.api.AlxBannerView
import com.rixengine.api.AlxBannerViewAdListener

class BannerActivity : BaseActivity(), View.OnClickListener {

    override val TAG = "AlxBannerActivity"

    private var mBnLoad: Button? = null
    private var mBnShow: Button? = null
    private var mTvTip: TextView? = null
    private var mBnLoadAndShow: Button? = null

    private var mAdContainer: FrameLayout? = null
    private var mAlxBannerView: AlxBannerView? = null
    private var mAlxBannerView2: AlxBannerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner)
        setActionBar()

        mTvTip = findViewById<View>(R.id.tv_tip) as TextView
        mBnLoad = findViewById<View>(R.id.bn_load) as Button
        mBnShow = findViewById<View>(R.id.bn_show) as Button
        mBnLoadAndShow = findViewById<View>(R.id.bn_load_show) as Button
        mAdContainer = findViewById<View>(R.id.ad_container) as FrameLayout
        mAlxBannerView = findViewById<View>(R.id.do_ad_banner) as AlxBannerView

        mBnLoad?.setOnClickListener(this)
        mBnShow?.setOnClickListener(this)
        mBnLoadAndShow?.setOnClickListener(this)
        mBnShow?.isEnabled = false
    }

    override fun onDestroy() {
        super.onDestroy()
        mAlxBannerView2?.destroy()
        mAlxBannerView?.destroy()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bn_load -> bnPreLoad()
            R.id.bn_show -> bnShow()
            R.id.bn_load_show -> bnLoadAndShow()
        }
    }

    private fun bnPreLoad() {
        mBnLoad?.isEnabled = false
        val startTime = System.currentTimeMillis()

        mAlxBannerView2 = AlxBannerView(this)
        mAlxBannerView2?.setBannerCanClose(false)
        mAlxBannerView2?.setBannerRefresh(0) //[ZH]不自动刷新  |  [EN]No automatic refresh
        mAlxBannerView2?.visibility = View.VISIBLE
        val userExtras: MutableMap<String, String> = HashMap()
        userExtras["bid_floor"] = "1.5"
        val builder = AlxAdParam.Builder().setUserExtras(userExtras)
        mAlxBannerView2?.loadAd(AdConfig.ALX_BANNER_AD_ID, builder.build(),object : AlxBannerViewAdListener() {
            override fun onAdLoaded() {
                Log.d(TAG, "onAdLoaded")
                mBnShow?.isEnabled = true
                mBnLoad?.isEnabled = true
                mTvTip?.let {
                    val msg = getString(
                        R.string.format_load_success,
                        (System.currentTimeMillis() - startTime) / 1000
                    ) + "｜ ecpm:" + mAlxBannerView2?.price
                    it.text = msg
                }

                mAlxBannerView2?.reportBiddingUrl()
                mAlxBannerView2?.reportChargingUrl()
            }

            override fun onAdError(errorCode: Int, errorMsg: String) {
                Log.d(TAG, "onAdError: errorMsg=$errorMsg  errorCode=$errorCode")
                mBnShow?.isEnabled = false
                mBnLoad?.isEnabled = true
                val msg = "errorCode=$errorCode;errorMsg=$errorMsg"
                mTvTip?.text = getString(R.string.format_load_failed, msg)
            }

            override fun onAdClicked() {
                Log.d(TAG, "onAdClicked")
            }

            override fun onAdShow() {
                Log.d(TAG, "onAdShow")
            }

            override fun onAdClose() {
                Log.d(TAG, "onAdClose")
            }
        })
    }

    private fun bnShow() {
        if (mAlxBannerView2?.isReady == true) {
            mAdContainer?.removeAllViews()
            mAdContainer?.addView(mAlxBannerView2)
            mTvTip?.text = ""
        }
    }

    private fun bnLoadAndShow() {
        val startTime = System.currentTimeMillis()
        mAlxBannerView?.setBannerCanClose(true)
        mAlxBannerView?.loadAd(AdConfig.ALX_BANNER_AD_ID, object : AlxBannerViewAdListener() {
            override fun onAdLoaded() {
                Log.d(TAG, "onAdLoaded:  | ecpm：" + mAlxBannerView?.price)
                mTvTip?.let {
                    val msg = getString(
                        R.string.format_load_success,
                        (System.currentTimeMillis() - startTime) / 1000
                    ) + "｜ ecpm:" + mAlxBannerView?.price
                    it.text = msg
                }
            }

            override fun onAdError(errorCode: Int, errorMsg: String) {
                Log.d(TAG, "onAdError: errorMsg=$errorMsg  errorCode=$errorCode")
                val msg = "errorCode=$errorCode;errorMsg=$errorMsg"
                mTvTip?.text = getString(R.string.format_load_failed, msg)
            }

            override fun onAdClicked() {
                Log.d(TAG, "onAdClicked")
            }

            override fun onAdShow() {
                Log.d(TAG, "onAdShow")
            }

            override fun onAdClose() {
                Log.d(TAG, "onAdClose")
            }
        })
    }
}