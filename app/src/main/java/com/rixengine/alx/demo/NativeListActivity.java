package com.rixengine.alx.demo;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.rixengine.R;


/**
 * native Ad
 */
public class NativeListActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AlxNativeDemoActivity";

    //AlxNativeAd.getCreativeType() 得到的广告素材类型【如：大图、小图、组图、视频、其他：未知类型】
    public static final int NATIVE_AD_CREATE_TYPE_UNKNOWN = 0; //未知类型
    public static final int NATIVE_AD_CREATE_TYPE_LARGE_IMAGE = 1; //大图
    public static final int NATIVE_AD_CREATE_TYPE_SMALL_IMAGE = 2; //小图
    public static final int NATIVE_AD_CREATE_TYPE_GROUP_IMAGE = 3; //组图
    public static final int NATIVE_AD_CREATE_TYPE_VIDEO = 4; //视频


    private RadioGroup mRadioList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_list);
        initView();
    }

    private void initView() {
        mRadioList = (RadioGroup) findViewById(R.id.radio_list_layout);
        findViewById(R.id.btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                bnShow();
                break;
        }
    }

    private void bnShow() {
        int showType = 0;
        switch (mRadioList.getCheckedRadioButtonId()) {
            case R.id.render_listview:
                showType = 0;
                break;
            case R.id.render_recyclerview:
                showType = 1;
                break;
            case R.id.render_other:
                showType = 2;
                break;
        }

        Intent intent;
        switch (showType) {
            case 1:
                intent = new Intent(this, NativeRecyclerViewActivity.class);
                break;
            case 2:
                intent = new Intent(this, NativeNormalActivity.class);
                break;
            default:
                intent = new Intent(this, NativeListViewActivity.class);
        }
        startActivity(intent);
    }
}