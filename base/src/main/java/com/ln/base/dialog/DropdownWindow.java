package com.ln.base.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

import com.ln.base.R;

public class DropdownWindow extends PopupWindow implements OnClickListener {

    /**
     * !注意：为了适配安卓7.0，应采用带shadowHeight参数的构造方法
     */
    private LinearLayout mContentLayout;
    private LinearLayout rootLayout;

    public DropdownWindow(Context context) {
        super(context);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(0));
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        View shadowView = new View(context);
        shadowView.setBackgroundResource(R.drawable.bg_bottom_window_shadow);

        mContentLayout = new LinearLayout(context);
        mContentLayout.setOrientation(LinearLayout.VERTICAL);
        mContentLayout.setBackgroundResource(R.color.white);

        rootLayout = new LinearLayout(context);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        rootLayout.setLayoutParams(params);
        rootLayout.setBackgroundColor(Color.parseColor("#80000000"));
        rootLayout.addView(mContentLayout, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        rootLayout.addView(shadowView, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, 16));

        setContentView(rootLayout);

        setWidth(LayoutParams.MATCH_PARENT);
        if (Build.VERSION.SDK_INT < 24) {
            setHeight(LayoutParams.MATCH_PARENT);
        } else {
            setHeight(LayoutParams.WRAP_CONTENT);
        }

        rootLayout.setId(R.id.tag_first);
        rootLayout.setOnClickListener(this);
        mContentLayout.setId(R.id.tag_second);
        mContentLayout.setOnClickListener(this);
    }

    public DropdownWindow(Context context, int layoutResID) {
        this(context);
        LayoutInflater.from(context).inflate(layoutResID, mContentLayout);
    }

    /**
     * 适配安卓7.0
     *
     * @param context
     * @param shadowHeight 阴影面的高度
     */
    public DropdownWindow(Context context, long shadowHeight) {
        super(context);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(0));
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        View shadowView = new View(context);
        shadowView.setBackgroundResource(R.drawable.bg_bottom_window_shadow);

        mContentLayout = new LinearLayout(context);
        mContentLayout.setOrientation(LinearLayout.VERTICAL);
        mContentLayout.setBackgroundResource(R.color.white);

        rootLayout = new LinearLayout(context);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        rootLayout.setLayoutParams(params);
        rootLayout.setBackgroundColor(Color.parseColor("#80000000"));
        rootLayout.addView(mContentLayout, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        if (Build.VERSION.SDK_INT > 23) {
            rootLayout.addView(shadowView, new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, (int) shadowHeight));
        }

        setContentView(rootLayout);

        setWidth(LayoutParams.MATCH_PARENT);
        if (Build.VERSION.SDK_INT < 24) {
            setHeight(LayoutParams.MATCH_PARENT);
        } else {
            setHeight(LayoutParams.WRAP_CONTENT);
        }

        rootLayout.setId(R.id.tag_first);
        rootLayout.setOnClickListener(this);
        mContentLayout.setId(R.id.tag_second);
        mContentLayout.setOnClickListener(this);
    }

    public DropdownWindow(Context context, View view) {
        this(context);
        addContent(view);
    }

    public LinearLayout getContentlLayout() {
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

    public void show(View view) {
        showAsDropDown(view);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tag_first) {
            dismiss();
        } else if (view.getId() == R.id.tag_second) {
            // Log.i("mcontent layout clicked");
        }
    }
}