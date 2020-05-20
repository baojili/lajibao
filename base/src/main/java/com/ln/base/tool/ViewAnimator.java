package com.ln.base.tool;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.ln.base.R;

public final class ViewAnimator implements AnimationListener {

    public static final int ANIM_SIDE_LEFT = 0;
    public static final int ANIM_SIDE_UP = 1;
    public static final int ANIM_SIDE_RIGHT = 2;
    public static final int ANIM_SIDE_DOWN = 3;

    private View mView;
    private Animation mShowAnimation, mHideAnimation;
    private boolean mIsShowing;

    public ViewAnimator(View view) {
        this(view, -1);
    }

    public ViewAnimator(View view, int animationType) {
        mView = view;
        switch (animationType) {
            case ANIM_SIDE_LEFT:
                setAnimation(R.anim.slide_right_show, R.anim.slide_left_hide);
                break;
            case ANIM_SIDE_UP:
                setAnimation(R.anim.slide_down_show, R.anim.slide_up_hide);
                break;
            case ANIM_SIDE_RIGHT:
                setAnimation(R.anim.slide_left_show, R.anim.slide_right_hide);
                break;
            case ANIM_SIDE_DOWN:
                setAnimation(R.anim.slide_up_show, R.anim.slide_down_hide);
                break;
            default:
                setAnimation(R.anim.fade_in, R.anim.fade_out);
        }
        mIsShowing = mView.getVisibility() == View.VISIBLE;
    }

    private void setAnimation(int showAnimation, int hideAnimation) {
        mShowAnimation = AnimationUtils.loadAnimation(mView.getContext(), showAnimation);
        mHideAnimation = AnimationUtils.loadAnimation(mView.getContext(), hideAnimation);
        mShowAnimation.setAnimationListener(this);
        mHideAnimation.setAnimationListener(this);
    }

    public void showView() {
        if (!mIsShowing) {
            mIsShowing = true;
            mView.setVisibility(View.VISIBLE);
            mView.startAnimation(mShowAnimation);
        }
    }

    public void hideView() {
        if (mIsShowing) {
            mIsShowing = false;
            mView.startAnimation(mHideAnimation);
        }
    }

    public View getView() {
        return mView;
    }

    public boolean isViewShowing() {
        return mIsShowing;
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        mView.clearAnimation();
        if (animation == mHideAnimation) {
            mView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }
}