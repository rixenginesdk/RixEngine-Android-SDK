package com.rixengine.max.demo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.applovin.sdk.AppLovinPrivacySettings;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.applovin.sdk.AppLovinUserService;

import java.util.ArrayList;
import java.util.List;

;

public class MaxDemoListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "MaxDemoListActivity";

    private MyAdapter mAdapter;
    private Context mContext;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mContext = this;
        ListView listView = new ListView(this);
        listView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(listView);

        initSdk();

        mAdapter = new MyAdapter(this, initData());
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);
    }

    private List<AdapterData> initData() {
        List<AdapterData> list = new ArrayList<>();

        AdapterData bannerItem = new AdapterData("banner AD", MaxBannerActivity.class);
        AdapterData rewardItem = new AdapterData("Reward AD", MaxRewardVideoActivity.class);
        AdapterData interstitialItem = new AdapterData("Interstitial AD", MaxInterstitialActivity.class);
        AdapterData nativeItem = new AdapterData("Native AD", MaxNativeActivity.class);
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

    //Applovin广告初始化

    private void initSdk() {

        AppLovinSdk.getInstance( mContext ).setMediationProvider( "Rixengine Android" );
        AppLovinSdk.initializeSdk( mContext, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration)
            {
                Log.i(TAG, "AppLovinSdk-init:");
                // showDialog();

                if (configuration.getConsentDialogState() == AppLovinSdkConfiguration.ConsentDialogState.APPLIES) {
                    AppLovinUserService userService = AppLovinSdk.getInstance(mContext.getApplicationContext()).getUserService();
                    userService.showConsentDialog(MaxDemoListActivity.this, new AppLovinUserService.OnConsentDialogDismissListener() {
                        @Override
                        public void onDismiss() {

                        }
                    });
                }
                // AppLovin SDK is initialized, start loading ads
            }
        } );

//        AppLovinSdk.getInstance(mContext.getApplicationContext()).setMediationProvider("max");
//        AppLovinSdk.initializeSdk(this, new AppLovinSdk.SdkInitializationListener() {
//            @Override
//            public void onSdkInitialized(AppLovinSdkConfiguration config) {
//
//
//            }
//        });
    }

//    private void initSdk() {
//        AppLovinSdk.getInstance(mContext.getApplicationContext()).setMediationProvider("max");
//        AppLovinSdk.initializeSdk(this, new AppLovinSdk.SdkInitializationListener() {
//            @Override
//            public void onSdkInitialized(AppLovinSdkConfiguration config) {
//                String name = config.getConsentDialogState().name();
//                Log.i(TAG, "AppLovinSdk-init:" + name);
//                showDialog();
//
////                if (config.getConsentDialogState() == AppLovinSdkConfiguration.ConsentDialogState.APPLIES) {
////                    AppLovinUserService userService = AppLovinSdk.getInstance(mContext.getApplicationContext()).getUserService();
////                    userService.showConsentDialog(MaxDemoListActivity.this, new AppLovinUserService.OnConsentDialogDismissListener() {
////                        @Override
////                        public void onDismiss() {
////
////                        }
////                    });
////                }
//
//            }
//        });
//    }

    private void showDialog() {
        Log.i(TAG, "showDialog:" + Thread.currentThread().getName());
        final AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle("update config info")
                .setMessage("update config info")
                .setPositiveButton("set to true", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppLovinPrivacySettings.setHasUserConsent(true, mContext);
                        AppLovinPrivacySettings.setIsAgeRestrictedUser(true, mContext);
                        AppLovinPrivacySettings.setDoNotSell(true, mContext);
                        Log.d(TAG, "true-onclick:" + AppLovinPrivacySettings.hasUserConsent(mContext));
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("set to false", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppLovinPrivacySettings.setHasUserConsent(false, mContext);
                        AppLovinPrivacySettings.setIsAgeRestrictedUser(false, mContext);
                        AppLovinPrivacySettings.setDoNotSell(false, mContext);
                        Log.d(TAG, "false-onclick:" + AppLovinPrivacySettings.hasUserConsent(mContext));
                        dialog.dismiss();
                    }
                })
                .setNeutralButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

}