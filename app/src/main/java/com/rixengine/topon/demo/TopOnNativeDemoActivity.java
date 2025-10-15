package com.rixengine.topon.demo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.bumptech.glide.Glide;
import com.rixengine.AppConfig;
import com.rixengine.R;
import com.thinkup.core.api.AdError;
import com.thinkup.core.api.TUAdInfo;
import com.thinkup.nativead.api.NativeAd;
import com.thinkup.nativead.api.TUNative;
import com.thinkup.nativead.api.TUNativeAdView;
import com.thinkup.nativead.api.TUNativeDislikeListener;
import com.thinkup.nativead.api.TUNativeEventExListener;
import com.thinkup.nativead.api.TUNativeImageView;
import com.thinkup.nativead.api.TUNativeMaterial;
import com.thinkup.nativead.api.TUNativeNetworkListener;
import com.thinkup.nativead.api.TUNativePrepareExInfo;
import com.thinkup.nativead.api.TUNativePrepareInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TopOnNativeDemoActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = TopOnNativeDemoActivity.class.getSimpleName();

    private TextView mTvLoad;
    private TextView mTvTip;

    private long mStartTime;

    private TUNative mATNative;
    private NativeAd mNativeAd;
    private TUNativeAdView mATNativeAdView; //渲染广告必须创建的容器


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topon_native_demo);
        initView();
    }

    private void initView() {
        mTvLoad = (TextView) findViewById(R.id.tv_load);
        mTvTip = (TextView) findViewById(R.id.tv_tip);
        mATNativeAdView = (TUNativeAdView) findViewById(R.id.ad_container);

        mTvLoad.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_load) {
            loadNativeAd();
        }
    }

    private void loadNativeAd() {
        mTvTip.setText("The ad loading...");
        mTvLoad.setEnabled(false);
        mStartTime = System.currentTimeMillis();

        mATNative = new TUNative(this, AppConfig.TOPON_NATIVE_PID, new TUNativeNetworkListener() {
            @Override
            public void onNativeAdLoaded() {
                Log.i(TAG, "onNativeAdLoaded： Native ad loads success" + getThreadName());
                mTvLoad.setEnabled(true);
                mTvTip.setText("Native ad loads success--Consume time--" + (System.currentTimeMillis() - mStartTime) / 1000 + "-秒");
                showNativeAd();
            }

            @Override
            public void onNativeAdLoadFail(AdError adError) {
                Log.e(TAG, "onNativeAdLoadFail-" + getThreadName() + "： " + adError.getFullErrorInfo());
                mTvLoad.setEnabled(true);
                mTvTip.setText("Native AD load fail--Consume time-" + (System.currentTimeMillis() - mStartTime) / 1000 + "-秒\r\n失败原因:" + adError.getFullErrorInfo());
            }
        });

        int mAdViewWidth = getResources().getDisplayMetrics().widthPixels;
        int mAdViewHeight = dip2px(340);
        Map<String, Object> localMap = new HashMap<>();
        localMap.put("imageWidth", mAdViewWidth);
        localMap.put("imageHeight", mAdViewHeight);
        localMap.put("nativeType", "0");
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
        mNativeAd.setNativeEventListener(new TUNativeEventExListener() {
            @Override
            public void onDeeplinkCallback(TUNativeAdView atNativeAdView, TUAdInfo atAdInfo, boolean b) {
                Log.i(TAG, "onDeeplinkCallback");
            }

            @Override
            public void onAdImpressed(TUNativeAdView atNativeAdView, TUAdInfo atAdInfo) {
                Log.i(TAG, "onAdImpressed");
            }

            @Override
            public void onAdClicked(TUNativeAdView atNativeAdView, TUAdInfo atAdInfo) {
                Log.i(TAG, "onAdClicked");
            }

            @Override
            public void onAdVideoStart(TUNativeAdView atNativeAdView) {
                Log.i(TAG, "onAdVideoStart");
            }

            @Override
            public void onAdVideoEnd(TUNativeAdView atNativeAdView) {
                Log.i(TAG, "onAdVideoEnd");
            }

            @Override
            public void onAdVideoProgress(TUNativeAdView atNativeAdView, int i) {
                Log.i(TAG, "onAdVideoProgress:" + i);
            }
        });

        mNativeAd.setDislikeCallbackListener(new TUNativeDislikeListener() {
            @Override
            public void onAdCloseButtonClick(TUNativeAdView view, TUAdInfo entity) {
                Log.i(TAG, "native ad onAdCloseButtonClick");
                //在这里开发者可实现广告View的移除操作
                mATNativeAdView.removeAllViews();
                if (mNativeAd != null) {
                    mNativeAd.destory();
                }
            }
        });

        mATNativeAdView.removeAllViews();
        TUNativePrepareInfo nativePrepareInfo = null;

        if (!mNativeAd.isNativeExpress()) {
            Log.d(TAG,"native self render");
            //自渲染 (如果也需要支持自渲染广告可参考自渲染广告集成方式)
            try {
                View view = getLayoutInflater().inflate(R.layout.topon_native_custom_ad_view, null);
                nativePrepareInfo = renderNativeAdView(mNativeAd, view);
                mNativeAd.renderAdContainer(mATNativeAdView, view);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        } else {
            Log.d(TAG,"native express");
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
    private TUNativePrepareInfo renderNativeAdView(NativeAd bean, View view) throws Exception {
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

        TUNativePrepareInfo nativePrepareInfo = new TUNativePrepareInfo();
        TUNativeMaterial adMaterial = bean.getAdMaterial();

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
            TUNativeImageView imageView = new TUNativeImageView(this);
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

        if (nativePrepareInfo instanceof TUNativePrepareExInfo) {
            List<View> creativeClickViewList = new ArrayList<>();//click views
            creativeClickViewList.add(callToActionView);
            ((TUNativePrepareExInfo) nativePrepareInfo).setCreativeClickViewList(creativeClickViewList);//bind custom view list
        }
        return nativePrepareInfo;
    }

    private String getThreadName() {
        return Thread.currentThread().getName();
    }

    public int dip2px(float dipValue) {
        float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

}