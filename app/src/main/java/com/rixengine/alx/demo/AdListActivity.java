package com.rixengine.alx.demo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.rixengine.R;


public class AdListActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_list);
        initView();
    }

    private void initView() {
        TextView tv_banner = findViewById(R.id.tv_banner);
        TextView tv_banner_listview=findViewById(R.id.tv_banner_list_view);
        TextView tv_banner_recyclerview=findViewById(R.id.tv_banner_recycler_view);
        TextView tv_video_ad = findViewById(R.id.tv_video_ad);
        TextView tv_interstitial_ad=findViewById(R.id.tv_interstitial_ad);
        TextView tv_native_ad=findViewById(R.id.tv_native_ad);
        tv_banner.setOnClickListener(this);
        tv_banner_listview.setOnClickListener(this);
        tv_banner_recyclerview.setOnClickListener(this);
        tv_video_ad.setOnClickListener(this);
        tv_interstitial_ad.setOnClickListener(this);
        tv_native_ad.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_banner:
                startActivity(new Intent(this, BannerActivity.class));
                break;
            case R.id.tv_banner_list_view:
                startActivity(new Intent(this, BannerListViewActivity.class));
                break;
            case R.id.tv_banner_recycler_view:
                startActivity(new Intent(this, BannerRecyclerViewActivity.class));
                break;
            case R.id.tv_video_ad:
                startActivity(new Intent(this, RewardVideoActivity.class));
                break;
            case R.id.tv_interstitial_ad:
                startActivity(new Intent(this, InterstitialActivity.class));
                break;
            case R.id.tv_native_ad:
                startActivity(new Intent(this, NativeListActivity.class));
                break;
        }
    }
}