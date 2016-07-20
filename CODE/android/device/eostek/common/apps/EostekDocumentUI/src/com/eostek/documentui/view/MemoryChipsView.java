
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
import android.widget.RelativeLayout;

import com.eostek.documentui.R;
import com.eostek.documentui.util.Utils;

@SuppressLint("DrawAllocation")
public class MemoryChipsView extends RelativeLayout {

    private Context mContext;

    private int mWidth;

    /**
     * the view xml height
     */
    private int mHeight = 16;

    /**
     * memory total size
     */
    private int memorySize = 10;

    /**
     * memory used size
     */
    private int memoryUsedSize = 6;

    private Paint mTextPaint;

    private Paint mBgPaint;

    private int textCenterVerticalBaselineY;

    private int drawTextwidth = 0;

    /**
     * 
     * @Title:  MemoryChipsView.   
     * @Description:    constructor.   
     * @param:  @param context
     * @param:  @param attrs
     * @param:  @param defStyle.  
     * @throws
     */
    public MemoryChipsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

    /**
     * 
     * @Title:  MemoryChipsView.   
     * @Description:    constructor.   
     * @param:  @param context
     * @param:  @param attrs.  
     * @throws
     */
    public MemoryChipsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    /**
     * 
     * @Title:  MemoryChipsView.   
     * @Description:    constructor.   
     * @param:  @param context.  
     * @throws
     */
    public MemoryChipsView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    /**
     * @Title: init.
     * @Description: 初始化(这里用一句话描述这个方法的作用).
     * @param: .
     * @return: void.
     * @throws
     */
    public void init() {
        mWidth = mContext.getResources().getDisplayMetrics().widthPixels;
        mHeight = Utils.dip2px(mContext, mHeight);
        mTextPaint = new Paint();
        mTextPaint.setTextSize(20);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStrokeWidth(5);
        mTextPaint.setTextAlign(Align.CENTER);

        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setStyle(Style.FILL);
        mBgPaint.setStrokeWidth(3);
        mBgPaint.setColor(mContext.getResources().getColor(R.color.green));

        FontMetrics fm = mTextPaint.getFontMetrics();
        // textCenterVertical
        textCenterVerticalBaselineY = (int) (mHeight/ 2 - fm.descent + (fm.bottom - fm.top) / 2);
        drawTextwidth = (int) mTextPaint.measureText(memorySize + "G");
    }

    /**
     * @Title: setMemoryUsedSize.
     * @Description: set MemoryUsedSize.
     * @param: @param size.
     * @return: void.
     * @throws
     */
    public void setMemoryUsedSize(int size) {
        this.memoryUsedSize = size;
        refresh();
    }

    /**
     * @Title: refresh.
     * @Description: refresh ui.
     * @param: .
     * @return: void.
     * @throws
     */
    public void refresh() {
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int memoryUsedLayoutWidth = (int) ((float) memoryUsedSize / memorySize * mWidth);
        if (memoryUsedSize > 0) {
            //draw green bg
            Rect rect = new Rect(0, 0, memoryUsedLayoutWidth, mHeight);
            canvas.drawRect(rect, mBgPaint);
            // draw memoryUsedText
            mTextPaint.setColor(mContext.getResources().getColor(R.color.memory_backgroud));
            canvas.drawText(memoryUsedSize + "G", (memoryUsedLayoutWidth - drawTextwidth) / 2,
                    textCenterVerticalBaselineY, mTextPaint);

        }
        // draw memoryLeftText
        mTextPaint.setColor(mContext.getResources().getColor(android.R.color.white));
        canvas.drawText(memorySize - memoryUsedSize + "G", (mWidth - memoryUsedLayoutWidth) / 2 + memoryUsedLayoutWidth
                - drawTextwidth / 2, textCenterVerticalBaselineY, mTextPaint);
    }
}
