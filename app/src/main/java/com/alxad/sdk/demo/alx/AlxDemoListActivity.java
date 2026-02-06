package com.alxad.sdk.demo.alx;

import android.os.Bundle;

import com.alxad.sdk.demo.BaseListViewActivity;
import com.alxad.sdk.demo.R;

import java.util.ArrayList;
import java.util.List;

public class AlxDemoListActivity extends BaseListViewActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public List<AdapterData> initAdapterData() {
        List<AdapterData> list = new ArrayList<>();
        list.add(new AdapterData(getString(R.string.banner_ad), BannerActivity.class));
        list.add(new AdapterData(getString(R.string.reward_ad), RewardVideoActivity.class));
        list.add(new AdapterData(getString(R.string.interstitial_video_ad), InterstitialVideoActivity.class));
        list.add(new AdapterData(getString(R.string.interstitial_banner_ad), InterstitialBannerActivity.class));
        list.add(new AdapterData(getString(R.string.native_ad), NativeActivity.class));
        return list;
    }

}