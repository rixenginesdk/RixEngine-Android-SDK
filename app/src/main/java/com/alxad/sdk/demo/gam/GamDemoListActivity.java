package com.alxad.sdk.demo.gam;

import android.os.Bundle;

import com.alxad.sdk.demo.BaseListViewActivity;
import com.alxad.sdk.demo.R;

import java.util.ArrayList;
import java.util.List;

public class GamDemoListActivity extends BaseListViewActivity {

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public List<AdapterData> initAdapterData() {
        List<AdapterData> list = new ArrayList<>();

        AdapterData bannerItem = new AdapterData(getString(R.string.banner_ad), GamBannerActivity.class);
        AdapterData rewardItem = new AdapterData(getString(R.string.reward_ad), GamRewardVideoActivity.class);
        AdapterData interstitialItem = new AdapterData(getString(R.string.interstitial_ad), GamInterstitialActivity.class);
        AdapterData nativeItem = new AdapterData(getString(R.string.native_ad), GamNativeActivity.class);
        list.add(bannerItem);
        list.add(rewardItem);
        list.add(interstitialItem);
        list.add(nativeItem);
        return list;
    }

}