package com.alxad.sdk.demo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alxad.sdk.demo.alx.AlxDemoListActivity;

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


}