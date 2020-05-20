package com.ln.ljb.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.ln.base.tool.Log;
import com.ln.ljb.R;
import com.ln.ljb.constant.SharedPreferKey;
import com.yanzhenjie.permission.runtime.Permission;
import com.yanzhenjie.permission.runtime.PermissionDef;

import java.util.List;

public class WelcomeActivity extends BaseActivity {

    private @PermissionDef
    String[] permissions = new String[]{
            Permission.WRITE_EXTERNAL_STORAGE,
            Permission.READ_EXTERNAL_STORAGE,
            Permission.READ_PHONE_STATE,
            Permission.ACCESS_COARSE_LOCATION,
            Permission.ACCESS_FINE_LOCATION,
            Permission.CAMERA,
            Permission.RECORD_AUDIO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("WelcomeActivity -> onCreate");
        setContentView(R.layout.activity_welcome);
        requestPermission(2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("WelcomeActivity -> onDestroy");
    }

    private void requestPermission(long delayTime) {
        getWindow().getDecorView().postDelayed(() -> requestPermission(permissions), delayTime);
    }

    private void gotoNextActivity() {
        Log.e("WelcomeActivity -> gotoNextActivity");
        Intent intent = null;
        if (!settings.contains(SharedPreferKey.KEY_AUTH)) {
            intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        } else {
            intent = new Intent(WelcomeActivity.this, MainActivity.class);
        }
        startActivity(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            WelcomeActivity.this.overridePendingTransition(0, 0);
        }
        finish();
    }

    @Override
    protected void onGranted(List<String> permissions) {
        super.onGranted(permissions);
        Log.e("WelcomeActivity -> onGranted");
        gotoNextActivity();
    }
}
