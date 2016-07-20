
package com.heran.launcher2.widget;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/**
 * projectName： HLauncher2 moduleName：LineChartView.java
 * 
 * @author laird.li
 * @version 1.0.0
 * @time 2016-03-30
 * @Copyright © 2016 Eos, Inc.
 */
public class LineChartView extends View {
    private static final String TAG = "LineChartView";

    private static final int CIRCLE_SIZE = 8;

    private Context mContext;

    private Paint mPaint1;

    private Paint mPaint2;

    private DisplayMetrics dm;

    private Canvas canvas;

    /**
     * data
     */
    private int canvasHeight;

    private int canvasWidth;

    private int bheight = 0;

    private int blwidh;

    private boolean isMeasure = true;

    /**
     * Y轴最大值
     */
    private int maxValue;

    /**
     * 曲线上总点数
     */
    private Point[] mPoints1;

    private Point[] mPoints2;

    /**
     * 纵坐标值
     */
    private ArrayList<Double> y1RawData;

    private ArrayList<Double> y2RawData;

    private ArrayList<Integer> xList = new ArrayList<Integer>();// 记录每个x的值

    public LineChartView(Context context) {
        this(context, null);

    }

    public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();

    }

    private void initView() {
        Log.d(TAG, "MyChartView init");
        this.mPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        canvas = new Canvas();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG, "onSizeChanged");
        if (isMeasure) {
            this.canvasHeight = getHeight();
            this.canvasWidth = getWidth();
            Log.d(TAG, "canvasHeight : " + canvasHeight);
            Log.d(TAG, "canvasWidth : " + canvasWidth);
            if (bheight == 0)
                bheight = canvasHeight;
            // 起始間隔
            blwidh = dip2px(0);
            isMeasure = false;
        }
    }

    @Override
    protected void onDraw(Canvas mCanvas) {
        Log.d(TAG, "onDraw");
        if (y1RawData == null || y2RawData == null) {
            return;
        }
        this.canvas = mCanvas;

        // 画直线（纵向）
        initX(canvas);
        // 点的操作设置
        mPoints1 = getPoints(y1RawData);
        mPoints2 = getPoints(y2RawData);

        drawScrollLine(canvas);

        mPaint1.setStyle(Style.FILL);
        mPaint2.setStyle(Style.FILL);
        Paint txtpaint1 = new Paint();
        txtpaint1.setTextSize(10);// 設定字體大小
        txtpaint1.setColor(Color.parseColor("#ffffff"));// 設定字體顏色
        txtpaint1.setTextAlign(Paint.Align.CENTER);
        Paint txtpaint2 = new Paint();
        txtpaint2.setTextSize(10);// 設定字體大小
        txtpaint2.setColor(Color.parseColor("#ffffff"));// 設定字體顏色
        txtpaint2.setTextAlign(Paint.Align.CENTER);

        mPaint1.setColor(Color.parseColor("#ffffff"));
        mPaint1.setStrokeWidth(dip2px(1.5f));

        mPaint2.setColor(Color.parseColor("#ffffff"));
        mPaint2.setStrokeWidth(dip2px(1.5f));

        for (int i = 0; i < mPoints1.length; i++) {
            canvas.drawCircle(mPoints1[i].x, mPoints1[i].y, CIRCLE_SIZE / 2, mPaint1);
            canvas.drawText(String.valueOf(y1RawData.get(i)), mPoints1[i].x, mPoints1[i].y - 10, txtpaint1);
        }

        for (int i = 0; i < mPoints2.length; i++) {
            canvas.drawCircle(mPoints2[i].x, mPoints2[i].y, CIRCLE_SIZE / 2, mPaint2);
            canvas.drawText(String.valueOf(y2RawData.get(i)), mPoints2[i].x, mPoints2[i].y + 15, txtpaint2);
        }
    }

    /**
     * 画所有纵向表格，包括Y轴
     */
    private void initX(Canvas canvas) {
        Log.d(TAG, "initX");
        Log.d(TAG, "blwidh : " + blwidh);
        int index_weigth = canvasWidth / (y1RawData.size());
        for (int i = 0; i < y1RawData.size(); i++) {
            xList.add(index_weigth * i + index_weigth / 2);
            Log.d(TAG, "xList[" + i + "]:" + xList.get(i));
        }

    }

    private void drawScrollLine(Canvas canvas) {
        Log.d(TAG, "drawScrollLine");
        Path path1 = new Path();
        mPaint1.setStyle(Paint.Style.FILL);
        mPaint1.setColor(Color.parseColor("#3B5168"));
        mPaint1.setAlpha(90);
        path1.moveTo(0, canvasHeight);
        path1.lineTo(0, mPoints1[0].y);
        path1.lineTo(mPoints1[0].x, mPoints1[0].y);
        path1.lineTo(mPoints1[1].x, mPoints1[1].y);
        path1.lineTo(mPoints1[2].x, mPoints1[2].y);
        path1.lineTo(mPoints1[3].x, mPoints1[3].y);
        path1.lineTo(mPoints1[4].x, mPoints1[4].y);
        path1.lineTo(mPoints1[5].x, mPoints1[5].y);
        path1.lineTo(mPoints1[6].x, mPoints1[6].y);
        path1.lineTo(canvasWidth, mPoints1[6].y);
        path1.lineTo(canvasWidth, canvasHeight);
        path1.close();
        canvas.drawPath(path1, mPaint1);

        Path path2 = new Path();
        mPaint2.setStyle(Paint.Style.FILL);
        mPaint2.setColor(Color.parseColor("#8B97A3"));
        mPaint2.setAlpha(90);
        path2.moveTo(0, canvasHeight);
        path2.lineTo(0, mPoints2[0].y);
        path2.lineTo(mPoints2[0].x, mPoints2[0].y);
        path2.lineTo(mPoints2[1].x, mPoints2[1].y);
        path2.lineTo(mPoints2[2].x, mPoints2[2].y);
        path2.lineTo(mPoints2[3].x, mPoints2[3].y);
        path2.lineTo(mPoints2[4].x, mPoints2[4].y);
        path2.lineTo(mPoints2[5].x, mPoints2[5].y);
        path2.lineTo(mPoints2[6].x, mPoints2[6].y);
        path2.lineTo(canvasWidth, mPoints2[6].y);
        path2.lineTo(canvasWidth, canvasHeight);
        path2.close();
        canvas.drawPath(path2, mPaint2);

    }

    private Point[] getPoints(ArrayList<Double> yRawData) {
        Log.d(TAG, "getPoints");
        Point[] points = new Point[yRawData.size()];
        for (int i = 0; i < yRawData.size(); i++) {

            Log.d("laird--", "yRawData.get(i):" + yRawData.get(i));
            int ph = bheight - (int) (bheight * (yRawData.get(i) / maxValue)) + 20;

            points[i] = new Point(xList.get(i), ph);
            Log.d(TAG, "getPoints max ph :" + ph);
        }

        return points;
    }

    public void setData(ArrayList<Double> y1RawData, ArrayList<Double> y2RawData, int maxValue, int averageValue) {

        Log.d(TAG, "setData");
        this.maxValue = maxValue;
        this.mPoints1 = new Point[y1RawData.size()];
        this.mPoints2 = new Point[y2RawData.size()];
        this.y1RawData = y1RawData;
        this.y2RawData = y2RawData;

        invalidate();

    }

    public void setTotalvalue(int maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(float dpValue) {
        return (int) (dpValue * dm.density + 0.5f);
    }

}
