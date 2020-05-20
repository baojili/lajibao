package com.ln.base.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.ln.base.R;
import com.ln.base.network.RequestUiHandler;
import com.ln.base.tool.AndroidUtils;
import com.ln.base.tool.DimenUtils;
import com.ln.base.tool.Log;

public class ToastDialog extends android.app.Dialog implements Runnable, OnCancelListener, RequestUiHandler {

    private ProgressBar mProgressBar;
    private ImageView mIconView;
    private TextView mInfoView;
    private Handler showTimeoutHandler;
    private Toast toast;
    private Context mContext;

    public ToastDialog(Context context) {
        super(context, AndroidUtils.getDialogTheme(context, R.attr.toastDialogStyle));
        setContentView(R.layout.dialog_toast);
        mContext = context;
        mProgressBar = findViewById(R.id.progress_bar);
        mIconView = findViewById(R.id.icon);
        mInfoView = findViewById(R.id.info);

        setWindowWidth((int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.8f));

        setOnCancelListener(this);
        showTimeoutHandler = new Handler();
    }

    public void setWindowWidth(int windowWidth) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = windowWidth;
        getWindow().setAttributes(lp);
    }

    public void setDefaultLoadingWindowWidth() {
        setWindowWidth(DimenUtils.dp2px(getContext(), 550));
    }

    public void setDefaultInfoWindowWidth() {
        setWindowWidth((int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.8f));
    }

    public void setContentTextViewGravity(int gravity) {
        mInfoView.setGravity(gravity);
    }

    public void showLoading(int resId) {
        showLoading(getContext().getString(resId));
    }

    public void showLoading(String info) {
        showLoading(info, 0);
    }

    public void showLoading(@StringRes int resId, long maxShowTimeMillis) {
        showLoading(getContext().getString(resId), maxShowTimeMillis);
    }

    public void showLoading(String info, long maxShowTimeMillis) {
        showTimeoutHandler.removeCallbacks(this);
        mProgressBar.setVisibility(View.VISIBLE);
        mIconView.setVisibility(View.GONE);
        mInfoView.setText(info);
        if (maxShowTimeMillis > 0) {
            showTimeoutHandler.postDelayed(this, maxShowTimeMillis);
        }
        if (!isShowing()) {
            setCanceledOnTouchOutside(false);
            setCancelable(false);
            show();
        }
    }

    public void show(int resId) {
        showToast(0, resId);
    }

    public void show(String info) {
        show(0, info);
    }

    public void showSuccess(int resId) {
        show(R.drawable.ic_success, resId);
    }

    public void showSuccess(String info) {
        show(R.drawable.ic_success, info);
    }

    public void showError(int resId) {
        show(R.drawable.ic_error, resId);
    }

    public void showError(String info) {
        show(R.drawable.ic_error, info);
    }

    public void showInfo(int resId) {
        show(R.drawable.ic_info, resId);
    }

    public void showInfo(String info) {
        show(R.drawable.ic_info, info);
    }

    public void show(int iconRes, int resId) {
        show(iconRes, getContext().getString(resId));
    }

    public void show(int iconRes, String info) {
        show(iconRes, info, 1000 + info.length() * 100);
    }

    public void show(int iconRes, int resId, int maxShowTimeMillis) {
        show(iconRes, getContext().getString(resId), maxShowTimeMillis);
    }

    public void show(int iconRes, String info, int maxShowTimeMillis) {
        setWindowWidth((int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.8f));
        showTimeoutHandler.removeCallbacks(this);
        mProgressBar.setVisibility(View.GONE);
        if (iconRes == 0) {
            mIconView.setVisibility(View.GONE);
        } else {
            mIconView.setVisibility(View.VISIBLE);
            mIconView.setImageResource(iconRes);
        }
        mInfoView.setText(info);
        setCanceledOnTouchOutside(true);
        showTimeoutHandler.postDelayed(this, maxShowTimeMillis);
        show();
    }

    public void showToast(String info) {
        showToast(info, Toast.LENGTH_SHORT);
    }

    public void showToast(int resId) {
        showToast(getContext().getString(resId), Toast.LENGTH_SHORT);
    }

    public void showToast(int resId, int duration) {
        showToast(getContext().getString(resId), duration);
    }

    public void showToast(String info, int duration) {
        if (toast == null) {
            View layout = getLayoutInflater().inflate(R.layout.view_toast, null);
            TextView textView = layout.findViewById(R.id.message);
            textView.setText(info);
            toast = new Toast(getContext());
            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 200);
            toast.setView(layout);
        }
        toast.setDuration(duration);
        ((TextView) toast.getView().findViewById(R.id.message)).setText(info);
        toast.show();
    }

    @Override
    public void show() {
        try {
            if (mContext instanceof Activity) {
                if (!((Activity) mContext).isFinishing()) {
                    super.show();
                }
            } else {
                Log.d("dialog cannot be show to user");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void dismiss() {
        //当调用带maxShowTimeMillis参数的show方法后，再调用dismiss()方法时，下面这句代码，防止onShowLoadingTimeOut()被调用
        showTimeoutHandler.removeCallbacks(this);
        try {
            if (mContext instanceof Activity) {
                if (!((Activity) mContext).isFinishing()) {
                    super.dismiss();
                }
            } else {
                super.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        onShowLoadingTimeOut();
    }

    public void onShowLoadingTimeOut() {
        dismiss();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        //dialog已被取消，防止onShowLoadingTimeOut()被调用
        showTimeoutHandler.removeCallbacks(this);
    }

    @Override
    public void onStart(String hint) {
        showLoading(hint);
    }

    @Override
    public void onError(int errCode, String errMsg) {
        dismiss();
        showToast(errMsg);
    }

    @Override
    public void onSuccess() {
        dismiss();
    }
}