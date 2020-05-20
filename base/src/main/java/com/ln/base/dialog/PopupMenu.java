package com.ln.base.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ln.base.R;

import java.util.Arrays;

public class PopupMenu extends PopupWindow implements OnClickListener {

    private Context mContext;

    public PopupMenu(Context context) {
        super(context);
        mContext = context;
    }

    public PopupMenu(Context context, String[] menus, int... disables) {
        this(context);
        mContext = context;
        ScrollView content = new ScrollView(context);
        LinearLayout parent = new LinearLayout(context);
        parent.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = 1;
        for (int i = 0; i < menus.length; i++) {
            if (Arrays.binarySearch(disables, i) < 0) {
                TextView tv = new TextView(context);
                tv.setBackgroundResource(R.drawable.bg_popup_menu_item);
                tv.setTextColor(0xff666666);
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                tv.setText(menus[i]);
                tv.setTag(i);
                tv.setOnClickListener(this);
                parent.addView(tv, lp);
            }
        }
        content.addView(parent);
        setContentView(content);
        setFocusable(true);
        setWidth(LayoutParams.WRAP_CONTENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_popup_menu));
        setAnimationStyle(R.style.PopupWindowAnimation);
    }

    public void show() {
        if (!(mContext instanceof Activity)) return;
        Rect rect = new Rect();
        View view = ((Activity) mContext).getWindow().getDecorView();
        view.getGlobalVisibleRect(rect);
        showAtLocation(view, Gravity.NO_GRAVITY, rect.right, rect.top);
    }

    public void show(View view) {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        showAtLocation(view, Gravity.NO_GRAVITY, rect.right, rect.bottom);
    }

    public void showAsSelect(View view) {
        setWidth(view.getWidth());
        showAsDropDown(view, 0, 1);
    }

    public void showAsSelect(View view, int width) {
        setWidth(width);
        showAsDropDown(view, 0, 1);
    }

    public void showInRightBottom(View view) {
        setAnimationStyle(-1);
        measureView(getContentView());
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        showAtLocation(view, Gravity.NO_GRAVITY, rect.right - getContentView().getMeasuredWidth(), rect.bottom - getContentView().getMeasuredHeight());
    }

    public void showAtRightTop(View view) {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        showAtLocation(view, Gravity.NO_GRAVITY, rect.right, 0);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (v.getTag() != null) {
            onMenuSelect((Integer) v.getTag());
        }
    }

    public void onMenuSelect(int position) {
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, params.width);
        int lpHeight = params.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

}