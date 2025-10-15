package com.rixengine.tradplus.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rixengine.AppConfig;
import com.rixengine.R;
import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.open.banner.BannerAdListener;
import com.tradplus.ads.open.banner.TPBanner;

public class TradPlusBannerDemoActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "TradPlusBannerDemo";

    private Button mBnLoad;
    private TextView mTvTip;
    private ViewGroup mAdContainer;

    private TPBanner mAdObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tradplus_banner_demo);
        initView();
    }

    private void initView(){
        mBnLoad=(Button)findViewById(R.id.bn_load);
        mTvTip=(TextView)findViewById(R.id.tv_tip);
        mAdContainer=findViewById(R.id.ad_container);
        mBnLoad.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bn_load) {
            bnLoad();
        }
    }

    private void bnLoad(){
        mBnLoad.setEnabled(false);
        mTvTip.setText("loading……");

        mAdObj=new TPBanner(this);

        mAdContainer.removeAllViews();
        mAdContainer.addView(mAdObj);

        mAdObj.setAdListener(new BannerAdListener(){
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {
                Log.d(TAG,"onAdLoaded:"+getThreadName());
                mBnLoad.setEnabled(true);
                mTvTip.setText("");
            }

            @Override
            public void onAdLoadFailed(TPAdError tpAdError) {
                Log.d(TAG,"onAdLoadFailed:"+tpAdError.getErrorCode()+"-"+tpAdError.getErrorMsg()+getThreadName());
                mBnLoad.setEnabled(true);
                mTvTip.setText("load err:"+tpAdError.getErrorCode()+"-"+tpAdError.getErrorMsg());
                Toast.makeText(getBaseContext(), "banner广告加载失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.d(TAG,"onAdClicked:"+getThreadName());
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.d(TAG,"onAdImpression:"+getThreadName());
            }

            @Override
            public void onAdShowFailed(TPAdError tpAdError, TPAdInfo tpAdInfo) {
                Log.d(TAG,"onAdShowFailed:"+tpAdError.getErrorCode()+"-"+tpAdError.getErrorMsg()+getThreadName());
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.d(TAG,"onAdClosed:"+getThreadName());
            }

            @Override
            public void onBannerRefreshed() {
                Log.d(TAG,"onBannerRefreshed:"+getThreadName());
            }
        });
        mAdObj.loadAd(AppConfig.TRAD_PLUS_BANNER_AD);
    }


    private String getThreadName(){
        return Thread.currentThread().getName();
    }

}