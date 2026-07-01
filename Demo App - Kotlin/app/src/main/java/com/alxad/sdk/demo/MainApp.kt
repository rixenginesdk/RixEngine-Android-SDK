package com.alxad.sdk.demo

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.multidex.MultiDex
import com.rixengine.api.AlxAdSDK

class MainApp : Application() {
    private val TAG = "MainAppTest"

    companion object {
        lateinit var mApp: MainApp
        fun getInstance(): MainApp {
            return mApp
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        mApp = this
        initAd()
    }

    /**
     * 广告配置
     */
    private fun initAd() {
        try {
            AlxAdSDK.init(
                this,
                AdConfig.ALX_HOST,
                AdConfig.ALX_TOKEN,
                AdConfig.ALX_SID,
                AdConfig.ALX_APP_ID
            ) { isOk, msg ->
                Log.i(TAG, Thread.currentThread().name + ":" + isOk + "-" + msg)
            }
            AlxAdSDK.setDebug(true)

//            用户扩展参数
            AlxAdSDK.addExtraParameters(
                "uid2_token",
                "NewAdvertisingTokenIjb6u6KcMAtd0/4ZIAYkXvFrMdlZVqfb9LNf99B+1ysE/lBzYVt64pxYxjobJMGbh5q/HsKY7KC0Xo5Rb/Vo8HC4dYOoWXyuGUaL7Jmbw4bzh+3pgokelUGyTX19DfArTeIg7n+8cxWQ="
            )
        } catch (e: Exception) {

        }
    }

}