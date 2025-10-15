package com.rixengine.alx.demo;


import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rixengine.AppConfig;
import com.rixengine.R;
import com.rixengine.api.AlxAdParam;
import com.rixengine.api.nativead.AlxMediaContent;
import com.rixengine.api.nativead.AlxMediaView;
import com.rixengine.api.nativead.AlxNativeAd;
import com.rixengine.api.nativead.AlxNativeAdLoadedListener;
import com.rixengine.api.nativead.AlxNativeAdLoader;
import com.rixengine.api.nativead.AlxNativeAdView;
import com.rixengine.api.nativead.AlxNativeEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * native Ad—— Display in RecyclerView
 */
public class NativeRecyclerViewActivity extends AppCompatActivity {
    private final String TAG = "AlxNativeRecyclerDemo";

    private MyAdapter mAdapter;

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
        AlxNativeAdLoader loader = new AlxNativeAdLoader.Builder(this, AppConfig.ALX_NATIVE_AD_PID).build();
        loader.loadAd(new AlxAdParam.Builder().build(), new AlxNativeAdLoadedListener() {
            @Override
            public void onAdFailed(int errorCode, String errorMsg) {
                Log.i(TAG, "onAdFailed:" + errorCode + ";" + errorMsg);
                Toast.makeText(getBaseContext(), "Ad loaded fail", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLoaded(List<AlxNativeAd> ads) {
                Log.i(TAG, "onAdLoaded");

                if (ads == null) {
                    return;
                }
                List<AlxNativeAd> list = new ArrayList<>();
                for (AlxNativeAd item : ads) {
                    for (int i = 0; i < 5; i++) {
                        list.add(null);
                    }
                    list.add(item);
                }
                for (int i = 0; i < 12; i++) {
                    list.add(null);
                }
                mAdapter.setRefreshData(list);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int TYPE_NORMAL = 0;
        private final int TYPE_NATIVE_VIDEO_TEMPLATE = 1;//后期有其他模版素材可以继续添加

        private Context mContext;
        private List<AlxNativeAd> mList;

        public MyAdapter(Context context) {
            mContext = context;
        }

        public void setRefreshData(List<AlxNativeAd> list) {
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
                case TYPE_NATIVE_VIDEO_TEMPLATE:
                    view = LayoutInflater.from(mContext).inflate(R.layout.native_video_template, parent, false);
                    return new NativeVideoTemplateHolder(view);
                default:
                    view = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_data, parent, false);
                    return new NormalViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int viewType = getItemViewType(position);
            switch (viewType) {
                case TYPE_NATIVE_VIDEO_TEMPLATE:
                    if (holder instanceof NativeVideoTemplateHolder) {
                        getNativeVideoTemplate((NativeVideoTemplateHolder) holder, position);
                    }
                    break;
                default:
                    if (holder instanceof NormalViewHolder) {
                        getNormalView((NormalViewHolder) holder, position);
                    }
            }
        }

        @Override
        public int getItemViewType(int position) {
            AlxNativeAd bean = mList.get(position);
            if (bean == null) {
                return TYPE_NORMAL;
            }
            switch (bean.getCreativeType()) {
                case NativeListActivity.NATIVE_AD_CREATE_TYPE_VIDEO: //也可以不共用一个模版
                case NativeListActivity.NATIVE_AD_CREATE_TYPE_LARGE_IMAGE:
                    return TYPE_NATIVE_VIDEO_TEMPLATE;
                default:
                    return TYPE_NORMAL;
            }
        }

        /**
         * Self-rendering Ad
         *
         * @param holder
         * @param position
         * @return
         */
        private void getNativeVideoTemplate(NativeVideoTemplateHolder holder,final int position) {
            final AlxNativeAd nativeAd = mList.get(position);

            holder.mNativeAdView.setTitleView(holder.mTitle);
            holder.mNativeAdView.setDescriptionView(holder.mDescription);
            holder.mNativeAdView.setIconView(holder.mIcon);
            holder.mNativeAdView.setCallToActionView(holder.mCallToAction);
            holder.mNativeAdView.setAdSourceView(holder.mSource);
            holder.mNativeAdView.setMediaView(holder.mMediaView);
            holder.mNativeAdView.setCloseView(holder.mClose);

            holder.mLogo.setImageBitmap(nativeAd.getAdLogo());
            holder.mTitle.setText(nativeAd.getTitle());
            holder.mDescription.setText(nativeAd.getDescription());
            holder.mMediaView.setMediaContent(nativeAd.getMediaContent());

            if (TextUtils.isEmpty(nativeAd.getAdSource())) {
                holder.mSource.setVisibility(View.GONE);
            } else {
                holder.mSource.setVisibility(View.VISIBLE);
                holder.mSource.setText(nativeAd.getAdSource());
            }

            String iconUrl = null;
            if (nativeAd.getIcon() != null) {
                iconUrl = nativeAd.getIcon().getImageUrl();
            }
            if (TextUtils.isEmpty(iconUrl)) {
                holder.mIcon.setVisibility(View.GONE);
            } else {
                holder.mIcon.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(iconUrl).into(holder.mIcon);
            }

            if (TextUtils.isEmpty(nativeAd.getCallToAction())) {
                holder.mCallToAction.setVisibility(View.GONE);
            } else {
                holder.mCallToAction.setVisibility(View.VISIBLE);
                holder.mCallToAction.setText(nativeAd.getCallToAction());
            }

            nativeAd.setNativeEventListener(new AlxNativeEventListener() {
                @Override
                public void onAdClicked() {
                    Log.i(TAG, "onAdClicked");
                }

                @Override
                public void onAdImpression() {
                    Log.i(TAG, "onAdImpression");
                }

                @Override
                public void onAdClosed() {
                    Log.i(TAG, "onAdClosed");
                    nativeAd.destroy();
                    mList.remove(nativeAd);
                    notifyItemChanged(position);
                }
            });

            if (nativeAd.getMediaContent() != null && nativeAd.getMediaContent().hasVideo()) {
                nativeAd.getMediaContent().setVideoLifecycleListener(new AlxMediaContent.VideoLifecycleListener() {
                    @Override
                    public void onVideoStart() {
                        Log.i(TAG, "onVideoStart");
                    }

                    @Override
                    public void onVideoEnd() {
                        Log.i(TAG, "onVideoEnd");
                    }

                    @Override
                    public void onVideoPlay() {
                        Log.i(TAG, "onVideoPlay");
                    }

                    @Override
                    public void onVideoPause() {
                        Log.i(TAG, "onVideoPause");
                    }

                    @Override
                    public void onVideoPlayError(int code, String error) {
                        Log.i(TAG, "onVideoPlayError:" + code + ";" + error);
                    }

                    @Override
                    public void onVideoMute(boolean isMute) {
                        Log.i(TAG, "onVideoMute:" + isMute);
                    }
                });
            }

            // Register a native ad object.
            holder.mNativeAdView.setNativeAd(nativeAd);
        }

        private void getNormalView(NormalViewHolder holder, int position) {
            holder.mTvTitle.setText("native test title -RecyclerView:  " + position);
            holder.mTvDesc.setText("native test desc-RecyclerView:  " + position);
        }
    }

    private static class NativeVideoTemplateHolder extends RecyclerView.ViewHolder {
        AlxNativeAdView mNativeAdView;
        ImageView mLogo;
        ImageView mIcon;
        TextView mTitle;
        TextView mDescription;
        TextView mSource;
        Button mCallToAction;
        ImageView mClose;
        AlxMediaView mMediaView;

        public NativeVideoTemplateHolder(@NonNull View itemView) {
            super(itemView);
            mNativeAdView = (AlxNativeAdView) itemView.findViewById(R.id.native_ad_view);
            mLogo = (ImageView) itemView.findViewById(R.id.ad_logo);
            mIcon = (ImageView) itemView.findViewById(R.id.ad_icon);
            mTitle = (TextView) itemView.findViewById(R.id.ad_title);
            mDescription = (TextView) itemView.findViewById(R.id.ad_desc);
            mSource = (TextView) itemView.findViewById(R.id.ad_source);
            mCallToAction = (Button) itemView.findViewById(R.id.ad_call_to_action);
            mClose = (ImageView) itemView.findViewById(R.id.ad_close);
            mMediaView = (AlxMediaView) itemView.findViewById(R.id.ad_media);
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