package com.rixengine;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import kotlin.Unit;
import kotlinx.coroutines.flow.FlowCollector;

import com.myopenpass.auth.OpenPassException;
import com.myopenpass.auth.OpenPassManager;
import com.myopenpass.auth.flow.WebSignInFlowClient;
import com.myopenpass.auth.flow.WebSignInFlowState;
import com.rixengine.alx.demo.AdListActivity;
import com.uid2.UID2Manager;
import com.uid2.data.IdentityRequest;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = MainActivity.class.getSimpleName();
    private WebSignInFlowClient mOpenPassClient = null;
    //Applying for relevant permissions can push AD resources more accurately
    String[] mPermissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initPermission();
        }
        initView();

//        try {
//            //initUID2();
//            callOpenPass();
//        } catch (Exception e) {
//            Log.e(TAG, "error:" + e.getMessage());
//        }
    }

    private void initView() {
        TextView alx_tv = findViewById(R.id.tv_alx_demo);
        TextView group_tv = findViewById(R.id.tv_group_demo);
        alx_tv.setOnClickListener(this);
        group_tv.setOnClickListener(this);

        TextView open_pass_tv = findViewById(R.id.tv_open_pass);
        open_pass_tv.setOnClickListener(this);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_alx_demo:
                startActivity(new Intent(this, AdListActivity.class));
                break;
            case R.id.tv_group_demo:
                startActivity(new Intent(this, GroupActivity.class));
                break;
            case R.id.tv_open_pass:
                callOpenPass();
                break;
        }
    }


    private void initUID2() throws Exception {
        //步骤1:初始化sdk
        UID2Manager.init(this.getApplicationContext());
        // UID2Manager.init(getApplicationContext(), UID2Manager.Environment.Production, NetworkSession.Companion,true);

        // 开发者通过产品层面的设计（如提供账号系统），引导用户提供邮箱或电话号码
        // 配置 Email 对象
        IdentityRequest.Email emailRequest = new IdentityRequest.Email("example@example.com");
        // 配置 Phone 对象
        IdentityRequest.Phone phoneRequest = new IdentityRequest.Phone("+8618688959089");


        String subscriptionId = "toPh8vgJgt";
        String publicKey = "UID2-X-I-MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEKAbPfOz7u25g1fL6riU7p2eeqhjmpALPeYoyjvZmZ1xM2NM8UeOmDZmCIBnKyRZ97pz5bMCjrs38WM22O7LJuw==";

        //步骤2: 调用UID2 SDK API，生成邮箱或电话输入的身份token（根据用户能提供的信息，可以选择邮箱或电话）
        // 邮箱示例
        UID2Manager.getInstance().generateIdentity(emailRequest, subscriptionId, publicKey, result -> {
            Log.i(TAG, "generate token result:" + result);
            return null;
        });
        // 电话示例
//        UID2Manager.getInstance().generateIdentity(phoneRequest, subscriptionId, publicKey, result->{
//            Log.i(TAG, "generate token result:"+result);
//            return null;
//        });
        // 延迟n秒获取token(存在一定时延，需要等待UID2生成返回后才有效）
        new Handler().postDelayed(() -> getUID2Token(), 5000);
    }

    private void getUID2Token() {
        // 步骤3: 获取UID2 广告token
        String token = UID2Manager.getInstance().getAdvertisingToken();
        Log.i(TAG, "token:" + token);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    protected void handleIntent(Intent intent) {
        if (intent != null) {
            if(mOpenPassClient != null) {
                mOpenPassClient.checkSignIn(intent);
            }
        }
    }

    // OpenPass
    private void callOpenPass() {
        mOpenPassClient = new WebSignInFlowClient("", OpenPassManager.getInstance());
        if (mOpenPassClient != null) {
            // Configure the web component to match the style of your app.
            mOpenPassClient.setToolbarColor(Color.BLACK);
            mOpenPassClient.setSecondaryToolbarColor(Color.RED);
            mOpenPassClient.setNavigationBarColor(Color.WHITE);
            mOpenPassClient.setNavigationBarDividerColor(Color.YELLOW);

            //
            try {
                mOpenPassClient.launchSignIn(this);
                mOpenPassClient.getState().collect((FlowCollector<WebSignInFlowState>) (state, continuation) -> {
                    if (state instanceof WebSignInFlowState.Error) {
                        // 处理错误状态
                        WebSignInFlowState.Error errorState = (WebSignInFlowState.Error) state;
                        //_viewState.emit(new ErrorState(errorState.getError()));
                    } else if (state instanceof WebSignInFlowState.Complete) {
                        // 处理完成状态，获取 tokens
                        UID2Manager manager = UID2Manager.getInstance();
                        Object tokens = manager.getAdvertisingToken(); // 根据实际返回类型使用合适的方法
                        if (tokens != null) {
                            // Tokens 可用
                            //_viewState.emit(new SignedInState(tokens));
                            Log.i(TAG, "tokens:" + tokens);
                        } else {
                            // Tokens 不可用
                            //_viewState.emit(new SignedOutState());
                            Log.i(TAG, "tokens is null");
                        }
                    } else if (state instanceof WebSignInFlowState.Launched) {
                        // Launched 状态无需处理
                        // Do nothing
                        Log.i(TAG, "Launched, do nothing");
                    }

                    return Unit.INSTANCE; // 返回 Unit.INSTANCE 表示继续收集
                }, null);
            } catch (OpenPassException e) {
                Log.e(TAG, "launchSignIn error:" + e.getMessage());
            }

        }

    }
}


