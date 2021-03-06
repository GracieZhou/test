
package com.heran.launcher2.widget;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class BarChartView extends View {

    public BarChartView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub

        init(context, null);
    }

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init(context, attrs);
    }

    // 绘制文本的画笔
    private Paint titlePaint;

    // 矩形画笔 柱状图的样式信息
    private Paint recPaint;

    private void init(Context context, AttributeSet attrs) {

        titlePaint = new Paint();
        recPaint = new Paint();

        titlePaint.setColor(Color.WHITE);
        recPaint.setColor(Color.WHITE);

    }

    // 7 条

    private ArrayList<Integer> thisDatas;

    /**
     * 跟新自身的数据 需要View子类重绘。 主线程 刷新控件的时候调用： this.invalidate(); 失效的意思。
     * this.postInvalidate(); 可以子线程 更新视图的方法调用。
     */
    // updata this year data
    public void updateThisData(ArrayList<Integer> thisData) {
        thisDatas = thisData;
        // this.invalidate(); //失效的意思。
        this.postInvalidate(); // 可以子线程 更新视图的方法调用。
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight() - 5;

        int step = width / 8;
        Log.d("laird", "width:" + width);
        Log.d("laird", "height:" + height);
        Log.d("laird", "step:" + step);

        // 5 绘制矩形
        if (thisDatas != null && thisDatas.size() > 0) {
            int thisCount = thisDatas.size();

            for (int i = 0; i < thisCount; i++) {
                int value = thisDatas.get(i);

                Rect rect = new Rect();
                rect.left = step * (i) + 20;
                rect.right = step * (i) + 70;
                // 当前的相对高度：
                rect.top = (height - value * height / 100) + 15;
                rect.bottom = height;
                Log.d("laird", "rect:" + rect);
                canvas.drawRect(rect, recPaint);
                canvas.drawText(String.valueOf(value), rect.left + 20, rect.top, titlePaint);
            }
        }
    }

}
