package com.ln.base.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ln.base.R;

public class BottomWindow extends PopupWindow implements
        OnClickListener {

    protected Context mContext;
    private TextView mTitleView, mConfirmView, mCancelView;
    private LinearLayout mContentLayout;

    public BottomWindow(Context context) {
        super(context);
        mContext = context;
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(0));
        setAnimationStyle(R.style.BottomWindowAnimation);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        View shadowView = new View(context);
        shadowView.setBackgroundResource(R.drawable.bg_bottom_window_shadow);

        mContentLayout = new LinearLayout(context);
        mContentLayout.setOrientation(LinearLayout.VERTICAL);
        mContentLayout.setBackgroundResource(R.color.white);

        LinearLayout rootLayout = new LinearLayout(context);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.addView(shadowView, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, 16));
        rootLayout.addView(mContentLayout, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setContentView(rootLayout);
        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.WRAP_CONTENT);
    }

    public BottomWindow(Context context, String title) {
        this(context);
        LayoutInflater.from(context).inflate(R.layout.view_bottom_window_title,
                mContentLayout);
        mTitleView = mContentLayout
                .findViewById(R.id.bottom_window_title);
        mConfirmView = mContentLayout
                .findViewById(R.id.bottom_window_confirm);
        mCancelView = mContentLayout
                .findViewById(R.id.bottom_window_cancel);
        mConfirmView.setOnClickListener(this);
        mCancelView.setOnClickListener(this);
        mTitleView.setText(title);
    }

    public BottomWindow(Context context, int layoutResID) {
        this(context);
        LayoutInflater.from(context).inflate(layoutResID, mContentLayout);
    }

    public BottomWindow(Context context, String title, int layoutResID) {
        this(context, title);
        LayoutInflater.from(context).inflate(layoutResID, mContentLayout);
    }


    public BottomWindow(Context context, View view) {
        this(context);
        mContentLayout.addView(view);
    }

    public BottomWindow setTitle(String title) {
        if (mTitleView != null) {
            mTitleView.setText(title);
        }
        return this;
    }

    public String getTitleText() {
        if (mTitleView != null) {
            return mTitleView.getText().toString();
        }
        return null;
    }

    public BottomWindow setTitleTextSize(float size) {
        if (mTitleView != null) {
            mTitleView.setTextSize(size);
        }
        return this;
    }

    public BottomWindow setTitleTextSize(int unit, float size) {
        if (mTitleView != null) {
            mTitleView.setTextSize(unit, size);
        }
        return this;
    }

    public BottomWindow setButton(String cancel, String confrim) {
        if (mCancelView != null && cancel != null) {
            mCancelView.setText(cancel);
        }
        if (mConfirmView != null && confrim != null) {
            mConfirmView.setText(confrim);
        }
        return this;
    }

    public BottomWindow setButtonVisibility(int visibility) {
        if (!(visibility == View.VISIBLE || visibility == View.INVISIBLE || visibility == View.GONE)) {
            throw new IllegalArgumentException("illegal argument for visibility parameter");
        }
        if (mCancelView != null) {
            mCancelView.setVisibility(visibility);
        }
        if (mConfirmView != null) {
            mConfirmView.setVisibility(visibility);
        }
        return this;
    }

    public LinearLayout getContentLayout() {
        return mContentLayout;
    }

    public LinearLayout addContent(View children) {
        mContentLayout.addView(children);
        return mContentLayout;
    }

    public LinearLayout addContent(int layoutResID) {
        mContentLayout.addView(LayoutInflater.from(mContentLayout.getContext())
                .inflate(layoutResID, null));
        return mContentLayout;
    }

    public void show() {
        if (!(mContext instanceof Activity)) return;
        //setBackgroundAlpha(0.4f);
        showAtLocation(((Activity) mContext).getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    public void show(View view) {
        //setBackgroundAlpha(0.4f);
        showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bottom_window_cancel) {
            onCancel();
        } else if (view.getId() == R.id.bottom_window_confirm) {
            onConfirm();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        //setBackgroundAlpha(1.0f);
    }

    public void setBackgroundAlpha(float bgAlpha) {
        Activity context = ((Activity) mContentLayout.getContext());
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        context.getWindow().setAttributes(lp);
    }

    public void onConfirm() {
        dismiss();
    }

    public void onCancel() {
        dismiss();
    }
}