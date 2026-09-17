package com.admob.custom.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VersionInfo;
import com.google.android.gms.ads.mediation.Adapter;
import com.google.android.gms.ads.mediation.InitializationCompleteCallback;
import com.google.android.gms.ads.mediation.MediationConfiguration;
import com.rixengine.api.AlxAdSDK;

import org.json.JSONObject;

import java.util.List;

public class AlxBaseAdapter extends Adapter {

    private static final String TAG = "AlxBaseAdapter";

    @Override
    public void initialize(@NonNull Context context, @NonNull InitializationCompleteCallback initializationCompleteCallback, @NonNull List<MediationConfiguration> list) {
        Log.d(TAG, "alx-admob-adapter: initialize");
        initializationCompleteCallback.onInitializationSucceeded();
    }

    @NonNull
    @Override
    public VersionInfo getVersionInfo() {
        String versionString = AlxMetaInf.ADAPTER_VERSION;
        VersionInfo result = getAdapterVersionInfo(versionString);
        if (result != null) {
            return result;
        }
        return new VersionInfo(0, 0, 0);
    }

    @NonNull
    @Override
    public VersionInfo getSDKVersionInfo() {
        String versionString = AlxAdSDK.getNetWorkVersion();
        VersionInfo result = getAdapterVersionInfo(versionString);
        if (result != null) {
            return result;
        }
        return new VersionInfo(0, 0, 0);
    }

    private VersionInfo getAdapterVersionInfo(String version) {
        if (TextUtils.isEmpty(version)) {
            return null;
        }
        try {
            String[] arr = version.split("\\.");
            if (arr == null || arr.length < 3) {
                return null;
            }
            int major = Integer.parseInt(arr[0]);
            int minor = Integer.parseInt(arr[1]);
            int micro = Integer.parseInt(arr[2]);
            return new VersionInfo(major, minor, micro);
        } catch (Exception e) {
            Log.e(TAG, "error:" + e.getMessage());
        }
        return null;
    }

    public static void sdkInfo() {
        try {
            JSONObject json = new JSONObject();
            json.put("sdk_name", "Admob");
            json.put("sdk_version", MobileAds.getVersion());
            json.put("adapter_version", AlxMetaInf.ADAPTER_VERSION);
            AlxAdSDK.addExtraParameters("alx_adapter", json);
        } catch (Exception e) {
            Log.e(TAG, "error:" + e.getMessage());
        }
    }


}
