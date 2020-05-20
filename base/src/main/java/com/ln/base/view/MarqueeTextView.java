package com.ln.base.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class MarqueeTextView extends AppCompatTextView {

    private OnTextOverflowChangeListener mListener;

    public MarqueeTextView(Context context) {
        this(context, null);
        setSingleLine(true);
        setHorizontalFadingEdgeEnabled(true);
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setMarqueeRepeatLimit(-1);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSingleLine(true);
        setHorizontalFadingEdgeEnabled(true);
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setMarqueeRepeatLimit(-1);
    }

    @Override
    public boolean isFocused() {
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mListener != null) {
            mListener.onTextOverflowChanged(isOverflowed());
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (getWidth() > 0 && mListener != null) {
            mListener.onTextOverflowChanged(isOverflowed());
        }
    }

    private boolean isOverflowed() {
        return getPaint().measureText(getText().toString()) > (getWidth() - getPaddingLeft() - getPaddingRight());
    }

    public void setOnTextChangedListener(OnTextOverflowChangeListener listener) {
        mListener = listener;
    }

    public interface OnTextOverflowChangeListener {
        void onTextOverflowChanged(boolean isOverflow);
    }

}