package com.alxad.sdk.demo.gam

import android.os.Bundle
import android.util.Log
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
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAd.OnNativeAdLoadedListener
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

class GamNativeActivity : BaseActivity(), View.OnClickListener {
    override val TAG = "GamNativeActivity"
    private var mAdContainerView: FrameLayout? = null
    private var mBnLoad: View? = null
    private var mTvTip: TextView? = null
    private var mStartTime: Long = 0
    private var mAdLoader: AdLoader? = null
    private var mNativeAd: NativeAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_ads)
        setActionBar()
        initView()
    }

    private fun initView() {
        mAdContainerView = findViewById<View>(R.id.ad_container) as FrameLayout
        mTvTip = findViewById<View>(R.id.tv_tip) as TextView
        mBnLoad = findViewById(R.id.bn_load)
        mBnLoad?.setOnClickListener(this)
    }

    private fun loadAd() {
        mTvTip?.setText(R.string.loading)
        mBnLoad?.isEnabled = false
        mStartTime = System.currentTimeMillis()
        mAdLoader = AdLoader.Builder(this, AdConfig.GAM_NATIVE_ID)
            .forNativeAd(OnNativeAdLoadedListener { nativeAd ->
                Log.d(TAG, "onNativeAdLoaded：${getCurrentThreadName()}")
                if (mAdLoader != null && mAdLoader?.isLoading == true) {
                    return@OnNativeAdLoadedListener
                }
                if (isDestroyed) {
                    nativeAd.destroy()
                    return@OnNativeAdLoadedListener
                }
                mNativeAd = nativeAd

                val adView = renderNativeAdView(nativeAd)
                mAdContainerView?.removeAllViews()
                mAdContainerView?.addView(adView)
            })
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    mBnLoad?.isEnabled = true
                    mTvTip?.text = getString(R.string.format_load_failed, loadAdError.message)
                    Log.d(TAG, "onAdFailedToLoad:" + loadAdError.message)
                    Toast.makeText(baseContext, getString(R.string.load_failed), Toast.LENGTH_SHORT).show()
                }

                override fun onAdClosed() {
                    Log.d(TAG, "onAdClosed:" + Thread.currentThread().name)
                    doCloseAd()
                }

                override fun onAdOpened() {
                    Log.d(TAG, "onAdOpened")
                }

                override fun onAdLoaded() {
                    Log.d(TAG, "onAdLoaded")
                    mBnLoad?.isEnabled = true
                    mTvTip?.text =getString(R.string.format_load_success, (System.currentTimeMillis() - mStartTime) / 1000)
                }

                override fun onAdClicked() {
                    Log.d(TAG, "onAdClicked")
                }

                override fun onAdImpression() {
                    Log.d(TAG, "onAdImpression")
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()
        mAdLoader?.loadAd(AdManagerAdRequest.Builder().build())
    }

    override fun onClick(v: View) {
        if (v.id == R.id.bn_load) {
            loadAd()
        }
    }

    private fun doCloseAd() {
        mTvTip?.text = ""
        mAdContainerView?.removeAllViews()
        mNativeAd?.destroy()
    }

    /**
     * 自渲染广告
     *
     * @return
     */
    private fun renderNativeAdView(bean: NativeAd): NativeAdView {
        val adView =
            layoutInflater.inflate(R.layout.admob_native_custom_ad_view, null) as NativeAdView
        val tvAdvertiser = adView.findViewById<View>(R.id.ad_advertiser) as TextView
        val ivIcon = adView.findViewById<View>(R.id.iv_ad_icon) as ImageView
        val tvTitle = adView.findViewById<View>(R.id.tv_ad_title) as TextView
        val tvDescription = adView.findViewById<View>(R.id.tv_ad_desc) as TextView
        val bnCallToAction = adView.findViewById<View>(R.id.ad_call_to_action) as Button
        val ivClose = adView.findViewById<View>(R.id.ad_close) as ImageView
        val ivMainImg = adView.findViewById<View>(R.id.iv_image) as MediaView

        adView.headlineView = tvTitle
        adView.bodyView = tvDescription
        adView.iconView = ivIcon
        //        adView.setImageView(ivMainImg);
        adView.callToActionView = bnCallToAction
        adView.advertiserView = tvAdvertiser
        adView.mediaView = ivMainImg

        tvTitle.text = bean.headline
        tvDescription.text = bean.body
        bnCallToAction.text = bean.callToAction
        tvAdvertiser.text = bean.advertiser
        ivMainImg.mediaContent = bean.mediaContent

//        List<NativeAd.Image> imageList = bean.getImages();
//        if (imageList != null && imageList.size() > 0) {
//            NativeAd.Image image = imageList.get(0);
//            if (image != null && image.getUri() != null) {
//                Glide.with(this).load(image.getUri()).into(ivMainImg);
//            }
//        }
        if (bean.icon != null && bean.icon?.uri != null) {
            Glide.with(this).load(bean.icon?.uri).into(ivIcon)
        }
        adView.setNativeAd(bean) //这句很重要。如果去掉了，点击就没有反应
        ivClose.setOnClickListener { doCloseAd() }
        return adView
    }

}