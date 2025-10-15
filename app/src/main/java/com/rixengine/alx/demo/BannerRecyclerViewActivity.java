package com.rixengine.alx.demo;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rixengine.AppConfig;
import com.rixengine.R;
import com.rixengine.api.AlxBannerView;
import com.rixengine.api.AlxBannerViewAdListener;

import java.util.ArrayList;
import java.util.List;

/**
 * banner广告——在RecyclerView中显示
 */
public class BannerRecyclerViewActivity extends AppCompatActivity {
    private final String TAG = "AlxBannerRecyclerDemo";

    private MyAdapter mAdapter;
    private AlxBannerView mBannerAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_recyclerview);
        initView();
        loadAd();
    }

    private void initView() {
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration dividerItem = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItem.setDrawable(getResources().getDrawable(R.drawable.divider_white));
        mRecyclerView.addItemDecoration(dividerItem);

        mAdapter = new MyAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
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
                Log.d(TAG, "onAdError banner Ad load fail :  errorMsg:" + errorMsg + "  errorCode:" + errorCode + ":" + getThreadName());
                Toast.makeText(getBaseContext(), "Banner load fail " , Toast.LENGTH_SHORT).show();
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

    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
        public long getItemId(int position) {
            return position;
        }


        @Override
        public int getItemCount() {
            return (mList == null || mList.isEmpty()) ? 0 : mList.size();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case ITEM_VIEW_TYPE_BANNER_AD:
                    view = LayoutInflater.from(mContext).inflate(R.layout.adapter_banner_ad, parent, false);
                    return new BannerViewAdHolder(view);
                default:
                    view = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_data, parent, false);
                    return new NormalViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int viewType = getItemViewType(position);
            switch (viewType) {
                case ITEM_VIEW_TYPE_BANNER_AD:
                    if (holder instanceof BannerViewAdHolder) {
                        getBannerViewAd((BannerViewAdHolder) holder, position);
                    }
                    break;
                case ITEM_VIEW_TYPE_NORMAL:
                default:
                    if (holder instanceof NormalViewHolder) {
                        getNormalView((NormalViewHolder) holder, position);
                    }
            }
        }

        @Override
        public int getItemViewType(int position) {
            AlxBannerView bean = mList.get(position);
            if (bean == null) {
                return ITEM_VIEW_TYPE_NORMAL;
            } else {
                return ITEM_VIEW_TYPE_BANNER_AD;
            }
        }


        /**
         * banner广告
         *
         * @param holder
         * @param position
         * @return
         */
        private void getBannerViewAd(final BannerViewAdHolder holder, final int position) {
            AlxBannerView bean = mList.get(position);
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
        }

        private void getNormalView(NormalViewHolder holder, int position) {
            holder.mTvTitle.setText("banner test  -RecyclerView:  " + position);
            holder.mTvDesc.setText("banner test -RecyclerView:  " + position);
        }
    }

    private static class BannerViewAdHolder extends RecyclerView.ViewHolder {
        FrameLayout mAdContainerView;

        public BannerViewAdHolder(View itemView) {
            super(itemView);
            mAdContainerView = (FrameLayout) itemView.findViewById(R.id.item_container);
        }
    }

    private static class NormalViewHolder extends RecyclerView.ViewHolder {
        ImageView mIvIcon;
        TextView mTvTitle;
        TextView mTvDesc;

        public NormalViewHolder(@NonNull View itemView) {
            super(itemView);
            mIvIcon = (ImageView) itemView.findViewById(R.id.item_icon);
            mTvTitle = (TextView) itemView.findViewById(R.id.item_title);
            mTvDesc = (TextView) itemView.findViewById(R.id.item_desc);
        }
    }

}