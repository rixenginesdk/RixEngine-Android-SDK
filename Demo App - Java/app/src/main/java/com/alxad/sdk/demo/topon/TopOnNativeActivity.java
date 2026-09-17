package com.alxad.sdk.demo.topon;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.R;
import com.secmtp.sdk.core.api.ATAdConst;
import com.secmtp.sdk.core.api.AdError;
import com.secmtp.sdk.core.api.ATAdInfo;
import com.secmtp.sdk.nativead.api.NativeAd;
import com.secmtp.sdk.nativead.api.ATNative;
import com.secmtp.sdk.nativead.api.ATNativeAdView;
import com.secmtp.sdk.nativead.api.ATNativeDislikeListener;
import com.secmtp.sdk.nativead.api.ATNativeEventExListener;
import com.secmtp.sdk.nativead.api.ATNativeImageView;
import com.secmtp.sdk.nativead.api.ATNativeMaterial;
import com.secmtp.sdk.nativead.api.ATNativeNetworkListener;
import com.secmtp.sdk.nativead.api.ATNativePrepareExInfo;
import com.secmtp.sdk.nativead.api.ATNativePrepareInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TopOnNativeActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = "TopOnNativeActivity";

    private View mBnLoad;
    private TextView mTvClearLog;
    private TextView mTvShowLog;

    private long mStartTime;

    private ATNative mATNative;
    private NativeAd mNativeAd;
    private ATNativeAdView mATNativeAdView; //渲染广告必须创建的容器


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topon_native);
        setActionBar();
        initView();
    }

    private void initView() {
        mATNativeAdView = (ATNativeAdView) findViewById(R.id.ad_container);
        mTvClearLog = (TextView) findViewById(R.id.tv_clear_log);
        mTvShowLog = (TextView) findViewById(R.id.tv_show_log);
        mBnLoad = findViewById(R.id.bn_load);

        mTvShowLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTvClearLog.setOnClickListener(this);
        mBnLoad.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bn_load) {
            loadAd();
        } else if (v.getId() == R.id.tv_clear_log) {
            clearLog();
        }
    }

    private void loadAd() {
        showLogMessage(getString(R.string.loading));
        mBnLoad.setEnabled(false);
        mStartTime = System.currentTimeMillis();

        mATNative = new ATNative(this, AdConfig.TOPON_NATIVE_ID, new ATNativeNetworkListener() {
            @Override
            public void onNativeAdLoaded() {
                Log.i(TAG, "onNativeAdLoaded：" + getCurrentThreadName());
                mBnLoad.setEnabled(true);
                showLogMessage("onNativeAdLoaded");
                showLogMessage(getString(R.string.format_load_success, (System.currentTimeMillis() - mStartTime) / 1000));
                showNativeAd();
            }

            @Override
            public void onNativeAdLoadFail(AdError adError) {
                Log.e(TAG, "onNativeAdLoadFail:" + adError.getFullErrorInfo());
                mBnLoad.setEnabled(true);
                showLogMessage("onNativeAdLoadFail");
                showLogMessage(getString(R.string.format_load_failed, adError.getFullErrorInfo()));
            }
        });

        int adViewWidth = getResources().getDisplayMetrics().widthPixels;
        int adViewHeight = adViewWidth * 3 / 4;
        Map<String, Object> localMap = new HashMap<>();
        localMap.put(ATAdConst.KEY.AD_WIDTH, adViewWidth);
        localMap.put(ATAdConst.KEY.AD_HEIGHT, adViewHeight);
        mATNative.setLocalExtra(localMap);

        //load ad
        mATNative.makeAdRequest();
    }

    private void showNativeAd() {
        if (mATNative == null) {
            return;
        }
        if (!mATNative.checkAdStatus().isReady()) {
            return;
        }

        NativeAd nativeAd = mATNative.getNativeAd();
        if (nativeAd == null) {
            return;
        }
        if (mNativeAd != null) {
            mNativeAd.destory();
        }
        mNativeAd = nativeAd;
        mNativeAd.setNativeEventListener(new ATNativeEventExListener() {
            @Override
            public void onDeeplinkCallback(ATNativeAdView atNativeAdView, ATAdInfo atAdInfo, boolean b) {
                Log.i(TAG, "onDeeplinkCallback");
                showLogMessage("onDeeplinkCallback");
            }


            public void onAdActRewardSuccess(ATAdInfo atAdInfo) {
                Log.i(TAG, "onAdActRewardSuccess");
                showLogMessage("onAdActRewardSuccess");
            }


            public void onAdActReward(ATAdInfo atAdInfo, int i) {
                Log.i(TAG, "onAdActReward");
                showLogMessage("onAdActReward");
            }

            @Override
            public void onAdImpressed(ATNativeAdView atNativeAdView, ATAdInfo atAdInfo) {
                Log.i(TAG, "onAdImpressed");
                showLogMessage("onAdImpressed");
            }

            @Override
            public void onAdClicked(ATNativeAdView atNativeAdView, ATAdInfo atAdInfo) {
                Log.i(TAG, "onAdClicked");
                showLogMessage("onAdClicked");
            }

            @Override
            public void onAdVideoStart(ATNativeAdView atNativeAdView) {
                Log.i(TAG, "onAdVideoStart");
                showLogMessage("onAdVideoStart");
            }

            @Override
            public void onAdVideoEnd(ATNativeAdView atNativeAdView) {
                Log.i(TAG, "onAdVideoEnd");
                showLogMessage("onAdVideoEnd");
            }

            @Override
            public void onAdVideoProgress(ATNativeAdView atNativeAdView, int i) {
                Log.i(TAG, "onAdVideoProgress:" + i);
            }
        });

        mNativeAd.setDislikeCallbackListener(new ATNativeDislikeListener() {
            @Override
            public void onAdCloseButtonClick(ATNativeAdView view, ATAdInfo entity) {
                Log.i(TAG, "native ad onAdCloseButtonClick");
                showLogMessage("onAdCloseButtonClick");
                //在这里开发者可实现广告View的移除操作
                mATNativeAdView.removeAllViews();
                if (mNativeAd != null) {
                    mNativeAd.destory();
                }
            }
        });

        mATNativeAdView.removeAllViews();
        ATNativePrepareInfo nativePrepareInfo = null;

        if (!mNativeAd.isNativeExpress()) {
            Log.d(TAG, "native self render");
            //自渲染 (如果也需要支持自渲染广告可参考自渲染广告集成方式)
            try {
                View view = getLayoutInflater().inflate(R.layout.topon_native_custom_ad_view, null);
                nativePrepareInfo = renderNativeAdView(mNativeAd, view);
                mNativeAd.renderAdContainer(mATNativeAdView, view);
            } catch (Exception e) {
                Log.e(TAG, "error:" + e.getMessage());
            }
        } else {
            Log.d(TAG, "native express");
            //模板渲染 (模版渲染只需要实现这步即可)
            mNativeAd.renderAdContainer(mATNativeAdView, null);
        }
        mNativeAd.prepare(mATNativeAdView, nativePrepareInfo);
    }


    /**
     * 自渲染广告
     *
     * @return
     */
    private ATNativePrepareInfo renderNativeAdView(NativeAd bean, View view) throws Exception {
        if (mATNativeAdView != null) {
            mATNativeAdView.removeAllViews();
            mATNativeAdView.addView(view);
        }

        TextView titleView = (TextView) view.findViewById(R.id.native_title);
        TextView descView = (TextView) view.findViewById(R.id.native_description);
        TextView adFromView = (TextView) view.findViewById(R.id.native_source);
        ImageView iconView = (ImageView) view.findViewById(R.id.native_icon);
//        ImageView imageView = (ImageView) view.findViewById(R.id.native_image);
        ImageView logoView = (ImageView) view.findViewById(R.id.native_logo);
        Button callToActionView = (Button) view.findViewById(R.id.ad_call_to_action);
        ImageView closeView = (ImageView) view.findViewById(R.id.native_close);
        FrameLayout contentArea = (FrameLayout) view.findViewById(R.id.native_media);

        ATNativePrepareInfo nativePrepareInfo = new ATNativePrepareInfo();
        ATNativeMaterial adMaterial = bean.getAdMaterial();

        List<View> clickViewList = new ArrayList<>();//click views

        // title
        String title = adMaterial.getTitle();
        if (!TextUtils.isEmpty(title)) {
            titleView.setText(title);
            nativePrepareInfo.setTitleView(titleView);//bind title
            clickViewList.add(titleView);
            titleView.setVisibility(View.VISIBLE);
        } else {
            titleView.setVisibility(View.GONE);
        }

        String descriptionText = adMaterial.getDescriptionText();
        if (!TextUtils.isEmpty(descriptionText)) {
            // desc
            descView.setText(descriptionText);
            nativePrepareInfo.setDescView(descView);//bind desc
            clickViewList.add(descView);
            descView.setVisibility(View.VISIBLE);
        } else {
            descView.setVisibility(View.GONE);
        }

        String iconUrl = adMaterial.getIconImageUrl();
        if (!TextUtils.isEmpty(descriptionText)) {
            Glide.with(this).load(iconUrl).into(iconView);
            nativePrepareInfo.setDescView(iconView);
            clickViewList.add(iconView);
            iconView.setVisibility(View.VISIBLE);
        } else {
            iconView.setVisibility(View.GONE);
        }

        String adFrom = adMaterial.getAdFrom();
        // ad from
        if (!TextUtils.isEmpty(adFrom)) {
            adFromView.setText(adFrom);
            adFromView.setVisibility(View.VISIBLE);
        } else {
            adFromView.setVisibility(View.GONE);
        }
        nativePrepareInfo.setAdFromView(adFromView);//bind ad from

        // cta button
        String callToActionText = adMaterial.getCallToActionText();
        if (!TextUtils.isEmpty(callToActionText)) {
            callToActionView.setText(callToActionText);
            nativePrepareInfo.setCtaView(callToActionView);//bind cta button
            clickViewList.add(callToActionView);
            callToActionView.setVisibility(View.VISIBLE);
        } else {
            callToActionView.setVisibility(View.GONE);
        }

        // media view
        View mediaView = adMaterial.getAdMediaView();

        RelativeLayout.LayoutParams mainImageParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        contentArea.removeAllViews();

        if (mediaView != null) {
            if (mediaView.getParent() != null) {
                ((ViewGroup) mediaView.getParent()).removeView(mediaView);
            }
//            mainImageParam.gravity = Gravity.CENTER;
            mediaView.setLayoutParams(mainImageParam);
            contentArea.addView(mediaView, mainImageParam);
            clickViewList.add(mediaView);
            contentArea.setVisibility(View.VISIBLE);
        } else if (!TextUtils.isEmpty(adMaterial.getMainImageUrl())) {
            ATNativeImageView imageView = new ATNativeImageView(this);
            imageView.setImage(adMaterial.getMainImageUrl());
            imageView.setLayoutParams(mainImageParam);
            contentArea.addView(imageView, mainImageParam);

            nativePrepareInfo.setMainImageView(imageView);//bind main image
            clickViewList.add(imageView);
            contentArea.setVisibility(View.VISIBLE);
        } else {
            contentArea.removeAllViews();
            contentArea.setVisibility(View.GONE);
        }

        //Ad Logo
        String adChoiceIconUrl = adMaterial.getAdChoiceIconUrl();
        Bitmap adLogoBitmap = adMaterial.getAdLogo();
        if (!TextUtils.isEmpty(adChoiceIconUrl)) {
            Glide.with(this).load(adChoiceIconUrl).into(logoView);
            nativePrepareInfo.setAdLogoView(logoView);//bind ad choice
            logoView.setVisibility(View.VISIBLE);
        } else if (adLogoBitmap != null) {
            logoView.setImageBitmap(adLogoBitmap);
            logoView.setVisibility(View.VISIBLE);
        } else {
            logoView.setImageBitmap(null);
            logoView.setVisibility(View.GONE);
        }

        nativePrepareInfo.setCloseView(closeView);

        nativePrepareInfo.setClickViewList(clickViewList);//bind click view list

        if (nativePrepareInfo instanceof ATNativePrepareExInfo) {
            List<View> creativeClickViewList = new ArrayList<>();//click views
            creativeClickViewList.add(callToActionView);
            ((ATNativePrepareExInfo) nativePrepareInfo).setCreativeClickViewList(creativeClickViewList);//bind custom view list
        }
        return nativePrepareInfo;
    }

    public int dip2px(float dipValue) {
        float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private void clearLog() {
        mTvShowLog.setText("");
    }

    private void showLogMessage(String msg) {
        mTvShowLog.append(msg);
        mTvShowLog.append("\r\n");
    }

}