package com.ln.base.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

import com.ln.base.R;
import com.ln.base.tool.DimenUtils;

public class BottomDialog extends Dialog {

    private LinearLayout mBottomContentView;

    public BottomDialog(Context context) {
        super(context);
        mBottomContentView = new LinearLayout(context);
        mBottomContentView.setOrientation(LinearLayout.VERTICAL);
        mBottomContentView.setBackgroundColor(0xfff6f6f6);
        super.setContentView(mBottomContentView);
        LayoutParams lp = getWindow().getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = DimenUtils.getWindowWidth(getWindow());
        lp.windowAnimations = R.style.BottomWindowAnimation;

    }

    public LinearLayout getBottomContentView() {
        return mBottomContentView;
    }

    @Override
    public void setContentView(int layoutRes) {
        getLayoutInflater().inflate(layoutRes, mBottomContentView);
    }

    public void show(View view) {
        // showAtLocation(view, Gravity.BOTTOM, 0, 0);
        show();
    }

}
