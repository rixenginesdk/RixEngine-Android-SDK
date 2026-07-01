package com.alxad.sdk.demo.topon

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.alxad.sdk.demo.AdConfig
import com.alxad.sdk.demo.BaseActivity
import com.alxad.sdk.demo.R
import com.bumptech.glide.Glide
import com.thinkup.core.api.AdError
import com.thinkup.core.api.TUAdInfo
import com.thinkup.nativead.api.NativeAd
import com.thinkup.nativead.api.TUNative
import com.thinkup.nativead.api.TUNativeAdView
import com.thinkup.nativead.api.TUNativeDislikeListener
import com.thinkup.nativead.api.TUNativeEventExListener
import com.thinkup.nativead.api.TUNativeImageView
import com.thinkup.nativead.api.TUNativeNetworkListener
import com.thinkup.nativead.api.TUNativePrepareExInfo
import com.thinkup.nativead.api.TUNativePrepareInfo


class TopOnNativeActivity : BaseActivity(), View.OnClickListener {
    override val TAG = "TopOnNativeActivity"
    private var mTvLoad: TextView? = null
    private var mTvTip: TextView? = null
    private var mStartTime: Long = 0
    private var mATNative: TUNative? = null
    private var mNativeAd: NativeAd? = null
    private var mATNativeAdView: TUNativeAdView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topon_native)
        setActionBar()
        initView()
    }

    private fun initView() {
        mTvLoad = findViewById<View>(R.id.tv_load) as TextView
        mTvTip = findViewById<View>(R.id.tv_tip) as TextView
        mATNativeAdView = findViewById<View>(R.id.ad_container) as TUNativeAdView
        mTvLoad?.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.tv_load) {
            loadNativeAd()
        }
    }

    private fun loadNativeAd() {
        mTvTip?.setText(R.string.loading)
        mTvLoad?.isEnabled = false
        mStartTime = System.currentTimeMillis()

        mATNative = TUNative(this, AdConfig.TOPON_NATIVE_ID, object : TUNativeNetworkListener {
            override fun onNativeAdLoaded() {
                Log.i(TAG, "onNativeAdLoaded：" + getCurrentThreadName())
                mTvLoad?.setEnabled(true)
                mTvTip?.text = getString(
                    R.string.format_load_success,
                    (System.currentTimeMillis() - mStartTime) / 1000
                )
                showNativeAd()
            }

            override fun onNativeAdLoadFail(adError: AdError) {
                Log.e(TAG, "onNativeAdLoadFail:" + adError.fullErrorInfo)
                mTvLoad?.setEnabled(true)
                mTvTip?.text = getString(R.string.format_load_failed, adError.fullErrorInfo)
            }
        })

        val mAdViewWidth = getResources().displayMetrics.widthPixels
        val mAdViewHeight = dip2px(340f)
        val localMap: MutableMap<String?, Any?> = HashMap<String?, Any?>()
        localMap["imageWidth"] = mAdViewWidth
        localMap["imageHeight"] = mAdViewHeight
        localMap["nativeType"] = "0"
        mATNative?.setLocalExtra(localMap)

        //load ad
        mATNative?.makeAdRequest()
    }

    private fun showNativeAd() {
        if (mATNative == null) {
            return
        }
        if(mATNative?.checkAdStatus()?.isReady == false){
            return
        }
        val nativeAd:NativeAd = mATNative?.nativeAd ?: return
        mNativeAd?.destory()

        mNativeAd = nativeAd
        mNativeAd?.setNativeEventListener(object : TUNativeEventExListener {
            override fun onDeeplinkCallback(
                atNativeAdView: TUNativeAdView?,
                atAdInfo: TUAdInfo?,
                b: Boolean
            ) {
                Log.i(TAG, "onDeeplinkCallback")
            }

            override fun onAdImpressed(atNativeAdView: TUNativeAdView?, atAdInfo: TUAdInfo?) {
                Log.i(TAG, "onAdImpressed")
            }

            override fun onAdClicked(atNativeAdView: TUNativeAdView?, atAdInfo: TUAdInfo?) {
                Log.i(TAG, "onAdClicked")
            }

            override fun onAdVideoStart(atNativeAdView: TUNativeAdView?) {
                Log.i(TAG, "onAdVideoStart")
            }

            override fun onAdVideoEnd(atNativeAdView: TUNativeAdView?) {
                Log.i(TAG, "onAdVideoEnd")
            }

            override fun onAdVideoProgress(atNativeAdView: TUNativeAdView?, i: Int) {
                Log.i(TAG, "onAdVideoProgress:" + i)
            }
        })

        mNativeAd?.setDislikeCallbackListener(object : TUNativeDislikeListener() {
            override fun onAdCloseButtonClick(view: TUNativeAdView?, entity: TUAdInfo?) {
                Log.i(TAG, "native ad onAdCloseButtonClick")
                //在这里开发者可实现广告View的移除操作
                mATNativeAdView?.removeAllViews()
                mNativeAd?.destory()
            }
        })

        mATNativeAdView?.removeAllViews()
        var nativePrepareInfo: TUNativePrepareInfo? = null

        if (mNativeAd?.isNativeExpress != true) {
            Log.d(TAG, "native self render")
            //自渲染 (如果也需要支持自渲染广告可参考自渲染广告集成方式)
            try {
                val view = layoutInflater.inflate(R.layout.topon_native_custom_ad_view, null)
                nativePrepareInfo = renderNativeAdView(mNativeAd!!, view)
                mNativeAd!!.renderAdContainer(mATNativeAdView, view)
            } catch (e: Exception) {
                Log.e(TAG, "error:${e.message}")
            }
        } else {
            Log.d(TAG, "native express")
            //模板渲染 (模版渲染只需要实现这步即可)
            mNativeAd?.renderAdContainer(mATNativeAdView, null)
        }
        mNativeAd?.prepare(mATNativeAdView, nativePrepareInfo)
        
    }

    /**
     * 自渲染广告
     *
     * @return
     */
    @Throws(java.lang.Exception::class)
    private fun renderNativeAdView(bean: NativeAd, view: View): TUNativePrepareInfo {
        if (mATNativeAdView != null) {
            mATNativeAdView?.removeAllViews()
            mATNativeAdView?.addView(view)
        }

        val titleView = view.findViewById<View?>(R.id.native_title) as TextView
        val descView = view.findViewById<View?>(R.id.native_description) as TextView
        val adFromView = view.findViewById<View?>(R.id.native_source) as TextView
        val iconView = view.findViewById<View?>(R.id.native_icon) as ImageView
        //        ImageView imageView = (ImageView) view.findViewById(R.id.native_image);
        val logoView = view.findViewById<View?>(R.id.native_logo) as ImageView
        val callToActionView = view.findViewById<View?>(R.id.ad_call_to_action) as Button
        val closeView = view.findViewById<View?>(R.id.native_close) as ImageView?
        val contentArea = view.findViewById<View?>(R.id.native_media) as FrameLayout

        val nativePrepareInfo = TUNativePrepareInfo()
        val adMaterial = bean.getAdMaterial()

        val clickViewList: MutableList<View?> = ArrayList<View?>() //click views

        // title
        val title = adMaterial.getTitle()
        if (!TextUtils.isEmpty(title)) {
            titleView.setText(title)
            nativePrepareInfo.setTitleView(titleView) //bind title
            clickViewList.add(titleView)
            titleView.setVisibility(View.VISIBLE)
        } else {
            titleView.setVisibility(View.GONE)
        }

        val descriptionText = adMaterial.getDescriptionText()
        if (!TextUtils.isEmpty(descriptionText)) {
            // desc
            descView.setText(descriptionText)
            nativePrepareInfo.setDescView(descView) //bind desc
            clickViewList.add(descView)
            descView.setVisibility(View.VISIBLE)
        } else {
            descView.setVisibility(View.GONE)
        }

        val iconUrl = adMaterial.getIconImageUrl()
        if (!TextUtils.isEmpty(descriptionText)) {
            Glide.with(this).load(iconUrl).into(iconView)
            nativePrepareInfo.setDescView(iconView)
            clickViewList.add(iconView)
            iconView.setVisibility(View.VISIBLE)
        } else {
            iconView.setVisibility(View.GONE)
        }

        val adFrom = adMaterial.getAdFrom()
        // ad from
        if (!TextUtils.isEmpty(adFrom)) {
            adFromView.setText(adFrom)
            adFromView.setVisibility(View.VISIBLE)
        } else {
            adFromView.setVisibility(View.GONE)
        }
        nativePrepareInfo.setAdFromView(adFromView) //bind ad from

        // cta button
        val callToActionText = adMaterial.getCallToActionText()
        if (!TextUtils.isEmpty(callToActionText)) {
            callToActionView.setText(callToActionText)
            nativePrepareInfo.setCtaView(callToActionView) //bind cta button
            clickViewList.add(callToActionView)
            callToActionView.setVisibility(View.VISIBLE)
        } else {
            callToActionView.setVisibility(View.GONE)
        }

        // media view
        val mediaView = adMaterial.getAdMediaView()

        val mainImageParam = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        contentArea.removeAllViews()

        if (mediaView != null) {
            if (mediaView.getParent() != null) {
                (mediaView.getParent() as ViewGroup).removeView(mediaView)
            }
            //            mainImageParam.gravity = Gravity.CENTER;
            mediaView.setLayoutParams(mainImageParam)
            contentArea.addView(mediaView, mainImageParam)
            clickViewList.add(mediaView)
            contentArea.setVisibility(View.VISIBLE)
        } else if (!TextUtils.isEmpty(adMaterial.getMainImageUrl())) {
            val imageView = TUNativeImageView(this)
            imageView.setImage(adMaterial.getMainImageUrl())
            imageView.setLayoutParams(mainImageParam)
            contentArea.addView(imageView, mainImageParam)

            nativePrepareInfo.setMainImageView(imageView) //bind main image
            clickViewList.add(imageView)
            contentArea.setVisibility(View.VISIBLE)
        } else {
            contentArea.removeAllViews()
            contentArea.setVisibility(View.GONE)
        }

        //Ad Logo
        val adChoiceIconUrl = adMaterial.getAdChoiceIconUrl()
        val adLogoBitmap = adMaterial.getAdLogo()
        if (!TextUtils.isEmpty(adChoiceIconUrl)) {
            Glide.with(this).load(adChoiceIconUrl).into(logoView)
            nativePrepareInfo.setAdLogoView(logoView) //bind ad choice
            logoView.setVisibility(View.VISIBLE)
        } else if (adLogoBitmap != null) {
            logoView.setImageBitmap(adLogoBitmap)
            logoView.setVisibility(View.VISIBLE)
        } else {
            logoView.setImageBitmap(null)
            logoView.setVisibility(View.GONE)
        }

        nativePrepareInfo.setCloseView(closeView)

        nativePrepareInfo.setClickViewList(clickViewList) //bind click view list

        if (nativePrepareInfo is TUNativePrepareExInfo) {
            val creativeClickViewList: MutableList<View?> = ArrayList<View?>() //click views
            creativeClickViewList.add(callToActionView)
            nativePrepareInfo.setCreativeClickViewList(creativeClickViewList) //bind custom view list
        }
        return nativePrepareInfo
    }

    fun dip2px(dipValue: Float): Int {
        val scale = this.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }
}