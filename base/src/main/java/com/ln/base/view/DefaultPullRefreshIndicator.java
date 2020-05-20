package com.ln.base.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;

import com.ln.base.R;

public class DefaultPullRefreshIndicator extends PullRefreshIndicator implements
        Runnable {

    private ProgressBar mProgressBar;
    private ImageView mIconView;
    private TextView mInfoView;
    private Animation rotateUpAnimation, rotateDownAnimation;
    private Scroller mScroller;
    private int mHeight, mOffset;
    private boolean isLoadFinished = false;
    private boolean isShowLoading = false;

    public DefaultPullRefreshIndicator(Context context) {
        super(context);
        setGravity(Gravity.CENTER);
        LayoutInflater.from(context).inflate(
                R.layout.view_pull_refresh_default, this);
        mProgressBar = findViewById(R.id.progress_bar);
        mIconView = findViewById(R.id.icon);
        mInfoView = findViewById(R.id.info);
        rotateUpAnimation = new RotateAnimation(0, 180,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotateDownAnimation = new RotateAnimation(180, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotateUpAnimation.setDuration(200);
        rotateDownAnimation.setDuration(200);
        rotateUpAnimation.setFillAfter(true);
        rotateDownAnimation.setFillAfter(true);
        mScroller = new Scroller(context, new DecelerateInterpolator());
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
        if (!isLoadFinished && isShowLoading) {
            showLoading();
        }
    }

    /* just called only one time,which controlled by isLoadFinished value */
    private void showLoading() {
        mOffset = mHeight;
        setPadding(0, 0, 0, 0);
        onRelease();
    }

    @Override
    public void onLoading() {
        /* show loading View whenView onAttachedToWindow() called */
        isShowLoading = true;
    }

    @Override
    public void onPulling(int offset) {
        if (mOffset < mHeight && offset >= mHeight) {
            mIconView.startAnimation(rotateUpAnimation);
            mInfoView.setText(getResources().getString(
                    R.string.release_refresh));
        } else if (mOffset >= mHeight && offset < mHeight) {
            mIconView.startAnimation(rotateDownAnimation);
            mInfoView.setText(getResources().getString(
                    R.string.pull_down_refresh));
        }
        mOffset = offset;
        setPadding(0, offset - mHeight, 0, 0);
    }

    @Override
    public boolean onRelease() {
        if (mOffset >= mHeight) {
            mScroller.startScroll(mOffset, 0, mHeight - mOffset, 0);
            mIconView.clearAnimation();
            mIconView.setVisibility(GONE);
            mProgressBar.setVisibility(VISIBLE);
            if (!isLoadFinished && isShowLoading) {
                mInfoView.setText(getResources().getString(
                        R.string.loading_wait));
            } else {
                mInfoView.setText(getResources().getString(
                        R.string.refreshing_wait));
            }
            invalidate();
            return true;
        } else {
            post(this);
            return false;
        }
    }

    @Override
    public void onRefreshComplete(boolean isSuccess, String msg) {
        mScroller.forceFinished(false);
        mProgressBar.setVisibility(GONE);
        mIconView.clearAnimation();
        mIconView.setImageResource(isSuccess ? R.drawable.ic_success
                : R.drawable.ic_error);
        mIconView.setVisibility(VISIBLE);
        if (!isLoadFinished && isShowLoading) {
            mInfoView.setText(isSuccess ? getResources().getString(
                    R.string.load_success) : getResources().getString(
                    R.string.load_fail));
        } else {
            mInfoView.setText(isSuccess ? getResources().getString(
                    R.string.refresh_success) : getResources().getString(
                    R.string.refresh_fail));
        }
        postDelayed(this, 600);
        isLoadFinished = true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mOffset = mScroller.getCurrX();
            setPadding(0, mOffset - mHeight, 0, 0);
            invalidate();
        }
        if (mOffset == 0) {
            mIconView.clearAnimation();
            mIconView.setImageResource(R.drawable.ic_pull_refresh);
            mInfoView.setText(getResources().getString(
                    R.string.pull_down_refresh));
        }
    }

    @Override
    public void run() {
        mScroller.startScroll(mOffset, 0, -mOffset, 0);
        invalidate();
    }

}