package com.alxad.sdk.demo.alx;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alxad.sdk.demo.BaseListViewActivity;
import com.alxad.sdk.demo.R;
import com.rixengine.api.AlxAdSDK;

import java.util.ArrayList;
import java.util.List;

public class AlxDemoListActivity extends BaseListViewActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addBottomLayout();
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

    private void addBottomLayout() {
        View convertView = LayoutInflater.from(this).inflate(R.layout.layout_bottom_content, null);
        TextView tvContent = (TextView) convertView.findViewById(R.id.bottom_tv_content);
        String sb = "SDK Name: " +
                AlxAdSDK.getNetWorkName() +
                "\r\n" +
                "SDK Version: " +
                AlxAdSDK.getNetWorkVersion();
        tvContent.setText(sb);
        mListView.addFooterView(convertView);
    }

}