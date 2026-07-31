package com.alxad.sdk.demo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.alxad.sdk.demo.alx.AlxDemoListActivity
import com.rixengine.api.AlxAdSDK

class MainActivity : BaseListViewActivity() {

    override val TAG = MainActivity::class.java.simpleName

    var mPermissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addBottomLayout()
        val toolbar: Toolbar = findViewById<View?>(R.id.toolBar) as Toolbar
        toolbar.setNavigationIcon(null)
        initPermission()
    }

    /**
     * Authority judgment and application
     */
    private fun initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (strPermission in mPermissions) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        strPermission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(this, mPermissions, 6)
                }
            }
        }
    }

    override fun initAdapterData(): MutableList<AdapterData>? {
        val list: MutableList<AdapterData> = ArrayList<AdapterData>()
        list.add(AdapterData(getString(R.string.alx_sdk_demo), AlxDemoListActivity::class.java))
        list.add(AdapterData(getString(R.string.other_platform_demo),OtherPlatformActivity::class.java))

        return list
    }

    private fun addBottomLayout() {
        val convertView = LayoutInflater.from(this).inflate(R.layout.layout_bottom_content, null)
        val tvContent = convertView.findViewById<View?>(R.id.bottom_tv_content) as TextView
        val sb = "SDK Name: " +
                AlxAdSDK.getNetWorkName() +
                "\r\n" +
                "SDK Version: " +
                AlxAdSDK.getNetWorkVersion()
        tvContent.setText(sb)
        mListView?.addFooterView(convertView)
    }

}