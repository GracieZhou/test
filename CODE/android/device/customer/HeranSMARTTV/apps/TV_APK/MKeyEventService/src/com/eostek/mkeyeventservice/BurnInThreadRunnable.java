
package com.eostek.mkeyeventservice;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

public class BurnInThreadRunnable implements  Runnable {
    private Context mContext;

    private boolean isClose = true;

    private boolean isPause = false;
	
	static final String TAG = "MKeyEventHolder";

    private static BurnInThreadRunnable single = null;

    public BurnInThreadRunnable(Context context) {
        mContext = context;
    }

    public synchronized static BurnInThreadRunnable getInstance(Context context) {
        if (single == null) {
            single = new BurnInThreadRunnable(context);
        }
        return single;
    }

    public synchronized void Burn() {
        this.notify();
    }

    public synchronized void onThreadPause() {
        isPause = true;
    }

    private void onThreadWait() {
        try {
            synchronized (this) {
                this.wait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void onThreadResume() {
        isPause = false;
        this.notify();
    }

    public boolean isClose() {
        return isClose;
    }

    public void setClose(boolean isClose) {
        this.isClose = isClose;
    }

    @Override
    public void run() {
        int index = 0;
        while (!isClose ) {
            if (Settings.System.getInt(mContext.getContentResolver(), "Burn", 0) == 1 && !isPause) {

                Utils.setTestPattern(index + 1);

                index = ((index + 1) % 5);

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {

                }
            } else {
				Log.d(TAG,"isPause:"+isPause +"Burn:"+Settings.System.getInt(mContext.getContentResolver(), "Burn", 0));
                onThreadWait();
            }
        }
    }
}
