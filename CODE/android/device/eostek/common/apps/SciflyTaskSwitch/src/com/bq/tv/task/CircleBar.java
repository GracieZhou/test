
package com.bq.tv.task;

import com.eos.notificationcenter.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 *  Draw a circle to show the inherent occupancy.
 */
public class CircleBar extends View {
    private int maxProgress = 100;

    private int progress = 1;

    private int progressStrokeWidth = 15;

    private Context mContext;

    private boolean isAnimationPlaying = false;

    // 画圆所在的距形区域
    RectF oval;

    Paint paint;

    /**
     * Simple constructor to use when creating a view from code.
     * @param context
     */
    public CircleBar(Context context) {
        this(context, null);
        this.mContext = context;
    }

    /**
     * Constructor with attribute.
     * @param context
     * @param attrs
     */
    public CircleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        oval = new RectF();
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();

        if (width != height) {
            int min = Math.min(width, height);
            width = min;
            height = min;
        }

        paint.setAntiAlias(true); // 设置画笔为抗锯齿
        paint.setColor(Color.rgb(57, 64, 71)); // 设置画笔颜色
        canvas.drawColor(Color.TRANSPARENT); // 白色背景
        paint.setStrokeWidth(progressStrokeWidth); // 线宽
        paint.setStyle(Style.STROKE);

        oval.left = progressStrokeWidth / 2; // 左上角x
        oval.top = progressStrokeWidth / 2; // 左上角y
        oval.right = width - progressStrokeWidth / 2; // 左下角x
        oval.bottom = height - progressStrokeWidth / 2; // 右下角y

        canvas.drawArc(oval, -90, 360, false, paint); // 绘制白色圆圈，即进度条背景
        paint.setColor(Color.rgb(23, 177, 179));// Color.rgb(0x57, 0x87, 0xb6)
        canvas.drawArc(oval, -90, ((float) progress / maxProgress) * 360, false, paint); // 绘制进度圆弧，这里是蓝色

        paint.setStrokeWidth(1);
        paint.setColor(Color.WHITE);
        String text = progress + "";
        int textHeight = height / 4;
        paint.setTextSize(textHeight);
        int textWidth = (int) paint.measureText(text, 0, text.length());
        paint.setStyle(Style.FILL);
        canvas.drawText(text, width / 2 - textWidth * 3 / 5, height * 3 / 7, paint);

        paint.setTextSize(textHeight / 2);
        canvas.drawText("%", width * 3 / 5, height * 3 / 7, paint);

        try {
            canvas.drawText(mContext.getResources().getString(R.string.memory_used), width / 4, height * 2 / 3, paint);
        } catch (Exception e) {
            e.printStackTrace();
            canvas.drawText("...", width / 4, height * 2 / 3, paint);
        }

    }

    /**
     * Get the maximum progress.
     * @return
     */
    public int getMaxProgress() {
        return maxProgress;
    }

    /**
     * Set the maximum progress.
     * @param maxProgress
     */
    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    /**
     * Set the progress.
     * @param progress
     */
    public void setProgress(int progress) {
        if (!isAnimationPlaying) {
            this.progress = progress;
            this.invalidate();
        }
    }

    /**
     * Play an animation.
     * @param oldValue
     * @param newValue
     */
    public void playAnimation(final int oldValue, final int newValue) {

        /** should not be called before animation ended */
        if (isAnimationPlaying) {
            return;
        }

        isAnimationPlaying = true;
        new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = oldValue; i >= 0; i--) {
                    try {
                        double acceleration = 1 - i / (oldValue * 1.0) > 0.5 ? 1 - i / (oldValue * 1.0) : 0.5;
                        Thread.sleep((long) (20 * acceleration));
                        setProgressNotInUiThread(i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                for (int i = 0; i <= newValue; i++) {
                    try {
                        double acceleration = 1 - i / (newValue * 1.0) > 0.5 ? 1 - i / (newValue * 1.0) : 0.5;

                        Thread.sleep((long) (20 * acceleration));
                        setProgressNotInUiThread(i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                isAnimationPlaying = false;
            }
        }).start();
    }

    /**
     * 非ＵＩ线程调用.
     */
    public void setProgressNotInUiThread(int progress) {
        this.progress = progress;
        this.postInvalidate();
    }

    /**Get progress.
     * @return
     */
    public int getProgress() {
        return progress;
    }

}
