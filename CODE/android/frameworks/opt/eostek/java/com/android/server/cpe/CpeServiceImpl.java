package com.android.server.cpe;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.eostek.tm.cpe.manager.ICpeManager;
import com.eostek.tm.cpe.CpeAndroidService;
import com.eostek.tm.logging.AndroidLogFactory;
import com.eostek.tm.logging.JrmLoggerFactory;

public class CpeServiceImpl extends ICpeManager.Stub{

	private static final String TAG = "CpeServiceImpl";
	private Context mContext;
	private static final String CPE_THREAD_NAME = "cpe-service-thread";
	private static final int MSG_ONCREATE = 1;
	private CpeAndroidService cpeAndroidService;
	private CpeServiceHandler mHandler;
	
	public CpeServiceImpl(Context context) {
		mContext = context;
	}

	@Override
	public String getBBNuber() throws RemoteException {
		String bbno = cpeAndroidService.getBbNo(mContext);
		Log.d(TAG, "----getBBNuber: "+bbno);
		return bbno;
	}
	
	@Override
	public String getProductClass() throws RemoteException {
		String productClass = cpeAndroidService.getProductClass(mContext);
		Log.d(TAG, "---getProductClass"+productClass);
		return productClass;
	}
	
	@Override
	public String getSerialNumber() throws RemoteException {
		String serialNumber = cpeAndroidService.getSerialNumber(mContext);
		Log.d(TAG, "---getSerialNumber"+serialNumber);
		return serialNumber;
	}
	
	@Override
	public String getCpeStatus() throws RemoteException {
		String cpeStatus = cpeAndroidService.getCpeStatus(mContext);
		Log.d(TAG, "---getCpeStatus"+cpeStatus);
		return cpeStatus;
	}
	
	 public synchronized void start() {
		 Log.d(TAG, "---systemReady");
		 mHandlerThread.start();
	 }
	 private HandlerThread mHandlerThread = new HandlerThread(CPE_THREAD_NAME) {
		protected void onLooperPrepared() {
			mHandler = new CpeServiceHandler(mHandlerThread.getLooper());
			mHandler.sendEmptyMessage(MSG_ONCREATE);
		};
	};
	 
	private class CpeServiceHandler extends Handler {
		CpeServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
			switch (msg.what) {
				case MSG_ONCREATE:
					try {
						Log.d(TAG,"build cpe...");
						JrmLoggerFactory.setLogFactory(new AndroidLogFactory());
						cpeAndroidService = new CpeAndroidService();
						cpeAndroidService.builderCpeServer(mContext);
					} catch (Exception e1) {
						Log.e(TAG, "start CpeServer failed:" + e1.getMessage());
					}
					break;
				default:
					break;
			}
		}
	}
	
}
