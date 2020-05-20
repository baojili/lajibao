package com.ln.base.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;

import com.ln.base.R;

public class DefaultLoadMoreIndicator extends LoadMoreIndicator {

    private int mHeight;

    public DefaultLoadMoreIndicator(Context context) {
        super(context);
        setGravity(Gravity.CENTER);
        LayoutInflater.from(context).inflate(R.layout.view_load_more_default, this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getLayoutParams() != null) {
            mHeight = getLayoutParams().height;
        }
        setPadding(0, 0, 0, 0);
        if (mHeight <= 0) {
            measure(0, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            mHeight = getMeasuredHeight();
        }
        setPadding(0, -mHeight, 0, 0);
    }

    @Override
    public void onLoadMoreStart() {
        setPadding(0, 0, 0, 0);
    }

    @Override
    public void onLoadMoreComplete() {
        setPadding(0, -mHeight, 0, 0);
    }

}