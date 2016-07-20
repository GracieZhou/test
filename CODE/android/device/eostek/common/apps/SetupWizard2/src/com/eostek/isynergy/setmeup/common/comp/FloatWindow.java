/*
 * 文 件 名: FloatWindow.java
 * 修 改 人:  yangf
 * 修改时间: 2012-2-29
 * 修改内容: <修改内容>
 */

package com.eostek.isynergy.setmeup.common.comp;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

/**
 * <一句话功能简述> <功能详细描述>
 * 
 * @version [版本号, 2012-2-29]
 */

public class FloatWindow {

    private final String TAG = "FloatWindow";

    private Context context;

    private WindowManager wm;

    private View view;

    private DisplayMetrics metrics;

    private WindowManager.LayoutParams param;

    // 步长 step length
    private int stepLen = 1;

    // 移动方向 1:右边；-1：左边
    private int directory = 1;

    /**
     * 停止自动移动线程
     */
    private Boolean isStop = false;

    // 移动线程停止信号
    private final int MOVE_FLAG = 0;

    // 悬浮窗 关闭信号
    public static final int WINDOW_CLOSE_FLAG = 1;

    private HandlerThread thread;

    private Handler handler;

    private Thread movThread = new Thread() {
        @Override
        public void run() {
            while (!isStop) {
                Message mes = handler.obtainMessage();
                mes.what = MOVE_FLAG;
                handler.sendMessage(mes);
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.e(TAG, "*********************************************I am existing");
        }
    };

    public FloatWindow(Context context, View view, boolean isAutoMove) {
        this.context = context;
        this.view = view;

        thread = new HandlerThread("float window");
        thread.start();

        handler = new Handler(thread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                int what = msg.what;
                if (what == MOVE_FLAG) {
                    move();
                } else if (what == WINDOW_CLOSE_FLAG) {
                    close();
                }
            }
        };

        init(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, false);

        // movThread.start();

        isStop = !isAutoMove;
    }

    /**
     * <默认构造函数>
     */
    public FloatWindow(Context context, View view, boolean isAutoMove, int winType, boolean focusable) {
        this.context = context;
        this.view = view;

        init(winType, focusable);

        isStop = !isAutoMove;
    }

    /**
     * 初始化浮动窗的参数
     * 
     * @param winType 参见WindowManager.LayoutParams.type
     * @param focusable true: 可获取焦点 false:不可获取焦点
     */
    private void init(int winType, boolean focusable) {
        wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        // 设置LayoutParams(全局变量）相关参数
        param = new WindowManager.LayoutParams();

        param.type = winType; // 系统提示类型,重要
        param.format = 1;
        if (!focusable) {
            param.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL; // 不能抢占聚焦点
        }
        param.flags = param.flags | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        param.flags = param.flags | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS; // 排版不受限制

        param.alpha = 1.0f;

        param.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角

        // 屏幕尺寸
        metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);

    }

    /**
     * 初始化第一次显示的位置
     * 
     * @param x 左上顶点 x
     * @param y 左上顶点 y
     * @param width 浮动窗口宽度
     * @param height 浮动窗口高度
     */
    public void initPosition(int x, int y, int width, int height) {
        param.x = x;
        param.y = y;

        // 设置悬浮窗口长宽数据
        param.width = width;
        param.height = height;
    }

    /**
     * 默认居中显示，可通过偏移量相对于中心点偏移
     * 
     * @param leftOffset 左边偏移
     * @param topOffset 顶部偏移
     * @param rightOffset 右边偏移
     * @param bottomOffset 底部偏移
     */
    public void centerPosition(int width, int height, int leftOffset, int topOffset, int rightOffset, int bottomOffset) {

        int x = (metrics.widthPixels - width) / 2;

        int y = (metrics.heightPixels - height) / 2;

        if (leftOffset > 0) {
            x -= leftOffset;
            rightOffset = 0;
        }

        if (rightOffset > 0) {
            x += rightOffset;
        }

        if (topOffset > 0) {
            y -= topOffset;
            bottomOffset = 0;
        }

        if (bottomOffset > 0) {
            y += bottomOffset;
        }

        initPosition(x, y, width, height);
    }

    public void show() {
        /*
         * synchronized(isStop){ isStop.notifyAll(); }
         */
        Log.d(TAG, "移动线程是否仍然活动============================================" + this.movThread.isAlive());
        wm.addView(view, param);
        movThread.start();

    }

    /**
     * 自动移动的方法
     */
    private void move() {
        if (param.x + param.width >= metrics.widthPixels) {
            directory = -1;
        } else if (param.x <= 0) {
            directory = 1;
        }

        param.x += directory * stepLen;
        wm.updateViewLayout(view, param);
    }

    /**
     * 非自动移动的移动控制 用于遥控的方向键控制
     * 
     * @param x x坐标
     * @param y y坐标
     */
    public void move(int x, int y) {
        if (param.x + param.width >= metrics.widthPixels) {
            return;
        } else if (param.x <= 0) {
            return;
        }

        param.x = x;
        param.y = y;
        wm.updateViewLayout(view, param);
    }

    /**
     * 更新悬浮框内容
     * 
     * @param view
     */
    public void updateView() {
        // this.view = view;
        wm.updateViewLayout(this.view, param);
    }

    public View getView() {
        return view;
    }

    /**
     * 释放线程
     */
    private void release() {
        isStop = true;
    }

    /**
     * 关闭悬浮窗
     */
    private void close() {
        if (!isStop) {
            release();
        }
        if (view != null) {
            Log.d(TAG, "deleting subview of float window...");
            wm.removeView(view);
            view = null;
        }

        if (thread != null) {
            handler.removeMessages(WINDOW_CLOSE_FLAG);
            thread.quit();
        }
    }

    public static int getCloseMes() {
        return WINDOW_CLOSE_FLAG;
    }

    public Handler getHandler() {
        return handler;
    }

    /**
     * 获取顶点X轴左标
     * 
     * @return
     */
    public int getX() {
        return param.x;
    }

    /**
     * 获取顶点Y轴左标
     * 
     * @return
     */
    public int getY() {
        return param.y;
    }

}
