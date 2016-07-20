package com.eostek.wasuwidgethost.business;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * projectName：WasuWidgetHost.
 * moduleName： WeatherWidgetService.java
 *
 * @author vicky.wang
 * @version 1.0.0
 * @time  2014-8-14 4:30pm
 * @Copyright © 2014 Eos Inc.
 */
public class WeatherWidgetService extends Service {

	private static final String TAG = "WeatherWidgetService";

	private static final String ACTION_UPDATE_ALL = "com.eostek.llauncher.widget.UPDATE_ALL";

	private static final int UPDATE_TIME = 60000;

	private UpdateThread mUpdateThread;
	
	private Context mContext;

	private int count = 0;

	@Override
	public void onCreate() {
		mUpdateThread = new UpdateThread();
		mUpdateThread.start();
		mContext = this.getApplicationContext();
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
        if (mUpdateThread != null) {
        	mUpdateThread.interrupt();
        }
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		super.onStartCommand(intent, flags, startId);
	    return START_STICKY;
	}
	
    private class UpdateThread extends Thread {

        @Override
        public void run() {
            super.run();
            try {
	            count = 0;
	            while (true) {
	            	Log.d(TAG, "run ... count:" + count);
	            	count++;
	        		Intent updateIntent = new Intent(ACTION_UPDATE_ALL);
	        		mContext.sendBroadcast(updateIntent);
	                Thread.sleep(UPDATE_TIME);
	            } 
            } catch (InterruptedException e) {
            	// 将 InterruptedException 定义在while循环之外，意味着抛出 InterruptedException 异常时，终止线程。
                e.printStackTrace();
            }
        }
    }
}
