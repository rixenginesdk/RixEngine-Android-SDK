package com.rixengine.alx.demo;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rixengine.AppConfig;
import com.rixengine.R;
import com.rixengine.api.AlxBannerView;
import com.rixengine.api.AlxBannerViewAdListener;

import java.util.ArrayList;
import java.util.List;


/**
 * banner广告——在ListView 中显示
 */
public class BannerListViewActivity extends AppCompatActivity {
    private final String TAG = "AlxBannerListDemo";

    private MyAdapter mAdapter;
    private AlxBannerView mBannerAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_listview);
        initView();
        initData();
        loadAd();
    }

    private void initView() {
        ListView mListView = (ListView) findViewById(R.id.listview);
        mAdapter = new MyAdapter(this);
        mListView.setAdapter(mAdapter);
    }

    private void initData() {
        Intent intent = getIntent();
    }

    private void loadAd() {
        mBannerAd = new AlxBannerView(this);
        mBannerAd.setBannerCanClose(true);
        mBannerAd.loadAd(AppConfig.ALX_BANNER_AD_PID,new AlxBannerViewAdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(TAG, "Banner Ad load success:" + getThreadName());
                List<AlxBannerView> list = new ArrayList<>();
                for (int i = 0; i < 12; i++) {
                    list.add(null);
                }
                list.add(mBannerAd);
                for (int i = 0; i < 12; i++) {
                    list.add(null);
                }
                mAdapter.setRefreshData(list);
            }

            @Override
            public void onAdError(int errorCode, String errorMsg) {
                Log.d(TAG, "onAdError   errorMsg:" + errorMsg + "  errorCode:" + errorCode + ":" + getThreadName());
                Toast.makeText(getBaseContext(), "Banner AD load fail", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClicked() {
                Log.d(TAG, "onAdClicked :" + getThreadName());
            }

            @Override
            public void onAdShow() {
                Log.d(TAG, "onAdShow :" + getThreadName());
            }

            @Override
            public void onAdClose() {
                Log.d(TAG, "onAdClose:" + getThreadName());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBannerAd != null) {
            mBannerAd.destroy();
        }
    }

    private String getThreadName() {
        return Thread.currentThread().getName();
    }

    private class MyAdapter extends BaseAdapter {

        private static final int ITEM_VIEW_TYPE_NORMAL = 0;
        private static final int ITEM_VIEW_TYPE_BANNER_AD = 1;

        private Context mContext;
        private List<AlxBannerView> mList;

        public MyAdapter(Context context) {
            mContext = context;
        }

        public void setRefreshData(List<AlxBannerView> list) {
            if (list == null) {
                if (mList == null) {
                    mList = new ArrayList<>();
                } else {
                    mList.clear();
                }
                notifyDataSetChanged();
                return;
            }
            if (mList == null) {
                mList = new ArrayList<>();
            }
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }


        @Override
        public int getCount() {
            return (mList == null || mList.isEmpty()) ? 0 : mList.size(); // for test
        }

        @Override
        public AlxBannerView getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            AlxBannerView bean = getItem(position);
            if (bean == null) {
                return ITEM_VIEW_TYPE_NORMAL;
            } else {
                return ITEM_VIEW_TYPE_BANNER_AD;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            switch (getItemViewType(position)) {
                case ITEM_VIEW_TYPE_BANNER_AD:
                    return getBannerViewAd(convertView, parent, position);
                default:
                    return getNormalView(convertView, parent, position);
            }
        }

        /**
         * 模版广告
         *
         * @param convertView
         * @param parent
         * @param position
         * @return
         */
        private View getBannerViewAd(View convertView, ViewGroup parent, final int position) {
            final AdExpressViewHolder holder;
            if (convertView == null) {
                holder = new AdExpressViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_banner_ad, parent, false);
                holder.mAdContainerView = (FrameLayout) convertView.findViewById(R.id.item_container);
                convertView.setTag(holder);
            } else {
                holder = (AdExpressViewHolder) convertView.getTag();
            }
            final AlxBannerView bean = getItem(position);


            if (holder.mAdContainerView != null && bean != null) {
                if (bean.getParent() != null && bean.getParent() instanceof ViewGroup) {
                    ViewGroup groupView = (ViewGroup) bean.getParent();
                    groupView.removeAllViews();
                }
                if (bean.getParent() == null) {
                    holder.mAdContainerView.removeAllViews();
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    holder.mAdContainerView.addView(bean, layoutParams);
                }
            }
            return convertView;
        }

        private View getNormalView(View convertView, ViewGroup parent, int position) {
            NormalViewHolder holder;
            if (convertView == null) {
                holder = new NormalViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_data, parent, false);
                holder.mIvIcon = (ImageView) convertView.findViewById(R.id.item_icon);
                holder.mTvTitle = (TextView) convertView.findViewById(R.id.item_title);
                holder.mTvDesc = (TextView) convertView.findViewById(R.id.item_desc);
                convertView.setTag(holder);
            } else {
                holder = (NormalViewHolder) convertView.getTag();
            }
            holder.mTvTitle.setText("banner test-ListView:  " + position);
            holder.mTvDesc.setText("banner test-ListView:  " + position);
            return convertView;
        }
    }

    private static class AdExpressViewHolder {
        FrameLayout mAdContainerView;
    }

    private static class NormalViewHolder {
        ImageView mIvIcon;
        TextView mTvTitle;
        TextView mTvDesc;
    }

}