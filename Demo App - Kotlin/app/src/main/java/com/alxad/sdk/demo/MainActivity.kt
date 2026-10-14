package com.alxad.sdk.demo

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.alxad.sdk.demo.alx.AlxDemoListActivity
import com.rixengine.api.AlxAdSDK
import com.rixengine.api.AlxDeviceInfo

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
        addSDKInfo()
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

    private fun addBottomLayout(deviceInfo: AlxDeviceInfo) {
        val convertView = LayoutInflater.from(this).inflate(R.layout.layout_bottom_content, null)
        val tvContent = convertView.findViewById<View?>(R.id.bottom_tv_content) as TextView
        val sb = StringBuilder()
        sb.append("SDK Name: ").append(AlxAdSDK.getNetWorkName()).append("\r\n")
        sb.append("SDK Version: ").append(AlxAdSDK.getNetWorkVersion()).append("\r\n")

        sb.append("\r\n")
        sb.append("Ad SDK Config: ").append("\r\n")
        sb.append("host: ").append(AdConfig.ALX_HOST).append("\r\n")
        sb.append("AppID: ").append(AdConfig.ALX_APP_ID).append("\r\n")
        sb.append("SID: ").append(AdConfig.ALX_SID).append("\r\n")
        sb.append("token: ").append(AdConfig.ALX_TOKEN).append("\r\n")

        sb.append("\r\n")
        sb.append("Ad SDK info: ").append("\r\n")
        sb.append("OMSDK Version: ").append(AlxAdSDK.getOMSDKVersion()).append("\r\n")
        sb.append("UserId: ")
            .append(deviceInfo.userId ?: "")
            .append("\r\n")
        sb.append("GAID: ").append(deviceInfo.gaid ?: "")
            .append("\r\n")

        sb.append("\r\n")
        sb.append("Package Name: ").append(packageName ?: "").append("\r\n")
        tvContent.text = sb

        val btnCopy: Button = convertView.findViewById(R.id.btn_copy_config)
        btnCopy.setOnClickListener {
            val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.setPrimaryClip(ClipData.newPlainText("config",sb))
        }

        mListView?.addFooterView(convertView)
    }

    private fun addSDKInfo() {
        AlxAdSDK.testModeDeviceInfo(this) { info ->
            runOnUiThread { addBottomLayout(info) }
        }
    }

}