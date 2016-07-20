
package com.heran.launcher2.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class AutoScrollTextView extends TextView implements OnClickListener {
    public final static String TAG = AutoScrollTextView.class.getSimpleName();

    private float textLength = 0f;// 文本长度

    private float viewWidth = 0f;

    private float step = 0f;// 文字的横坐标

    private float y = 0f;// 文字的纵坐标

    private float temp_view_plus_text_length = 0.0f;// 用于计算的临时变量

    private float temp_view_plus_two_text_length = 0.0f;// 用于计算的临时变量

    public boolean isStarting = true;// 是否开始滚动

    private Paint paint = null;// 绘图样式

    private String text = "";// 文本内容

    private int mRecLen = 0;

    public AutoScrollTextView(Context context) {
        super(context);
        initView();
    }

    public AutoScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public AutoScrollTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        setOnClickListener(this);
    }

    public void init() {
        paint = new TextPaint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(25);
        text = getText().toString();
        textLength = paint.measureText(text);
        viewWidth = 1500;
        temp_view_plus_text_length = viewWidth;
        temp_view_plus_two_text_length = viewWidth + textLength;
        y = getTextSize() + getPaddingTop();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);

        ss.step = step;
        ss.isStarting = isStarting;

        return ss;

    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        step = ss.step;
        isStarting = ss.isStarting;

    }

    public static class SavedState extends BaseSavedState {
        public boolean isStarting = false;

        public float step = 0.0f;

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeBooleanArray(new boolean[] {
                isStarting
            });
            out.writeFloat(step);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }
        };

        private SavedState(Parcel in) {
            super(in);
            boolean[] b = null;
            in.readBooleanArray(b);
            if (b != null && b.length > 0)
                isStarting = b[0];
            step = in.readFloat();
        }
    }

    public void startScroll() {
        new Thread(new MyThread()).start();

        step = 0f;
        isStarting = true;
        invalidate();
    }

    public void stopScroll() {
        isStarting = false;
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {

        if (paint == null) {
            paint = new TextPaint();
            paint.setColor(Color.WHITE);
            paint.setTextSize(25);
        }
        canvas.drawText(text, temp_view_plus_text_length - step, y, paint);
        if (!isStarting) {
            return;
        }
        step += 2;// 文字滚动速度。
        if (step > temp_view_plus_two_text_length) {
            step = 0;
        }

        invalidate();

    }

    @Override
    public void onClick(View v) {
        if (isStarting) {
            stopScroll();
        } else {
            startScroll();
        }

    }

    /**
     * @param mOSDListener the mOSDListener to set
     */
    // public void setOSDListener(OSDListener mOSDListener) {
    // this.mOSDListener = mOSDListener;
    // }

    final Handler handler = new Handler() { // handle
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mRecLen++;
            }
            super.handleMessage(msg);
        }
    };

    public class MyThread implements Runnable { // thread
        @Override
        public void run() {
            while (isStarting) {
                try {
                    Thread.sleep(1000); // sleep 1000ms
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);

                } catch (Exception e) {
                }
            }
            Log.i(TAG, "MyThread stoped.");
        }
    }
}
