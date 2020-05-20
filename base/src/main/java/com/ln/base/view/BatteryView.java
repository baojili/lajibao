package com.ln.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.ln.base.R;

/**
 * 自定义水平\垂直电池控件
 */
public class BatteryView extends View {
    private int mPower = 0;
    private int orientation;
    private int width;
    private int height;
    private int mBorderColor;
    private float mBorderWidth;
    private int mBackgroundColor;
    private int mTextColor;
    private int mTextSize;
    private Paint mPaint;
    private Paint mTextPaint;

    public BatteryView(Context context) {
        this(context, null);
    }

    public BatteryView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.batteryViewStyle);
    }

    public BatteryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.BatteryView, defStyle, 0);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderColor = typedArray.getColor(R.styleable.BatteryView_borderColor, Color.BLACK);
        mBackgroundColor = typedArray.getColor(R.styleable.BatteryView_capacityBackgroundColor, Color.TRANSPARENT);
        mTextColor = typedArray.getColor(R.styleable.BatteryView_android_textColor, Color.WHITE);
        mBorderWidth = typedArray.getDimension(R.styleable.BatteryView_borderWidth, getResources().getDimension(R.dimen.radius_big));
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.BatteryView_android_textSize, getResources().getDimensionPixelSize(R.dimen.size_small));
        orientation = typedArray.getInt(R.styleable.BatteryView_orientation, 0);
        mPower = typedArray.getInt(R.styleable.BatteryView_power, mPower);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        /**
         * recycle() :官方的解释是：回收TypedArray，以便后面重用。在调用这个函数后，你就不能再使用这个TypedArray。
         * 在TypedArray后调用recycle主要是为了缓存。当recycle被调用后，这就说明这个对象从现在可以被重用了。
         *TypedArray 内部持有部分数组，它们缓存在Resources类中的静态字段中，这样就不用每次使用前都需要分配内存。
         */
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //对View上的內容进行测量后得到的View內容占据的宽度
        width = getMeasuredWidth();
        //对View上的內容进行测量后得到的View內容占据的高度
        height = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //判断电池方向    horizontal: 0   vertical: 1
        if (orientation == 0) {
            drawHorizontalBattery(canvas);
        } else {
            drawVerticalBattery(canvas);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * 绘制水平电池
     *
     * @param canvas
     */
    private void drawHorizontalBattery(Canvas canvas) {
        float borderWidthHalf = mBorderWidth / 2.0f;
        float headWidth = mBorderWidth;
        //画电池身体
        mPaint.reset();
        mPaint.setColor(mBorderColor);
        mPaint.setStrokeWidth(mBorderWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        RectF bodyF = new RectF(borderWidthHalf, borderWidthHalf, width - headWidth - borderWidthHalf, height - borderWidthHalf);
        canvas.drawRect(bodyF, mPaint);
        //画电池头
        mPaint.reset();
        mPaint.setColor(mBorderColor);
        mPaint.setStrokeWidth(0);
        mPaint.setStyle(Paint.Style.FILL);
        RectF headF = new RectF(width - headWidth, height * 0.25f, width, height * 0.75f);
        canvas.drawRect(headF, mPaint);
        //画电池内电量矩形背景
        mPaint.reset();
        mPaint.setColor(mBackgroundColor);
        mPaint.setStrokeWidth(0);
        mPaint.setStyle(Paint.Style.FILL);
        float offset1 = width - mBorderWidth - headWidth;
        RectF emptyF = new RectF(mBorderWidth, mBorderWidth, offset1, height - mBorderWidth);
        canvas.drawRect(emptyF, mPaint);
        //画电池内矩形电量
        mPaint.reset();
        if (mPower < 30) {
            mPaint.setColor(getResources().getColor(R.color.red));
        }
        if (mPower >= 30 && mPower < 50) {
            mPaint.setColor(getResources().getColor(R.color.blue));
        }
        if (mPower >= 50) {
            mPaint.setColor(getResources().getColor(R.color.green_dark));
        }
        float offset2 = offset1 * mPower / 100.0f;
        RectF capacityF = new RectF(mBorderWidth, mBorderWidth, offset2, height - mBorderWidth);
        canvas.drawRect(capacityF, mPaint);
        //画电池内电量数值
        mTextPaint.reset();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        float textWidth = mTextPaint.measureText(String.valueOf(mPower));
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        canvas.drawText(String.valueOf(mPower), width / 2.0f - headWidth - textWidth / 2.0f, (height - fontMetrics.bottom + fontMetrics.top) / 2.0f - fontMetrics.top, mTextPaint);
    }

    /**
     * 绘制垂直电池
     *
     * @param canvas
     */
    private void drawVerticalBattery(Canvas canvas) {
        float borderWidthHalf = mBorderWidth / 2.0f;
        float headHeight = mBorderWidth;
        //画电池头
        mPaint.reset();
        mPaint.setStrokeWidth(0);
        mPaint.setColor(mBorderColor);
        mPaint.setStyle(Paint.Style.FILL);
        RectF headF = new RectF(width * 0.25f, 0, width * 0.75f, headHeight);
        canvas.drawRect(headF, mPaint);
        //画电池身体
        mPaint.reset();
        mPaint.setStrokeWidth(mBorderWidth);
        mPaint.setColor(mBorderColor);
        mPaint.setStyle(Paint.Style.STROKE);
        RectF bodyF = new RectF(borderWidthHalf, headHeight + borderWidthHalf, width - borderWidthHalf, height - borderWidthHalf);
        canvas.drawRect(bodyF, mPaint);
        //画电池电量矩形背景
        mPaint.reset();
        mPaint.setStrokeWidth(0);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mBackgroundColor);
        RectF emptyF = new RectF(mBorderWidth, headHeight + mBorderWidth, width - mBorderWidth, height - mBorderWidth);
        canvas.drawRect(emptyF, mPaint);
        //画电池内矩形电量
        mPaint.reset();
        if (mPower < 30) {
            mPaint.setColor(getResources().getColor(R.color.red));
        }
        if (mPower >= 30 && mPower < 50) {
            mPaint.setColor(getResources().getColor(R.color.blue));
        }
        if (mPower >= 50) {
            mPaint.setColor(getResources().getColor(R.color.green_dark));
        }
        float topOffset = (height - headHeight - mBorderWidth * 2.0f) * (100 - mPower) / 100.0f;
        RectF capacityF = new RectF(mBorderWidth, headHeight + mBorderWidth + topOffset, width - mBorderWidth, height - mBorderWidth);
        canvas.drawRect(capacityF, mPaint);
        //画电池内电量数值
        mTextPaint.reset();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        float textWidth = mTextPaint.measureText(String.valueOf(mPower));
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        canvas.drawText(String.valueOf(mPower), width / 2.0f - textWidth / 2.0f, (height - fontMetrics.bottom + fontMetrics.top) / 2.0f - fontMetrics.top + headHeight, mTextPaint);
    }

    /**
     * 设置电池颜色
     *
     * @param color
     */
    public void setBorderColor(int color) {
        this.mBorderColor = color;
        invalidate();
    }

    /**
     * 设置电池容量背景颜色
     *
     * @param color
     */
    public void setCapacityBackgroundColor(int color) {
        this.mBackgroundColor = color;
        invalidate();
    }

    /**
     * 获取电池电量
     *
     * @return
     */
    public int getPower() {
        return mPower;
    }

    /**
     * 设置电池电量
     *
     * @param power
     */
    public void setPower(int power) {
        this.mPower = power;
        if (mPower < 0) {
            mPower = 0;
        }
        if (mPower > 100) {
            mPower = 100;
        }
        invalidate();//刷新VIEW
    }
}