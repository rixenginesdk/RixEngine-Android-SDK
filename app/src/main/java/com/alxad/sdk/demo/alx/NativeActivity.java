package com.alxad.sdk.demo.alx;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.R;
import com.bumptech.glide.Glide;
import com.rixengine.api.AlxAdParam;
import com.rixengine.api.nativead.AlxMediaContent;
import com.rixengine.api.nativead.AlxMediaView;
import com.rixengine.api.nativead.AlxNativeAd;
import com.rixengine.api.nativead.AlxNativeAdLoadedListener;
import com.rixengine.api.nativead.AlxNativeAdLoader;
import com.rixengine.api.nativead.AlxNativeAdView;
import com.rixengine.api.nativead.AlxNativeEventListener;

import java.util.List;

/**
 * native Ad
 */
public class NativeActivity extends BaseActivity {
    private final String TAG = "AlxNativeActivity";

    //中文：AlxNativeAd.getCreativeType() 得到的广告素材类型【如：大图、小图、组图、视频、其他：未知类型】
    //English：AlxNativeAd. GetCreativeType () the advertising material type (such as a larger version, insets, picture, video and other: unknown type 】

    //中文：未知类型
    //English：Unknown type
    public static final int NATIVE_AD_CREATE_TYPE_UNKNOWN = 0;

    //中文：大图
    //English：Large image
    public static final int NATIVE_AD_CREATE_TYPE_LARGE_IMAGE = 1;

    //中文：小图
    //English：Small image
    public static final int NATIVE_AD_CREATE_TYPE_SMALL_IMAGE = 2;

    //中文：多图
    //English：Multiple images
    public static final int NATIVE_AD_CREATE_TYPE_GROUP_IMAGE = 3;

    //中文：视频
    //English：Video
    public static final int NATIVE_AD_CREATE_TYPE_VIDEO = 4;

    private FrameLayout mAdContainer;
    private AlxNativeAd mNativeAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);
        setActionBar();
        initView();
        loadAd();
    }

    private void initView() {
        mAdContainer = (FrameLayout) findViewById(R.id.ad_container);
    }

    private void loadAd() {
        AlxNativeAdLoader loader = new AlxNativeAdLoader.Builder(this, AdConfig.ALX_NATIVE_AD_ID).build();
        loader.loadAd(new AlxAdParam.Builder().build(), new AlxNativeAdLoadedListener() {
            @Override
            public void onAdFailed(int errorCode, String errorMsg) {
                Log.i(TAG, "onAdFailed:" + errorCode + ";" + errorMsg);
                Toast.makeText(getBaseContext(), getString(R.string.load_failed), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLoaded(List<AlxNativeAd> ads) {
                Log.i(TAG, "onAdLoaded");
                if (ads == null || ads.isEmpty()) {
                    return;
                }
                if (mNativeAd != null) {
                    mNativeAd.destroy();
                }
                mNativeAd = ads.get(0);
                Log.i(TAG, "price=" + mNativeAd.getPrice());
                mNativeAd.reportBiddingUrl();
                mNativeAd.reportChargingUrl();

                showNativeAd();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNativeAd != null) {
            mNativeAd.destroy();
        }
    }

    private void showNativeAd() {
        View nativeView = createNativeView(mNativeAd);
        if (nativeView != null) {
            mNativeAd.setNativeEventListener(new AlxNativeEventListener() {
                @Override
                public void onAdClicked() {
                    Log.i(TAG, "onAdClicked");
                }

                @Override
                public void onAdImpression() {
                    Log.i(TAG, "onAdImpression");
                }

                @Override
                public void onAdClosed() {
                    Log.i(TAG, "onAdClosed");
                    mNativeAd.destroy();
                    mAdContainer.removeAllViews();
                }
            });
            mAdContainer.removeAllViews();
            mAdContainer.addView(nativeView);
        }
    }

    private View createNativeView(AlxNativeAd nativeAd) {
        int createType = nativeAd.getCreativeType();
        if (createType == NATIVE_AD_CREATE_TYPE_VIDEO || createType == NATIVE_AD_CREATE_TYPE_LARGE_IMAGE) { //也可以不共用一个模版
            return createVideoTemplateView(nativeAd);
        }
        return null;
    }

    //中文：视频模版View
    //English：Video template View
    private View createVideoTemplateView(AlxNativeAd nativeAd) {
        View convertView = LayoutInflater.from(this).inflate(R.layout.native_video_template, null);
        AlxNativeAdView nativeView = (AlxNativeAdView) convertView.findViewById(R.id.native_ad_view);
        ImageView logo = (ImageView) convertView.findViewById(R.id.ad_logo);
        ImageView icon = (ImageView) convertView.findViewById(R.id.ad_icon);
        TextView title = (TextView) convertView.findViewById(R.id.ad_title);
        TextView description = (TextView) convertView.findViewById(R.id.ad_desc);
        TextView source = (TextView) convertView.findViewById(R.id.ad_source);
        Button callToAction = (Button) convertView.findViewById(R.id.ad_call_to_action);
        ImageView close = (ImageView) convertView.findViewById(R.id.ad_close);
        AlxMediaView mediaView = (AlxMediaView) convertView.findViewById(R.id.ad_media);

        nativeView.setTitleView(title);
        nativeView.setDescriptionView(description);
        nativeView.setIconView(icon);
        nativeView.setCallToActionView(callToAction);
        nativeView.setCloseView(close);
        nativeView.setMediaView(mediaView);
        nativeView.setAdSourceView(source);

        title.setText(nativeAd.getTitle());
        description.setText(nativeAd.getDescription());
        logo.setImageBitmap(nativeAd.getAdLogo());
        mediaView.setMediaContent(nativeAd.getMediaContent());

        if (TextUtils.isEmpty(nativeAd.getAdSource())) {
            source.setVisibility(View.GONE);
        } else {
            source.setVisibility(View.VISIBLE);
            source.setText(nativeAd.getAdSource());
        }

        String iconUrl = null;
        if (nativeAd.getIcon() != null) {
            iconUrl = nativeAd.getIcon().getImageUrl();
        }
        if (TextUtils.isEmpty(iconUrl)) {
            icon.setVisibility(View.GONE);
        } else {
            icon.setVisibility(View.VISIBLE);
            Glide.with(this).load(iconUrl).into(icon);
        }

        if (TextUtils.isEmpty(nativeAd.getCallToAction())) {
            callToAction.setVisibility(View.GONE);
        } else {
            callToAction.setVisibility(View.VISIBLE);
            callToAction.setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getMediaContent() != null && nativeAd.getMediaContent().hasVideo()) {
            nativeAd.getMediaContent().setVideoLifecycleListener(new AlxMediaContent.VideoLifecycleListener() {
                @Override
                public void onVideoStart() {
                    Log.i(TAG, "onVideoStart");
                }

                @Override
                public void onVideoEnd() {
                    Log.i(TAG, "onVideoEnd");
                }

                @Override
                public void onVideoPlay() {
                    Log.i(TAG, "onVideoPlay");
                }

                @Override
                public void onVideoPause() {
                    Log.i(TAG, "onVideoPause");
                }

                @Override
                public void onVideoPlayError(int code, String error) {
                    Log.i(TAG, "onVideoPlayError:" + code + ";" + error);
                }

                @Override
                public void onVideoMute(boolean isMute) {
                    Log.i(TAG, "onVideoMute:" + isMute);
                }
            });
        }

        // Register a native ad object.
        nativeView.setNativeAd(nativeAd);

        return nativeView;
    }


}