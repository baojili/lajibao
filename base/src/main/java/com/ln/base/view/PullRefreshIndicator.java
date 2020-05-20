package com.ln.base.view;

import android.content.Context;
import android.widget.LinearLayout;

public abstract class PullRefreshIndicator extends LinearLayout {

    public PullRefreshIndicator(Context context) {
        super(context);
    }

    public abstract void onLoading();

    public abstract void onPulling(int offset);

    public abstract boolean onRelease();

    public abstract void onRefreshComplete(boolean isSuccess, String msg);

}