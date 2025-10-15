package com.rixengine.ironsource.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ironsource.mediationsdk.IronSource;
import com.rixengine.AppConfig;

import java.util.ArrayList;
import java.util.List;

public class IronSourceDemoListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_iron_source_demo_list);
        ListView listView=new ListView(this);
        listView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(listView);
        mAdapter= new MyAdapter(this,initData());
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);

        adInit();
    }

    private List<AdapterData> initData() {
        List<AdapterData> list = new ArrayList<>();
        IronSourceDemoListActivity.AdapterData bannerItem = new IronSourceDemoListActivity.AdapterData("Banner AD", IronSourceBannerActivity.class);
        AdapterData rewardItem = new AdapterData("Reward AD", IronSourceRewardedVideoActivity.class);
        AdapterData interstitialItem = new AdapterData("Interstitial AD", IronSourceInterstitialActivity.class);

       // IronSourceDemoListActivity.AdapterData nativeItem = new IronSourceDemoListActivity.AdapterData("native 广告", MoPubNativeActivity.class);
        list.add(bannerItem);
        list.add(rewardItem);
        list.add(interstitialItem);
        //list.add(nativeItem);
        return list;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AdapterData item=mAdapter.getItem(position);
        Intent intent=new Intent(this,item.jumpActivity);
        startActivity(intent);
    }

    private class AdapterData {
        public String name;
        public Class jumpActivity;

        public AdapterData(String name, Class jumpActivity) {
            this.name = name;
            this.jumpActivity = jumpActivity;
        }
    }

    private class MyAdapter extends BaseAdapter {
        private Context mContext;
        private List<AdapterData> mList;
        private LayoutInflater mInflater;

        public MyAdapter(Context context, List<AdapterData> list) {
            this.mContext = context;
            this.mList = list;
            this.mInflater = LayoutInflater.from(context);
        }


        @Override
        public int getCount() {
            return (mList == null || mList.isEmpty()) ? 0 : mList.size();
        }

        @Override
        public AdapterData getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView == null) {
                convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            }
            textView = (TextView) convertView;

           AdapterData item = getItem(position);
            textView.setText(item.name);
            return convertView;
        }

    }


    //广告配置
    private void adInit() {
        //IronSource初始化
        IronSource.init(this, AppConfig.IRON_SOURCE_APP_KEY,
                IronSource.AD_UNIT.OFFERWALL,
                IronSource.AD_UNIT.INTERSTITIAL,
                IronSource.AD_UNIT.REWARDED_VIDEO,
                IronSource.AD_UNIT.BANNER);
    }

}