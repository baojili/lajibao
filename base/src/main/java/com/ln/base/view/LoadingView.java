package com.ln.base.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ln.base.R;
import com.ln.base.dialog.AlertDialog;
import com.ln.base.network.RequestUiHandler;
import com.ln.base.tool.ViewAnimator;

public class LoadingView extends LinearLayout implements OnLongClickListener, RequestUiHandler {

    private ImageView mLoadingImage;
    private TextView mLoadingText;
    private Button mRetryButton;
    private ViewAnimator mViewAnimator;
    private String missingInfo;
    private AnimationDrawable anim;


    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.loadingViewStyle);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public LoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (NO_ID == getId()) {
            setId(R.id.loading_view);
        }
        if (getBackground() == null) {
            setBackgroundColor(getResources().getColor(R.color.bg_window));
        }
        setClickable(true);
        setGravity(Gravity.CENTER);
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.view_loading, this);
        mLoadingImage = findViewById(R.id.loading_image);
        mLoadingText = findViewById(R.id.loading_text);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingView, defStyle, 0);
        String text = typedArray.getString(R.styleable.LoadingView_android_text);
        if (text != null) {
            mLoadingText.setText(text);
        }
        Drawable src = typedArray.getDrawable(R.styleable.LoadingView_android_src);
        typedArray.getResourceId(R.styleable.LoadingView_android_src, 0);
        if (src != null) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mLoadingImage.setBackground(src);
            } else {
                mLoadingImage.setBackgroundDrawable(src);
            }
        }
        typedArray.recycle();

        mLoadingText.setOnLongClickListener(this);
        mRetryButton = findViewById(R.id.loading_retry);

        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(),
                getResources().getDimensionPixelOffset(R.dimen.title_bar_height));
        if (context instanceof OnClickListener) {
            setOnRetryClickListener((OnClickListener) context);
        }
        mViewAnimator = new ViewAnimator(this);
    }

    public void setOnRetryClickListener(OnClickListener onClickListener) {
        mRetryButton.setOnClickListener(onClickListener);
    }

    public Button getRetryButton() {
        return mRetryButton;
    }


    public LoadingView show(int resIdOfTips, int resIdOfIcon) {
        return show(getContext().getString(resIdOfTips), resIdOfIcon);
    }

    public LoadingView show(String tips, int resIdOfIcon) {
        if (anim != null && anim.isRunning()) {
            anim.stop();
        }
        mLoadingText.setText(tips);
        mLoadingImage.setBackgroundResource(resIdOfIcon);
        mRetryButton.setVisibility(View.GONE);
        mViewAnimator.showView();
        return this;
    }

    public LoadingView h5Reloading() {
        mLoadingImage.setBackgroundResource(R.color.transparent);
        mLoadingText.setText(null);
        mRetryButton.setVisibility(View.GONE);
        mViewAnimator.showView();
        return this;
    }

    public LoadingView show(String tips, Drawable drawable) {
        if (anim != null && anim.isRunning()) {
            anim.stop();
        }
        mLoadingText.setText(tips);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mLoadingImage.setBackground(drawable);
        } else {
            mLoadingImage.setBackgroundDrawable(drawable);
        }
        mRetryButton.setVisibility(View.GONE);
        mViewAnimator.showView();
        return this;
    }

    public LoadingView loading(int resID) {
        return loading(getContext().getString(resID));
    }

    public LoadingView loading(String text) {
        mLoadingText.setText(text);
//        mLoadingImage.setBackgroundResource(R.drawable.bg_loading);
        anim = (AnimationDrawable) mLoadingImage.getBackground();
        anim.start();
        mRetryButton.setVisibility(View.GONE);
        mViewAnimator.showView();
        return this;
    }

    public LoadingView info(int resID) {
        return info(getContext().getString(resID));
    }

    public LoadingView info(String text) {
        if (anim != null && anim.isRunning()) {
            anim.stop();
        }
        mLoadingText.setText(text);
        mLoadingImage.setBackgroundResource(R.drawable.im_error);
        mRetryButton.setVisibility(View.GONE);
        mViewAnimator.showView();
        return this;
    }

    public LoadingView error(int resID) {
        return error(getContext().getString(resID));
    }

    public LoadingView error(String text) {
        if (anim != null && anim.isRunning()) {
            anim.stop();
        }
        mLoadingText.setText(text);
        mLoadingImage.setBackgroundResource(R.drawable.im_error);
        mRetryButton.setVisibility(View.VISIBLE);
        mViewAnimator.showView();
        return this;
    }

    public LoadingView hide() {
        if (anim != null && anim.isRunning()) {
            anim.stop();
        }
        mViewAnimator.hideView();
        return this;
    }

    @Override
    public void onStart(String hint) {
        loading(hint);
    }

    @Override
    public void onError(int errCode, String errMsg) {
        if (errMsg != null && errMsg.endsWith("missing")) {
            error(mLoadingText.getResources().getString(R.string.not_get_whole_content));
            missingInfo = errMsg;
        } else {
            error(errMsg);
            missingInfo = null;
        }
    }

    @Override
    public void onSuccess() {
        hide();
    }

    @Override
    public boolean onLongClick(View arg0) {
        if (missingInfo != null) {
            new AlertDialog(getContext(), mLoadingText.getResources().getString(R.string.data_missing), missingInfo)
                    .setButton(mLoadingText.getResources().getString(R.string.ok)).show();
            return true;
        }
        return false;
    }

}