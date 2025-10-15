package com.rixengine.alx.demo;


import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
 * native Ad—— Display in ListView
 */
public class NativeListViewActivity extends AppCompatActivity {
    private final String TAG = "AlxNativeListDemo";

    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_listview);
        initView();
        loadAd();
    }

    private void initView() {
        ListView mListView = (ListView) findViewById(R.id.listview);
        mAdapter = new MyAdapter(this);
        mListView.setAdapter(mAdapter);
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

    private class MyAdapter extends BaseAdapter {

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
        public int getCount() {
            return (mList == null || mList.isEmpty()) ? 0 : mList.size(); // for test
        }

        @Override
        public AlxNativeAd getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            AlxNativeAd bean = getItem(position);
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            switch (getItemViewType(position)) {
                case TYPE_NATIVE_VIDEO_TEMPLATE:
                    return getNativeVideoTemplate(convertView, parent, position);
                default:
                    return getNormalView(convertView, parent, position);
            }
        }

        /**
         * Self-rendering Ad
         *
         * @param convertView
         * @param parent
         * @param position
         * @return
         */
        private View getNativeVideoTemplate(View convertView, ViewGroup parent, int position) {
            NativeVideoTemplateHolder holder;
            if (convertView == null) {
                holder = new NativeVideoTemplateHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.native_video_template, parent, false);
                holder.mNativeAdView = (AlxNativeAdView) convertView.findViewById(R.id.native_ad_view);
                holder.mLogo = (ImageView) convertView.findViewById(R.id.ad_logo);
                holder.mIcon = (ImageView) convertView.findViewById(R.id.ad_icon);
                holder.mTitle = (TextView) convertView.findViewById(R.id.ad_title);
                holder.mDescription = (TextView) convertView.findViewById(R.id.ad_desc);
                holder.mSource = (TextView) convertView.findViewById(R.id.ad_source);
                holder.mCallToAction = (Button) convertView.findViewById(R.id.ad_call_to_action);
                holder.mClose = (ImageView) convertView.findViewById(R.id.ad_close);
                holder.mMediaView = (AlxMediaView) convertView.findViewById(R.id.ad_media);
                convertView.setTag(holder);
            } else {
                holder = (NativeVideoTemplateHolder) convertView.getTag();
            }
            final AlxNativeAd nativeAd = getItem(position);

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
                    notifyDataSetChanged();
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
            holder.mTvTitle.setText("native test title-ListView:  " + position);
            holder.mTvDesc.setText("native test desc-ListView:  " + position);
            return convertView;
        }

    }

    private static class NativeVideoTemplateHolder {
        AlxNativeAdView mNativeAdView;
        ImageView mLogo;
        ImageView mIcon;
        TextView mTitle;
        TextView mDescription;
        TextView mSource;
        Button mCallToAction;
        ImageView mClose;
        AlxMediaView mMediaView;
    }

    private static class NormalViewHolder {
        ImageView mIvIcon;
        TextView mTvTitle;
        TextView mTvDesc;
    }

}