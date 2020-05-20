package com.ln.base.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentActivity;

import com.ln.base.R;
import com.ln.base.config.Config;
import com.ln.base.config.SharedPreferKey;
import com.ln.base.dialog.AlertDialog;
import com.ln.base.dialog.Dialog;
import com.ln.base.dialog.ToastDialog;
import com.ln.base.model.Auth;
import com.ln.base.tool.JsonUtils;
import com.ln.base.tool.StatusBarUtils;
import com.ln.base.tool.language.MultiLanguage;
import com.ln.base.view.LoadingView;
import com.ln.base.view.TitleBar;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;
import com.yanzhenjie.permission.runtime.PermissionDef;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;

public class BaseActivity extends FragmentActivity implements OnClickListener {

    public static SharedPreferences settings;
    public static Auth auth;
    private TitleBar mTitleBar;
    private ToastDialog mToastDialog;
    private LoadingView mLoadingView;
    private @PermissionDef
    String[] mRequestPermissions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (settings == null) {
            settings = getSharedPreferences(Config.DEFAULT_SHARED_PREFERENCES, MODE_PRIVATE);
        }
        if (auth == null) {
            if (settings.contains(SharedPreferKey.KEY_AUTH)) {
                auth = JsonUtils.toEntity(settings.getString(SharedPreferKey.KEY_AUTH,
                        null), Auth.class);
            } else {
                auth = new Auth(this);
            }
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        //AndroidBug5497Workaround.assistActivity(findViewById(android.R.id.content));
        onInit();
        onFindViews();
        onBindListener();
        onInitViewData();
    }

    protected void onInit() {
        onInitStatusBar();
        ButterKnife.bind(this);
    }

    protected void onFindViews() {

    }

    protected void onBindListener() {

    }

    protected void onInitViewData() {

    }

    @Override
    public Resources getResources() {
        //解决字体随系统调节而变化的问题
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("ResourceType")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.drawable.bg_title_back) {
            onBackPressed();
        } else if (view.getId() == R.id.loading_retry) {
            onLoadingRetry();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MultiLanguage.setLocal(newBase));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Config.PERMISSION_REQUEST_CODE) {
            //Log.e("PERMISSION_REQUEST_CODE");
            if (mRequestPermissions == null) return;
            if (!AndPermission.hasPermissions(this, mRequestPermissions)) {
                requestPermission(mRequestPermissions);
            } else {
                onGranted(Arrays.asList(mRequestPermissions));
            }
        }
    }

    protected void onInitStatusBar() {
        StatusBarUtils.with(this).init();
    }

    public ToastDialog getToastDialog() {
        return mToastDialog != null ? mToastDialog : (mToastDialog = new ToastDialog(this));
    }

    public TitleBar getTitleBar() {
        return mTitleBar != null ? mTitleBar : (mTitleBar = findViewById(R.id.title_view));
    }

    public LoadingView getLoadingView() {
        if (mLoadingView == null) {
            mLoadingView = findViewById(R.id.loading_view);
        }
        return mLoadingView;
    }

    public void showToast(@StringRes int resId) {
        showToast(getString(resId));
    }

    public void showToast(final String info) {
        //Log.e("showToast -> " + Looper.getMainLooper().getThread());
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            getToastDialog().showToast(info);
        } else {
            runOnUiThread(() -> getToastDialog().showToast(info));
        }
    }

    protected void onLoadingRetry() {
    }

    protected void requestPermission(@PermissionDef @NotNull String[] permissions) {
        mRequestPermissions = permissions;
        //Log.e("mRequestPermissions -> "+JsonUtils.toJsonViewStr(mRequestPermissions));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onGranted(Arrays.asList(mRequestPermissions));
        } else {
            AndPermission.with(this).runtime()
                    .permission(permissions)
                    .rationale((context, rationalePermissions, executor) -> {
                        //Log.e("showRationale ->" + JsonUtils.toJsonViewStr(rationalePermissions));
                        onRationale(context, rationalePermissions, executor);
                    })
                    .onDenied(deniedPermissions -> {
                        //Log.e("onDenied ->" + JsonUtils.toJsonViewStr(deniedPermissions));
                        if (AndPermission.hasAlwaysDeniedPermission(BaseActivity.this, deniedPermissions)) {
                            showGotoPermissionSettingDialog(deniedPermissions);
                        } else {
                            onDenied(deniedPermissions);
                        }
                    })
                    .onGranted(grantedPermissions -> onGranted(grantedPermissions))
                    .start();
        }
    }

    private void showGotoPermissionSettingDialog(List<String> deniedPermissions) {
        Dialog dialog = new AlertDialog(BaseActivity.this, BaseActivity.this.getString(R.string.always_denied_permission_tips, Permission.transformText(BaseActivity.this, deniedPermissions))) {
            @Override
            public void onConfirm() {
                super.onConfirm();
                AndPermission.with(BaseActivity.this)
                        .runtime()
                        .setting()
                        .start(Config.PERMISSION_REQUEST_CODE);
            }
        }.setButton(R.string.ok);
        dialog.showStable();
    }

    /**
     * 向系统申请权限前回调（只有请求权限列表里有未被用户允许的权限时），向用户解释为什么要申请这些权限
     *
     * @param rationalePermissions rationalePermissions.size() <= mRequestPermissions.length,意向权限列表小于等于请求权限列表
     */
    protected void onRationale(Context context, List<String> rationalePermissions, RequestExecutor executor) {
        Dialog dialog = new AlertDialog(context, context.getString(R.string.rationale_permission_tips, Permission.transformText(context, rationalePermissions))) {
            @Override
            public void onConfirm() {
                super.onConfirm();
                executor.execute();
            }
        }.setButton(R.string.ok);
        dialog.showStable();
    }


    /**
     * 权限申请被用户拒绝时回调（除去被永久拒绝的情况）
     *
     * @param deniedPermissions deniedPermissions.size() <= mRequestPermissions.length,已拒绝权限列表小于等于请求权限列表
     */
    protected void onDenied(List<String> deniedPermissions) {
        //Log.e("onDenied ->" + JsonUtils.toJsonViewStr(deniedPermissions));
        requestPermission(deniedPermissions.toArray(new String[deniedPermissions.size()]));
    }

    /**
     * 权限申请被用户全部允许时回调（也包括当前页面申请的权限之前已经全部允许，再次申请时会回调；权限申请被永久拒绝后，通过当前应用里的引导进入权限设置页面，并设置当前页面申请的请权限全部允许时回调）
     *
     * @param grantedPermissions grantedPermissions.size() == mRequestPermissions.length,请求权限列表和已授予列表相同
     */
    protected void onGranted(List<String> grantedPermissions) {
        //Log.e("onGranted ->" + JsonUtils.toJsonViewStr(grantedPermissions));
    }
}