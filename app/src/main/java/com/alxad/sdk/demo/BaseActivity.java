package com.alxad.sdk.demo;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BaseActivity extends AppCompatActivity {

    public final String TAG = BaseActivity.class.getSimpleName();

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initBackView() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    protected void setActionBar() {
        setSupportActionBar(findViewById(R.id.toolBar));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public void checkNavigationBar(View rootView) {
//        View rootView = findViewById(R.id.root_view);
        if (rootView == null) {
            return;
        }
        int orientation = getResources().getConfiguration().orientation;
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                int navLeft = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).left;
                if(navLeft > 0){
                    v.setPadding(
                            navLeft,
                            v.getPaddingTop(),
                            v.getPaddingRight(),
                            v.getPaddingBottom()
                    );
                }else{
                    int navRight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).right;
                    v.setPadding(
                            v.getPaddingLeft(),
                            v.getPaddingTop(),
                            navRight,
                            v.getPaddingBottom()
                    );
                }
            } else {
                int navHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
                v.setPadding(
                        v.getPaddingLeft(),
                        v.getPaddingTop(),
                        v.getPaddingRight(),
                        navHeight
                );
            }
            return insets;
        });
    }

}
