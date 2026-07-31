package com.alxad.sdk.demo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alxad.sdk.demo.alx.AlxDemoListActivity;
import com.rixengine.api.AlxAdSDK;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseListViewActivity {
    private final String TAG = MainActivity.class.getSimpleName();

    //Applying for relevant permissions can push AD resources more accurately
    String[] mPermissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addBottomLayout();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setNavigationIcon(null);
        initPermission();
    }


    /**
     * Authority judgment and application
     */
    private void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String strPermission : mPermissions) {
                if (ContextCompat.checkSelfPermission(this,
                        strPermission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, mPermissions, 6);
                }
            }
        }
    }

    @Override
    public List<AdapterData> initAdapterData() {
        List<AdapterData> list = new ArrayList<>();
        list.add(new AdapterData(getString(R.string.alx_sdk_demo), AlxDemoListActivity.class));
        list.add(new AdapterData(getString(R.string.other_platform_demo), OtherPlatformActivity.class));

        return list;
    }

    private void addBottomLayout() {
        View convertView = LayoutInflater.from(this).inflate(R.layout.layout_bottom_content, null);
        TextView tvContent = (TextView) convertView.findViewById(R.id.bottom_tv_content);
        String sb = "SDK Name: " +
                AlxAdSDK.getNetWorkName() +
                "\r\n" +
                "SDK Version: " +
                AlxAdSDK.getNetWorkVersion();
        tvContent.setText(sb);
        mListView.addFooterView(convertView);
    }


}