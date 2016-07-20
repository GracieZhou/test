
package com.eostek.tv.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ProgressText extends LinearLayout {

    private String mText;

    private float x;

    private Paint mPaint;

    public void setmText(String mText) {
        this.mText = mText;
    }

    public void setX(float x) {
        this.x = x;
    }

    public ProgressText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
    }

    public ProgressText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }

    public ProgressText(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public void refresh() {
        this.invalidate();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setColor(Color.WHITE);
            mPaint.setTextSize(dip2px(12));
            mPaint.setAntiAlias(true);
            mPaint.setTypeface(Typeface.DEFAULT);
            mPaint.setTextAlign(Paint.Align.LEFT);
        }
        if (TextUtils.isEmpty(mText)) {
            canvas.drawText("00:00:00", 0, dip2px(12), mPaint);
        } else {
            canvas.drawText(mText, x, dip2px(12), mPaint);
        }
    }

    private int dip2px(float dipValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
