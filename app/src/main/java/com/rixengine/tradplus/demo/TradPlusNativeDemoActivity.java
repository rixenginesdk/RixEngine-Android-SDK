package com.rixengine.tradplus.demo;

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

import androidx.appcompat.app.AppCompatActivity;

import com.rixengine.AppConfig;
import com.rixengine.R;
import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.base.bean.TPBaseAd;
import com.tradplus.ads.open.nativead.NativeAdListener;
import com.tradplus.ads.open.nativead.TPNative;
import com.tradplus.ads.open.nativead.TPNativeAdRender;


public class TradPlusNativeDemoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TradPlusNativeDemo";

    private TextView mTvTip;
    private ViewGroup mAdContainer;
    private long startTime;

    private TPNative mAdObj;
    private boolean isReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tradplus_native_demo);
        initView();
    }

    private void initView() {
        TextView tv_video_load = findViewById(R.id.tv_video_load);
        mTvTip = findViewById(R.id.tv_tip);
        mAdContainer = findViewById(R.id.ad_container);
        tv_video_load.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_video_load) {
            loadAd();
        }
    }

    /**
     * 加载广告
     */
    public void loadAd() {
        mTvTip.setText("The ad loading...");
        isReady = false;
        startTime = System.currentTimeMillis();

        mAdObj = new TPNative(this, AppConfig.TRAD_PLUS_NATIVE_AD);
        mAdObj.setAdListener(new NativeAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo, TPBaseAd tpBaseAd) {
                Log.i(TAG, "onAdLoaded:" + getThreadName());
                Toast.makeText(getBaseContext(), "Native AD load success", Toast.LENGTH_SHORT).show();
                mTvTip.setText("Native AD load success--Consume time--" + (System.currentTimeMillis() - startTime) / 1000 + "-秒");
                isReady = true;

                //以下两种方式任选其一都可以
//                mAdObj.showAd(mAdContainer,R.layout.tp_native_ad_list_item,null);
                mAdObj.showAd(mAdContainer, new CustomAdRender(TradPlusNativeDemoActivity.this), "");
            }

            @Override
            public void onAdLoadFailed(TPAdError tpAdError) {
                Log.i(TAG, "onAdLoadFailed： " + tpAdError.getErrorCode() + " " + tpAdError.getErrorMsg() + ";" + getThreadName());
                Toast.makeText(getBaseContext(), "Native AD loads fail", Toast.LENGTH_SHORT).show();
                mTvTip.setText("Native AD loads fai");
                isReady = false;
            }

            @Override
            public void onAdShowFailed(TPAdError tpAdError, TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdShowFailed： " + tpAdError.getErrorCode() + " " + tpAdError.getErrorMsg() + ";" + getThreadName());
                Toast.makeText(getBaseContext(), "onAdShowFailed：", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.i(TAG, "onAdClicked:" + getThreadName());
                Toast.makeText(getBaseContext(), "onAdClicked", Toast.LENGTH_SHORT).show();
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
            ViewGroup view = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.adapter_tradplus_native_item, null);

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