
package com.eostek.isynergy.setmeup.common;

import android.util.Log;

public class TimeoutThread extends Thread {
    private int timeout = 0;

    private final String TAG = "TimeoutThread";

    public TimeoutThread(int timeout, Runnable task) {
        super(task);
        this.timeout = timeout;

        TimeoutException exception = new TimeoutException("This thread is interupped because of timeout!");
        TimeoutMonitorThread monitor = new TimeoutMonitorThread(this.timeout, exception, this);
        try {
            monitor.start();
        } catch (TimeoutException ex) {
            ex.printStackTrace();
        }
    }

    private class TimeoutMonitorThread extends Thread {
        /**
         * 计时器超时时间
         */
        private long timeout;

        /**
         * 当计时器超时时抛出的异常
         */
        private TimeoutException timeoutException;

        /**
         * 构造器
         * 
         * @param timeout 指定超时的时间
         */
        public TimeoutMonitorThread(long timeout, TimeoutException timeoutErr, Thread currentThread) {

            super();

            this.timeout = timeout;

            this.timeoutException = timeoutErr;

            // 设置本线程为守护线程

            this.setDaemon(true);

            this.setUncaughtExceptionHandler(new UncaughtExceptionHandlerImpl(currentThread));

        }

        /**
         * 启动超时计时器
         */
        @Override
        public void run() {
            try {
                Thread.sleep(timeout);

                throw timeoutException;
            } catch (InterruptedException e) {

                e.printStackTrace();

            }

        }

        /**
         * 对线程中抛出异常的处理
         */
        private class UncaughtExceptionHandlerImpl implements Thread.UncaughtExceptionHandler {

            private Thread currentThread;

            public UncaughtExceptionHandlerImpl(Thread currentThread) {

                this.currentThread = currentThread;

            }

            public void uncaughtException(Thread t, Throwable e) {

                // currentThread.interrupt();
                Log.d(TAG, "dev name thread time out ");
                currentThread.stop();
            }

        }
    }

    private class TimeoutException extends RuntimeException {
        /**
         * 序列化号
         */
        private static final long serialVersionUID = -8078853655388692688L;

        public TimeoutException(String errMessage) {
            super(errMessage);
        }
    }
}
