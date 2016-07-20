
package com.android.server.scifly.theme;

import android.util.Log;

/**
 * debug cycle manager.
 * 
 * @author Youpeng
 * @date 2014-01-10.
 */
public class CycleManager extends Thread {

    public static final String TAG = "CycleManager";

    private Runnable mTask = null;

    private boolean runWhenStart = true;

    private int interval = 10000;

    private boolean isDebugMode = true;

    private Object lock = new Object();

    public CycleManager(Runnable task) {
        this.mTask = task;
    }

    @Override
    public void run() {
        super.run();

        while (runWhenStart) {
            try {

                if (!isDebugMode) {
                    // cut the cycle if it isn't debug mode.
                    synchronized (this) {
                        this.wait();
                    }
                } else {
                    // if task exists,run it.
                    synchronized (lock) {
                        if (mTask != null) {
                            mTask.run();
                        }
                    }

                    // execute logic later.
                    Thread.sleep(interval);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public synchronized void startCycle() {
        synchronized (lock) {
            print("startCycle.");
            isDebugMode = true;
        }

        this.notify();
    }

    public synchronized void stopCycle() {
        synchronized (lock) {
            print("stopCycle.");
            isDebugMode = false;
        }
        this.notify();
    }

    private void print(String str) {
        Log.i(TAG, str);
    }
}
