package com.rixengine;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import androidx.annotation.NonNull;


import com.myopenpass.auth.OpenPassManager;
import com.rixengine.api.AlxAdSDK;
import com.rixengine.api.AlxSdkInitCallback;

public class MainApp extends Application {
    private final String TAG = "MainApp";

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            //Alx Ad Init
            AlxAdSDK.init(this, AppConfig.ALX_HOST, AppConfig.ALX_TOKEN, AppConfig.ALX_SID, AppConfig.ALX_APP_ID,new AlxSdkInitCallback() {
                @Override
                public void onInit(boolean b, String s) {
                    Log.i(TAG, Thread.currentThread().getName() + ":" + b + "-" + s);
                }
            });
//            AlxAdSDK.init(this, AppConfig.ALX_TOKEN, AppConfig.ALX_SID, AppConfig.ALX_APP_ID,  AlxSdkInitCallback () {
//                @Override
//                public void onInit(boolean isOk, String msg) {
//                    Log.i(TAG, Thread.currentThread().getName() + ":" + isOk + "-" + msg);
//                }
//            });
            AlxAdSDK.setDebug(true);

            //TopOn Ad Init

            //Google Ad Init

            // OpenPass Init
            OpenPassManager.init(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

}
