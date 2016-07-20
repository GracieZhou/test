
package com.google.tv.eoslauncher.business;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.tv.eoslauncher.HomeActivity;
import com.google.tv.eoslauncher.HomeApplication;
import com.google.tv.eoslauncher.util.Constants;
import com.google.tv.eoslauncher.util.HistoryRec;
import com.google.tv.eoslauncher.util.Utils;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/*
 * projectName： EosLauncher
 * moduleName： NetworkReceiver.java
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-2-8 下午4:06:11
 * @Copyright © 2013 Eos Inc.
 */

public class NetworkReceiver extends BroadcastReceiver {

    private static final String TAG = "NetworkReceiver";

    private HomeActivity mContext;

    // get ads from web pages
    private AdAction mAdction;

    private AdTextAction mAdtxtction;

    private AdAppStoreAction mAdAppStoreAction;

    private AppAction mAppAction;

    private final Object lock = new Object();

    private final int DELAY_CHECK = 0x201;

    private final int UPDATE_APP_STORE = 2;

    private final int UPDATE_APP_INFO = 3;
	
    private final int UploadFiles = 6;
    
    private final int downloadlogo = 7;

    // -------- add by Jason ---------------------------------------------------
    private Boolean first_PowerOn = true;

    // -------------------------------------------------------------------------

    // the flag whether to show ThemeDisplayAct the first time
    public static volatile boolean isDelay = true;

    public NetworkReceiver(HomeActivity context, AdAction adAction, AdTextAction adTextAction,
            AdAppStoreAction adAppStoreAction, AppAction appAction) {
        this.mContext = context;
        this.mAdction = adAction;
        this.mAdtxtction = adTextAction;
        this.mAdAppStoreAction = adAppStoreAction;
        this.mAppAction = appAction;
    }

    private Runnable mNetworkRunnable = new Runnable() {

        @Override
        public void run() {
            synchronized (lock) {
                if (Utils.isNetworkConnected(mContext)) {
                    Utils.isNetworkState = true;
                    mHandler.sendEmptyMessage(Constants.NETWORK_CHANGE_UPDATE);
                    mHandler.sendEmptyMessageDelayed(downloadlogo, 4 * 1000);
                    Log.i(TAG, "NETWORK_CHANGE_UPDATE");
                } else {
                    Utils.isNetworkState = false;
                }
                Log.v(TAG, "isNetworkState = " + Utils.isNetworkState);
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.NETWORK_CHANGE_UPDATE:
                    long time = System.currentTimeMillis();
                    mAdction.parsePgmJson();
                    mAdtxtction.parsePgmJson();
                    // mAdAppStoreAction.parsePgmJson();
                    // mAppAction.parsePgmJson();
                    mHandler.sendEmptyMessageDelayed(UPDATE_APP_STORE, 30 * 1000);
                    mHandler.sendEmptyMessageDelayed(UPDATE_APP_INFO, 60 * 1000);
                    Log.v(TAG, "update..." + (System.currentTimeMillis() - time));
                    // -------- add by Jason
                    // ---------------------------------------------------
                    if (first_PowerOn) {
                    	Log.d("jjj","first_PowerOn");
                        first_PowerOn = false;
                        mHandler.sendEmptyMessageDelayed(UploadFiles, 30*1000);
                    }
                    // -------------------------------------------------------------------------
                    
                    
                    break;
                case DELAY_CHECK:
                    Log.i(TAG, "delay check network");
                    startCheckNetwork(mContext);
                    break;
                case UPDATE_APP_STORE:
                    mAdAppStoreAction.parsePgmJson();
                    break;
                case UPDATE_APP_INFO:
                    mAppAction.parsePgmJson();
                    break;
                case UploadFiles:
                	Log.d("jjj","handler UploadFiles");
                	 HistoryRec.GetFetchTime();
                     try{
                     String recData = HistoryRec.block[6] + ',' + HistoryRec.block7Action[0] + ',' + "" + ',' +android.provider.Settings.System.getString(mContext.getContentResolver(), "openTvRecord");
                     Log.d("jjj", "recData open:" + recData);
                     HistoryRec.writeToFile(recData);
                     recData = "";

                     recData = HistoryRec.block[6] + ',' + HistoryRec.block7Action[1] + ',' + "" + ',' +android.provider.Settings.System.getString(mContext.getContentResolver(), "closeTvRecord");
                     Log.d("jjj", "recData close:" + recData);
                     HistoryRec.writeToFile(recData);
                     recData = "";
                     }catch(Exception e){
                     	Log.d("jjj","writeToFile error :"+e.toString());
                     }
                     HistoryRec.GetBackendSwitch();
                    break;
                case downloadlogo:
                    Log.v(TAG,"HistoryRec.Get_Rec(open_AD)");
                    HistoryRec.Get_Rec("open_AD");
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, "action = " + action);
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
            /*Hander 机制 过滤1500ms 内的重复广播*/
                mHandler.removeMessages(DELAY_CHECK);
                mHandler.sendEmptyMessageDelayed(DELAY_CHECK, 1500);
        }
    }

    private void startCheckNetwork(final Context context) {
        // start a Thread to handler network changed
        HomeApplication.getInstance().addNewTask(mNetworkRunnable);
    }
}
