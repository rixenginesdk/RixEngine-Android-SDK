package com.rixengine.admob.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.rixengine.AppConfig;
import com.rixengine.R;

import java.util.List;


public class AdmobNativeActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "AdmobNativeActivity";

    private TextView mTvLoad;
    private TextView mTvTip;
    private FrameLayout mAdContainer;
    private long mStartTime;

    private AdLoader mAdLoader;
    private NativeAd mNativeAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admob_native_demo);
        initView();
    }

    private void initView() {
        mTvLoad = (TextView) findViewById(R.id.tv_load);
        mTvTip = (TextView) findViewById(R.id.tv_tip);
        mAdContainer = (FrameLayout) findViewById(R.id.ad_container);

        mTvLoad.setOnClickListener(this);
    }

    private void loadAd() {
        mTvTip.setText("The ad is loading...");
        mTvLoad.setEnabled(false);
        mStartTime = System.currentTimeMillis();
        mAdLoader = new AdLoader.Builder(this, AppConfig.ADMOB_NATIVE_ID)
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        Log.d(TAG, "Native AD load success" + getThreadName());
                        mTvLoad.setEnabled(true);
                        mTvTip.setText("Native AD loads success --Consume time-" + (System.currentTimeMillis() - mStartTime) / 1000 + "-秒");

                        if (mAdLoader != null && mAdLoader.isLoading()) {
                            return;
                        }
                        if (isDestroyed()) {
                            nativeAd.destroy();
                            return;
                        }
                        mNativeAd = nativeAd;

                        NativeAdView adView = renderNativeAdView(nativeAd);
//                        View adView=renderExpressNativeAdView(nativeAd);
                        mAdContainer.removeAllViews();
                        mAdContainer.addView(adView);
                    }
                })
                .withAdListener(new AdListener() {

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        mTvLoad.setEnabled(true);
                        mTvTip.setText("Native Ad load fail --Consume time-" + (System.currentTimeMillis() - mStartTime) / 1000 + "-秒\r\n失败原因:" + loadAdError.getMessage());
                        Log.d(TAG, "onAdFailedToLoad: Failed: " + loadAdError.getMessage());
                    }

                    @Override
                    public void onAdClosed() {
                        Log.d(TAG, "onAdClosed:" + Thread.currentThread().getName());
                        doCloseAd();
                    }

                    @Override
                    public void onAdOpened() {
                        Log.d(TAG, "onAdOpened");
                    }

                    @Override
                    public void onAdLoaded() {
                        Log.d(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdClicked() {
                        Log.d(TAG, "onAdClicked");
                    }

                    @Override
                    public void onAdImpression() {
                        Log.d(TAG, "onAdImpression");
                    }
                })
                .withNativeAdOptions(new com.google.android.gms.ads.nativead.NativeAdOptions.Builder().build())
                .build();
        mAdLoader.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_load:
                loadAd();
                break;
        }
    }

    private void doCloseAd() {
        mTvTip.setText("Ads close - please reload");
        mAdContainer.removeAllViews();
        if (mNativeAd != null) {
            mNativeAd.destroy();
        }
    }

    /**
     * 自渲染广告
     *
     * @return
     */
    private NativeAdView renderNativeAdView(NativeAd bean) {
        NativeAdView adView = (NativeAdView) getLayoutInflater().inflate(R.layout.adapter_admob_native_item, null);
        TextView tvAdvertiser = (TextView) adView.findViewById(R.id.ad_advertiser);
        ImageView ivIcon = (ImageView) adView.findViewById(R.id.iv_ad_icon);
        TextView tvTitle = (TextView) adView.findViewById(R.id.tv_ad_title);
        TextView tvDescription = (TextView) adView.findViewById(R.id.tv_ad_desc);
        Button bnSource = (Button) adView.findViewById(R.id.adapter_ad_source);
        ImageView ivClose = (ImageView) adView.findViewById(R.id.adapter_ad_close);
        ImageView ivMainImg = (ImageView) adView.findViewById(R.id.iv_image);

        tvTitle.setText(bean.getHeadline());
        tvDescription.setText(bean.getBody());
        bnSource.setText(bean.getCallToAction());
        tvAdvertiser.setText(bean.getAdvertiser());

        List<NativeAd.Image> imageList = bean.getImages();
        if (imageList != null && imageList.size() > 0) {
            NativeAd.Image image = imageList.get(0);
            if (image != null && image.getUri() != null) {
                Glide.with(this).load(image.getUri()).into(ivMainImg);
            }
        }

        if (bean.getIcon() != null && bean.getIcon().getUri() != null) {
            Glide.with(this).load(bean.getIcon().getUri()).into(ivIcon);
        }

        adView.setHeadlineView(tvTitle);
        adView.setBodyView(tvDescription);
        adView.setIconView(ivIcon);
        adView.setImageView(ivMainImg);
        adView.setCallToActionView(bnSource);
        adView.setAdvertiserView(tvAdvertiser);

        adView.setNativeAd(bean);//这句很重要。如果去掉了，点击就没有反应

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCloseAd();
            }
        });
        return adView;
    }

    private String getThreadName() {
        return Thread.currentThread().getName();
    }

    /**
     * 模版广告
     *
     * @return
     */
    private NativeAdView renderExpressNativeAdView(NativeAd bean) {
        NativeAdView adView = (NativeAdView) getLayoutInflater().inflate(R.layout.adapter_admob_native_express, null);
        MediaView mediaView = (MediaView) adView.findViewById(R.id.item_express);

        mediaView.setMediaContent(bean.getMediaContent());
        adView.setMediaView(mediaView);

        adView.setNativeAd(bean);

        return adView;
    }

}