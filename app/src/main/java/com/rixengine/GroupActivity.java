package com.rixengine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.rixengine.admob.demo.AdmobDemoListActivity;
import com.rixengine.ironsource.demo.IronSourceDemoListActivity;
import com.rixengine.max.demo.MaxDemoListActivity;
import com.rixengine.topon.demo.TopOnAdDemoListActivity;
import com.rixengine.tradplus.demo.TradPlusDemoListActivity;


public class GroupActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "GroupActivity";

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_group);
        initView();
    }

    private void initView() {
        TextView tv_top_on_ad = findViewById(R.id.tv_top_on_ad);
        TextView tv_admob_ad = findViewById(R.id.tv_admob_ad);
        TextView tv_trad_plus_ad = findViewById(R.id.tv_tradplus_ad);
        TextView tv_iron_source_ad = findViewById(R.id.tv_ironsource_ad);
        TextView tv_max_ad = findViewById(R.id.tv_max_ad);
        tv_top_on_ad.setOnClickListener(this);
        tv_admob_ad.setOnClickListener(this);
        tv_trad_plus_ad.setOnClickListener(this);
        tv_iron_source_ad.setOnClickListener(this);
        tv_max_ad.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_top_on_ad) {
            startActivity(new Intent(this, TopOnAdDemoListActivity.class));
        } else if (id == R.id.tv_admob_ad) {
            startActivity(new Intent(this, AdmobDemoListActivity.class));
        } else if (id == R.id.tv_tradplus_ad) {
            startActivity(new Intent(this, TradPlusDemoListActivity.class));
        } else if (id == R.id.tv_ironsource_ad) {
            startActivity(new Intent(this, IronSourceDemoListActivity.class));
        } else if (id == R.id.tv_max_ad) {
            startActivity(new Intent(this, MaxDemoListActivity.class));
        }
    }



}