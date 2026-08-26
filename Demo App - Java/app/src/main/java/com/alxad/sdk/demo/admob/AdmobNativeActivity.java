package com.alxad.sdk.demo.admob;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.R;


public class AdmobNativeActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "AdmobNativeActivity";

    private FrameLayout mAdContainerView;
    private View mBnLoad;
    private TextView mTvClearLog;
    private TextView mTvShowLog;
    private long mStartTime;

    private AdLoader mAdLoader;
    private NativeAd mNativeAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_ads);
        setActionBar();
        initView();
    }

    private void initView() {
        mAdContainerView = (FrameLayout) findViewById(R.id.ad_container);
        mTvClearLog = (TextView) findViewById(R.id.tv_clear_log);
        mTvShowLog = (TextView) findViewById(R.id.tv_show_log);
        mBnLoad = findViewById(R.id.bn_load);

        mTvShowLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTvClearLog.setOnClickListener(this);
        mBnLoad.setOnClickListener(this);
    }

    private void loadAd() {
        showLogMessage(getString(R.string.loading));
        mBnLoad.setEnabled(false);
        mStartTime = System.currentTimeMillis();
        mAdLoader = new AdLoader.Builder(this, AdConfig.ADMOB_NATIVE_ID)
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        Log.d(TAG, "onNativeAdLoaded：" + getCurrentThreadName());
                        if (mAdLoader != null && mAdLoader.isLoading()) {
                            return;
                        }
                        if (isDestroyed()) {
                            nativeAd.destroy();
                            return;
                        }
                        mNativeAd = nativeAd;
                        showLogMessage("onNativeAdLoaded");

                        NativeAdView adView = renderNativeAdView(nativeAd);
                        mAdContainerView.removeAllViews();
                        mAdContainerView.addView(adView);
                    }
                })
                .withAdListener(new AdListener() {

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mBnLoad.setEnabled(true);
                        String msg = "errorCode=" + loadAdError.getCode() + ";errorMsg=" + loadAdError.getMessage();
                        Log.d(TAG, "onAdFailedToLoad:" + msg);
                        showLogMessage("onAdFailedToLoad");
                        showLogMessage(getString(R.string.format_load_failed, msg));
                    }

                    @Override
                    public void onAdClosed() {
                        Log.d(TAG, "onAdClosed:" + getCurrentThreadName());
                        showLogMessage("onAdClosed");
                        doCloseAd();
                    }

                    @Override
                    public void onAdOpened() {
                        Log.d(TAG, "onAdOpened");
                        showLogMessage("onAdOpened");
                    }

                    @Override
                    public void onAdLoaded() {
                        Log.d(TAG, "onAdLoaded");
                        mBnLoad.setEnabled(true);
                        showLogMessage("onAdLoaded");
                        showLogMessage(getString(R.string.format_load_success, (System.currentTimeMillis() - mStartTime) / 1000));
                    }

                    @Override
                    public void onAdClicked() {
                        Log.d(TAG, "onAdClicked");
                        showLogMessage("onAdClicked");
                    }

                    @Override
                    public void onAdImpression() {
                        Log.d(TAG, "onAdImpression");
                        showLogMessage("onAdImpression");
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder().build())
                .build();
        mAdLoader.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bn_load) {
            loadAd();
        } else if (v.getId() == R.id.tv_clear_log) {
            clearLog();
        }
    }

    private void doCloseAd() {
        mAdContainerView.removeAllViews();
        if (mNativeAd != null) {
            mNativeAd.destroy();
        }
    }

    /**
     * Self-rendering AD
     *
     * @return
     */
    private NativeAdView renderNativeAdView(NativeAd bean) {
        NativeAdView adView = (NativeAdView) getLayoutInflater().inflate(R.layout.admob_native_custom_ad_view, null);
        TextView tvAdvertiser = (TextView) adView.findViewById(R.id.ad_advertiser);
        ImageView ivIcon = (ImageView) adView.findViewById(R.id.iv_ad_icon);
        TextView tvTitle = (TextView) adView.findViewById(R.id.tv_ad_title);
        TextView tvDescription = (TextView) adView.findViewById(R.id.tv_ad_desc);
        Button bnCallToAction = (Button) adView.findViewById(R.id.ad_call_to_action);
        ImageView ivClose = (ImageView) adView.findViewById(R.id.ad_close);
        MediaView ivMainImg = (MediaView) adView.findViewById(R.id.iv_image);

        adView.setHeadlineView(tvTitle);
        adView.setBodyView(tvDescription);
        adView.setIconView(ivIcon);
//        adView.setImageView(ivMainImg);
        adView.setCallToActionView(bnCallToAction);
        adView.setAdvertiserView(tvAdvertiser);
        adView.setMediaView(ivMainImg);

        tvTitle.setText(bean.getHeadline());
        tvDescription.setText(bean.getBody());
        bnCallToAction.setText(bean.getCallToAction());
        tvAdvertiser.setText(bean.getAdvertiser());
        ivMainImg.setMediaContent(bean.getMediaContent());

//        List<NativeAd.Image> imageList = bean.getImages();
//        if (imageList != null && imageList.size() > 0) {
//            NativeAd.Image image = imageList.get(0);
//            if (image != null && image.getUri() != null) {
//                Glide.with(this).load(image.getUri()).into(ivMainImg);
//            }
//        }

        if (bean.getIcon() != null && bean.getIcon().getUri() != null) {
            Glide.with(this).load(bean.getIcon().getUri()).into(ivIcon);
        }

        adView.setNativeAd(bean);//这句很重要。如果去掉了，点击就没有反应

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCloseAd();
            }
        });
        return adView;
    }

    private void clearLog() {
        mTvShowLog.setText("");
    }

    private void showLogMessage(String msg) {
        mTvShowLog.append(msg);
        mTvShowLog.append("\r\n");
    }

}