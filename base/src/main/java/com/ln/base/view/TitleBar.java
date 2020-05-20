package com.ln.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ln.base.R;

public class TitleBar extends RelativeLayout {

    private LinearLayout mLeftBox, mRightBox;
    private TextView mTitleView;
    private OnClickListener mOnClickListener;
    private int mIconWidth;

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.layout.view_title_bar);
    }

    public TitleBar(Context context, AttributeSet attrs, int layoutRes) {
        this(context, attrs, layoutRes, R.attr.titleBarStyle);
    }

    public TitleBar(Context context, AttributeSet attrs, int layoutRes, int defStyle) {
        super(context, attrs, defStyle);
        setId(R.id.title_view);
        mIconWidth = getResources().getDimensionPixelSize(R.dimen.title_bar_height);
        if (getBackground() == null) {
            setBackgroundColor(getResources().getColor(R.color.base));
        }
        if (context instanceof OnClickListener) {
            mOnClickListener = (OnClickListener) context;
        }
        LayoutInflater.from(context).inflate(layoutRes, this);
        mTitleView = findViewById(R.id.title_text);
        mLeftBox = findViewById(R.id.title_left_box);
        mRightBox = findViewById(R.id.title_right_box);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitleBar, defStyle, 0);
        int iconResId = a.getResourceId(R.styleable.TitleBar_leftIcon, 0);
        if (iconResId > 0) {
            addIconButton(iconResId, true);
        }
        iconResId = a.getResourceId(R.styleable.TitleBar_leftExtraIcon, 0);
        if (iconResId > 0) {
            addIconButton(iconResId, true);
        }
        iconResId = a.getResourceId(R.styleable.TitleBar_rightIcon, 0);
        if (iconResId > 0) {
            addIconButton(iconResId, false);
        }
        iconResId = a.getResourceId(R.styleable.TitleBar_rightExtraIcon, 0);
        if (iconResId > 0) {
            addIconButton(iconResId, false);
        }
        setTitle(a.getString(R.styleable.TitleBar_android_text));
        int colorResId = a.getResourceId(R.styleable.TitleBar_android_textColor, 0);
        if (colorResId > 0) {
            setTitleColor(colorResId);
        }
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(
                    getResources().getDimensionPixelSize(R.dimen.title_bar_height), MeasureSpec.EXACTLY));
        }
    }

    /**
     * add a button
     */
    public ImageButton addIconButton(int iconResId, boolean isLeft) {
        return addIconButton(iconResId, isLeft, mOnClickListener);
    }

    /**
     * add a button
     */
    public ImageButton addIconButton(int iconResId, boolean isLeft, OnClickListener onClickListener) {
        ImageButton button = new ImageButton(getContext());
        button.setPadding(0, 0, 0, 0);
        button.setId(iconResId);
        button.setImageResource(iconResId);
        button.setBackgroundResource(R.color.transparent);
        button.setOnClickListener(onClickListener);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mIconWidth, mIconWidth);
        if (isLeft) {
            if (mLeftBox.getChildCount() > 0) {
                lp.leftMargin = -getResources().getDimensionPixelSize(R.dimen.spacing_smaller);
            }
            mLeftBox.addView(button, lp);
        } else {
            if (mRightBox.getChildCount() > 0) {
                lp.rightMargin = -getResources().getDimensionPixelSize(R.dimen.spacing_smaller);
            }
            mRightBox.addView(button, 0, lp);
        }
        return button;
    }

    public void removeButtonView(int index, boolean isLeft) {
        if (isLeft) {
            if (mLeftBox.getChildCount() > index)
                mLeftBox.removeViewAt(index);
        } else {
            if (mRightBox.getChildCount() > index)
                mRightBox.removeViewAt(index);
        }

    }

    /**
     * get Right Box
     *
     * @return mRightBox
     */
    public LinearLayout getRightBox() {
        return mRightBox;
    }

    /**
     * get Left Box
     *
     * @return mLeftBox
     */
    public LinearLayout getLeftBox() {
        return mLeftBox;
    }

    /**
     * add a Text button
     */
    public TextView addTextButton(int strResId, boolean isLeft) {
        return addTextButton(strResId, isLeft, mOnClickListener);
    }

    /**
     * add a Text button
     */
    public TextView addTextButton(int strResId, boolean isLeft, OnClickListener onClickListener) {
        TextView button = new TextView(getContext());
        button.setGravity(Gravity.CENTER);
        button.setTextColor(getResources().getColor(R.color.white));
        button.setPadding(getResources().getDimensionPixelOffset(R.dimen.spacing_normal), 0, getResources()
                .getDimensionPixelOffset(R.dimen.spacing_normal), 0);
        button.setId(strResId);
        button.setText(strResId);
        button.setBackgroundResource(R.drawable.bg_empty_pr_base_dark);
        button.setOnClickListener(onClickListener);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, mIconWidth);
        if (isLeft) {
            if (mLeftBox.getChildCount() > 0) {
                lp.leftMargin = -getResources().getDimensionPixelSize(R.dimen.spacing_smaller);
            }
            mLeftBox.addView(button, lp);
        } else {
            if (mRightBox.getChildCount() > 0) {
                lp.rightMargin = -getResources().getDimensionPixelSize(R.dimen.spacing_smaller);
            }
            mRightBox.addView(button, 0, lp);
        }
        return button;
    }

    /**
     * set title text
     *
     * @param title
     */
    public void setTitle(String title) {
        mTitleView.setText(title);
    }

    /**
     * set title text color
     *
     * @param color
     */
    public void setTitleColor(int color) {
        mTitleView.setTextColor(getResources().getColor(color));
    }

    public void addTitleBackButton() {
        getLeftBox().removeAllViews();
        addIconButton(R.drawable.bg_title_back, true, mOnClickListener);
    }

}