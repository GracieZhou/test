
package com.android.server.scifly;

import scifly.ISciflyManager;
import scifly.virtualmouse.IVirtualMouseManager;
import scifly.thememanager.IThemeManager;
import scifly.intent.IntentExtra;
import scifly.security.ISecurityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.SystemProperties;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

public class SciflyServiceImpl extends ISciflyManager.Stub {

    private static final String TAG = "SciflyServiceImpl";

    private static final boolean DBG = true;

    private Context mContext;

    // handler for UI, maybe later
    private HandlerThread mHanderThread;

    private SciflyHandler mSciflyHander;

    // virtual mouse service for handling IR key event
    private VirtualMouseService mVirtualMouseService = null;

    // theme manager
    private ThemeManagerService mThemeManagerService = null;
    
//    // voice-recognition manager
//    private VoiceRecognitionService mVoiceRecognitionService = null;

    // security manager for something, such as blacklist
    private SecurityManagerService mSecurityManagerService = null;

    private SciflyReceiver mSciflyReceiver = null;
    private WifiReceiver mWifiReceiver=null;

    /**
     * BroadcastReceiver for system broadcast.
     */
    private class SciflyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (DBG) {
                Log.d(TAG, "intent : " + intent.toString());
            }

            String action = intent.getAction();
            // authorize broadcast
            if (IntentExtra.ACTION_AUTHORIZE_STATE_CHANGED.equals(action)) {
                Message msg = mSciflyHander.obtainMessage();
                int state = intent.getIntExtra(IntentExtra.EXTRA_AUTHORIZE_STATE, 0);
                if (IntentExtra.AUTHORIZE_STATE_SUCCESSFUL == state) {
                    msg.what = SciflyHandler.DISMISS_AUTHORIZE_FAILED_VIEW;

                } else if (IntentExtra.AUTHORIZE_STATE_FAILED == state) {
                    msg.what = SciflyHandler.SHOW_AUTHORIZE_FAILED_VIEW;
                }
                msg.obj = intent.getStringExtra(IntentExtra.EXTRA_AUTHORIZE_MESSAGE);
                msg.sendToTarget();
            }
        }

    };
	/**
	 * Adding a BroadcastReceiver, use it to monitor the number of wifi disconnected.
	 * @author melody.xu
	 * @date 2014-6-24
	 */
	private class WifiReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (DBG) {
				Log.d(TAG, "intent : " + intent.toString());
			}
			String action = intent.getAction();
			// authorize broadcast
			if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

				final NetworkInfo networkInfo = (NetworkInfo) intent
						.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if (networkInfo != null) {
					Log.d(TAG,
							"networkInfo.getDetailedState():"
									+ networkInfo.getDetailedState());
					// observer the wifi_connect or wifi_disconnect
					String wifiChangeTag = SystemProperties
							.get("net.wifi_change_tag");
					switch (networkInfo.getDetailedState()) {
					case CONNECTED:
						Log.d(TAG, "CONNECTED");

						if ("".equals(wifiChangeTag) || wifiChangeTag == null) {
							wifiChangeTag = "1";
							Log.d(TAG, "wifiChangeTag is null");
						}
						Log.d(TAG, "wifiChangeTag" + wifiChangeTag);
						// BackUpData.backupData("wifi", "wifi_ssid",
						// "wifi_ssid");
						if (wifiChangeTag.equals("0")) {
							wifiChangeTag = "1";
						}
						SystemProperties.set("net.wifi_change_tag",
								wifiChangeTag);
						break;
					case DISCONNECTED:
						Log.d(TAG, "DISCONNECTED");

						if ("".equals(wifiChangeTag) || wifiChangeTag == null) {
							wifiChangeTag = "0";
							Log.d(TAG, "wifiChangeTag is null");
						}
						// when the state of wifi changes from connect to
						// disconnect
						// ,it counts 1.
						Log.d(TAG, "wifi_change_tag" + wifiChangeTag);
						if (wifiChangeTag.equals("1")) {
							String value = SystemProperties
									.get("net.wifi_disconnect_times");

							if ("".equals(value) || value == null) {
								value = "1";
							} else {
								value = (Integer.parseInt(value) + 1) + "";
							}
							Log.d(TAG, "value:" + value);
							SystemProperties.set("net.wifi_disconnect_times",
									value);
							wifiChangeTag = "0";
							SystemProperties.set("net.wifi_change_tag",
									wifiChangeTag);
						}
						break;
					default:
						break;
					}
				}

			}
		}

	};
    /**
     * Manager of all SciflyUI service.
     * 
     * @param context {@link Context}
     */
    public SciflyServiceImpl(Context context) {
        mContext = context;
        mHanderThread = new HandlerThread("scifly_service_handlerthread");
        mHanderThread.start();
    }

    /**
     * Init all Service and relative Object.
     */
    public synchronized void start() {
        Log.d(TAG, "systemReady");


        // for ui refresh
        mSciflyHander = new SciflyHandler(mHanderThread.getLooper(), mContext);
        // register
        registerBroadcastReceiver();
        Log.d(TAG,"registerBroadcastReceiver");
        // new virtual mouse service
        mVirtualMouseService = new VirtualMouseService(mContext);
        mThemeManagerService = new ThemeManagerService(mContext);
        // initial security manager
        mSecurityManagerService = new SecurityManagerService(mContext);
//        // initial voice-recognition manager
//        mVoiceRecognitionService = new VoiceRecognitionService(mContext);
    }

    /**
     * Destory Scifly Service and recycle all resources.
     */
    public synchronized void shutdown() {
        // check the caller wether have the permission to shutdown.
        if (mContext.checkCallingOrSelfPermission(android.Manifest.permission.SHUTDOWN) != PackageManager.PERMISSION_GRANTED) {
            throw new SecurityException(String.format("Requires %s permission", android.Manifest.permission.SHUTDOWN));
        }

        unregisterBroadcastReceiver();
        mVirtualMouseService = null;
        mThemeManagerService = null;
        mSecurityManagerService = null;
//        mVoiceRecognitionService = null;
    }

    /**
     * @return The Instance of Virtual Mouse Manager.
     * @throws RemoteException
     */
    public IVirtualMouseManager getVirtualMouseManager() throws RemoteException {
        if (mVirtualMouseService == null) {
            Log.w(TAG, "oho, virtual mouse is null?");
            mVirtualMouseService = new VirtualMouseService(mContext);
        }

        return mVirtualMouseService;
    }

    /**
     * @return The Instance of Theme Manager.
     * @throws RemoteException
     */
    public IThemeManager getThemeManager() throws RemoteException {
        if (mThemeManagerService == null) {
            Log.w(TAG, "oho, mThemeManagerService is null?");
            mThemeManagerService = new ThemeManagerService(mContext);
        }

        return mThemeManagerService;
    }

    /**
     * @return The Instance of Security Manager.
     * @throws RemoteException
     */
    public ISecurityManager getSecurityManager() throws RemoteException {
        if (mSecurityManagerService == null) {
            Log.w(TAG, "oho, mSecurityManagerService is null?");
            mSecurityManagerService = new SecurityManagerService(mContext);
        }

        return mSecurityManagerService;
    }
    
//    public IVoiceRecognitionManager getVoiceRecognitionService() throws RemoteException {
//        if (mVoiceRecognitionService == null) {
//            Log.w(TAG, "oho, mVoiceRecognitionService is null?");
//            mVoiceRecognitionService = new VoiceRecognitionService(mContext);
//        }

//        return null;//mVoiceRecognitionService;
//    }

    private void registerBroadcastReceiver() {
        mSciflyReceiver = new SciflyReceiver();
        IntentFilter filter = new IntentFilter();
        // register receiver for authorize
        filter.addAction(IntentExtra.ACTION_AUTHORIZE_STATE_CHANGED);
        mContext.registerReceiver(mSciflyReceiver, filter);
        
        mWifiReceiver = new WifiReceiver();
        IntentFilter filter1 = new IntentFilter();
		filter1.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		mContext.registerReceiver(mWifiReceiver, filter1);
		Log.d(TAG,"WifiManager");
    }

    private void unregisterBroadcastReceiver() {
        mContext.unregisterReceiver(mSciflyReceiver);
    }
}
