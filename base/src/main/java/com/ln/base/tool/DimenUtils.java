package com.ln.base.tool;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class DimenUtils {

    public static int dp2px(Context context, int dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(Context context, int pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static float sp2pxOriginal(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (spValue * fontScale + 0.5f);
    }

    public static float getDeviceDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static DisplayMetrics getDisplayMetrics(WindowManager windowManager) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    public static DisplayMetrics getDisplayMetrics(Window window) {
        return getDisplayMetrics(window.getWindowManager());
    }

    public static DisplayMetrics getDisplayMetrics(Activity activity) {
        return getDisplayMetrics(activity.getWindowManager());
    }

    public static int getPxFromDimen(Context context, int dimenRes) {
        return context.getResources().getDimensionPixelOffset(dimenRes);
    }

    public static int getWindowWidth(Window window) {
        return getDisplayMetrics(window).widthPixels;
    }

    public static int getWindowHeight(Window window) {
        return getDisplayMetrics(window).heightPixels;
    }

    public static int getWindowWidth(Activity activity) {
        return getDisplayMetrics(activity).widthPixels;
    }

    public static int getWindowHeight(Activity activity) {
        return getDisplayMetrics(activity).heightPixels;
    }

    /**
     * 获取底部导航栏高度
     *
     * @return 导航栏高度
     */
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}