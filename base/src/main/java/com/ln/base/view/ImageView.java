package com.ln.base.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.ln.base.R;


@SuppressLint("AppCompatCustomView")
public class ImageView extends android.widget.ImageView {

    private Drawable mLoadingDrawable, mErrorDrawable;
    private float mRatioX, mRatioY;
    private float mRotate;

    private RectF mBorderRectF;
    private RectF mBorderRadiusRectF;
    private PorterDuffXfermode mBorderXfermode;
    private Paint mBorderPaint;
    private float mBorderRadius;
    private float mBorderWidth;
    private float mBorderPadding;

    public ImageView(Context context) {
        this(context, null);
    }

    public ImageView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.imageViewStyle);
    }

    public ImageView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, 0, 0, 0, 0, 0, 0);
    }

    public ImageView(Context context, float ratioX, float ratioY) {
        this(context, null, R.attr.imageViewStyle, ratioX, ratioY, 0, 0, 0, 0);
    }

    public ImageView(Context context, float borderRadius, float borderWidth, float borderPadding, int borderColor) {
        this(context, null, R.attr.imageViewStyle, 0, 0, borderRadius, borderWidth, borderPadding, borderColor);
    }

    public ImageView(Context context, AttributeSet attrs, int defStyle, float ratioX, float ratioY, float borderRadius,
                     float borderWidth, float borderPadding, int borderColor) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageView, defStyle, 0);
        mLoadingDrawable = a.getDrawable(R.styleable.ImageView_loadingDrawable);
        mErrorDrawable = a.getDrawable(R.styleable.ImageView_errorDrawable);
        mRatioX = a.getFloat(R.styleable.ImageView_ratioX, ratioX);
        mRatioY = a.getFloat(R.styleable.ImageView_ratioY, ratioY);
        mBorderRadius = a.getDimension(R.styleable.ImageView_borderRadius, borderRadius);
        if (mBorderRadius > 0) {
            mBorderRadiusRectF = new RectF();
            mBorderXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        }
        mBorderWidth = a.getDimension(R.styleable.ImageView_borderWidth, borderWidth);
        if (mBorderWidth > 0) {
            mBorderPadding = a.getDimension(R.styleable.ImageView_borderPadding, borderPadding);
            mBorderRectF = new RectF();
            mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mBorderPaint.setStyle(Style.STROKE);
            mBorderPaint.setColor(a.getColor(R.styleable.ImageView_borderColor, borderColor));
            mBorderPaint.setStrokeWidth(mBorderWidth);
        }
        if (a.getResourceId(R.styleable.ImageView_loadingDrawable, -1) == R.drawable.ic_loading) {
            mRotate = -1;
        } else {
            mRotate = -2;
        }
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mRatioX > 0 && mRatioY > 0) {
            if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
                int widthSize = MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = (int) (widthSize * mRatioY / mRatioX);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
            } else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
                int heightSize = MeasureSpec.getSize(heightMeasureSpec);
                int widthSize = (int) (heightSize * mRatioX / mRatioY);
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (mLoadingDrawable != null) {
            final int loadingWidth = mLoadingDrawable.getIntrinsicWidth();
            final int left = (w - loadingWidth) >> 1;
            final int top = (h - loadingWidth) >> 1;
            mLoadingDrawable.setBounds(left, top, left + loadingWidth, top + loadingWidth);
        }
        if (mBorderRadiusRectF != null) {
            mBorderRadiusRectF.left = mBorderPadding;
            mBorderRadiusRectF.top = mBorderPadding;
            mBorderRadiusRectF.right = w - mBorderPadding;
            mBorderRadiusRectF.bottom = h - mBorderPadding;
        }
        if (mBorderRectF != null) {
            mBorderRectF.left = mBorderWidth / 2;
            mBorderRectF.top = mBorderRectF.left;
            mBorderRectF.right = w - mBorderRectF.left;
            mBorderRectF.bottom = h - mBorderRectF.top;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mRotate < 0 || mLoadingDrawable == null) {
            if (getDrawable() instanceof BorderBitmapDrawable) {
                if (mBorderRadius > 0) {
                    final Paint paint = ((BorderBitmapDrawable) getDrawable()).getPaint();
                    final int saveCount = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);
                    paint.setAntiAlias(true);
                    canvas.drawRoundRect(mBorderRadiusRectF, mBorderRadius, mBorderRadius, paint);
                    paint.setXfermode(mBorderXfermode);
                    super.onDraw(canvas);
                    paint.setXfermode(null);
                    canvas.restoreToCount(saveCount);
                } else {
                    super.onDraw(canvas);
                }
                if (mBorderWidth > 0) {
                    canvas.drawRoundRect(mBorderRectF, mBorderRadius, mBorderRadius, mBorderPaint);
                }
            } else {
                super.onDraw(canvas);
            }
        } else {
            canvas.rotate(mRotate, getWidth() >> 1, getHeight() >> 1);
            mLoadingDrawable.draw(canvas);
            mRotate = (mRotate + 3) % 360;
            invalidate();
        }
    }

    public void showLoading() {
        if (mRotate < -1) {
            setImageDrawable(mLoadingDrawable);
        } else {
            mRotate = 0;
            invalidate();
        }
    }

    public void showError() {
        setImageDrawable(mErrorDrawable);
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        if (mBorderRadius > 0 || mBorderWidth > 0) {
            setImageDrawable(new BorderBitmapDrawable(getResources(), bitmap));
        } else {
            setImageDrawable(new BitmapDrawable(getResources(), bitmap));
        }
    }

    public void setErrorDrawable(int resId) {
        mErrorDrawable = getResources().getDrawable(resId);
    }

    public void setLoadingDrawable(int resId) {
        mLoadingDrawable = getResources().getDrawable(resId);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        mRotate = -2;
        super.setImageDrawable(drawable);
    }

    @Override
    public void setImageResource(int resId) {
        mRotate = -2;
        super.setImageResource(resId);
    }

    private final static class BorderBitmapDrawable extends BitmapDrawable {

        public BorderBitmapDrawable(Resources resources, Bitmap bitmap) {
            super(resources, bitmap);
        }

    }

}