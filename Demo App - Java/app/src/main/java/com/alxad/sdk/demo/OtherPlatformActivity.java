package com.alxad.sdk.demo;

import android.os.Bundle;

import com.alxad.sdk.demo.admob.AdmobDemoListActivity;
import com.alxad.sdk.demo.gam.GamDemoListActivity;
import com.alxad.sdk.demo.ironsource.IronSourceDemoListActivity;
import com.alxad.sdk.demo.max.MaxDemoListActivity;
import com.alxad.sdk.demo.topon.TopOnDemoListActivity;
import com.alxad.sdk.demo.tradplus.TradPlusDemoListActivity;

import java.util.ArrayList;
import java.util.List;

public class OtherPlatformActivity extends BaseListViewActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public List<AdapterData> initAdapterData() {
        List<AdapterData> list = new ArrayList<>();

        list.add(new AdapterData("TopOn AD Demo", TopOnDemoListActivity.class));
        list.add(new AdapterData("Admob AD Demo", AdmobDemoListActivity.class));
        list.add(new AdapterData("Gam AD Demo", GamDemoListActivity.class));
        list.add(new AdapterData("TradPlus AD Demo", TradPlusDemoListActivity.class));
        list.add(new AdapterData("LevelPlay AD Demo", IronSourceDemoListActivity.class));
        list.add(new AdapterData("Max AD Demo", MaxDemoListActivity.class));

        return list;
    }
}
