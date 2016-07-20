
package com.eostek.documentui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.eostek.documentui.R;
import com.eostek.documentui.util.Utils;

/**
 * @ClassName: DownloadSpeedLayout.
 * @Description:refresh speed and pencent view.
 * @author: lucky.li.
 * @date: Sep 18, 2015 9:29:28 AM.
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved.
 */
@SuppressLint("DrawAllocation")
public class DownloadSpeedLayout extends LinearLayout {

    private Context mContext;

    private int mSpeed;

    private float mPercent;

    private Paint mTextPaint;

    private Paint mBgPaint;

    private int mWidth;

    private int mHeight = 15;

    private int textCenterVerticalBaselineY;

    /**
     * @Title: DownloadSpeedLayout.
     * @Description: constructor.
     * @param: @param context
     * @param: @param attrs
     * @param: @param defStyle.
     * @throws
     */
    public DownloadSpeedLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        initViews();
    }

    /**
     * @Title: DownloadSpeedLayout.
     * @Description: constructor.
     * @param: @param context
     * @param: @param attrs.
     * @throws
     */
    public DownloadSpeedLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initViews();
    }

    /**
     * @Title: DownloadSpeedLayout.
     * @Description: constructor.
     * @param: @param context.
     * @throws
     */
    public DownloadSpeedLayout(Context context) {
        super(context);
        this.mContext = context;
        initViews();
    }

    /**
     * @Title: initViews.
     * @Description: initial controls and paints.
     * @param: .
     * @return: void.
     * @throws
     */
    private void initViews() {
        setBackgroundColor(mContext.getResources().getColor(R.color.memory_backgroud));
        mHeight = Utils.dip2px(mContext, 15);
        mWidth = Utils.dip2px(mContext, 115);

        mTextPaint = new Paint();
        mTextPaint.setTextSize(15);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStrokeWidth(5);
        mTextPaint.setTextAlign(Align.CENTER);

        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setStyle(Style.FILL);
        mBgPaint.setStrokeWidth(3);
        mBgPaint.setColor(mContext.getResources().getColor(R.color.downloding_speed_bg_color));

        FontMetrics fm = mTextPaint.getFontMetrics();
        // textCenterVertical
        textCenterVerticalBaselineY = (int) (mHeight / 2 - fm.descent + (fm.bottom - fm.top) / 2);
    }

    /**
     * @Title: refresh.
     * @Description: refresh ui.
     * @param: @param mSpeed
     * @param: @param mPercent.
     * @return: void.
     * @throws
     */
    public void refresh(int mSpeed, float mPercent) {
        if (mSpeed != this.mSpeed || mPercent != this.mPercent) {
            this.mSpeed = mSpeed;
            this.mPercent = mPercent;
            invalidate();
        }
    }

    public void setSpeed(int mSpeed) {
        this.mSpeed = mSpeed;
    }

    public void setPercent(float mPercent) {
        this.mPercent = mPercent;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int pencentLayoutWidth = (int) (mPercent * mWidth);
        // draw bg
        Rect rect = new Rect(0, 0, pencentLayoutWidth, mHeight);
        canvas.drawRect(rect, mBgPaint);
        // draw speed text
        mTextPaint.setColor(mContext.getResources().getColor(R.color.speed_text_color));
        canvas.drawText(Utils.formatFileSize(mSpeed)+"/S", Utils.dip2px(mContext, 28), textCenterVerticalBaselineY,
                mTextPaint);
        // draw pencent text
        mTextPaint.setColor(mContext.getResources().getColor(android.R.color.white));
        String percentString = "— —";
        if (mPercent != 0) {
            percentString = Utils.formatFloatAccordingDigit(mPercent * 100, 100) + "%";
        }
        canvas.drawText(percentString, mWidth - 30, textCenterVerticalBaselineY, mTextPaint);
    }
}
