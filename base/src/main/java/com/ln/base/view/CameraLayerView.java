package com.ln.base.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.ln.base.R;

public class CameraLayerView extends View {

    private Paint bgPaint, imageXfPaint, paint;

    private Bitmap bitmap;

    private int width, height;

    public CameraLayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPain();
    }

    public CameraLayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPain();
    }

    public CameraLayerView(Context context) {
        super(context);
        initPain();
    }

    private void initPain() {
        bgPaint = new Paint();
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(Color.parseColor("#99000000"));

        paint = new Paint();

        imageXfPaint = new Paint();
        imageXfPaint.setStyle(Paint.Style.FILL);
        imageXfPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        imageXfPaint.setColor(getResources().getColor(R.color.transparent));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            width = getWidth();
            height = getHeight();
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if (bitmap == null)
            return;
        if (bitmap.getHeight() > height * 0.9) {
            //适配小屏幕
            bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * height * 0.9 / bitmap.getHeight()), (int) (height * 0.9), true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            canvas.drawRect(new Rect(0, 0, width, height), bgPaint);
            canvas.drawBitmap(bitmap, width / 2 - bitmap.getWidth() / 2, height / 2 - bitmap.getHeight() / 2, imageXfPaint);
        }
        //解决采用setXfermode之后imageXFpaint的颜色不管用，所以先画出透明区域，再在透明区域画图片
        canvas.drawBitmap(bitmap, width / 2 - bitmap.getWidth() / 2, height / 2 - bitmap.getHeight() / 2, paint);
    }

    public void setIsFront(boolean isFront) {
        if (isFront) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_layer_front);
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_layer_back);
        }
        invalidate();
    }
}
