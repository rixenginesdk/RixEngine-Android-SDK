package com.alxad.sdk.demo.tradplus;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alxad.sdk.demo.AdConfig;
import com.alxad.sdk.demo.BaseActivity;
import com.alxad.sdk.demo.R;
import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.base.bean.TPBaseAd;
import com.tradplus.ads.open.nativead.NativeAdListener;
import com.tradplus.ads.open.nativead.TPNative;
import com.tradplus.ads.open.nativead.TPNativeAdRender;


public class TradPlusNativeActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "TradPlusNativeDemo";

    private FrameLayout mAdContainerView;
    private View mBnLoad;
    private TextView mTvTip;
    private long startTime;

    private TPNative mAdObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_ads);
        setActionBar();
        initView();
    }

    private void initView() {
        mAdContainerView = (FrameLayout) findViewById(R.id.ad_container);
        mTvTip = findViewById(R.id.tv_tip);
        mBnLoad = findViewById(R.id.bn_load);
        mBnLoad.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bn_load) {
            loadAd();
        }
    }

    /**
     * 加载广告
     */
    public void loadAd() {
        mTvTip.setText(R.string.loading);
        mBnLoad.setEnabled(false);
        startTime = System.currentTimeMillis();

        mAdObj = new TPNative(this, AdConfig.TRAD_PLUS_NATIVE_AD);
        mAdObj.setAdListener(new NativeAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo, TPBaseAd tpBaseAd) {
                Log.i(TAG, "onAdLoaded:" + getThreadName());
                mBnLoad.setEnabled(true);
                Toast.makeText(getBaseContext(), getString(R.string.load_success), Toast.LENGTH_SHORT).show();
                mTvTip.setText(getString(R.string.format_load_success, (System.currentTimeMillis() - startTime) / 1000));

                //以下两种方式任选其一都可以
//                mAdObj.showAd(mAdContainer,R.layout.tp_native_ad_list_item,null);
                mAdObj.showAd(mAdContainerView, new CustomAdRender(TradPlusNativeActivity.this), "");
            }

            @Override
            public void onAdLoadFailed(TPAdError tpAdError) {
                Log.i(TAG, "onAdLoadFailed：" + tpAdError.getErrorCode() + " " + tpAdError.getErrorMsg() + ";" + getThreadName());
                mBnLoad.setEnabled(true);
                Toast.makeText(getBaseContext(), getString(R.string.load_failed), Toast.LENGTH_SHORT).show();
                mTvTip.setText(R.string.load_failed);
            }

            @Override
            public void onAdShowFailed(TPAdError tpAdError, TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdShowFailed： " + tpAdError.getErrorCode() + " " + tpAdError.getErrorMsg() + ";" + getThreadName());
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClicked:" + getThreadName());
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdImpression:" + getThreadName());
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClosed:" + getThreadName());
            }
        });
        mAdObj.loadAd();
    }

    private String getThreadName() {
        return Thread.currentThread().getName();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdObj != null) {
            mAdObj.onDestroy();
        }
    }

    private class CustomAdRender extends TPNativeAdRender {
        private Context context;

        public CustomAdRender(Context context) {
            this.context = context;
        }

        @Override
        public ViewGroup createAdLayoutView() {
            ViewGroup view = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.tradplus_native_custom_ad_view, null);

            TextView titleView = (TextView) view.findViewById(R.id.native_title);
            setTitleView(titleView, true);

            TextView description = (TextView) view.findViewById(R.id.native_description);
            setSubTitleView(description, true);

            ImageView iconView = (ImageView) view.findViewById(R.id.native_icon);
            setIconView(iconView, true);

            ImageView mainView = (ImageView) view.findViewById(R.id.native_image);
            setImageView(mainView, true);

            TextView callToActionView = (TextView) view.findViewById(R.id.native_source);
            setCallToActionView(callToActionView, true);

            FrameLayout adChoiceView = (FrameLayout) view.findViewById(R.id.native_choice_container);
            setAdChoicesContainer(adChoiceView, false);

//            ImageView adChoice=(ImageView)view.findViewById(R.id.native_choice);
//            setAdChoiceView(adChoice,true);

            return view;
        }

    }

}