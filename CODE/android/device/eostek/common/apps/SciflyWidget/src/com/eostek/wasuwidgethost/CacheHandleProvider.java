
package com.eostek.wasuwidgethost;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManagerExtra;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.eostek.scifly.widget.R;

/**
 * projectName：WasuWidgetHost.
 * moduleName： CacheHandleProvider.java
 *
 * @author vicky.wang
 * @version 1.0.0
 * @time  2014-8-14 4:30 pm
 * @Copyright © 2014 Eos Inc.
 */
public class CacheHandleProvider extends BaseAppWidgetProvider {

    private static final String TAG = CacheHandleProvider.class.getSimpleName();

    private static final String CLICK_NAME_ACTION = "com.eostek.wasuwidgethost.CacheHandleProvider";

    private static final int CLEAN_TO_ZERO = 0;

    private static final int SHOW_CURRENT_FROM_ZERO = 1;

    private static final int SHOW_CURRENT_USED_MEMORY = 2;

    private static final int MSG_ANIMATION_DELAY_TIME = 5;

    private long mTotalMemory = 0;

    private static ActivityManager mActivityManager;

    private static Context mContext;

    private static RemoteViews rv;

    private static int beforeClean = 0;

    private static int curProcess = 0;

    private static int afterClean = 0;
    
    private final static int MSG_SHOW_CURRENT_DELAY_TIME = 10 * 1000;

    // the flag whether the clean animation running
    private static volatile boolean isCleaning = false;

    private static int[] mAppWidgetIds;

    private Bitmap circleBitmap = null;

    // the flag whether show the circle
    private static boolean mShowCircle = false;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CLEAN_TO_ZERO:
                    // show clean animation from current to beforeClean,then
                    // start animation from 0 to afterClean
                    if (curProcess > 2) {
                        isCleaning = true;
                        curProcess -= 2;
                        updateWidgetView(curProcess, true);
                        mHandler.sendEmptyMessageDelayed(CLEAN_TO_ZERO, MSG_ANIMATION_DELAY_TIME);
                    } else {
                        curProcess = 0;
                        updateWidgetView(curProcess, true);
                        mHandler.sendEmptyMessageDelayed(SHOW_CURRENT_FROM_ZERO, MSG_ANIMATION_DELAY_TIME);
                    }
                    break;
                case SHOW_CURRENT_FROM_ZERO:
                    if (curProcess < afterClean) {
                        curProcess += 2;
                        updateWidgetView(curProcess, true);
                        mHandler.sendEmptyMessageDelayed(SHOW_CURRENT_FROM_ZERO, MSG_ANIMATION_DELAY_TIME);
                    } else {
                        curProcess = afterClean;
                        isCleaning = false;
                        mShowCircle = false;
                        updateWidgetView(curProcess, false);
                    }
                    break;
                case SHOW_CURRENT_USED_MEMORY:
                    mShowCircle = false;
                    mHandler.removeMessages(SHOW_CURRENT_USED_MEMORY);
                    // refresh the used memory every five minite
                    if (!isCleaning) {
                        curProcess = (int) getUsedMemoryPercent(mContext);
                        Log.i(TAG, "update circle percentage");
                    }
                    updateWidgetView(curProcess, mShowCircle);
                    break;
                default:
                    break;
            }
        }
    };

    private void updateWidgetView(int percent, boolean showCircl) {
        // update widget view
        if (mAppWidgetIds != null) {
            for (int id : mAppWidgetIds) {
                updateWidgetView(id, curProcess, showCircl);
            }
        } else {
            Log.d(TAG, "updateWidgetView mAppWidgetIds = null");
        }
    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.v(TAG, "onUpdate ");
        mContext = context;
        // clear former widget to avoid create too much widget object
        clearFormerWidget(CacheHandleProvider.class.getName());

        // init the widget view
        mAppWidgetIds = appWidgetIds;
        Log.d(TAG, "CacheHandleProvider mAppWidgetIds=" + mAppWidgetIds);
        curProcess = (int) getUsedMemoryPercent(context);
        for (int id : mAppWidgetIds) {
            updateWidgetView(id, curProcess, mShowCircle);
        }
        
        mHandler.sendEmptyMessageDelayed(SHOW_CURRENT_USED_MEMORY, MSG_SHOW_CURRENT_DELAY_TIME);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.v(TAG, "action = " + intent.getAction() + "; cleaning = " + isCleaning);
        mContext = context;
        if (intent.getAction().equals(CLICK_NAME_ACTION) && !isCleaning) {
            startCleanAnimation(context);
        }
        if (intent.getAction().equals("com.eostek.wasuwidgethost.updatewidget")) {
            String cacheId = intent.getStringExtra("cacheId");
            int widgetID;
            if (cacheId == null) {
                widgetID = 0;
            } else {
                widgetID = Integer.parseInt(cacheId);
            }
            curProcess = (int) getUsedMemoryPercent(context);
            updateWidgetView(widgetID, curProcess, mShowCircle);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.v(TAG, "onDeleted");
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.v(TAG, "onEnabled");
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.v(TAG, "onDisabled");
    }

    /**
     * start clean cache animation 1.show animation frombeforeClean to 0 2.show.
     * animation from 0 to afterClean
     * 
     * @param context
     */
    private void startCleanAnimation(Context context) {
        Log.v(TAG, "startCleanAnimation");
        beforeClean = (int) getUsedMemoryPercent(context);
        curProcess = beforeClean;
        mShowCircle = true;
        // start animation
        mHandler.sendEmptyMessage(CLEAN_TO_ZERO);
        // release memory
        killProcesses();
        afterClean = (int) getUsedMemoryPercent(context);
    }

    /**
     * show the cache used pecentage and udpate the widget.
     * 
     * @param widgetID The widget ID
     * @param percent
     */
    synchronized private void updateWidgetView(int widgetID, int percent, boolean showCircle) {
        //release the former bitmap
        if (circleBitmap != null) {
            circleBitmap.recycle();
        }

        rv = new RemoteViews(mContext.getPackageName(), R.layout.cache_ui);
        circleBitmap = drawProgress(200, 200, percent, true, showCircle);
        rv.setImageViewBitmap(R.id.circlebar, circleBitmap);

        if (Locale.getDefault().getCountry().equals("TW")) {
            rv.setViewVisibility(R.id.image_tw, View.VISIBLE);
            rv.setViewVisibility(R.id.image, View.GONE);
            rv.setViewVisibility(R.id.image_en, View.GONE);
        } else if (Locale.getDefault().getCountry().equals("US")) {
            rv.setViewVisibility(R.id.image_en, View.VISIBLE);
            rv.setViewVisibility(R.id.image, View.GONE);
            rv.setViewVisibility(R.id.image_tw, View.GONE);
        } else if (Locale.getDefault().getCountry().equals("CN")) {
            rv.setViewVisibility(R.id.image, View.VISIBLE);
            rv.setViewVisibility(R.id.image_tw, View.GONE);
            rv.setViewVisibility(R.id.image_en, View.GONE);
        }

        // set click listener
        Intent intentClick = new Intent(CLICK_NAME_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intentClick, 0);
        rv.setOnClickPendingIntent(R.id.layout, pendingIntent);
        if (mAppWidgetManager == null) {
            mAppWidgetManager = AppWidgetManager.getInstance(mContext);
        }
        mAppWidgetManager.updateAppWidget(widgetID, rv);
    }

    /**
     * get used memory percentage.
     * 
     * @param context
     * @return
     */
    private long getUsedMemoryPercent(Context context) {
        if (mActivityManager == null) {
            mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        }
        if (mTotalMemory == 0) {
            mTotalMemory = getTotalMemory();
        }
        MemoryInfo info = new MemoryInfo();
        mActivityManager.getMemoryInfo(info);
        // info.availMem 剩余内存.
        return 100 - (info.availMem / 1024) * 100 / mTotalMemory;
    }

    /**
     * get total memory.
     * 
     * @return
     */
    private long getTotalMemory() {
        long mTotal;
        String path = "/proc/meminfo";
        String content = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path), 8);
            String line = br.readLine();
            if (line != null) {
                content = line;
            } else {
                content = "";
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (content != null) {
         // beginIndex
            int begin = content.indexOf(':');
            // endIndex
            int end = content.indexOf('k');
            content = content.substring(begin + 1, end).trim();
            mTotal = Integer.parseInt(content);
            return mTotal;
        }
        return 0;
    }

    /**
     * kill backgroud apks to save cache memory.
     */
    private void killProcesses() {
        try {
            ActivityManagerExtra ame = ActivityManagerExtra.getInstance();
            ame.killAllBackgroundApks();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * draw circle progress and pecentage.
     * 
     * @param width
     * @param height
     * @param progress
     * @param mIsCleaning
     * @return
     */
    synchronized private Bitmap drawProgress(int width, int height, int progress, boolean isCleaning, boolean showCircle) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        int padding = 7;
        width -= padding * 2;
        height -= padding * 2;
        Canvas canvas = new Canvas(bitmap);
        int progressStrokeWidth = 16;

        RectF oval = new RectF();
        Paint paint = new Paint();

        paint.setAntiAlias(true); // 设置画笔为抗锯齿
        paint.setColor(Color.rgb(57, 64, 71)); // 设置画笔颜色
        canvas.drawColor(Color.TRANSPARENT); // 白色背景
        paint.setStrokeWidth(progressStrokeWidth); // 线宽
        paint.setStyle(Style.STROKE);

        oval.left = padding + progressStrokeWidth / 2; // 左上角x
        oval.top = padding + progressStrokeWidth / 2; // 左上角y
        oval.right = padding + width - progressStrokeWidth / 2; // 左下角x
        oval.bottom = padding + height - progressStrokeWidth / 2; // 右下角y

        // only show circle when the cleaning animation is running
        Log.d(TAG, "progress=" + progress + ", showCircle=" + showCircle);
        if (showCircle) {
            paint.setColor(Color.WHITE); // Color.rgb(0x57, 0x87, 0xb6)
            canvas.drawArc(oval, -90, 360, false, paint); // 绘制白色圆圈，即进度条背景

            paint.setColor(getColor(progress));
            canvas.drawArc(oval, -90, ((float) progress / 100) * 360, false, paint); // 绘制进度圆弧，这里是蓝色
        }

        paint.setColor(Color.WHITE);
        paint.setShader(null);

        int textHeight = height;
        String text = progress + "";
        paint.setTextSize(textHeight * 3 / 8f);
        int textWidth = (int) paint.measureText(text, 0, text.length());
        paint.setStyle(Style.FILL);
        canvas.drawText(text, width / 2f - textWidth * 5 / 10f, height * 3 / 5f, paint);

        paint.setTextSize(textHeight / 6f); 
        canvas.drawText("%", width * 7 / 10f, height * 3 / 5f, paint);

        return bitmap;
    }

    /**
     * get get circle color.
     * 
     * @param perent
     * @return
     */
    private int getColor(int perent) {
        float redPer = (0xff - 0x76) / 100;
        float greenPer = 0xff / 100;
        int red = (int) (0x76 + redPer * perent);
        int green = (int) (0xff - greenPer * perent);
        if (perent > 0 && perent <= 50) {
            return Color.rgb(0x00, 0xff, 0x00);
        } else if (perent > 50 && perent <= 70) {
            return Color.rgb(red, green, 0x00);
        } else if (perent > 70 && perent <= 90) {
            return Color.rgb(red, green, 0x00);
        } else {
            return Color.rgb(0xff, 0x00, 0x00);
        }
    }

}
