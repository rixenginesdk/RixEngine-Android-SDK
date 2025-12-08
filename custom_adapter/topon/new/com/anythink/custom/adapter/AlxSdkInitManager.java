package com.anythink.custom.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.anythink.core.api.ATInitMediation;
import com.anythink.core.api.MediationInitCallback;
import com.rixengine.api.AlxAdSDK;
import com.rixengine.api.AlxSdkInitCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AlxSdkInitManager extends ATInitMediation {

    private static final String TAG = "AlxSdkInitManager";

    private volatile static AlxSdkInitManager sInstance;
    private boolean hasCallInit;
    Boolean success = false;

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
        String appid = getStringFromMap(serviceExtras, "appid");
        String sid = getStringFromMap(serviceExtras, "sid");
        String token = getStringFromMap(serviceExtras, "token");
        String host = getStringFromMap(serviceExtras, "host");
        if(TextUtils.isEmpty(host)){
            Log.e(TAG, "server host url is null, please check it, now use default host : " + AlxMetaInf.ADAPTER_SDK_HOST_URL);
            host = AlxMetaInf.ADAPTER_SDK_HOST_URL;
        }

        try {
            AlxAdSDK.init(context, host, token, sid, appid, new AlxSdkInitCallback() {
                @Override
                public void onInit(boolean isOk, String msg) {
                    Log.d(TAG, "alx sdk init success");
                    success = true;

                }
            });

            Map<String, Object> extraParameters = parseCustomExt(serviceExtras);
            printExtraParameters(extraParameters);
            setAlxExtraParameters(extraParameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (mediationInitCallback != null) {
            if (success) {
                mediationInitCallback.onSuccess();
            } else {
                mediationInitCallback.onFail("AlxSdk initSDK failed.");
            }
        }

    }

    private void setAlxExtraParameters(Map<String, Object> parameters) {
        if (parameters != null && !parameters.isEmpty()) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                AlxAdSDK.addExtraParameters(entry.getKey(), entry.getValue());
            }
        }
    }

    private Map<String, Object> parseCustomExt(Map<String, Object> serverExtra) {
        if (serverExtra == null || serverExtra.isEmpty()) {
            return null;
        }
        try {
            if (!serverExtra.containsKey("custom_ext")) {
                return null;
            }
            String custom_ext = (String) serverExtra.get("custom_ext");
            JSONObject json = new JSONObject(custom_ext);
            if (json.has("extras")) {
                JSONObject extras = json.optJSONObject("extras");
                return getAlxExtraParameters(extras);
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, "parse custom_ext error:" + e.getMessage());
        }
        return null;
    }

    private Map<String, Object> getAlxExtraParameters(JSONObject extras) {
        Map<String, Object> map = new HashMap<>();
        try {
            if (extras == null) {
                return map;
            }
            Iterator<String> keys = extras.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = extras.get(key);
                map.put(key, value);
            }
        } catch (Exception e) {
            Log.e(TAG, "alx extras field error:" + e.getMessage());
        }
        return map;
    }

    private void printExtraParameters(Map<String, Object> map) {
        try {
            if (map == null || map.isEmpty()) {
                Log.d(TAG, "alx Extra Parameters:null");
                return;
            }
            JSONObject json = new JSONObject(map);
            Log.d(TAG, "alx Extra Parameters:" + json.toString());
        } catch (Exception e) {
            Log.e(TAG, "printExtraParameters error:" + e.getMessage());
        }
    }

}

