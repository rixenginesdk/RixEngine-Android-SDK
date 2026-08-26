package com.anythink.custom.adapter;

import android.content.Context;
import android.util.Log;

import com.anythink.core.api.ATInitMediation;
import com.anythink.core.api.MediationInitCallback;
import com.rixengine.api.AlxAdSDK;
import com.rixengine.api.AlxSdkInitCallback;
import android.text.TextUtils;

import java.util.Map;

public class AlxSdkInitManager extends ATInitMediation {

    private volatile static AlxSdkInitManager sInstance;
    private String TAG = "AlxSdkInitManager";
    Boolean success = false;
    private String appid = "";
    private String sid = "";
    private String token = "";
    private String host = "";

    private AlxSdkInitManager() {

    }

    public static AlxSdkInitManager getInstance() {
        if (sInstance == null) {
            synchronized (AlxSdkInitManager.class) {
                if (sInstance == null)
                    sInstance = new AlxSdkInitManager();
            }
        }
        return sInstance;
    }

    public synchronized void initSDK(Context context, Map<String, Object> serviceExtras) {
        initSDK(context, serviceExtras, null);
    }

    @Override
    public void initSDK(Context context, Map<String, Object> serviceExtras, MediationInitCallback mediationInitCallback) {
        try {
            if (serviceExtras.containsKey("host")) {
                host = (String) serviceExtras.get("host");
            }
            if (serviceExtras.containsKey("appid")) {
                appid = (String) serviceExtras.get("appid");
            }
            if (serviceExtras.containsKey("sid")) {
                sid = (String) serviceExtras.get("sid");
            }
            if (serviceExtras.containsKey("token")) {
                token = (String) serviceExtras.get("token");
            }
            if (TextUtils.isEmpty(host) && !TextUtils.isEmpty(AlxMetaInf.ADAPTER_SDK_HOST_URL)) {
                host = AlxMetaInf.ADAPTER_SDK_HOST_URL;
                Log.e(TAG,"host url is null, please check it, now use default host : " + AlxMetaInf.ADAPTER_SDK_HOST_URL);

            }

            AlxAdSDK.init(context, host, token, sid, appid, new AlxSdkInitCallback() {
                @Override
                public void onInit(boolean isOk, String msg) {
                    Log.d("TAG", "Alx sdk init success");
                    success = true;

                }
            });
        } catch (Exception e) {
            Log.e("TAG", "Alx sdk init failed:" +e.getMessage());
        }

        if (mediationInitCallback != null) {
            if (success) {
                mediationInitCallback.onSuccess();
            } else {
                mediationInitCallback.onFail("AlxSdk initSDK failed.");
            }
        }

    }

}

