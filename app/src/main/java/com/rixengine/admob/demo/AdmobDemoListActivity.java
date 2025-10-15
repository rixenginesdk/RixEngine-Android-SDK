package com.rixengine.admob.demo;

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

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.List;

public class AdmobDemoListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private MyAdapter mAdapter;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        //Google Ad Init
        //MobileAds.initialize(this);
        ListView listView = new ListView(this);
        listView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(listView);


        mAdapter = new MyAdapter(this, initData());

        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);
        initSdk();
    }

    private List<AdapterData> initData() {
        List<AdapterData> list = new ArrayList<>();

        AdapterData bannerItem = new AdapterData("Banner AD", AdmobBannerActivity.class);
        AdapterData rewardItem = new AdapterData("Reward AD", AdmobRewardVideoActivity.class);
        AdapterData interstitialItem = new AdapterData("Interstitial AD", AdmobInterstitialActivity.class);
        AdapterData nativeItem = new AdapterData("Native AD", AdmobNativeActivity.class);
        list.add(bannerItem);
        list.add(rewardItem);
        list.add(interstitialItem);
        list.add(nativeItem);
        return list;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AdapterData item = mAdapter.getItem(position);
        Intent intent = new Intent(this, item.jumpActivity);
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
    private void initSdk(){
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
    }

}