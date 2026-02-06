package com.alxad.sdk.demo;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.multidex.MultiDex;
import com.rixengine.api.AlxAdSDK;
import com.rixengine.api.AlxSdkInitCallback;

public class MainApp extends Application {
    private final String TAG = "MainApp";

    private static MainApp mApp;

    public static MainApp getInstance(){
        return mApp;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApp=this;
        initAd();
    }

    /**
     * 广告配置
     */
    public void initAd(){
        try {
            //Alx Ad Init
            AlxAdSDK.init(this,AdConfig.ALX_HOST, AdConfig.ALX_TOKEN, AdConfig.ALX_SID, AdConfig.ALX_APP_ID, new AlxSdkInitCallback() {
                @Override
                public void onInit(boolean isOk, String msg) {
                    Log.i(TAG, Thread.currentThread().getName() + ":" + isOk + "-" + msg);
                }
            });
            AlxAdSDK.setDebug(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
