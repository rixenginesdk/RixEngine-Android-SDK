package com.admob.custom.adapter;

/**
 * Admob/GAM RixEngine Adapter
 *
 */
public interface AlxMetaInf {

    String ADAPTER_VERSION = "3.9.8";
    // Chinese: SDK请求EndPoint域名, 由平台分配，请手动修改， 例如：https://yoursubdomain.svr.rixengine.com/rtb
    // English: SDK requests the EndPoint domain, assigned by the platform, please manually modify, for example: https://yoursubdomain.svr.rixengine.com/rtb
    // Chinese: https://demo.svr.rixengine.com/rtb 是测试HOST，正式需要修改
    // English: https://demo.svr.rixengine.com/rtb is the test HOST, officially need to be modified
    String ADAPTER_SDK_HOST_URL = "https://demo.svr.rixengine.com/rtb";//测试HOST，正式需要修改
}
