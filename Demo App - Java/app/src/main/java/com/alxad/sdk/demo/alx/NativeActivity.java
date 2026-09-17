package com.alxad.sdk.demo.alx;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rixengine.api.AlxAdParam;
import com.rixengine.api.nativead.AlxMediaContent;
import com.rixengine.api.nativead.AlxMediaView;
import com.rixengine.api.nativead.AlxNativeAd;
import com.rixengine.api.nativead.AlxNativeAdCreativeType;
import com.rixengine.api.nativead.AlxNativeAdLoadedListener;
import com.rixengine.api.nativead.AlxNativeAdLoader;
import com.rixengine.api.nativead.AlxNativeAdView;
import com.rixengine.api.nativead.AlxNativeEventListener;
import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.R;
import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * native Ad
 */
public class NativeActivity extends BaseActivity implements View.OnClickListener{
    private final String TAG = "AlxNativeActivity";

    private TextView mAdInfo;
    private TextView mTvClearLog;
    private TextView mTvShowLog;
    private TextView mTvShow;
    private FrameLayout mAdContainerView;
    private AlxNativeAd mNativeAd;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);
        setActionBar();
        initView();
    }

    private void initView() {
        TextView tv_load = findViewById(R.id.tv_load);
        mTvShow = findViewById(R.id.tv_show);
        mAdInfo = (TextView) findViewById(R.id.ad_info);
        mTvClearLog = (TextView) findViewById(R.id.tv_clear_log);
        mTvShowLog = (TextView) findViewById(R.id.tv_show_log);
        mAdContainerView = (FrameLayout) findViewById(R.id.ad_container);
        mTvShow.setEnabled(false);

        mTvShowLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTvClearLog.setOnClickListener(this);
        tv_load.setOnClickListener(this);
        mTvShow.setOnClickListener(this);

        setAdInfo(AdConfig.ALX_NATIVE_AD_ID);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_load) {
            loadAd();
        } else if (v.getId() == R.id.tv_show) {
            showAd();
        } else if (v.getId() == R.id.tv_clear_log) {
            clearLog();
        }
    }



    private void loadAd() {
        showLogMessage(getString(R.string.loading));
        startTime = System.currentTimeMillis();

        Map<String, String> userExtras = new HashMap<>();
        userExtras.put("bid_floor", "1.5");
        AlxAdParam.Builder builder = new AlxAdParam.Builder().setUserExtras(userExtras);
        AlxNativeAdLoader loader = new AlxNativeAdLoader.Builder(this, AdConfig.ALX_NATIVE_AD_ID).build();
        loader.loadAd(builder.build(), new AlxNativeAdLoadedListener() {
            @Override
            public void onAdFailed(int errorCode, String errorMsg) {
                Log.i(TAG, "onAdFailed:" + errorCode + ";" + errorMsg);
                mTvShow.setEnabled(false);
                String msg = "errorCode=" + errorCode + ";errorMsg=" + errorMsg;
                showLogMessage("onAdFailed");
                showLogMessage(getString(R.string.format_load_failed, msg));
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
                mTvShow.setEnabled(true);
                mNativeAd = ads.get(0);
                Log.i(TAG, "price=" + mNativeAd.getPrice());
                showLogMessage("onAdLoaded");
                showLogMessage(getString(R.string.format_load_success, (System.currentTimeMillis() - startTime) / 1000) + "｜ ecpm:" + mNativeAd.getPrice());
                mNativeAd.reportBiddingUrl();
                mNativeAd.reportChargingUrl();

//                showNativeAd();
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
                    showLogMessage("onAdClicked");
                }

                @Override
                public void onAdImpression() {
                    Log.i(TAG, "onAdImpression");
                    showLogMessage("onAdImpression");
                }

                @Override
                public void onAdClosed() {
                    Log.i(TAG, "onAdClosed");
                    showLogMessage("onAdClosed");
                    mNativeAd.destroy();
                    mAdContainerView.removeAllViews();
                }
            });
            mAdContainerView.removeAllViews();
            mAdContainerView.addView(nativeView);
        }
    }

    private View createNativeView(AlxNativeAd nativeAd) {
        int createType = nativeAd.getCreativeType();
        if (createType == AlxNativeAdCreativeType.VIDEO || createType == AlxNativeAdCreativeType.LARGE_IMAGE) { //也可以不共用一个模版
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
                    showLogMessage("onVideoStart");
                }

                @Override
                public void onVideoEnd() {
                    Log.i(TAG, "onVideoEnd");
                    showLogMessage("onVideoEnd");
                }

                @Override
                public void onVideoPlay() {
                    Log.i(TAG, "onVideoPlay");
                    showLogMessage("onVideoPlay");
                }

                @Override
                public void onVideoPause() {
                    Log.i(TAG, "onVideoPause");
                    showLogMessage("onVideoPause");
                }

                @Override
                public void onVideoPlayError(int code, String error) {
                    Log.i(TAG, "onVideoPlayError:" + code + ";" + error);
                    showLogMessage("onVideoPlayError:" + code + ";" + error);
                }

                @Override
                public void onVideoMute(boolean isMute) {
                    Log.i(TAG, "onVideoMute:" + isMute);
                    showLogMessage("onVideoMute:" + isMute);
                }
            });
        }

        // Register a native ad object.
        nativeView.setNativeAd(nativeAd);

        return nativeView;
    }

    private void showAd() {
        if (mNativeAd == null) {
            Toast.makeText(this, getString(R.string.show_ad_no_load), Toast.LENGTH_SHORT).show();
            return;
        }
        showNativeAd();
    }


    private void clearLog() {
        mTvShowLog.setText("");
    }

    private void showLogMessage(String msg) {
        mTvShowLog.append(msg);
        mTvShowLog.append("\r\n");
    }

    private void setAdInfo(String unitId){
        mAdInfo.setText(getString(R.string.format_ad_unitid, getString(R.string.native_ad), unitId));
    }


}