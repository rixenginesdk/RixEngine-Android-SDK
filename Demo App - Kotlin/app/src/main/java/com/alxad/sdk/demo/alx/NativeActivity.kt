package com.alxad.sdk.demo.alx

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.alxad.sdk.demo.AdConfig
import com.alxad.sdk.demo.BaseActivity
import com.alxad.sdk.demo.R
import com.bumptech.glide.Glide
import com.rixengine.api.AlxAdParam
import com.rixengine.api.nativead.AlxMediaContent.VideoLifecycleListener
import com.rixengine.api.nativead.AlxMediaView
import com.rixengine.api.nativead.AlxNativeAd
import com.rixengine.api.nativead.AlxNativeAdLoadedListener
import com.rixengine.api.nativead.AlxNativeAdLoader
import com.rixengine.api.nativead.AlxNativeAdView
import com.rixengine.api.nativead.AlxNativeEventListener

class NativeActivity : BaseActivity() {

    override val TAG = "AlxNativeActivity"


    //中文：AlxNativeAd.getCreativeType() 得到的广告素材类型【如：大图、小图、组图、视频、其他：未知类型】
    //English：AlxNativeAd. GetCreativeType () the advertising material type (such as a larger version, insets, picture, video and other: unknown type 】
    //中文：未知类型
    //English：Unknown type
    val NATIVE_AD_CREATE_TYPE_UNKNOWN: Int = 0

    //中文：大图
    //English：Large image
    val NATIVE_AD_CREATE_TYPE_LARGE_IMAGE: Int = 1

    //中文：小图
    //English：Small image
    val NATIVE_AD_CREATE_TYPE_SMALL_IMAGE: Int = 2

    //中文：多图
    //English：Multiple images
    val NATIVE_AD_CREATE_TYPE_GROUP_IMAGE: Int = 3

    //中文：视频
    //English：Video
    val NATIVE_AD_CREATE_TYPE_VIDEO: Int = 4

    private var mTvTip: TextView? = null
    private var mAdContainerView: FrameLayout? = null
    private var mNativeAd: AlxNativeAd? = null
    private var startTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native)
        setActionBar()
        initView()
        loadAd()
    }

    private fun initView() {
        mTvTip = findViewById(R.id.tv_tip)
        mAdContainerView = findViewById<View>(R.id.ad_container) as FrameLayout
    }

    private fun loadAd() {
        mTvTip?.setText(R.string.loading)
        startTime = System.currentTimeMillis()

        val userExtras: MutableMap<String, String> = HashMap()
        userExtras["bid_floor"] = "1.5"
        val builder = AlxAdParam.Builder().setUserExtras(userExtras)
        val loader = AlxNativeAdLoader.Builder(this, AdConfig.ALX_NATIVE_AD_ID).build()
        loader.loadAd(builder.build(), object : AlxNativeAdLoadedListener {
            override fun onAdFailed(errorCode: Int, errorMsg: String) {
                Log.i(TAG, "onAdFailed:$errorCode;$errorMsg")
                val msg = "errorCode=$errorCode;errorMsg=$errorMsg"
                mTvTip?.text = getString(R.string.format_load_failed, msg)
            }

            override fun onAdLoaded(ads: List<AlxNativeAd>) {
                Log.i(TAG, "onAdLoaded")
                if (ads == null || ads.isEmpty()) {
                    return
                }
                mNativeAd?.destroy()
                mNativeAd = ads[0]
                Log.i(TAG, "price=" + mNativeAd?.price)
                mTvTip?.let {
                    val msg = getString(
                        R.string.format_load_success,
                        (System.currentTimeMillis() - startTime) / 1000
                    ) + "｜ ecpm:" + mNativeAd?.price
                    it.text = msg
                }
                mNativeAd?.reportBiddingUrl()
                mNativeAd?.reportChargingUrl()

                showNativeAd()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mNativeAd?.destroy()
    }

    private fun showNativeAd() {
        if (mNativeAd == null) {
            return
        }
        val nativeView = createNativeView(mNativeAd!!)
        if (nativeView != null) {
            mNativeAd?.setNativeEventListener(object : AlxNativeEventListener() {
                override fun onAdClicked() {
                    Log.i(TAG, "onAdClicked")
                }

                override fun onAdImpression() {
                    Log.i(TAG, "onAdImpression")
                }

                override fun onAdClosed() {
                    Log.i(TAG, "onAdClosed")
                    mNativeAd?.destroy()
                    mAdContainerView?.removeAllViews()
                }
            })
            mAdContainerView?.removeAllViews()
            mAdContainerView?.addView(nativeView)
        }
    }

    private fun createNativeView(nativeAd: AlxNativeAd): View? {
        val createType = nativeAd.creativeType
        return if (createType == NATIVE_AD_CREATE_TYPE_VIDEO || createType == NATIVE_AD_CREATE_TYPE_LARGE_IMAGE) { //也可以不共用一个模版
            createVideoTemplateView(nativeAd)
        } else null
    }

    //视频模版View
    private fun createVideoTemplateView(nativeAd: AlxNativeAd): View? {
        val convertView: View =
            LayoutInflater.from(this).inflate(R.layout.native_video_template, null)
        val nativeView = convertView.findViewById<View>(R.id.native_ad_view) as AlxNativeAdView
        val logo = convertView.findViewById<View>(R.id.ad_logo) as ImageView
        val icon = convertView.findViewById<View>(R.id.ad_icon) as ImageView
        val title = convertView.findViewById<View>(R.id.ad_title) as TextView
        val description = convertView.findViewById<View>(R.id.ad_desc) as TextView
        val source = convertView.findViewById<View>(R.id.ad_source) as TextView
        val callToAction = convertView.findViewById<View>(R.id.ad_call_to_action) as Button
        val close = convertView.findViewById<View>(R.id.ad_close) as ImageView
        val mediaView = convertView.findViewById<View>(R.id.ad_media) as AlxMediaView

        nativeView.titleView = title
        nativeView.descriptionView = description
        nativeView.iconView = icon
        nativeView.callToActionView = callToAction
        nativeView.closeView = close
        nativeView.mediaView = mediaView
        nativeView.adSourceView = source

        title.text = nativeAd.title
        description.text = nativeAd.description
        logo.setImageBitmap(nativeAd.adLogo)
        mediaView.setMediaContent(nativeAd.mediaContent)

        if (TextUtils.isEmpty(nativeAd.adSource)) {
            source.visibility = View.GONE
        } else {
            source.visibility = View.VISIBLE
            source.text = nativeAd.adSource
        }

        var iconUrl: String? = null
        if (nativeAd.icon != null) {
            iconUrl = nativeAd.icon.imageUrl
        }
        if (TextUtils.isEmpty(iconUrl)) {
            icon.visibility = View.GONE
        } else {
            icon.visibility = View.VISIBLE
            Glide.with(this).load(iconUrl).into(icon)
        }

        if (TextUtils.isEmpty(nativeAd.callToAction)) {
            callToAction.visibility = View.GONE
        } else {
            callToAction.visibility = View.VISIBLE
            callToAction.text = nativeAd.callToAction
        }

        if (nativeAd.mediaContent != null && nativeAd.mediaContent.hasVideo()) {
            nativeAd.mediaContent.videoLifecycleListener = object : VideoLifecycleListener() {
                override fun onVideoStart() {
                    Log.i(TAG, "onVideoStart")
                }

                override fun onVideoEnd() {
                    Log.i(TAG, "onVideoEnd")
                }

                override fun onVideoPlay() {
                    Log.i(TAG, "onVideoPlay")
                }

                override fun onVideoPause() {
                    Log.i(TAG, "onVideoPause")
                }

                override fun onVideoPlayError(code: Int, error: String) {
                    Log.i(TAG, "onVideoPlayError:$code;$error")
                }

                override fun onVideoMute(isMute: Boolean) {
                    Log.i(TAG, "onVideoMute:$isMute")
                }
            }
        }

        // Register a native ad object.
        nativeView.setNativeAd(nativeAd)
        return nativeView
    }
}