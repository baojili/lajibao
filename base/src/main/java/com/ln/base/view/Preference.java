package com.ln.base.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.StringRes;

import com.ln.base.R;
import com.ln.base.tool.DimenUtils;

public class Preference extends LinearLayout implements MarqueeTextView.OnTextOverflowChangeListener {

    private TextView mTitleView;
    private MarqueeTextView mHintView;
    private LinearLayout mContentView;
    private ImageView mIconView;

    public Preference(Context context) {
        this(context, null);
    }

    public Preference(Context context, int iconRes) {
        this(context);
        setIcon(iconRes);
    }

    public Preference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.preferenceStyle);
    }

    public Preference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        setGravity(Gravity.CENTER_VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.view_preference, this);
        mTitleView = findViewById(R.id.title);
        mContentView = findViewById(R.id.content);
        mIconView = findViewById(R.id.icon);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Preference, defStyle, 0);
        setTitle(a.getString(R.styleable.Preference_android_text));
        setTitleDrawableLeft(a.getDrawable(R.styleable.Preference_android_drawableLeft), a.getDimensionPixelSize(R.styleable.Preference_android_drawablePadding, DimenUtils.dp2px(context, 2)));
        mTitleView.setTextColor(a.getColor(R.styleable.Preference_android_textColor,
                getResources().getColor(R.color.text_dark)));
        setIcon(a.getDrawable(R.styleable.Preference_android_src));
        if (a.getString(R.styleable.Preference_android_hint) != null) {
            mHintView = new MarqueeTextView(context);
            setHint(a.getString(R.styleable.Preference_android_hint));
            mHintView.setTextColor(a.getColor(R.styleable.Preference_android_textColorHint,
                    getResources().getColor(R.color.text_light)));
            mContentView.addView(mHintView);
            mHintView.setOnTextChangedListener(this);
        }
        a.recycle();

    }

    public String getTitle() {
        return mTitleView.getText().toString();
    }

    public Preference setTitle(int resId) {
        return setTitle(getContext().getString(resId));
    }

    public Preference setTitle(String title) {
        mTitleView.setText(title);
        return this;
    }

    public Preference setTitleDrawableLeft(int iconRes) {
        mTitleView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.spacing_smallest));
        mTitleView.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0);
        return this;
    }

    public Preference setTitleDrawableLeft(Drawable drawable) {
        mTitleView.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.spacing_smallest));
        mTitleView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        return this;
    }

    public Preference setTitleDrawableLeft(Drawable drawable, int padding) {
        mTitleView.setCompoundDrawablePadding(padding);
        mTitleView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        return this;
    }

    public TextView getTitleView() {
        return mTitleView;
    }

    public Preference setHint(@StringRes int resId) {
        return setHint(getContext().getString(resId));
    }

    public Preference setHint(CharSequence hint) {
        if (mHintView != null) {
            mHintView.setText(hint);
        } else {
            mHintView = new MarqueeTextView(mContentView.getContext());
            mHintView.setText(hint);
            mContentView.addView(mHintView);
            mHintView.setTextColor(getResources().getColor(R.color.text_light));
            mHintView.setOnTextChangedListener(this);
        }
        return this;
    }

    public void setHintColor(@ColorInt int color) {
        if (mHintView != null) {
            mHintView.setTextColor(color);
        } else {
            mHintView = new MarqueeTextView(mContentView.getContext());
            mContentView.addView(mHintView);
            mHintView.setTextColor(color);
            mHintView.setOnTextChangedListener(this);
        }
    }

    public TextView getHintView() {
        if (mHintView == null) {
            mHintView = new MarqueeTextView(mContentView.getContext());
            mContentView.addView(mHintView);
            mHintView.setTextColor(getResources().getColor(R.color.text_light));
            mHintView.setOnTextChangedListener(this);
        }
        return mHintView;
    }

    public LinearLayout getContentlLayout() {
        return mContentView;
    }

    public LinearLayout addContent(View children) {
        mContentView.addView(children);
        return mContentView;
    }

    public void setContentGravity(int gravity) {
        mContentView.setGravity(gravity);
    }

    public LinearLayout addContent(int layoutResID) {
        mContentView.addView(LayoutInflater.from(mContentView.getContext()).inflate(layoutResID, null));
        return mContentView;
    }

    public Preference setIcon(int iconRes) {
        mIconView.setVisibility(View.VISIBLE);
        mIconView.setImageResource(iconRes);
        return this;
    }

    public Preference setIcon(Drawable drawable) {
        if (drawable == null) {
            mIconView.setVisibility(View.GONE);
        } else {
            mIconView.setVisibility(View.VISIBLE);
            mIconView.setImageDrawable(drawable);
        }
        return this;
    }

    public void setIconClickListener(OnClickListener listener) {
        mIconView.setOnClickListener(listener);
    }

    public ImageView getIconView() {
        return mIconView;
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public void onTextOverflowChanged(boolean isOverflow) {
        mHintView.setGravity(isOverflow ? Gravity.NO_GRAVITY : Gravity.RIGHT);
    }

}