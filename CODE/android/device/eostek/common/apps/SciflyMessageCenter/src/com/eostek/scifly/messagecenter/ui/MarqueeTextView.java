
package com.eostek.scifly.messagecenter.ui;

import com.eostek.scifly.messagecenter.util.Util;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class MarqueeTextView extends TextView {

    /** 是否停止滚动 */
    private boolean mStopMarquee;

    private String mText;

    private float mCoordinateX;

    private float mTextWidth;

    private Context mContext;

    public MarqueeTextView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
        this.mContext = context;
    }

    public void setText(String text) {
        this.mText = text;
        if (!Util.isRoll(text, 8)) {
            this.mStopMarquee = true;
            invalidate();
        } else {
            startMarquee();
        }
    }

    public void stopMarquee(String text) {
        mHandler.removeMessages(0);
        this.mStopMarquee = true;
    }

    public void startMarquee() {
        this.mStopMarquee = false;
        mTextWidth = getPaint().measureText(mText);
        if (mHandler.hasMessages(0)) {
            mHandler.removeMessages(0);
        }
        mHandler.sendEmptyMessageDelayed(0, 500);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mText != null)
            canvas.drawText(mText, mCoordinateX, dip2px(mContext, 21), getPaint());
    }

    private int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    } 

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mHandler.removeMessages(0);
                    if (!mStopMarquee) {
                        if (Math.abs(mCoordinateX) >= (mTextWidth)) {
                            mCoordinateX = 0;
                        } else {
                            mCoordinateX -= 1;
                        }
                        invalidate();
                        sendEmptyMessageDelayed(0, 100);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

}
