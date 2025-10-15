package com.rixengine.alx.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.rixengine.AppConfig;
import com.rixengine.MainActivity;
import com.rixengine.R;
import com.rixengine.api.AlxSplashAd;
import com.rixengine.api.AlxSplashAdListener;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {
    private final String TAG = "AlxSplashDemo";

    public static final int REQUEST_CODE_CONTACT = 1000;
    private final int AD_TIMEOUT = 5 * 1000;//开屏广告加载的超时时间5s

    private FrameLayout mAdContainer;
    private ImageView mIvWelcome;

    //控制开屏广告点击跳转
    private boolean canJump = false;
    private AlxSplashAd mSlashAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
        initData();
    }

    private void initView() {
        mAdContainer = (FrameLayout) findViewById(R.id.ad_container);
        mIvWelcome = (ImageView) findViewById(R.id.iv_welcome);
    }

    private void initData() {
        List<String> permissionList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
            //验证是否许可权限
            for (String a_permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, a_permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(a_permission);
                }
            }
            String[] denied_permissions = permissionList.toArray(new String[permissionList.size()]);
            if (denied_permissions.length > 0) {
                requestPermissions(denied_permissions, REQUEST_CODE_CONTACT);
            } else {
                initSplashAd();
            }
        } else {
            initSplashAd();
        }
    }

    private void initSplashAd() {
        //初始化广告位。仅调用一次。
        mSlashAd = new AlxSplashAd(this, AppConfig.ALX_SPLASH_AD_PID);
        Log.d(TAG, "ad start load");
        mSlashAd.load(new AlxSplashAdListener() {
            @Override
            public void onAdLoadSuccess() {
                Log.d(TAG, "onAdLoadSuccess: | 单价：" + mSlashAd.getPrice());
                mSlashAd.showAd(mAdContainer);
                mSlashAd.reportChargingUrl();
                mSlashAd.reportBiddingUrl();
            }

            @Override
            public void onAdLoadFail(int errorCode, String errorMsg) {
                Log.e(TAG, "onAdLoadFail:" + errorCode + "--" + errorMsg);
                goToMainActivity();
            }

            @Override
            public void onAdShow() {
                Log.d(TAG, "onAdShow");
                mIvWelcome.setVisibility(View.GONE);
            }

            @Override
            public void onAdClick() {
                Log.d(TAG, "onAdClick");
                canJump = true;
            }

            @Override
            public void onAdDismissed() {
                Log.d(TAG, "onAdDismissed");
                Toast.makeText(SplashActivity.this, "onAdDismissed be called", Toast.LENGTH_SHORT).show();
                goToMainActivity();
            }
        }, AD_TIMEOUT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canJump) {
            goToMainActivity();
        }
//        canJump = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
//        canJump = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSlashAd != null) {
            mSlashAd.destroy();
        }
    }

    private void goToMainActivity() {
        this.startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CONTACT && hasAllPermissionsGranted(grantResults)) {
            initSplashAd();
        } else {
            // 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
            Toast.makeText(this, "The application lacks the necessary permissions! Please click permissions to open the required permissions.",
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            finish();
        }
    }

    private boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

}