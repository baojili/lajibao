package com.ln.base.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import com.ln.base.R;
import com.ln.base.tool.DimenUtils;

import java.util.ArrayList;
import java.util.List;

public class WheelPicker extends View {

    private VelocityTracker mVelocityTracker;

    private Drawable mDividerDrawable, mForegroundDrawable;

    private OnSelectLineChangeListener mListener;

    private Scroller mScroller;

    private Adapter mAdapter;

    private Paint mSelectedPaint, mTextPaint, mHintTextPaint;

    private int mSelectedTextMaxWidth;

    private int mDividerHeight, mDividerOffset;

    private int mDividerTop, mDividerBottom;

    private int mFadingEdgeColor;

    private int mFadingEdgeLength;

    private int mSpacingLeft, mSpacingRight, mLineSpacingExtra;

    private int mLines, mLineHeight, mSelectLine;

    private float mLastDownOrMoveEventY;

    private int mTextGravity;

    private float mTextOffsetY;

    private float mSelectedTextOffSetY;

    private int mMinFlingVelocity, mMaxFlingVelocity;

    private int mScrollY, mMaxScrollY;

    private int mPreviousScrollerY;

    private boolean mCyclic;

    private String mHintText;

    private int mHintTextSpacing, mHintTextWidth;

    private float mExtraOffsetX;

    /**
     * 文字超出宽度时滚动的速率，默认20ms偏移一次;
     */
    private int mScrollRate = 20;

    /**
     * 单次水平移动，最小偏移单位, 默认每次移动2个像素;
     */
    private int mHorizonScrollUnit = 2;
    Runnable mHorizonScrollRunnable = new Runnable() {
        @Override
        public void run() {
            mExtraOffsetX += mHorizonScrollUnit;
            invalidate();
        }
    };
    /*
     * 是否允许文字走出宽度时，自动滚动；
     */
    private boolean mDisableMarquee;
    /**
     * 文字关再出现的时候与前一次文字显示末尾的间距，单位为dp
     */
    private int mMarqueeSpacing = 30;

    public WheelPicker(Context context) {
        this(context, null);
    }

    public WheelPicker(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.wheelPickerStyle);
    }

    public WheelPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mSelectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHintTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.WheelPicker, defStyle, 0);
        // 文字大小，颜色
        mSelectedPaint.setTextSize(typedArray.getDimensionPixelSize(
                R.styleable.WheelPicker_selectedTextSize,
                DimenUtils.sp2px(context, 16)));
        mSelectedPaint.setColor(typedArray
                .getColor(R.styleable.WheelPicker_android_textColorHighlight,
                        Color.BLACK));
        mTextPaint.setTextSize(typedArray.getDimensionPixelSize(
                R.styleable.WheelPicker_android_textSize,
                DimenUtils.sp2px(context, 14)));
        mTextPaint.setColor(typedArray.getColor(
                R.styleable.WheelPicker_android_textColor, Color.BLACK));
        mTextGravity = typedArray.getInt(
                R.styleable.WheelPicker_android_gravity, Gravity.CENTER);
        mHintText = typedArray.getString(R.styleable.WheelPicker_android_hint);
        mHintTextPaint.setTextSize(mSelectedPaint.getTextSize());
        mHintTextPaint.setColor(typedArray.getColor(
                R.styleable.WheelPicker_android_textColorHint, Color.GRAY));
        // 分割线
        mDividerDrawable = typedArray
                .getDrawable(R.styleable.WheelPicker_android_divider);
        int defaultDividerHeight = mDividerDrawable != null ? mDividerDrawable
                .getIntrinsicHeight() : -1;
        mDividerHeight = typedArray.getDimensionPixelSize(
                R.styleable.WheelPicker_android_dividerHeight,
                defaultDividerHeight < 0 ? 2 : defaultDividerHeight);
        mDividerOffset = typedArray.getDimensionPixelSize(
                R.styleable.WheelPicker_dividerOffset, 0);
        mForegroundDrawable = typedArray
                .getDrawable(R.styleable.WheelPicker_android_foreground);
        // 行数及行间隔
        mLines = typedArray.getInt(R.styleable.WheelPicker_android_lines, 3);
        if (mLines % 2 == 0) {
            mLines++;
        }
        mLineSpacingExtra = typedArray.getDimensionPixelSize(
                R.styleable.WheelPicker_android_lineSpacingExtra,
                (int) mTextPaint.getTextSize());
        mHintTextSpacing = typedArray.getDimensionPixelSize(
                R.styleable.WheelPicker_hintSpacing, 0);
        // 循环
        mCyclic = typedArray.getBoolean(R.styleable.WheelPicker_cyclic, false);
        // 边缘渐变
        mFadingEdgeLength = typedArray.getDimensionPixelSize(
                R.styleable.WheelPicker_android_fadingEdgeLength, 0);
        mFadingEdgeColor = typedArray.getColor(
                R.styleable.WheelPicker_fadingEdgeColor, 0);
        setVerticalFadingEdgeEnabled(mFadingEdgeLength > 0);
        setFadingEdgeLength(mFadingEdgeLength);
        typedArray.recycle();

        mSpacingLeft = getPaddingLeft();
        mSpacingRight = getPaddingRight();
//        setPadding(0, 0, 0, 0);

        ViewConfiguration configuration = ViewConfiguration.get(context);
        mMinFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = configuration.getScaledMaximumFlingVelocity();
        mScroller = new Scroller(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        /**
         * 计算出hint文字及hintSpacing占用的宽度，没有hintText,则宽度为0；
         */
        if (mHintText != null) {
            mHintTextWidth = (int) mHintTextPaint.measureText(mHintText)
                    + mHintTextSpacing;
        } else {
            mHintTextWidth = 0;
        }
        int[] result = computerMaxWidth();
        mSelectedTextMaxWidth = result[0];

        int width, height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = mSelectedTextMaxWidth + mSpacingLeft + mHintTextWidth
                    + mSpacingRight;
            if (widthMode == MeasureSpec.AT_MOST) {
                width = Math.min(widthSize, width);
            }
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = ((int) mTextPaint.getTextSize() + mLineSpacingExtra)
                    * mLines;
            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
            }
        }
        setMeasuredDimension(width, height);
    }

    /**
     * onSizeChange在View初始化的时候会调用一次，在这里算好高度的一些偏移值，控件的使用场景，高度一般是固定的，处理一次就可以了；
     * 文字内容由adapter控制，更新adapter时会更新item文字，这时文字宽度就会发生变化，但由于大多数场景控件宽度固定，onSizeChanged就不会再次调用，会影响到宽度偏移的计算，所以宽度的偏移就放在ondraw方法里面了
     */
    @SuppressLint("RtlHardcoded")
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mLineHeight = h / mLines;
        FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float fontHeight = fontMetrics.descent - fontMetrics.ascent;
        mTextOffsetY = mLineHeight + fontMetrics.leading - fontMetrics.descent
                - (mLineHeight - fontHeight) / 2.0f + mTextPaint.getTextSize() / 24;
        fontMetrics = mSelectedPaint.getFontMetrics();
        fontHeight = fontMetrics.descent - fontMetrics.ascent;
        mSelectedTextOffSetY = mLineHeight + fontMetrics.leading
                - fontMetrics.descent - (mLineHeight - fontHeight) / 2 + mSelectedPaint.getTextSize() / 24;
        if (mAdapter != null) {
            mScrollY = mSelectLine * mLineHeight;
            mMaxScrollY = mLineHeight * (mAdapter.getCount() - 1);
        } else {
            mSelectLine = mScrollY = mMaxScrollY = 0;
        }
        mDividerTop = mLines / 2 * mLineHeight - mDividerHeight / 2
                + mDividerOffset;
        mDividerBottom = mDividerTop + mLineHeight + mDividerHeight
                - mDividerOffset * 2;
    }

    @Override
    public int getSolidColor() {
        return mFadingEdgeColor;
    }

    @Override
    protected float getTopFadingEdgeStrength() {
        return 1.0f;
    }

    @Override
    protected float getBottomFadingEdgeStrength() {
        return 1.0f;
    }

    /**
     * 计算具体绘制时每一项的绘制起点偏移量
     *
     * @param maxTextWidth 整个列表中文字占用的最大宽度，这里一般使用文字最多的时被选中状态下的宽度，可以达到选中与相邻的非选中的文字看起来对齐居中的效果
     * @param realWidth    具体item文字的实际宽度
     * @return 具体item绘制起点的偏移量
     */
    private float getItemOffsetX(float maxTextWidth, float realWidth) {
        float textOffsetX;
        float maxSpace = getWidth() - mHintTextWidth - mSpacingLeft - mSpacingRight;
        switch (mTextGravity) {
            /**
             * gravity属于为left时，文字居左，但保持整列文字在居左的同时，居中对齐，（完全居左对齐效果较差一些）
             */
            case Gravity.LEFT:
            case Gravity.START:
                if (maxSpace < maxTextWidth) {
                    textOffsetX = maxSpace > realWidth ? mSpacingLeft + (maxSpace - realWidth) / 2.0f : mSpacingLeft;
                } else {
                    textOffsetX = mSpacingLeft + (maxTextWidth - realWidth) / 2.0f;
                }
                break;
            /**
             * gravity属于为right时，文字居右，但保持整列文字在居右的同时，居中对齐
             */
            case Gravity.RIGHT:
            case Gravity.END:
                if (maxSpace < maxTextWidth) {
                    textOffsetX = maxSpace > realWidth ? mSpacingLeft + (maxSpace - realWidth) / 2.0f : mSpacingLeft;
                } else {
                    textOffsetX = maxSpace + mSpacingLeft - (maxTextWidth + realWidth) / 2.0f;
                }
                break;
            /**
             * 文字整体居中对齐
             */
            default:
                if (maxSpace < maxTextWidth) {
                    textOffsetX = maxSpace > realWidth ? mSpacingLeft + (maxSpace - realWidth) / 2.0f : mSpacingLeft;
                } else {
                    textOffsetX = mSpacingLeft + (maxSpace - realWidth) / 2.0f;
                }
                break;
        }
        return textOffsetX;
    }

    /**
     * 绘制hint字符的起点偏移量，每一个控件hint字符的位置是固定不变的，不受文字列表的滚动影响
     *
     * @return
     */
    private float getHintTextOffsetX() {
        float hintTextOffsetX;
        float maxSpace = getWidth() - mSpacingLeft - mSpacingRight;
        switch (mTextGravity) {
            case Gravity.LEFT:
                hintTextOffsetX = mSpacingLeft + mSelectedTextMaxWidth + mHintTextSpacing;
                break;
            case Gravity.RIGHT:
                hintTextOffsetX = getWidth() - mSpacingRight - mHintTextPaint.measureText(mHintText);
                break;
            default:
                if (maxSpace < mSelectedTextMaxWidth + mHintTextWidth) {
                    hintTextOffsetX = getWidth() - mSpacingRight - mHintTextPaint.measureText(mHintText);
                } else {
                    hintTextOffsetX = mSpacingLeft + (maxSpace - mHintTextWidth - mSelectedTextMaxWidth) / 2.0f
                            + mSelectedTextMaxWidth + mHintTextSpacing;
                }
                break;
        }
        return hintTextOffsetX;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        removeCallbacks(mHorizonScrollRunnable);
        canvas.save();
        canvas.clipRect(mSpacingLeft, 0, getWidth() - mSpacingRight - mHintTextWidth, getHeight()); // 实现左右padding效果， 优先显示hintText
        if (mAdapter != null) {
            int firstLine = mScrollY / mLineHeight - mLines / 2;
            int lineOffsetY = mScrollY % mLineHeight;
            for (int i = -1; i <= mLines; i++) {
                float realTextWidth;
                int line = firstLine + i;
                if (mCyclic) {
                    line %= mAdapter.getCount();
                    if (line < 0) {
                        line += mAdapter.getCount();
                    } else if (line >= mAdapter.getCount()) {
                        line -= mAdapter.getCount();
                    }
                    if (getSelectLine() != line) {
                        realTextWidth = mTextPaint.measureText(mAdapter.getShowStr(line));
                        canvas.drawText(mAdapter.getShowStr(line), getItemOffsetX(mSelectedTextMaxWidth, realTextWidth), i
                                        * mLineHeight + mTextOffsetY - lineOffsetY,
                                mTextPaint);
                    } else {
                        realTextWidth = mSelectedPaint.measureText(mAdapter.getShowStr(line));
                        canvas.drawText(mAdapter.getShowStr(line), getItemOffsetX(mSelectedTextMaxWidth, realTextWidth) - mExtraOffsetX, i
                                        * mLineHeight + mSelectedTextOffSetY - lineOffsetY,
                                mSelectedPaint);
                        translateX(realTextWidth);
                    }
                } else if (line >= 0 && line < mAdapter.getCount()) {
                    if (getSelectLine() != line) {
                        realTextWidth = mTextPaint.measureText(mAdapter.getShowStr(line));
                        canvas.drawText(mAdapter.getShowStr(line), getItemOffsetX(mSelectedTextMaxWidth, realTextWidth), i
                                        * mLineHeight + mTextOffsetY - lineOffsetY,
                                mTextPaint);
                    } else {
                        realTextWidth = mSelectedPaint.measureText(mAdapter.getShowStr(line));
                        canvas.drawText(mAdapter.getShowStr(line), getItemOffsetX(mSelectedTextMaxWidth, realTextWidth) - mExtraOffsetX, i
                                * mLineHeight + mSelectedTextOffSetY
                                - lineOffsetY, mSelectedPaint);
                        float maxSpace = getWidth() - mHintTextWidth - mSpacingLeft - mSpacingRight;
                        /** 跑马灯循环滚动效果，在原来的Text后面再画一份*/
                        if (maxSpace < realTextWidth && !mDisableMarquee) {
                            canvas.drawText(mAdapter.getShowStr(line), getItemOffsetX(mSelectedTextMaxWidth, realTextWidth) - mExtraOffsetX + realTextWidth
                                    + DimenUtils.dp2px(getContext(), mMarqueeSpacing), i * mLineHeight + mSelectedTextOffSetY
                                    - lineOffsetY, mSelectedPaint);
                        }
                        translateX(realTextWidth);
                    }
                }
            }
        }
        canvas.restore();
        if (mHintText != null) {
            canvas.drawText(mHintText, getHintTextOffsetX(), mLines / 2
                    * mLineHeight + mSelectedTextOffSetY, mHintTextPaint);
        }
        if (mDividerDrawable != null && mDividerHeight > 0) {
            canvas.save();
            canvas.clipRect(0, mDividerTop, getWidth(), mDividerTop
                    + mDividerHeight);
            mDividerDrawable.setBounds(0, mDividerTop, getWidth(), mDividerTop
                    + mDividerHeight);
            mDividerDrawable.draw(canvas);
            canvas.restore();
            canvas.save();
            canvas.clipRect(0, mDividerBottom - mDividerHeight, getWidth(),
                    mDividerBottom);
            mDividerDrawable.setBounds(0, mDividerBottom - mDividerHeight,
                    getWidth(), mDividerBottom);
            mDividerDrawable.draw(canvas);
            canvas.restore();
        }

        if (mForegroundDrawable != null) {
            canvas.save();
            canvas.clipRect(0, mDividerTop + mDividerHeight, getWidth(),
                    mDividerBottom - mDividerHeight);
            mForegroundDrawable.setBounds(0, mDividerTop + mDividerHeight,
                    getWidth(), mDividerBottom - mDividerHeight);
            mForegroundDrawable.draw(canvas);
            canvas.restore();
        }
    }

    public void setHorzScrollUnit(int horzScrollUnit) {
        this.mHorizonScrollUnit = horzScrollUnit;
    }

    public void setDisableMarquee(boolean disableMarquee) {
        this.mDisableMarquee = disableMarquee;
    }

    public void setmScrollRate(int mScrollRate) {
        this.mScrollRate = mScrollRate;
    }

    /**
     * 文字在画面中的x偏移控制， 实现跑马灯效果
     *
     * @param realTextWidth 文字实际测量宽度
     */
    private void translateX(float realTextWidth) {
        if (mDisableMarquee) {
            return;
        }
        float maxSpace = getWidth() - mHintTextWidth - mSpacingLeft - mSpacingRight;
        if (realTextWidth > maxSpace && realTextWidth + DimenUtils.dp2px(getContext(), mMarqueeSpacing) > mExtraOffsetX) {
            if (mExtraOffsetX == 0) {
                postDelayed(mHorizonScrollRunnable, DateUtils.SECOND_IN_MILLIS);
            } else {
                postDelayed(mHorizonScrollRunnable, mScrollRate);
            }
        } else if (realTextWidth > maxSpace && realTextWidth + DimenUtils.dp2px(getContext(), mMarqueeSpacing) <= mExtraOffsetX) {
            mExtraOffsetX = -mHorizonScrollUnit;
            postDelayed(mHorizonScrollRunnable, DateUtils.SECOND_IN_MILLIS);
        } else {
            mExtraOffsetX = 0;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled() || mAdapter == null) {
            return false;
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                mLastDownOrMoveEventY = event.getY();
                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float currentMoveY = event.getY();
                addScrollY((int) (mLastDownOrMoveEventY - currentMoveY));
                invalidate();
                mLastDownOrMoveEventY = currentMoveY;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
                int initialVelocity = (int) mVelocityTracker.getYVelocity();
                if ((!mCyclic && (mScrollY < 0 || mScrollY > mMaxScrollY))
                        || Math.abs(initialVelocity) < mMinFlingVelocity) {
                    adjustWheel();
                } else {
                    mPreviousScrollerY = 0;
                    mScroller.fling(0, 0, 0, -initialVelocity, 0, 0,
                            Integer.MIN_VALUE, Integer.MAX_VALUE);
                }
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (mScroller.isFinished()) {
            return;
        }
        mScroller.computeScrollOffset();
        int currentScrollerY = mScroller.getCurrY();
        if (mPreviousScrollerY == 0) {
            mPreviousScrollerY = mScroller.getStartY();
        }
        addScrollY(currentScrollerY - mPreviousScrollerY);
        mPreviousScrollerY = currentScrollerY;
        if (mScroller.isFinished()) {
            adjustWheel();
        } else {
            invalidate();
        }
    }

    private void addScrollY(int deltaY) {
        if (mCyclic) {
            mScrollY += deltaY;
            if (mScrollY < 0) {
                mScrollY = mMaxScrollY + mLineHeight + mScrollY;
            } else if (mScrollY - mMaxScrollY >= mLineHeight) {
                mScrollY = mScrollY - mMaxScrollY - mLineHeight;
            }
        } else {
            if (mScroller.isFinished()) {
                if (mScrollY > mMaxScrollY && deltaY > 0) {
                    mScrollY += deltaY
                            / ((mScrollY - mMaxScrollY) / mLineHeight + 5);
                } else if (mScrollY < 0 && deltaY < 0) {
                    mScrollY += deltaY / (-mScrollY / mLineHeight + 5);
                } else {
                    mScrollY += deltaY;
                }
            } else {
                if (mScrollY - mMaxScrollY > mLineHeight && deltaY > 0) {
                    mScroller.forceFinished(true);
                } else if (-mScrollY > mLineHeight && deltaY < 0) {
                    mScroller.forceFinished(true);
                } else {
                    mScrollY += deltaY;
                }
            }
        }

        if (mAdapter != null) {
            int line = mScrollY / mLineHeight;
            if (mScrollY % mLineHeight > mLineHeight / 2) {
                line++;
            }
            if (line < 0) {
                line = mCyclic ? mAdapter.getCount() - 1 : 0;
            } else if (line > mAdapter.getCount() - 1) {
                line = mCyclic ? 0 : mAdapter.getCount() - 1;
            }
            if (line != mSelectLine) {
                mSelectLine = line;
                if (mListener != null) {
                    mListener.onSelectLineChange(this, mSelectLine);
                    mExtraOffsetX = 0;
                }
            }
        }
    }

    private int[] computerMaxWidth() {
        int[] result = {0, 0};
        if (mAdapter == null) {
            return result;
        } else {
            float maxSelectedWidth = 0;
            float maxWidth = 0;
            for (int i = 0; i < mAdapter.getCount(); i++) {
                maxSelectedWidth = Math.max(
                        mSelectedPaint.measureText(mAdapter.getShowStr(i)), maxSelectedWidth);
                maxWidth = Math.max(
                        mTextPaint.measureText(mAdapter.getShowStr(i)), maxWidth);
            }
            result[0] = (int) maxSelectedWidth;
            result[1] = (int) maxWidth;
            return result;
        }
    }

    private void adjustWheel() {
        int offset;
        if (mCyclic && mScrollY > mMaxScrollY) {
            offset = mScrollY % mLineHeight;
            if (offset >= mLineHeight / 2) {
                offset = mLineHeight - offset;
            } else {
                offset = -offset;
            }
        } else {
            offset = mSelectLine * mLineHeight - mScrollY;
        }
        if (offset != 0) {
            mPreviousScrollerY = 0;
            mScroller.startScroll(0, 0, 0, offset, 600);
            invalidate();
        }
    }

    public void setAdapter(Adapter adapter, int line) {
        setAdapter(adapter, line, false);
    }

    public void setAdapter(Adapter adapter, int line, boolean smoothScroll) {
        mAdapter = adapter;
        if (mAdapter == null) {
            mSelectLine = mScrollY = mMaxScrollY = 0;
            invalidate();
            return;
        }
        mMaxScrollY = mLineHeight * (mAdapter.getCount() - 1);
        setSelectLine(line, smoothScroll);
    }

    public Adapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(Adapter adapter) {
        setAdapter(adapter, 0);
        requestLayout();
    }

    public String getSelectItemShowStr() {
        if (mAdapter == null)
            return null;
        Object object = mAdapter.getItem(getSelectLine());
        if (object instanceof WheelPickerItem) {
            return ((WheelPickerItem) object).getShowStr();
        }
        return (String) object;
    }

    public Object getSelectItem() {
        if (mAdapter == null)
            return null;
        return mAdapter.getItem(getSelectLine());
    }

    public void setSelectLine(int line, boolean smoothScroll) {
        if (mAdapter != null) {
            line = Math.max(0, line);
            line = Math.min(mAdapter.getCount() - 1, line);
            if (getWidth() > 0 && smoothScroll) {
                mPreviousScrollerY = 0;
                int offset = mLineHeight * line - mScrollY;
                if (offset != 0) {
                    offset += (offset > 0 ? 1 : -1) * mLineHeight / 3;
                    mScroller.startScroll(0, 0, 0, offset, 200);
                }
            } else {
                mSelectLine = line;
                mScrollY = line * mLineHeight;
                if (mListener != null) {
                    mListener.onSelectLineChange(this, mSelectLine);
                    mExtraOffsetX = 0;
                }
            }
            invalidate();
        }
    }

    public int getSelectLine() {
        return mSelectLine;
    }

    public void setSelectLine(int line) {
        setSelectLine(line, false);
    }

    public void setOnSelectLineChangeListener(
            OnSelectLineChangeListener listener) {
        mListener = listener;
    }

    public interface OnSelectLineChangeListener {
        void onSelectLineChange(WheelPicker wheelPicker, int selectLine);
    }

    public interface Adapter {

        int getCount();

        Object getItem(int position);

        String getShowStr(int position);
    }

    public static abstract class WheelPickerItem {
        private String showStr;

        public String getShowStr() {
            return showStr;
        }

        public void setShowStr(String showStr) {
            this.showStr = showStr;
        }
    }

    public static class NumberAdapter implements Adapter {

        public static final int SORT_ASC = 0;
        public static final int SORT_DESC = 1;
        private int mMin, mMax;
        private String mPattern;
        private int sort = SORT_ASC;

        public NumberAdapter(int min, int max) {
            this(min, max, null);
        }

        public NumberAdapter(int min, int max, int sort) {
            this(min, max, null);
            this.sort = sort;
        }

        public NumberAdapter(int min, int max, String pattern) {
            mMin = Math.min(min, max);
            mMax = Math.max(min, max);
            mPattern = pattern;
        }

        @Override
        public int getCount() {
            return mMax - mMin + 1;
        }

        @Override
        public String getItem(int position) {
            if (mPattern == null) {
                if (sort == SORT_DESC) {
                    return String.valueOf(mMax - position);
                } else {
                    return String.valueOf(mMin + position);
                }
            }
            if (sort == SORT_DESC) {
                return String.format(mPattern, mMax - position);

            } else {
                return String.format(mPattern, mMin + position);

            }
        }

        @Override
        public String getShowStr(int position) {
            return getItem(position);
        }
    }

    public static class StringListAdapter implements Adapter {

        private List<String> mItemList;

        public StringListAdapter(List<String> itemList) {
            if (itemList == null) {
                mItemList = new ArrayList<>();
            } else {
                mItemList = itemList;
            }
        }

        @Override
        public int getCount() {
            return mItemList.size();
        }

        @Override
        public String getItem(int position) {
            return mItemList.get(position);
        }

        @Override
        public String getShowStr(int position) {
            return getItem(position);
        }
    }

    public static class WheelPickerItemListAdapter<T extends WheelPickerItem> implements Adapter {

        private List<T> mItemList;

        public WheelPickerItemListAdapter(List<T> itemList) {
            if (itemList == null) {
                mItemList = new ArrayList<>();
            } else {
                mItemList = itemList;
            }
        }

        @Override
        public int getCount() {
            return mItemList.size();
        }

        @Override
        public T getItem(int position) {
            return mItemList.get(position);
        }

        @Override
        public String getShowStr(int position) {
            return getItem(position).getShowStr();
        }

    }

    public static class StringArrayAdapter implements Adapter {

        private String[] mItems;

        public StringArrayAdapter(String[] items) {
            if (items == null) {
                mItems = new String[0];
            } else {
                mItems = items;
            }
        }

        @Override
        public int getCount() {
            return mItems.length;
        }

        @Override
        public String getItem(int position) {
            return mItems[position];
        }

        @Override
        public String getShowStr(int position) {
            return getItem(position);
        }

    }

}