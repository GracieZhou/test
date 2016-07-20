
package com.android.settings.network.downloadspeed;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class DownloadSpeedProgressbar extends View {
    private int maxProgress = 100;

    private int progress = 80;

    private int progressStrokeWidth = 12;

    private String mCircleText = null;

    RectF oval;

    Paint paint;

    public DownloadSpeedProgressbar(Context context) {
        this(context, null);
    }

    public DownloadSpeedProgressbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        oval = new RectF();
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i("karaoke", ">>>>>>CircleBar---onDraw");
        int width = this.getWidth();
        int height = this.getHeight();

        if (width != height) {
            int min = Math.min(width, height);
            width = min;
            height = min;
        }

        paint.setAntiAlias(true);
        canvas.drawColor(Color.TRANSPARENT);
        paint.setColor(Color.argb(00, 00, 00, 00));
        canvas.drawCircle(width / 2, width / 2, width / 2, paint);
        // EosTek Patch Begin
        // comment : add color for download_speed's progressbar
        paint.setColor(Color.argb(48, 255, 255, 255));
        // EosTek Patch End
        paint.setStrokeWidth(progressStrokeWidth);
        paint.setStyle(Style.STROKE);

        // 9 is the outside black circle width
        oval.left = (progressStrokeWidth / 2) + 9;
        oval.top = (progressStrokeWidth / 2) + 9;
        oval.right = (width - progressStrokeWidth / 2) - 9;
        oval.bottom = (height - progressStrokeWidth / 2) - 9;

        canvas.drawArc(oval, -90, 360, false, paint);
        paint.setColor(Color.argb(255, 101, 230, 147));
        canvas.drawArc(oval, -90, ((float) progress / maxProgress) * 360, false, paint);

    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
        Log.i("karaoke", ">>>>>>setMaxProgress maxProgress = " + maxProgress);

    }

    public void setProgress(int progress) {
        this.progress = progress;
        this.invalidate();
        Log.i("karaoke", ">>>>>>setProgress progress = " + progress);
    }

    public void setMicText(String circleText) {
        mCircleText = circleText;
        Log.i("karaoke", ">>>>>>serCircleBarType mCircleText = " + mCircleText);
    }

}
