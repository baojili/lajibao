package com.ln.base.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ln.base.R;
import com.ln.base.tool.AndroidUtils;
import com.ln.base.tool.DimenUtils;
import com.ln.base.tool.Log;

public class Dialog extends android.app.Dialog implements OnClickListener {

    private LinearLayout contentView;
    private Context mContext;

    public Dialog(Context context) {
        this(context, AndroidUtils.getDialogTheme(context, R.attr.dialogStyle));
    }

    public Dialog(Context context, int theme) {
        this(context, theme, 0f, 0f);
    }

    public Dialog(Context context, float dialogWidthRatio) {
        this(context, AndroidUtils.getDialogTheme(context, R.attr.dialogStyle), dialogWidthRatio, 0f);
    }

    public Dialog(Context context, int theme, float dialogWidthRatio) {
        this(context, theme, dialogWidthRatio, 0f);
    }

    public Dialog(Context context, float dialogWidthRatio, float dialogHeightRatio) {
        this(context, AndroidUtils.getDialogTheme(context, R.attr.dialogStyle), dialogWidthRatio, dialogHeightRatio);
    }

    public Dialog(Context context, int theme, float dialogWidthRatio, float dialogHeightRatio) {
        super(context, theme);
        mContext = context;
        setCanceledOnTouchOutside(true);
        contentView = new LinearLayout(context);
        contentView.setOrientation(LinearLayout.VERTICAL);
        contentView.setGravity(Gravity.CENTER);
        super.setContentView(contentView);
        if (dialogWidthRatio > 0) {
            setDialogSize(dialogWidthRatio, dialogHeightRatio);
        } else {
            setDialogSize(0.86f, dialogHeightRatio);
        }
    }

    public LinearLayout getContentView() {
        return contentView;
    }

    @Override
    public void setContentView(int layoutRes) {
        getLayoutInflater().inflate(layoutRes, contentView);
    }

    @Override
    public void setContentView(View view) {
        contentView.addView(view);
    }

    public void setDialogSize(float dialogWidthRatio, float dialogHeightRatio) {
        final Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (dialogWidthRatio > 0f) {
            lp.width = (int) (DimenUtils.getWindowWidth(window) * dialogWidthRatio);
        }
        if (dialogHeightRatio > 0f) {
            lp.height = (int) (DimenUtils.getWindowHeight(window) * dialogHeightRatio);
        }
        window.setAttributes(lp);
    }

    public Dialog setTitle(String title) {
        if (title != null) {
            setContentView(R.layout.dialog_title);
            TextView titleView = findViewById(R.id.dialog_title);
            titleView.setText(title);
        }
        return this;
    }

    public Dialog setTitleDrawableLeft(int iconRes) {
        TextView titleView = findViewById(R.id.dialog_title);
        titleView.setCompoundDrawablePadding(titleView.getResources().getDimensionPixelSize(R.dimen.spacing_smallest));
        titleView.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0);
        return this;
    }

    public Dialog setTitleDrawableLeft(Drawable drawable) {
        TextView titleView = findViewById(R.id.dialog_title);
        titleView.setCompoundDrawablePadding(titleView.getResources().getDimensionPixelSize(R.dimen.spacing_smallest));
        titleView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        return this;
    }

    public Dialog setButton(int resIdConfirmButtonText) {
        return setButton(getContext().getString(resIdConfirmButtonText));
    }

    public Dialog setButton(String confirmButtonText) {
        Button button = new Button(getContext(), null, R.attr.dialogButtonStyle);
        button.setId(R.id.dialog_button_confirm);
        button.setText(confirmButtonText);
        button.setOnClickListener(this);
        contentView.addView(button, contentView.getChildCount());
        return this;
    }

    public Dialog setButton(int resIdCancelButtonText, int resIdConfirmButtonText) {
        return setButton(getContext().getString(resIdCancelButtonText), getContext().getString(resIdConfirmButtonText));
    }

    public Dialog setButton(String cancelButtonText, String confirmButtonText) {
        LinearLayout btnLayout = new LinearLayout(getContext());
        Button button = new Button(getContext(), null, R.attr.dialogButtonLeftStyle);
        button.setId(R.id.dialog_button_cancel);
        button.setText(cancelButtonText);
        button.setOnClickListener(this);
        btnLayout.addView(button, new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
        button = new Button(getContext(), null, R.attr.dialogButtonRightStyle);
        button.setId(R.id.dialog_button_confirm);
        button.setText(confirmButtonText);
        button.setOnClickListener(this);
        btnLayout.addView(button, new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
        contentView.addView(btnLayout, contentView.getChildCount());
        return this;
    }

    public Dialog setDefaultButton() {
        setButton(contentView.getResources().getString(R.string.cancel),
                contentView.getResources().getString(R.string.ok));
        return this;
    }

    public void showStable() {
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.dialog_button_cancel) {
            onCancel();
        } else if (view.getId() == R.id.dialog_button_confirm) {
            onConfirm();
        }
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
            Log.e(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
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

    public void onCancel() {
        dismiss();
    }


    public void onConfirm() {
        dismiss();
    }
}